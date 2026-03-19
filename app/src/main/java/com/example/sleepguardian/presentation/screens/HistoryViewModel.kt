package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.presentation.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel(private val repository: SleepHistoryRepository) :
    BaseViewModel<HistoryViewModel.HistoryUiState>(HistoryUiState()) {

    val historyState: StateFlow<HistoryUiState> = repository.getAllSessions()
        .map { sessions ->
            val weeklyAverages = calculateWeeklyAverages(sessions)
            HistoryUiState(
                sessions = sessions.map { it.toDisplayModel() },
                weeklyAverageScore = weeklyAverages.first,
                weeklyAverageDuration = weeklyAverages.second,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState(isLoading = true)
        )

    data class HistoryUiState(
        val sessions: List<SessionDisplayModel> = emptyList(),
        val weeklyAverageScore: String = "--",
        val weeklyAverageDuration: String = "--",
        val isLoading: Boolean = false
    )

    data class SessionDisplayModel(
        val id: Long,
        val date: String,
        val duration: String,
        val score: String
    )

    private fun calculateWeeklyAverages(sessions: List<SleepSessionEntity>): Pair<String, String> {
        if (sessions.isEmpty()) return Pair("--", "--")

        val last7Days = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        val recentSessions = sessions.filter { it.startTime > last7Days }

        if (recentSessions.isEmpty()) return Pair("--", "--")

        val avgScore = recentSessions.mapNotNull { it.sleepScore }.average().toInt()
        val avgDuration = recentSessions.map {
            if (it.endTime != null) it.endTime - it.startTime else 0L
        }.average().toLong()

        val hours = avgDuration / 3600000
        val minutes = (avgDuration % 3600000) / 60000

        return Pair(avgScore.toString(), "${hours}h ${minutes}m")
    }

    private fun SleepSessionEntity.toDisplayModel(): SessionDisplayModel {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date(startTime))

        val durationMillis = if (endTime != null) endTime - startTime else 0L
        val hours = durationMillis / 3600000
        val minutes = (durationMillis % 3600000) / 60000
        val durationStr = "${hours}h ${minutes}m"

        return SessionDisplayModel(
            id = id,
            date = dateStr,
            duration = durationStr,
            score = sleepScore?.toString() ?: "--"
        )
    }
}
