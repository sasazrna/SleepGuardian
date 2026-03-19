package com.example.sleepguardian.data.audio

import com.example.sleepguardian.domain.model.RelaxingSound
import com.example.sleepguardian.domain.repository.SoundRepository

class StaticSoundRepository : SoundRepository {
    override fun getRelaxingSounds(): List<RelaxingSound> {
        return listOf(
            RelaxingSound("white_noise", "White Noise", 0), // Placeholders
            RelaxingSound("rain", "Rain", 0),
            RelaxingSound("forest", "Forest", 0)
        )
    }
}
