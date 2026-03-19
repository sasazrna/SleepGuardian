package com.example.sleepguardian.domain.usecase

import com.example.sleepguardian.domain.model.AlarmSettings
import java.util.*

class SmartAlarmUseCase {

    /**
     * Determines if the alarm should fire based on a detected calm period
     * within the specified wake window.
     */
    fun shouldWakeUp(currentTime: Long, settings: AlarmSettings, isCalmDetected: Boolean): Boolean {
        if (!settings.isEnabled) return false

        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.HOUR_OF_DAY, settings.hour)
            set(Calendar.MINUTE, settings.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetWakeTime = calendar.timeInMillis
        val windowStartTime = targetWakeTime - (settings.wakeWindowMinutes * 60 * 1000L)

        // If we are within the window and a calm period is detected, wake up
        return currentTime >= windowStartTime && currentTime < targetWakeTime && isCalmDetected
    }
}
