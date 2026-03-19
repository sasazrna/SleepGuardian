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
            HistoryUiState(
                sessions = sessions.map { it.toDisplayModel() },
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
        val isLoading: Boolean = false
    )

    data class SessionDisplayModel(
        val id: Long,
        val date: String,
        val duration: String,
        val score: String
    )

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
