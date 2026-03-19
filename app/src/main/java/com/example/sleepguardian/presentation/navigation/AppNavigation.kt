package com.example.sleepguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sleepguardian.domain.repository.OnboardingRepository
import com.example.sleepguardian.presentation.screens.HistoryScreen
import com.example.sleepguardian.presentation.screens.HomeScreen
import com.example.sleepguardian.presentation.screens.OnboardingScreen
import com.example.sleepguardian.presentation.screens.OnboardingViewModel
import com.example.sleepguardian.presentation.screens.SessionScreen
import com.example.sleepguardian.presentation.screens.SettingsScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Composable
fun AppNavigation(startDestination: String, repository: OnboardingRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            val onboardingViewModel: OnboardingViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return OnboardingViewModel(repository) as T
                    }
                }
            )
            OnboardingScreen(onboardingViewModel, navController)
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
