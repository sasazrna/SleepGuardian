package com.example.sleepguardian.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        val maxBars = 40
        val recentAmplitudes = amplitudes.takeLast(maxBars)
        val step = if (recentAmplitudes.isNotEmpty()) width / maxBars else 0f

        recentAmplitudes.forEachIndexed { index, amplitude ->
            val x = index * step
            // Note: In a real Compose app, you'd want to animate each bar individually
            // For MVP simplicity, we scale the height. A full per-bar animation
            // would require a list of animated states.
            val barHeight = amplitude * centerY * 0.9f

            // Main line
            drawLine(
                color = color,
                start = Offset(x, centerY - barHeight),
                end = Offset(x, centerY + barHeight),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Subtle glow
            drawLine(
                color = color.copy(alpha = 0.2f),
                start = Offset(x, centerY - barHeight - 2.dp.toPx()),
                end = Offset(x, centerY + barHeight + 2.dp.toPx()),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
