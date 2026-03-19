package com.example.sleepguardian.data.repository

import com.example.sleepguardian.data.local.SleepDao
import com.example.sleepguardian.data.local.SleepSessionEntity
import com.example.sleepguardian.data.local.SleepStageEntity
import com.example.sleepguardian.data.local.SoundEventEntity
import com.example.sleepguardian.domain.repository.SleepHistoryRepository
import kotlinx.coroutines.flow.Flow

class SleepHistoryRepositoryImpl(private val sleepDao: SleepDao) : SleepHistoryRepository {

    override suspend fun startNewSession(startTime: Long): Long {
        return sleepDao.insertSession(SleepSessionEntity(startTime = startTime))
    }

    override suspend fun updateSession(session: SleepSessionEntity) {
        sleepDao.updateSession(session)
    }

    override fun getAllSessions(): Flow<List<SleepSessionEntity>> {
        return sleepDao.getAllSessions()
    }

    override suspend fun addSoundEvent(event: SoundEventEntity) {
        sleepDao.insertSoundEvent(event)
    }

    override suspend fun getSoundEventsForSession(sessionId: Long): List<SoundEventEntity> {
        return sleepDao.getSoundEventsForSession(sessionId)
    }

    override suspend fun addSleepStage(stage: SleepStageEntity) {
        sleepDao.insertSleepStage(stage)
    }

    override suspend fun getSleepStagesForSession(sessionId: Long): List<SleepStageEntity> {
        return sleepDao.getSleepStagesForSession(sessionId)
    }
}
