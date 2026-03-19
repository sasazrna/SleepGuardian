package com.example.soundpattern.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Session : Screen("session")
    object History : Screen("history")
    object Settings : Screen("settings")
}
