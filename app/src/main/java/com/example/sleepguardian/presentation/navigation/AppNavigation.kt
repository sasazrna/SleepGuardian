package com.example.sleepguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sleepguardian.domain.repository.OnboardingRepository
import com.example.sleepguardian.presentation.screens.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import com.example.sleepguardian.data.audio.AudioTracker
import com.example.sleepguardian.data.local.SleepDatabase
import com.example.sleepguardian.data.repository.SessionRepositoryImpl
import com.example.sleepguardian.data.repository.SleepHistoryRepositoryImpl
import com.example.sleepguardian.domain.usecase.SleepSessionUseCase

@Composable
fun AppNavigation(startDestination: String, repository: OnboardingRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Manual DI container-like optimization to avoid re-allocation during recomposition
    val dependencies = remember(context) {
        val database = SleepDatabase.getDatabase(context)
        val audioTracker = AudioTracker()
        val sessionRepository = SessionRepositoryImpl(audioTracker)
        val historyRepository = SleepHistoryRepositoryImpl(database.sleepDao())
        val useCase = SleepSessionUseCase(sessionRepository)

        object {
            val historyRepo = historyRepository
            val sessionUseCase = useCase
        }
    }

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
            val sessionViewModel: SessionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SessionViewModel(dependencies.sessionUseCase, dependencies.historyRepo) as T
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
