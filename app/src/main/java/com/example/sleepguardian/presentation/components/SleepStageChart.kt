package com.example.sleepguardian.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sleepguardian.presentation.screens.SessionDetailViewModel

@Composable
fun SleepStageChart(
    stages: List<SessionDetailViewModel.SleepStageDisplayModel>,
    modifier: Modifier = Modifier
) {
    val awakeColor = MaterialTheme.colorScheme.error
    val lightSleepColor = MaterialTheme.colorScheme.primary
    val deepSleepColor = MaterialTheme.colorScheme.tertiary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        if (stages.isEmpty()) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / stages.size

        stages.forEachIndexed { index, stage ->
            val color = when (stage.stage) {
                "Awake" -> awakeColor
                "Light Sleep" -> lightSleepColor
                "Deep Sleep" -> deepSleepColor
                else -> Color.Gray
            }

            val heightFactor = when (stage.stage) {
                "Awake" -> 0.3f
                "Light Sleep" -> 0.6f
                "Deep Sleep" -> 1.0f
                else -> 0.1f
            }

            drawRect(
                color = color,
                topLeft = Offset(index * barWidth, canvasHeight * (1 - heightFactor)),
                size = Size(barWidth, canvasHeight * heightFactor)
            )
        }
    }
}
