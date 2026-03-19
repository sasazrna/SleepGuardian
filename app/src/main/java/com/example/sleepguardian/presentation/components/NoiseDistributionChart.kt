package com.example.sleepguardian.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoiseDistributionChart(
    noiseCount: Int,
    loudNoiseCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "Noise Distribution", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        StatusIndicator(label = "Moderate Noise", value = noiseCount.toString(), indicatorColor = MaterialTheme.colorScheme.primary)
        StatusIndicator(label = "Loud Noise", value = loudNoiseCount.toString(), indicatorColor = MaterialTheme.colorScheme.error)
    }
}
