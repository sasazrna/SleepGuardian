package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.domain.repository.AlarmRepository
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.presentation.BaseViewModel
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val historyRepository: SleepHistoryRepository,
    private val alarmRepository: AlarmRepository
) : BaseViewModel<HomeViewModel.HomeUiState>(HomeUiState()) {

    val homeState: StateFlow<HomeUiState> = combine(
        historyRepository.getAllSessions(),
        alarmRepository.getAlarmSettings()
    ) { sessions, settings ->
        val lastSession = sessions.maxByOrNull { it.startTime }
        val goalHours = settings.sleepGoalHours

        val lastDurationMillis = if (lastSession?.endTime != null) {
            lastSession.endTime - lastSession.startTime
        } else 0L

        val lastDurationHours = lastDurationMillis / 3600000.0
        val progress = if (goalHours > 0) (lastDurationHours / goalHours).toFloat().coerceIn(0f, 1f) else 0f

        HomeUiState(
            lastScore = lastSession?.sleepScore ?: 0,
            lastDurationStr = formatDuration(lastDurationMillis),
            sleepGoalHours = goalHours,
            goalProgress = progress,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    private fun formatDuration(millis: Long): String {
        val hours = millis / 3600000
        val minutes = (millis % 3600000) / 60000
        return if (hours > 0 || minutes > 0) "${hours}h ${minutes}m" else "--"
    }

    data class HomeUiState(
        val lastScore: Int = 0,
        val lastDurationStr: String = "--",
        val sleepGoalHours: Int = 8,
        val goalProgress: Float = 0f,
        val isLoading: Boolean = false
    )
}
