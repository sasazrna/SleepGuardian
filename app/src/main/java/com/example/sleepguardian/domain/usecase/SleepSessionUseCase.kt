package com.example.sleepguardian.domain.usecase

import com.example.sleepguardian.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SleepSessionUseCase(
    private val repository: SessionRepository,
    private val classifySoundUseCase: ClassifySoundUseCase
) {

    fun execute(): Flow<SoundResult> {
        return repository.startSession().map { sample ->
            val aiResult = classifySoundUseCase.execute(sample.data, sample.maxAmplitude)

            val soundLevel = classifySound(sample.maxAmplitude)
            SoundResult(soundLevel, sample.maxAmplitude, aiResult.label, aiResult.confidence)
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

data class SoundResult(
    val level: SoundLevel,
    val amplitude: Int,
    val aiLabel: String? = null,
    val aiConfidence: Float? = null
)

sealed class SoundLevel(val label: String) {
    object Quiet : SoundLevel("Quiet")
    object Noise : SoundLevel("Noise")
    object LoudNoise : SoundLevel("Loud Noise")
}
