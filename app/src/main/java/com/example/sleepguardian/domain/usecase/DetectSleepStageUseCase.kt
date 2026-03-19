package com.example.sleepguardian.domain.usecase

import com.example.sleepguardian.domain.model.SleepStage

class DetectSleepStageUseCase {

    private val soundHistory = mutableListOf<String>()
    private val maxHistorySize = 10 // Last 10 detected sounds

    /**
     * Determines the sleep stage based on the recent history of sound levels.
     * Simple logic:
     * - frequent loud noise/talking -> AWAKE
     * - moderate noise -> LIGHT_SLEEP
     * - mostly silence -> DEEP_SLEEP
     */
    fun execute(newSoundLabel: String): SleepStage {
        soundHistory.add(newSoundLabel)
        if (soundHistory.size > maxHistorySize) {
            soundHistory.removeAt(0)
        }

        val noiseCount = soundHistory.count { it != "Silence" && it != "Quiet" }
        val loudNoiseCount = soundHistory.count { it == "Loud Noise" || it == "Talking" }

        return when {
            loudNoiseCount >= 2 || noiseCount >= 6 -> SleepStage.AWAKE
            noiseCount >= 2 -> SleepStage.LIGHT_SLEEP
            else -> SleepStage.DEEP_SLEEP
        }
    }
}
