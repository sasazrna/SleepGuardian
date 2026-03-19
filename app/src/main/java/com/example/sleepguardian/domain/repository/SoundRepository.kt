package com.example.sleepguardian.domain.repository

import com.example.sleepguardian.domain.model.RelaxingSound

interface SoundRepository {
    fun getRelaxingSounds(): List<RelaxingSound>
}
