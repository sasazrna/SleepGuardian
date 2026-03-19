package com.example.sleepguardian.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class SoundPlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private val binder = SoundPlayerBinder()
    private val handler = Handler(Looper.getMainLooper())
    private var stopRunnable: Runnable? = null

    inner class SoundPlayerBinder : Binder() {
        fun getService(): SoundPlayerService = this@SoundPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun playSound(resourceId: Int, durationMinutes: Int? = null) {
        stopMediaPlayer()

        if (resourceId == 0) return // Skip placeholder

        mediaPlayer = MediaPlayer.create(this, resourceId).apply {
            isLooping = true
            start()
        }

        durationMinutes?.let {
            scheduleStop(it)
        }
    }

    fun pauseSound() {
        mediaPlayer?.pause()
    }

    fun resumeSound() {
        mediaPlayer?.start()
    }

    fun stopSound() {
        stopMediaPlayer()
        cancelScheduledStop()
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun scheduleStop(minutes: Int) {
        cancelScheduledStop()
        stopRunnable = Runnable {
            stopSound()
        }
        handler.postDelayed(stopRunnable!!, minutes * 60 * 1000L)
    }

    private fun cancelScheduledStop() {
        stopRunnable?.let { handler.removeCallbacks(it) }
        stopRunnable = null
    }

    override fun onDestroy() {
        stopMediaPlayer()
        cancelScheduledStop()
        super.onDestroy()
    }
}
