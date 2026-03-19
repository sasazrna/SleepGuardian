package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.domain.model.RelaxingSound
import com.example.sleepguardian.domain.repository.SoundRepository
import com.example.sleepguardian.presentation.BaseViewModel
import com.example.sleepguardian.service.SoundPlayerService
import kotlinx.coroutines.launch

class SoundsViewModel(
    private val repository: SoundRepository
) : BaseViewModel<SoundsViewModel.SoundsUiState>(SoundsUiState()) {

    private var soundPlayerService: SoundPlayerService? = null

    init {
        val sounds = repository.getRelaxingSounds()
        _uiState.value = _uiState.value.copy(sounds = sounds)
    }

    fun setService(service: SoundPlayerService) {
        this.soundPlayerService = service
    }

    fun onSoundSelected(sound: RelaxingSound) {
        _uiState.value = _uiState.value.copy(selectedSound = sound)
    }

    fun togglePlayback() {
        val current = _uiState.value
        val isPlaying = !current.isPlaying
        _uiState.value = current.copy(isPlaying = isPlaying)

        if (isPlaying) {
            current.selectedSound?.let {
                soundPlayerService?.playSound(it.resourceId, current.timerMinutes)
            }
        } else {
            soundPlayerService?.pauseSound()
        }
    }

    fun setTimer(minutes: Int) {
        _uiState.value = _uiState.value.copy(timerMinutes = minutes)
    }

    data class SoundsUiState(
        val sounds: List<RelaxingSound> = emptyList(),
        val selectedSound: RelaxingSound? = null,
        val isPlaying: Boolean = false,
        val timerMinutes: Int = 30
    )
}
