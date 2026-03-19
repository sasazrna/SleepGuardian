package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase
import com.example.sleepguardian.domain.usecase.SoundLevel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionViewModel(private val sleepSessionUseCase: SleepSessionUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var trackingJob: Job? = null

    init {
        startTimer()
        startSoundTracking()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
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
            sleepSessionUseCase.execute().collect { soundLevel ->
                _uiState.update { it.copy(currentSound = soundLevel.label) }
            }
        }
    }

    fun stopSession() {
        viewModelScope.launch {
            timerJob?.cancel()
            trackingJob?.cancel()
            sleepSessionUseCase.stopSession()
            _uiState.update { it.copy(status = "Idle", currentSound = "Stopped") }
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
        val currentSound: String = "None"
    )
}
