package com.example.sleepguardian.domain.usecase

import com.example.sleepguardian.data.local.SoundEventEntity

class SleepScoreUseCase {

    /**
     * Calculates a sleep score from 0-100 based on session duration and noise events.
     * Higher is better.
     */
    fun calculateScore(durationMillis: Long, noiseEvents: List<SoundEventEntity>): Int {
        if (durationMillis <= 0) return 0

        // Base score for duration (max 70 points for 8 hours)
        val idealDuration = 8 * 60 * 60 * 1000L
        val durationScore = ((durationMillis.toDouble() / idealDuration) * 70).coerceAtMost(70.0)

        // Penalty for noise (max 30 points)
        // 0 noises = 30 points, 100+ noises = 0 points
        val noiseCount = noiseEvents.count { it.label != "Quiet" }
        val noisePenalty = (noiseCount * 0.5).coerceAtMost(30.0)
        val noiseScore = 30.0 - noisePenalty

        return (durationScore + noiseScore).toInt().coerceIn(0, 100)
    }
}
