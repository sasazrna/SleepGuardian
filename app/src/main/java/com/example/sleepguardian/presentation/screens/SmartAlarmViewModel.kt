package com.example.sleepguardian.presentation.screens

import androidx.lifecycle.viewModelScope
import com.example.sleepguardian.domain.model.AlarmSettings
import com.example.sleepguardian.domain.repository.AlarmRepository
import com.example.sleepguardian.presentation.BaseViewModel
import com.example.sleepguardian.service.AlarmScheduler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SmartAlarmViewModel(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) : BaseViewModel<AlarmSettings>(AlarmSettings()) {

    init {
        viewModelScope.launch {
            repository.getAlarmSettings().collectLatest { settings ->
                _uiState.value = settings
            }
        }
    }

    fun updateHour(hour: Int) {
        _uiState.value = _uiState.value.copy(hour = hour)
    }

    fun updateMinute(minute: Int) {
        _uiState.value = _uiState.value.copy(minute = minute)
    }

    fun updateWakeWindow(minutes: Int) {
        _uiState.value = _uiState.value.copy(wakeWindowMinutes = minutes)
    }

    fun toggleEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isEnabled = enabled)
    }

    fun saveSettings() {
        viewModelScope.launch {
            val settings = _uiState.value
            repository.saveAlarmSettings(settings)
            if (settings.isEnabled) {
                alarmScheduler.schedule(settings)
            } else {
                alarmScheduler.cancel()
            }
        }
    }
}
