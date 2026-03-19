package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repository: OnboardingRepository) : ViewModel() {

    private val _events = MutableSharedFlow<OnboardingEvent>()
    val events = _events.asSharedFlow()

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted(true)
            _events.emit(OnboardingEvent.OnboardingCompleted)
        }
    }

    sealed class OnboardingEvent {
        object OnboardingCompleted : OnboardingEvent()
    }
}
