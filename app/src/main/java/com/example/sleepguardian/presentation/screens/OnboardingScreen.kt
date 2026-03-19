package com.example.sleepguardian.presentation.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sleepguardian.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel, navController: NavController) {
    val pages = listOf(
        OnboardingPage.Welcome,
        OnboardingPage.Microphone,
        OnboardingPage.Notifications,
        OnboardingPage.GetStarted
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is OnboardingViewModel.OnboardingEvent.OnboardingCompleted) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { position ->
            OnboardingPageUI(page = pages[position])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page Indicators
            Row {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(8.dp)
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = color,
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }
            }

            // Buttons
            Button(onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    viewModel.completeOnboarding()
                }
            }) {
                Text(if (pagerState.currentPage < pages.size - 1) "Next" else "Get Started")
            }
        }
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> /* Handle permission result */ }
    )

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> /* Handle permission result */ }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (page is OnboardingPage.Microphone) {
            Button(onClick = {
                microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }) {
                Text("Grant Microphone Permission")
            }
        }

        if (page is OnboardingPage.Notifications && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Button(onClick = {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }) {
                Text("Grant Notification Permission")
            }
        }
    }
}

sealed class OnboardingPage(val title: String, val description: String) {
    object Welcome : OnboardingPage("Welcome to SleepGuardian", "Your companion for a better sleep. Let's get you set up.")
    object Microphone : OnboardingPage("Microphone Access", "We need microphone access to detect sound patterns while you sleep.")
    object Notifications : OnboardingPage("Stay Informed", "Enable notifications to receive sleep reports and alarms.")
    object GetStarted : OnboardingPage("All Set!", "You are ready to start using SleepGuardian. Happy sleeping!")
}
