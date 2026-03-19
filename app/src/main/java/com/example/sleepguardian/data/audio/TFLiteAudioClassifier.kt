package com.example.sleepguardian.data.audio

import com.example.sleepguardian.domain.model.SoundAIResult
import com.example.sleepguardian.domain.usecase.AudioAIClassifier
import kotlin.random.Random

class TFLiteAudioClassifier : AudioAIClassifier {

    /**
     * In a real implementation, this would load a .tflite model and run inference.
     * For MVP, we simulate classification based on amplitude and some random variance
     * to show the structure is working.
     */
    override fun classify(audioData: ShortArray, amplitude: Int): SoundAIResult {
        return when {
            amplitude < 300 -> SoundAIResult("Silence", 0.95f)
            amplitude > 15000 -> SoundAIResult("Loud Noise", 0.90f)
            else -> {
                // Simulate various sound types when amplitude is in a moderate range
                val random = Random.nextInt(100)
                when {
                    random < 20 -> SoundAIResult("Snoring", 0.75f + Random.nextFloat() * 0.2f)
                    random < 40 -> SoundAIResult("Cough", 0.70f + Random.nextFloat() * 0.2f)
                    random < 60 -> SoundAIResult("Talking", 0.65f + Random.nextFloat() * 0.2f)
                    else -> SoundAIResult("Noise", 0.80f + Random.nextFloat() * 0.1f)
                }
            }
        }
    }
}
