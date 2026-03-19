package com.example.sleepguardian.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun startSession(): Flow<Int>
    suspend fun stopSession()
}
