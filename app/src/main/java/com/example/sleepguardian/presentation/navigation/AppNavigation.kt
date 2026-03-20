package com.example.sleepguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.sleepguardian.domain.repository.OnboardingRepository
import com.example.sleepguardian.presentation.screens.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import com.example.sleepguardian.data.audio.AudioTracker
import com.example.sleepguardian.data.audio.StaticSoundRepository
import com.example.sleepguardian.data.audio.TFLiteAudioClassifier
import com.example.sleepguardian.data.local.DataStoreAlarmRepository
import com.example.sleepguardian.data.local.SleepDatabase
import com.example.sleepguardian.data.repository.SessionRepositoryImpl
import com.example.sleepguardian.data.repository.SleepHistoryRepositoryImpl
import com.example.sleepguardian.domain.usecase.*
import com.example.sleepguardian.service.AlarmScheduler

@Composable
fun AppNavigation(startDestination: String, repository: OnboardingRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Manual DI container
    val dependencies = remember(context) {
        val database = SleepDatabase.getDatabase(context)
        val audioTracker = AudioTracker()
        val sessionRepository = SessionRepositoryImpl(audioTracker)
        val historyRepository = SleepHistoryRepositoryImpl(database.sleepDao())
        val alarmRepository = DataStoreAlarmRepository(context)

        val aiClassifier = TFLiteAudioClassifier(context)
        val classifyUseCase = ClassifySoundUseCase(aiClassifier)

        val detectStageUseCase = DetectSleepStageUseCase()
        val sessionUseCase = SleepSessionUseCase(sessionRepository, classifyUseCase, detectStageUseCase)
        val scoreUseCase = SleepScoreUseCase()
        val smartAlarmUseCase = SmartAlarmUseCase()
        val alarmScheduler = AlarmScheduler(context)
        val soundRepository = StaticSoundRepository()

        object {
            val historyRepo = historyRepository
            val alarmRepo = alarmRepository
            val sessionUseCase = sessionUseCase
            val scoreUseCase = scoreUseCase
            val smartAlarmUseCase = smartAlarmUseCase
            val alarmScheduler = alarmScheduler
            val soundRepo = soundRepository
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
            val smartAlarmViewModel: SmartAlarmViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SmartAlarmViewModel(dependencies.alarmRepo, dependencies.alarmScheduler) as T
                    }
                }
            )
            HomeScreen(navController, smartAlarmViewModel)
        }
        composable(Screen.Session.route) {
            val sessionViewModel: SessionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SessionViewModel(
                            context.applicationContext,
                            dependencies.sessionUseCase,
                            dependencies.historyRepo,
                            dependencies.scoreUseCase,
                            dependencies.alarmRepo,
                            dependencies.smartAlarmUseCase
                        ) as T
                    }
                }
            )
            SessionScreen(sessionViewModel, navController)
        }
        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return HistoryViewModel(dependencies.historyRepo) as T
                    }
                }
            )
            HistoryScreen(historyViewModel, navController)
        }
        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: -1L
            val detailViewModel: SessionDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SessionDetailViewModel(sessionId, dependencies.historyRepo) as T
                    }
                }
            )
            SessionDetailScreen(detailViewModel)
        }
        composable(Screen.SmartAlarm.route) {
            val smartAlarmViewModel: SmartAlarmViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SmartAlarmViewModel(dependencies.alarmRepo, dependencies.alarmScheduler) as T
                    }
                }
            )
            SmartAlarmScreen(smartAlarmViewModel)
        }
        composable(Screen.Sounds.route) {
            val soundsViewModel: SoundsViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return SoundsViewModel(dependencies.soundRepo) as T
                    }
                }
            )
            SoundsScreen(soundsViewModel)
        }
        composable(Screen.MorningGame.route) {
            val morningGameViewModel: MorningGameViewModel = viewModel()
            MorningGameScreen(morningGameViewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
