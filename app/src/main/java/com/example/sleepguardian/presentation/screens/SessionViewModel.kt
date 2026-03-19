package com.example.sleepguardian.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.data.local.SoundEventEntity
import com.example.sleepguardian.domain.repository.AlarmRepository
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.domain.usecase.SleepScoreUseCase
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase
import com.example.sleepguardian.domain.usecase.SmartAlarmUseCase
import com.example.sleepguardian.presentation.BaseViewModel
import com.example.sleepguardian.service.AlarmService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionViewModel(
    private val applicationContext: Context,
    private val sleepSessionUseCase: SleepSessionUseCase,
    private val sleepHistoryRepository: SleepHistoryRepository,
    private val sleepScoreUseCase: SleepScoreUseCase,
    private val alarmRepository: AlarmRepository,
    private val smartAlarmUseCase: SmartAlarmUseCase
) : BaseViewModel<SessionViewModel.SessionUiState>(SessionUiState()) {

    private var timerJob: Job? = null
    private var trackingJob: Job? = null
    private var currentSessionId: Long = -1
    private var sessionStartTime: Long = 0
    private var isAlarmTriggered: Boolean = false

    init {
        startTimer()
        startSoundTracking()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            sessionStartTime = System.currentTimeMillis()
            currentSessionId = sleepHistoryRepository.startNewSession(sessionStartTime)

            var seconds = 0L
            while (true) {
                delay(1000)
                seconds++
                _uiState.update { it.copy(elapsedTime = formatDuration(seconds)) }
            }
        }
    }

    private fun startSoundTracking() {
        trackingJob = viewModelScope.launch {
            _uiState.update { it.copy(status = "Listening") }

            val alarmSettings = alarmRepository.getAlarmSettings().first()

            sleepSessionUseCase.execute().collect { result ->
                // Update UI with AI label if available, otherwise fall back to simple level
                val labelToDisplay = if (result.aiLabel != null && result.aiLabel != "Silence" && result.aiLabel != "Noise") {
                    "${result.aiLabel} (${((result.aiConfidence ?: 0f) * 100).toInt()}%)"
                } else {
                    result.level.label
                }
                _uiState.update { it.copy(currentSound = labelToDisplay) }

                // Smart Alarm Check - only trigger once
                val isCalm = result.level.label == "Quiet" || result.aiLabel == "Silence"
                if (!isAlarmTriggered && smartAlarmUseCase.shouldWakeUp(System.currentTimeMillis(), alarmSettings, isCalm)) {
                    isAlarmTriggered = true
                    triggerImmediateAlarm()
                }

                // Optimization: Only save sound events that are NOT "Quiet" to save battery/IO
                if (currentSessionId != -1L && result.level.label != "Quiet" && result.aiLabel != "Silence") {
                    val finalLabel = result.aiLabel ?: result.level.label
                    sleepHistoryRepository.addSoundEvent(
                        SoundEventEntity(
                            sessionId = currentSessionId,
                            timestamp = System.currentTimeMillis(),
                            label = finalLabel,
                            amplitude = result.amplitude
                        )
                    )
                }
            }
        }
    }

    private fun triggerImmediateAlarm() {
        val intent = Intent(applicationContext, AlarmService::class.java)
        applicationContext.startService(intent)
    }

    fun stopSession() {
        viewModelScope.launch {
            timerJob?.cancel()
            trackingJob?.cancel()
            sleepSessionUseCase.stopSession()

            if (currentSessionId != -1L) {
                val endTime = System.currentTimeMillis()
                val events = sleepHistoryRepository.getSoundEventsForSession(currentSessionId)
                val score = sleepScoreUseCase.calculateScore(endTime - sessionStartTime, events)

                sleepHistoryRepository.updateSession(
                    SleepSessionEntity(
                        id = currentSessionId,
                        startTime = sessionStartTime,
                        endTime = endTime,
                        sleepScore = score
                    )
                )
            }

            _uiState.update { it.copy(status = "Idle", currentSound = "Stopped", isSessionFinished = true) }
        }
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    data class SessionUiState(
        val elapsedTime: String = "00:00:00",
        val status: String = "Initializing",
        val currentSound: String = "None",
        val isSessionFinished: Boolean = false
    )
}
