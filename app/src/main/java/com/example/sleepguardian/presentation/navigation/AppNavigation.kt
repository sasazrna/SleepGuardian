package com.example.sleepguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sleepguardian.domain.repository.OnboardingRepository
import com.example.sleepguardian.presentation.screens.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sleepguardian.data.audio.AudioTracker
import com.example.sleepguardian.data.repository.SessionRepositoryImpl
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase

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
            HomeScreen(navController)
        }
        composable(Screen.Session.route) {
            val audioTracker = AudioTracker()
            val sessionRepository = SessionRepositoryImpl(audioTracker)
            val useCase = SleepSessionUseCase(sessionRepository)
            val sessionViewModel: SessionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SessionViewModel(useCase) as T
                    }
                }
            )
            SessionScreen(sessionViewModel, navController)
        }
        composable(Screen.History.route) {
            HistoryScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
