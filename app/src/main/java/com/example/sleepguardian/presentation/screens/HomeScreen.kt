package com.example.sleepguardian.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sleepguardian.presentation.components.InfoCard
import com.example.sleepguardian.presentation.components.PrimaryButton
import com.example.sleepguardian.presentation.components.StatusIndicator
import com.example.sleepguardian.presentation.navigation.Screen

@Composable
fun HomeScreen(navController: NavController, alarmViewModel: SmartAlarmViewModel) {
    val settings by alarmViewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sleep / Sound tracker",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            InfoCard(
                title = "Last night summary",
                content = {
                    StatusIndicator(label = "Duration", value = "7h 45m")
                    StatusIndicator(label = "Deep sleep", value = "2h 15m")
                    StatusIndicator(label = "Efficiency", value = "92%")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                title = "Sleep score",
                content = {
                    Text(
                        text = "88",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Good quality sleep",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                title = "Daily Goal",
                content = {
                    Column {
                        Text(text = "Goal: ${settings.sleepGoalHours} hours", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Simulated progress for MVP
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text(
                            text = "75% completed",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Start Sleep Session",
                onClick = {
                    navController.navigate(Screen.Session.route)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = "History",
                    onClick = {
                        navController.navigate(Screen.History.route)
                    },
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Smart Alarm",
                    onClick = {
                        navController.navigate(Screen.SmartAlarm.route)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Sounds",
                onClick = {
                    navController.navigate(Screen.Sounds.route)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Morning Alertness Game",
                onClick = {
                    navController.navigate(Screen.MorningGame.route)
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
