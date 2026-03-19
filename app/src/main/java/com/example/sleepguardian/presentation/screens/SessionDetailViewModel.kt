package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.data.local.SleepStageEntity
import com.example.sleepguardian.data.local.SoundEventEntity
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import com.example.sleepguardian.presentation.BaseViewModel
import kotlinx.coroutines.flow.first
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

            // Fetch session for the score
            val session = repository.getAllSessions().first().find { it.id == sessionId }
            val events = repository.getSoundEventsForSession(sessionId)
            val stages = repository.getSleepStagesForSession(sessionId)

            val noiseCount = events.count { it.label == "Noise" }
            val loudNoiseCount = events.count { it.label == "Loud Noise" }
            val snoreCount = events.count { it.label == "Snoring" }
            val insights = generateInsights(events, stages)

            _uiState.value = DetailUiState(
                sleepScore = session?.sleepScore ?: 0,
                events = events.map { it.toDisplayModel() },
                stages = stages.map { it.toDisplayModel() },
                noiseCount = noiseCount,
                loudNoiseCount = loudNoiseCount,
                snoreCount = snoreCount,
                insights = insights,
                isLoading = false
            )
        }
    }

    private fun generateInsights(events: List<SoundEventEntity>, stages: List<SleepStageEntity>): List<String> {
        val list = mutableListOf<String>()
        val snoreCount = events.count { it.label == "Snoring" }

        if (events.count { it.label == "Loud Noise" } > 5) {
            list.add("You had frequent loud disturbances.")
        }
        if (stages.count { it.stage == "Deep Sleep" } > 10) {
            list.add("You had stable deep sleep periods.")
        }
        if (snoreCount > 20) {
            list.add("High snore count detected ($snoreCount). Try sleeping on your side to reduce snoring.")
        } else if (snoreCount > 0) {
            list.add("Some snoring was detected ($snoreCount).")
        }

        if (list.isEmpty()) {
            list.add("Your sleep was relatively calm.")
        }
        return list
    }

    data class DetailUiState(
        val sleepScore: Int = 0,
        val events: List<SoundEventDisplayModel> = emptyList(),
        val stages: List<SleepStageDisplayModel> = emptyList(),
        val noiseCount: Int = 0,
        val loudNoiseCount: Int = 0,
        val snoreCount: Int = 0,
        val insights: List<String> = emptyList(),
        val isLoading: Boolean = false
    )

    data class SoundEventDisplayModel(
        val time: String,
        val label: String,
        val amplitude: Int
    )

    data class SleepStageDisplayModel(
        val time: String,
        val stage: String
    )

    private fun SoundEventEntity.toDisplayModel(): SoundEventDisplayModel {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return SoundEventDisplayModel(
            time = dateFormat.format(Date(timestamp)),
            label = label,
            amplitude = amplitude
        )
    }

    private fun SleepStageEntity.toDisplayModel(): SleepStageDisplayModel {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return SleepStageDisplayModel(
            time = dateFormat.format(Date(timestamp)),
            stage = stage
        )
    }
}
