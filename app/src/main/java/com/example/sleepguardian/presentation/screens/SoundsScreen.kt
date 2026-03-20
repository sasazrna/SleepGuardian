package com.example.sleepguardian.presentation.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sleepguardian.presentation.components.InfoCard
import com.example.sleepguardian.presentation.components.PrimaryButton
import com.example.sleepguardian.service.SoundPlayerService

@Composable
fun SoundsScreen(viewModel: SoundsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Bind to SoundPlayerService
    DisposableEffect(Unit) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as SoundPlayerService.SoundPlayerBinder
                viewModel.setService(binder.getService())
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }

        context.bindService(
            Intent(context, SoundPlayerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        onDispose {
            context.unbindService(connection)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            com.example.sleepguardian.presentation.components.ScreenHeader(title = "Relaxing Sounds")

            InfoCard(
                title = "Sleep Timer",
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${uiState.timerMinutes} minutes")
                        Slider(
                            value = uiState.timerMinutes.toFloat(),
                            onValueChange = { viewModel.setTimer(it.toInt()) },
                            valueRange = 5f..120f,
                            modifier = Modifier.weight(1f).padding(start = 16.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Choose a sound", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(uiState.sounds) { sound ->
                    val isSelected = uiState.selectedSound?.id == sound.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onSoundSelected(sound) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sound.name,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (uiState.isPlaying) "Pause" else "Play",
                onClick = { viewModel.togglePlayback() },
                containerColor = if (uiState.isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        }
    }
}
