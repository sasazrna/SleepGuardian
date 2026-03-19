package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.data.local.SoundEventEntity
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.domain.usecase.SleepScoreUseCase
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase
import com.example.sleepguardian.presentation.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sleepSessionUseCase: SleepSessionUseCase,
    private val sleepHistoryRepository: SleepHistoryRepository,
    private val sleepScoreUseCase: SleepScoreUseCase
) : BaseViewModel<SessionViewModel.SessionUiState>(SessionUiState()) {

    private var timerJob: Job? = null
    private var trackingJob: Job? = null
    private var currentSessionId: Long = -1
    private var sessionStartTime: Long = 0

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
            sleepSessionUseCase.execute().collect { result ->
                _uiState.update { it.copy(currentSound = result.level.label) }

                if (currentSessionId != -1L) {
                    sleepHistoryRepository.addSoundEvent(
                        SoundEventEntity(
                            sessionId = currentSessionId,
                            timestamp = System.currentTimeMillis(),
                            label = result.level.label,
                            amplitude = result.amplitude
                        )
                    )
                }
            }
        }
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
