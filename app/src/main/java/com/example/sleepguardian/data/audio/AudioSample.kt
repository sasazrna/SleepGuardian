package com.example.sleepguardian.data.audio

data class AudioSample(
    val data: ShortArray,
    val maxAmplitude: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AudioSample
        if (!data.contentEquals(other.data)) return false
        if (maxAmplitude != other.maxAmplitude) return false
        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + maxAmplitude
        return result
    }
}
