package com.example.sleepguardian.data.repository

import com.example.sleepguardian.data.audio.AudioSample
import com.example.sleepguardian.data.audio.AudioTracker
import com.example.sleepguardian.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

class SessionRepositoryImpl(private val audioTracker: AudioTracker) : SessionRepository {

    override fun startSession(): Flow<AudioSample> {
        return audioTracker.startTracking()
    }

    override suspend fun stopSession() {
        // AudioTracker stops itself when flow is canceled
    }
}
