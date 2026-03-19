package com.example.sleepguardian.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.sleepguardian.domain.model.AlarmSettings
import com.example.sleepguardian.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreAlarmRepository(private val context: Context) : AlarmRepository {

    private object PreferencesKeys {
        val ALARM_ENABLED = booleanPreferencesKey("alarm_enabled")
        val ALARM_HOUR = intPreferencesKey("alarm_hour")
        val ALARM_MINUTE = intPreferencesKey("alarm_minute")
        val WAKE_WINDOW = intPreferencesKey("wake_window")
    }

    override fun getAlarmSettings(): Flow<AlarmSettings> {
        return context.dataStore.data.map { preferences ->
            AlarmSettings(
                isEnabled = preferences[PreferencesKeys.ALARM_ENABLED] ?: false,
                hour = preferences[PreferencesKeys.ALARM_HOUR] ?: 7,
                minute = preferences[PreferencesKeys.ALARM_MINUTE] ?: 0,
                wakeWindowMinutes = preferences[PreferencesKeys.WAKE_WINDOW] ?: 30
            )
        }
    }

    override suspend fun saveAlarmSettings(settings: AlarmSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ALARM_ENABLED] = settings.isEnabled
            preferences[PreferencesKeys.ALARM_HOUR] = settings.hour
            preferences[PreferencesKeys.ALARM_MINUTE] = settings.minute
            preferences[PreferencesKeys.WAKE_WINDOW] = settings.wakeWindowMinutes
        }
    }
}
