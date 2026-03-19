package com.example.sleepguardian.domain.model

data class AlarmSettings(
    val isEnabled: Boolean = false,
    val hour: Int = 7,
    val minute: Int = 0,
    val wakeWindowMinutes: Int = 30
)
