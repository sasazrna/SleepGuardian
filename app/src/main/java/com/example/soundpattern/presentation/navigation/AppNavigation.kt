package com.example.soundpattern.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soundpattern.presentation.screens.HistoryScreen
import com.example.soundpattern.presentation.screens.HomeScreen
import com.example.soundpattern.presentation.screens.OnboardingScreen
import com.example.soundpattern.presentation.screens.SessionScreen
import com.example.soundpattern.presentation.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen()
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Session.route) {
            SessionScreen()
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
