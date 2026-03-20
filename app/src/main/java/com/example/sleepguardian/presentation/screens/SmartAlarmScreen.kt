package com.example.sleepguardian.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sleepguardian.presentation.components.PrimaryButton

@Composable
fun SmartAlarmScreen(viewModel: SmartAlarmViewModel) {
    val settings by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            com.example.sleepguardian.presentation.components.ScreenHeader(title = "Smart Alarm")

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Enable Alarm", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = settings.isEnabled,
                    onCheckedChange = { viewModel.toggleEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Wake up at", style = MaterialTheme.typography.titleSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                NumberPicker(
                    value = settings.hour,
                    range = 0..23,
                    onValueChange = { viewModel.updateHour(it) }
                )
                Text(" : ", style = MaterialTheme.typography.headlineLarge)
                NumberPicker(
                    value = settings.minute,
                    range = 0..59,
                    onValueChange = { viewModel.updateMinute(it) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Wake window: ${settings.wakeWindowMinutes} mins", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = settings.wakeWindowMinutes.toFloat(),
                onValueChange = { viewModel.updateWakeWindow(it.toInt()) },
                valueRange = 10f..60f,
                steps = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Daily Sleep Goal: ${settings.sleepGoalHours} hours", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = settings.sleepGoalHours.toFloat(),
                onValueChange = { viewModel.updateSleepGoal(it.toInt()) },
                valueRange = 4f..12f,
                steps = 8
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Save Settings",
                onClick = { viewModel.saveSettings() }
            )
        }
    }
}

@Composable
fun NumberPicker(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    // Basic number picker for MVP
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Text("+")
        }
        Text(text = value.toString().padStart(2, '0'), style = MaterialTheme.typography.headlineLarge)
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Text("-")
        }
    }
}
