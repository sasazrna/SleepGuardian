package com.example.sleepguardian.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Session : Screen("session")
    object History : Screen("history")
    object Settings : Screen("settings")
    object SmartAlarm : Screen("smart_alarm")
    object Sounds : Screen("sounds")
    object MorningGame : Screen("morning_game")
    object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: Long) = "session_detail/$sessionId"
    }
}
