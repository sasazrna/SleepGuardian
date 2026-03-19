package com.example.sleepguardian.presentation.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.domain.repository.AlarmRepository
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.domain.usecase.SleepScoreUseCase
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase
import com.example.sleepguardian.domain.usecase.SmartAlarmUseCase
import com.example.sleepguardian.presentation.BaseViewModel
import com.example.sleepguardian.service.SleepTrackingService
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

    private var sleepTrackingService: SleepTrackingService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SleepTrackingService.SleepTrackingBinder
            sleepTrackingService = binder.getService()
            isBound = true

            // Start tracking in the service
            sleepTrackingService?.startTracking(
                sleepSessionUseCase,
                sleepHistoryRepository,
                sleepScoreUseCase,
                alarmRepository,
                smartAlarmUseCase
            )

            // Observe service state
            viewModelScope.launch {
                sleepTrackingService?.trackingState?.collect { state ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            elapsedTime = state.elapsedTime,
                            currentSound = state.currentSound,
                            currentStage = state.currentStage,
                            amplitudes = state.amplitudes,
                            isSessionFinished = state.isFinished
                        )
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            sleepTrackingService = null
        }
    }

    init {
        bindTrackingService()
    }

    private fun bindTrackingService() {
        val intent = Intent(applicationContext, SleepTrackingService::class.java)
        applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        applicationContext.startService(intent)
    }

    fun stopSession() {
        if (isBound) {
            sleepTrackingService?.stopTracking()
        }
    }

    override fun onCleared() {
        if (isBound) {
            applicationContext.unbindService(serviceConnection)
            isBound = false
        }
        super.onCleared()
    }

    data class SessionUiState(
        val elapsedTime: String = "00:00:00",
        val status: String = "Listening", // Fixed for foreground service
        val currentSound: String = "None",
        val currentStage: String = "Initializing",
        val isSessionFinished: Boolean = false,
        val amplitudes: List<Float> = emptyList()
    )
}
