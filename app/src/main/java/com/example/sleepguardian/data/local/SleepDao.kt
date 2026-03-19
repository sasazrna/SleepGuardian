package com.example.sleepguardian.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Insert
    suspend fun insertSession(session: SleepSessionEntity): Long

    @Update
    suspend fun updateSession(session: SleepSessionEntity)

    @Query("SELECT * FROM sleep_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SleepSessionEntity>>

    @Insert
    suspend fun insertSoundEvent(event: SoundEventEntity)

    @Query("SELECT * FROM sound_events WHERE sessionId = :sessionId")
    suspend fun getSoundEventsForSession(sessionId: Long): List<SoundEventEntity>
}
