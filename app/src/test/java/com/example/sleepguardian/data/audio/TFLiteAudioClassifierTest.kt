package com.example.sleepguardian.data.audio

import android.content.Context
import com.example.sleepguardian.domain.model.SoundAIResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class TFLiteAudioClassifierTest {

    private lateinit var classifier: TFLiteAudioClassifier
    private val context = mock(Context::class.java)

    @Before
    fun setUp() {
        // We don't have a real model file in tests, so it should fall back to simulation
        classifier = TFLiteAudioClassifier(context)
    }

    @Test
    fun `classify returns Silence for low amplitude`() {
        val result = classifier.classify(ShortArray(10), 100)
        assertEquals("Silence", result.label)
        assertEquals(0.95f, result.confidence)
    }

    @Test
    fun `classify returns Loud Noise for very high amplitude`() {
        val result = classifier.classify(ShortArray(10), 20000)
        assertEquals("Loud Noise", result.label)
        assertEquals(0.90f, result.confidence)
    }

    @Test
    fun `classify returns a valid label for moderate amplitude`() {
        val result = classifier.classify(ShortArray(10), 5000)
        val validLabels = listOf("Snoring", "Cough", "Talking", "Noise")
        assertTrue("Label ${result.label} should be one of $validLabels", result.label in validLabels)
        assertTrue("Confidence ${result.confidence} should be between 0 and 1", result.confidence in 0f..1f)
    }
}
