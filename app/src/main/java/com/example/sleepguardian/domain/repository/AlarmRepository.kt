package com.example.sleepguardian.domain.repository

import com.example.sleepguardian.domain.model.AlarmSettings
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarmSettings(): Flow<AlarmSettings>
    suspend fun saveAlarmSettings(settings: AlarmSettings)
}
