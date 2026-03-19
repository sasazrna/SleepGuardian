package com.example.sleepguardian.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sleepguardian.presentation.screens.SessionDetailViewModel

@Composable
fun WaveformSummary(
    events: List<SessionDetailViewModel.SoundEventDisplayModel>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        if (events.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val step = width / (events.size.coerceAtLeast(1))

        events.forEachIndexed { index, event ->
            val x = index * step
            // Normalize amplitude (assuming max 32768)
            val normalizedAmplitude = (event.amplitude.toFloat() / 32768f).coerceIn(0f, 1f)
            val barHeight = normalizedAmplitude * centerY

            drawLine(
                color = color.copy(alpha = 0.6f),
                start = Offset(x, centerY - barHeight),
                end = Offset(x, centerY + barHeight),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}
