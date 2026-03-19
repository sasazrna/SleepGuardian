package com.example.sleepguardian.domain.repository

import com.example.sleepguardian.data.audio.AudioSample
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun startSession(): Flow<AudioSample>
    suspend fun stopSession()
}
