package com.example.sleepguardian.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MorningGameScreen(viewModel: MorningGameViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val backgroundColor = when (uiState.gameState) {
        MorningGameViewModel.GameState.START -> MaterialTheme.colorScheme.background
        MorningGameViewModel.GameState.WAITING -> Color.Red
        MorningGameViewModel.GameState.GO -> Color.Green
        MorningGameViewModel.GameState.FINISHED -> MaterialTheme.colorScheme.primaryContainer
    }

    LaunchedEffect(uiState.gameState) {
        if (uiState.gameState == MorningGameViewModel.GameState.WAITING) {
            delay(2000L + (0..3000).random())
            viewModel.onSignalReady()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { viewModel.onTap() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (uiState.gameState) {
                MorningGameViewModel.GameState.START -> {
                    Text("Morning Alertness Test", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.startGame() }) {
                        Text("Start")
                    }
                }
                MorningGameViewModel.GameState.WAITING -> {
                    Text("Wait for Green...", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                MorningGameViewModel.GameState.GO -> {
                    Text("TAP NOW!", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                MorningGameViewModel.GameState.FINISHED -> {
                    Text("Reaction Time", style = MaterialTheme.typography.titleLarge)
                    Text("${uiState.reactionTime} ms", fontSize = 64.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.startGame() }) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}
