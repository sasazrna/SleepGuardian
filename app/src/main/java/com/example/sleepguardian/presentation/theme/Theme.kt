package com.example.sleepguardian.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val SleepDarkColorScheme = darkColorScheme(
    primary = SoftLavender,
    onPrimary = DeepIndigo,
    primaryContainer = MutedSlate,
    onPrimaryContainer = StarLight,
    secondary = PurpleGrey80,
    background = DeepIndigo,
    surface = DeepIndigo,
    onBackground = StarLight,
    onSurface = StarLight,
    surfaceVariant = MutedSlate,
    onSurfaceVariant = SoftLavender
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun SleepGuardianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // For sleep guardian, we prioritize the dark theme for sleep-friendliness
    forceDark: Boolean = true,
    dynamicColor: Boolean = false, // Disable dynamic color to maintain sleep-friendly palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme || forceDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        forceDark || darkTheme -> SleepDarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
