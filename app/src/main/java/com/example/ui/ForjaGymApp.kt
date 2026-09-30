package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ForjaBottomNav
import com.example.ui.components.ForjaTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.CardioScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExerciseDetailScreen
import com.example.ui.screens.ExerciseLibraryScreen
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HydrationScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.RecordsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VolumeScreen
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.ForgeBlack
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun ForjaGymApp(
    viewModel: ForjaViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val activeWorkout by viewModel.activeWorkout.collectAsState()

    val streakCount = profile?.currentStreak ?: 0

    val isTopLevelScreen = currentScreen == Screen.DASHBOARD ||
            currentScreen == Screen.WORKOUTS ||
            currentScreen == Screen.PROGRESS ||
            currentScreen == Screen.HISTORY ||
            currentScreen == Screen.PROFILE

    val isFullScreenAuth = currentScreen == Screen.ONBOARDING

    // Handle back press
    BackHandler(enabled = !isFullScreenAuth && currentScreen != Screen.DASHBOARD) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ForgeBlack,
        topBar = {
            if (!isFullScreenAuth && currentScreen != Screen.ACTIVE_WORKOUT) {
                ForjaTopBar(
                    currentScreen = currentScreen,
                    streakCount = streakCount,
                    canNavigateBack = !isTopLevelScreen,
                    onNavigateBack = { viewModel.navigateBack() },
                    onOpenSettings = { viewModel.navigateTo(Screen.SETTINGS) },
                    onStreakClick = { viewModel.navigateTo(Screen.ACHIEVEMENTS) }
                )
            }
        },
        bottomBar = {
            if (!isFullScreenAuth && currentScreen != Screen.ACTIVE_WORKOUT) {
                ForjaBottomNav(
                    currentScreen = currentScreen,
                    onTabSelected = { targetScreen ->
                        viewModel.navigateTo(targetScreen)
                    },
                    activeWorkoutName = activeWorkout?.workoutName,
                    activeWorkoutElapsedSeconds = activeWorkout?.elapsedSeconds ?: 0,
                    onOpenActiveWorkout = { viewModel.navigateTo(Screen.ACTIVE_WORKOUT) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ForgeBlack)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.LANDING, Screen.AUTH -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.ONBOARDING -> OnboardingScreen(
                    viewModel = viewModel
                )

                Screen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.WORKOUTS -> WorkoutsScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.ACTIVE_WORKOUT -> ActiveWorkoutScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.EXERCISES -> ExerciseLibraryScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.EXERCISE_DETAIL -> ExerciseDetailScreen(
                    viewModel = viewModel
                )

                Screen.PROGRESS -> ProgressScreen(
                    viewModel = viewModel
                )

                Screen.HYDRATION -> HydrationScreen(
                    viewModel = viewModel
                )

                Screen.RECORDS -> RecordsScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.VOLUME -> VolumeScreen(
                    viewModel = viewModel
                )

                Screen.CARDIO -> CardioScreen(
                    viewModel = viewModel
                )

                Screen.CALENDAR -> CalendarScreen(
                    viewModel = viewModel
                )

                Screen.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.GOALS -> GoalsScreen(
                    viewModel = viewModel
                )

                Screen.ACHIEVEMENTS -> AchievementsScreen(
                    viewModel = viewModel
                )

                Screen.CALCULATORS -> CalculatorsScreen(
                    viewModel = viewModel
                )

                Screen.REPORTS -> ReportsScreen(
                    viewModel = viewModel
                )

                Screen.PROFILE -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )

                Screen.ADMIN -> AdminScreen()
            }
        }
    }
}
