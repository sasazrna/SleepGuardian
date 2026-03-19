package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.data.local.SoundEventEntity
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.presentation.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SessionDetailViewModel(
    private val sessionId: Long,
    private val repository: SleepHistoryRepository
) : BaseViewModel<SessionDetailViewModel.DetailUiState>(DetailUiState()) {

    init {
        loadSessionDetails()
    }

    private fun loadSessionDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val events = repository.getSoundEventsForSession(sessionId)

            val noiseCount = events.count { it.label == "Noise" }
            val loudNoiseCount = events.count { it.label == "Loud Noise" }

            _uiState.value = DetailUiState(
                events = events.map { it.toDisplayModel() },
                noiseCount = noiseCount,
                loudNoiseCount = loudNoiseCount,
                isLoading = false
            )
        }
    }

    data class DetailUiState(
        val events: List<SoundEventDisplayModel> = emptyList(),
        val noiseCount: Int = 0,
        val loudNoiseCount: Int = 0,
        val isLoading: Boolean = false
    )

    data class SoundEventDisplayModel(
        val time: String,
        val label: String,
        val amplitude: Int
    )

    private fun SoundEventEntity.toDisplayModel(): SoundEventDisplayModel {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return SoundEventDisplayModel(
            time = dateFormat.format(Date(timestamp)),
            label = label,
            amplitude = amplitude
        )
    }
}
