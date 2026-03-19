package com.example.sleepguardian.domain.repository

import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.data.local.SleepStageEntity
import com.example.sleepguardian.data.local.SoundEventEntity
import kotlinx.coroutines.flow.Flow

interface SleepHistoryRepository {
    suspend fun startNewSession(startTime: Long): Long
    suspend fun updateSession(session: SleepSessionEntity)
    fun getAllSessions(): Flow<List<SleepSessionEntity>>
    suspend fun addSoundEvent(event: SoundEventEntity)
    suspend fun getSoundEventsForSession(sessionId: Long): List<SoundEventEntity>
    suspend fun addSleepStage(stage: SleepStageEntity)
    suspend fun getSleepStagesForSession(sessionId: Long): List<SleepStageEntity>
}
