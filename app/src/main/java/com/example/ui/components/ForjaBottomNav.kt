package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.utils.FormatUtils
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeDark
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun ForjaBottomNav(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    activeWorkoutName: String? = null,
    activeWorkoutElapsedSeconds: Int = 0,
    onOpenActiveWorkout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ForgeDark)
            .navigationBarsPadding()
            .testTag("forja_bottom_nav")
    ) {
        // Floating Active Workout Strip if active and not currently on that screen
        AnimatedVisibility(visible = activeWorkoutName != null && currentScreen != Screen.ACTIVE_WORKOUT) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF26180E))
                    .clickable { onOpenActiveWorkout() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("active_workout_mini_banner"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ForgeOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(
                            text = "EM ANDAMENTO: ${activeWorkoutName ?: ""}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ForgeOrange
                        )
                        Text(
                            text = "Tempo: ${FormatUtils.formatSecondsToTime(activeWorkoutElapsedSeconds)} - Toque para continuar",
                            fontSize = 11.sp,
                            color = TextPrimaryDark
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = ForgeBorder, thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Início",
                selected = currentScreen == Screen.DASHBOARD,
                onClick = { onTabSelected(Screen.DASHBOARD) },
                testTag = "nav_home"
            )
            NavItem(
                icon = Icons.Default.FitnessCenter,
                label = "Treinos",
                selected = currentScreen == Screen.WORKOUTS || currentScreen == Screen.EXERCISES || currentScreen == Screen.EXERCISE_DETAIL,
                onClick = { onTabSelected(Screen.WORKOUTS) },
                testTag = "nav_workouts"
            )
            NavItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Progresso",
                selected = currentScreen == Screen.PROGRESS || currentScreen == Screen.RECORDS || currentScreen == Screen.VOLUME || currentScreen == Screen.HYDRATION,
                onClick = { onTabSelected(Screen.PROGRESS) },
                testTag = "nav_progress"
            )
            NavItem(
                icon = Icons.Default.History,
                label = "Histórico",
                selected = currentScreen == Screen.HISTORY || currentScreen == Screen.CALENDAR,
                onClick = { onTabSelected(Screen.HISTORY) },
                testTag = "nav_history"
            )
            NavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                selected = currentScreen == Screen.PROFILE || currentScreen == Screen.SETTINGS || currentScreen == Screen.ACHIEVEMENTS || currentScreen == Screen.GOALS,
                onClick = { onTabSelected(Screen.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) ForgeOrange else TextSecondaryDark,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) ForgeOrange else TextSecondaryDark
        )
    }
}
