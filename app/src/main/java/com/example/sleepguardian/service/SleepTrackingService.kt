package com.example.sleepguardian.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.sleepguardian.app.MainActivity
import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.data.local.SleepStageEntity
import com.example.sleepguardian.data.local.SoundEventEntity
import com.example.sleepguardian.domain.repository.AlarmRepository
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.domain.usecase.SleepScoreUseCase
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase
import com.example.sleepguardian.domain.usecase.SmartAlarmUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SleepTrackingService : Service() {

    private val binder = SleepTrackingBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var trackingJob: Job? = null
    private var timerJob: Job? = null

    private val _trackingState = MutableStateFlow(TrackingState())
    val trackingState = _trackingState.asStateFlow()

    // Dependencies injected via Binder for simplicity in this MVP
    private lateinit var sleepSessionUseCase: SleepSessionUseCase
    private lateinit var sleepHistoryRepository: SleepHistoryRepository
    private lateinit var sleepScoreUseCase: SleepScoreUseCase
    private lateinit var alarmRepository: AlarmRepository
    private lateinit var smartAlarmUseCase: SmartAlarmUseCase

    private var currentSessionId: Long = -1
    private var sessionStartTime: Long = 0
    private var isAlarmTriggered: Boolean = false
    private var lastRecordedStage: String? = null

    inner class SleepTrackingBinder : Binder() {
        fun getService(): SleepTrackingService = this@SleepTrackingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_TRACKING) {
            stopTracking()
        }
        return START_NOT_STICKY
    }

    fun startTracking(
        sessionUseCase: SleepSessionUseCase,
        historyRepo: SleepHistoryRepository,
        scoreUseCase: SleepScoreUseCase,
        alarmRepo: AlarmRepository,
        smartUseCase: SmartAlarmUseCase
    ) {
        this.sleepSessionUseCase = sessionUseCase
        this.sleepHistoryRepository = historyRepo
        this.sleepScoreUseCase = scoreUseCase
        this.alarmRepository = alarmRepo
        this.smartAlarmUseCase = smartUseCase

        startForegroundService()

        sessionStartTime = System.currentTimeMillis()

        serviceScope.launch {
            currentSessionId = sleepHistoryRepository.startNewSession(sessionStartTime)
            startTimer()
            startAudioMonitoring()
        }
    }

    private fun startTimer() {
        timerJob = serviceScope.launch {
            var seconds = 0L
            while (isActive) {
                delay(1000)
                seconds++
                _trackingState.update { it.copy(elapsedTime = formatDuration(seconds)) }
            }
        }
    }

    private fun startAudioMonitoring() {
        trackingJob = serviceScope.launch {
            val alarmSettings = alarmRepository.getAlarmSettings().first()

            sleepSessionUseCase.execute().collect { result ->
                val labelToDisplay = if (result.aiLabel != null && result.aiLabel != "Silence" && result.aiLabel != "Noise") {
                    "${result.aiLabel} (${((result.aiConfidence ?: 0f) * 100).toInt()}%)"
                } else {
                    result.level.label
                }

                val normalizedAmplitude = (result.amplitude.toFloat() / 32768f).coerceIn(0f, 1.0f)

                _trackingState.update { currentState ->
                    val newAmplitudes = (currentState.amplitudes + normalizedAmplitude).takeLast(50)
                    currentState.copy(
                        currentSound = labelToDisplay,
                        currentStage = result.sleepStage?.label ?: currentState.currentStage,
                        amplitudes = newAmplitudes
                    )
                }

                // Record stage change
                val currentStageLabel = result.sleepStage?.label
                if (currentSessionId != -1L && currentStageLabel != null && currentStageLabel != lastRecordedStage) {
                    lastRecordedStage = currentStageLabel
                    sleepHistoryRepository.addSleepStage(
                        SleepStageEntity(sessionId = currentSessionId, timestamp = System.currentTimeMillis(), stage = currentStageLabel)
                    )
                }

                // Smart Alarm
                val isCalm = result.level.label == "Quiet" || result.aiLabel == "Silence"
                if (!isAlarmTriggered && smartAlarmUseCase.shouldWakeUp(System.currentTimeMillis(), alarmSettings, isCalm)) {
                    isAlarmTriggered = true
                    triggerImmediateAlarm()
                }

                // Persistence
                if (currentSessionId != -1L && result.level.label != "Quiet" && result.aiLabel != "Silence") {
                    sleepHistoryRepository.addSoundEvent(
                        SoundEventEntity(
                            sessionId = currentSessionId,
                            timestamp = System.currentTimeMillis(),
                            label = result.aiLabel ?: result.level.label,
                            amplitude = result.amplitude
                        )
                    )
                }
            }
        }
    }

    fun stopTracking() {
        serviceScope.launch {
            timerJob?.cancel()
            trackingJob?.cancel()
            sleepSessionUseCase.stopSession()

            if (currentSessionId != -1L) {
                val endTime = System.currentTimeMillis()
                val events = sleepHistoryRepository.getSoundEventsForSession(currentSessionId)
                val score = sleepScoreUseCase.calculateScore(endTime - sessionStartTime, events)

                sleepHistoryRepository.updateSession(
                    SleepSessionEntity(id = currentSessionId, startTime = sessionStartTime, endTime = endTime, sleepScore = score)
                )
            }

            _trackingState.update { it.copy(isFinished = true) }
            stopForeground(true)
            stopSelf()
        }
    }

    private fun triggerImmediateAlarm() {
        val intent = Intent(this, AlarmService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun startForegroundService() {
        val channelId = "sleep_tracking_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Sleep Tracking", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, SleepTrackingService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("SleepGuardian")
            .setContentText("Sleep tracking is active")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    data class TrackingState(
        val elapsedTime: String = "00:00:00",
        val currentSound: String = "None",
        val currentStage: String = "Initializing",
        val amplitudes: List<Float> = emptyList(),
        val isFinished: Boolean = false
    )

    companion object {
        private const val NOTIFICATION_ID = 2001
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
    }
}
