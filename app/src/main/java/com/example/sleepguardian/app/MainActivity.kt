package com.example.sleepguardian.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleepguardian.data.local.DataStoreOnboardingRepository
import com.example.sleepguardian.domain.repository.OnboardingRepository
import com.example.sleepguardian.presentation.MainViewModel
import com.example.sleepguardian.presentation.navigation.AppNavigation
import com.example.sleepguardian.presentation.theme.SleepGuardianTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Manual dependency injection for now, as DI framework is not requested yet
        val repository: OnboardingRepository = DataStoreOnboardingRepository(applicationContext)

        setContent {
            SleepGuardianTheme {
                val mainViewModel: MainViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return MainViewModel(repository) as T
                        }
                    }
                )

                val startDestination by mainViewModel.startDestination.collectAsState()

                startDestination?.let { destination ->
                    AppNavigation(startDestination = destination, repository = repository)
                }
            }
        }
    }
}
