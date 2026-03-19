package com.example.sleepguardian.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val step = if (amplitudes.isNotEmpty()) width / (amplitudes.size.coerceAtLeast(1)) else 0f

        // Draw multiple lines for a "glow" effect
        amplitudes.forEachIndexed { index, amplitude ->
            val x = index * step
            val barHeight = amplitude * centerY * 0.8f // Scale for visibility

            // Main line
            drawLine(
                color = color,
                start = Offset(x, centerY - barHeight),
                end = Offset(x, centerY + barHeight),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Glow effect (outer)
            drawLine(
                color = color.copy(alpha = 0.3f),
                start = Offset(x, centerY - barHeight - 4.dp.toPx()),
                end = Offset(x, centerY + barHeight + 4.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
