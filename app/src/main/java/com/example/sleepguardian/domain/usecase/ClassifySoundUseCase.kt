package com.example.sleepguardian.domain.usecase

import com.example.sleepguardian.domain.model.SoundAIResult

interface AudioAIClassifier {
    fun classify(audioData: ShortArray, amplitude: Int): SoundAIResult
}

class ClassifySoundUseCase(private val classifier: AudioAIClassifier) {
    fun execute(audioData: ShortArray, amplitude: Int): SoundAIResult {
        return classifier.classify(audioData, amplitude)
    }
}
