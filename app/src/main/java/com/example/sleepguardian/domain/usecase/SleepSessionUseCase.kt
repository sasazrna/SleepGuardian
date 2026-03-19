package com.example.sleepguardian.domain.usecase

import com.example.sleepguardian.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SleepSessionUseCase(private val repository: SessionRepository) {

    fun execute(): Flow<SoundResult> {
        return repository.startSession().map { amplitude ->
            val soundLevel = classifySound(amplitude)
            SoundResult(soundLevel, amplitude)
        }
    }

    private fun classifySound(amplitude: Int): SoundLevel {
        return when {
            amplitude < 500 -> SoundLevel.Quiet
            amplitude < 5000 -> SoundLevel.Noise
            else -> SoundLevel.LoudNoise
        }
    }

    suspend fun stopSession() {
        repository.stopSession()
    }
}

data class SoundResult(val level: SoundLevel, val amplitude: Int)

sealed class SoundLevel(val label: String) {
    object Quiet : SoundLevel("Quiet")
    object Noise : SoundLevel("Noise")
    object LoudNoise : SoundLevel("Loud Noise")
}
