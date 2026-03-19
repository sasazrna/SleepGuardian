package com.example.sleepguardian.data.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.abs

class AudioTracker {

    private val sampleRate = 44100
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    @SuppressLint("MissingPermission")
    fun startTracking(): Flow<Int> = flow {
        val audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        val buffer = ShortArray(bufferSize)
        audioRecord.startRecording()

        try {
            while (true) {
                val readCount = audioRecord.read(buffer, 0, bufferSize)
                if (readCount > 0) {
                    var maxAmplitude = 0
                    for (i in 0 until readCount) {
                        maxAmplitude = maxOf(maxAmplitude, abs(buffer[i].toInt()))
                    }
                    emit(maxAmplitude)
                }
                delay(100) // Emit every 100ms
            }
        } finally {
            audioRecord.stop()
            audioRecord.release()
        }
    }.flowOn(Dispatchers.IO)
}
