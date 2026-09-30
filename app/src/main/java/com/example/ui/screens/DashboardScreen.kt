package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.utils.FormatUtils
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.SimpleLineChart
import com.example.ui.components.StatCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBlue
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeYellow
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel
import kotlin.math.abs

@Composable
fun DashboardScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val plans by viewModel.workoutPlans.collectAsState()
    val weightRecords by viewModel.weightRecords.collectAsState()
    val todayHydration by viewModel.todayHydration.collectAsState()
    val recentWorkouts by viewModel.completedWorkouts.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val activeWorkout by viewModel.activeWorkout.collectAsState()
    val prAlert by viewModel.prAlert.collectAsState()

    val userName = profile?.name?.ifBlank { "Atleta" } ?: "Atleta"
    val currentWeight = profile?.currentWeightKg ?: 80f
    val targetWeight = profile?.targetWeightKg ?: 75f
    val initialWeight = profile?.initialWeightKg ?: currentWeight
    val diffToTarget = targetWeight - currentWeight
    val weightChange = currentWeight - initialWeight

    val todayWorkoutPlan = plans.firstOrNull() ?: plans.getOrNull(0)
    val nextWorkoutPlan = if (plans.size > 1) plans[1] else null

    val consumedWater = todayHydration?.consumedMl ?: 0
    val targetWater = todayHydration?.targetMl ?: (profile?.dailyWaterGoalMl ?: 3000)
    val hydrationProgress = (consumedWater.toFloat() / targetWater.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Greeting & Motivation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OLÁ, ${userName.uppercase()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Foco em ${profile?.mainGoal ?: "Hipertrofia"} • Dia de Forja",
                        fontSize = 13.sp,
                        color = ForgeOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Streak flame icon
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF26180E))
                        .border(1.dp, ForgeOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onNavigate(Screen.ACHIEVEMENTS) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = ForgeOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile?.currentStreak ?: 0}D",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = ForgeOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PR Banner Notification if active
            AnimatedVisibility(visible = prAlert != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF291A08))
                        .border(1.dp, ForgeOrange, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ForgeOrange)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = prAlert ?: "",
                            color = TextPrimaryDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Workout of the Day Hero Card
            if (todayWorkoutPlan != null) {
                ForjaCard(
                    backgroundColor = ForgeCardElevated,
                    borderColor = ForgeOrange.copy(alpha = 0.8f),
                    testTag = "workout_of_day_card"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ForgeOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = todayWorkoutPlan.code,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TREINO DO DIA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeOrange,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = todayWorkoutPlan.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Grupos: ${todayWorkoutPlan.targetMuscles} • Estimado: ${todayWorkoutPlan.estimatedMinutes} min",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ForjaPrimaryButton(
                        text = if (activeWorkout != null) "CONTINUAR TREINO EM ANDAMENTO" else "INICIAR TREINO",
                        icon = Icons.Default.PlayArrow,
                        onClick = {
                            if (activeWorkout != null) {
                                onNavigate(Screen.ACTIVE_WORKOUT)
                            } else {
                                viewModel.startWorkout(todayWorkoutPlan.id)
                            }
                        },
                        testTag = "start_daily_workout_btn"
                    )
                }
            }

            if (nextWorkoutPlan != null) {
                Spacer(modifier = Modifier.height(10.dp))
                ForjaCard(
                    backgroundColor = ForgeCard,
                    borderColor = ForgeBorder,
                    onClick = { onNavigate(Screen.WORKOUTS) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PRÓXIMO TREINO PROGRAMADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = nextWorkoutPlan.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ForgeOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Body Weight & Evolution Card
            SectionHeader(
                title = "PESO CORPORAL & EVOLUÇÃO",
                actionText = "Ver Detalhes",
                onActionClick = { onNavigate(Screen.PROGRESS) }
            )

            ForjaCard(
                backgroundColor = ForgeCard,
                borderColor = ForgeBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PESO ATUAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format("%.1f", currentWeight),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = " kg",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "META: ${String.format("%.1f", targetWeight)} kg",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (diffToTarget == 0f) "Meta alcançada! 🎉"
                            else if (diffToTarget > 0) "Faltam +${String.format("%.1f", diffToTarget)} kg"
                            else "Faltam ${String.format("%.1f", abs(diffToTarget))} kg para secar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (diffToTarget <= 0) ForgeGreen else ForgeYellow
                        )
                    }
                }

                if (weightRecords.size >= 2) {
                    Spacer(modifier = Modifier.height(10.dp))
                    val weightPoints = weightRecords.map { it.weightKg }
                    SimpleLineChart(points = weightPoints)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Hydration Card
            SectionHeader(
                title = "HIDRATAÇÃO DIÁRIA",
                actionText = "Ajustar",
                onActionClick = { onNavigate(Screen.HYDRATION) }
            )

            ForjaCard(
                backgroundColor = ForgeCard,
                borderColor = ForgeBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ForgeBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = ForgeBlue)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "$consumedWater / $targetWater ml",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${(hydrationProgress * 100).toInt()}% da meta diária",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForgeCardElevated)
                                .border(1.dp, ForgeBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.addWater(250) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("quick_water_250"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+250ml", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForgeCardElevated)
                                .border(1.dp, ForgeBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.addWater(500) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("quick_water_500"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+500ml", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { hydrationProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = ForgeBlue,
                    trackColor = ForgeBorder
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Hub grid: Calculadoras, PRs, Cardio, Metas
            SectionHeader(title = "HUB DA ACADEMIA")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Recordes (PR)",
                    value = "Ver Cargas",
                    subtitle = "Histórico de Força",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = ForgeOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.RECORDS) }
                )
                StatCard(
                    title = "Calculadoras",
                    value = "IMC / 1RM",
                    subtitle = "TMB & Calorias",
                    icon = Icons.Default.Calculate,
                    accentColor = ForgeYellow,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.CALCULATORS) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Cardio",
                    value = "Registrar",
                    subtitle = "Corrida, Bike, HIIT",
                    icon = Icons.Default.DirectionsRun,
                    accentColor = ForgeGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.CARDIO) }
                )
                StatCard(
                    title = "Volume",
                    value = "Séries x Kg",
                    subtitle = "Tonelagem Total",
                    icon = Icons.Default.FitnessCenter,
                    accentColor = ForgeBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.VOLUME) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Completed Workouts
            SectionHeader(
                title = "ÚLTIMAS ATIVIDADES",
                actionText = "Ver Histórico",
                onActionClick = { onNavigate(Screen.HISTORY) }
            )
        }

        if (recentWorkouts.isEmpty()) {
            item {
                ForjaCard(backgroundColor = ForgeCard) {
                    Text(
                        text = "Nenhum treino concluído ainda. Inicie seu primeiro treino para forjar sua história!",
                        color = TextSecondaryDark,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(recentWorkouts.take(3)) { workout ->
                ForjaCard(
                    backgroundColor = ForgeCard,
                    modifier = Modifier.padding(bottom = 8.dp),
                    onClick = { onNavigate(Screen.HISTORY) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = workout.workoutName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${workout.dateString} • Duração: ${FormatUtils.formatSecondsToDuration(workout.durationSeconds)}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${workout.totalSets} séries",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForgeOrange
                            )
                            Text(
                                text = "${workout.totalVolumeKg.toInt()} kg vol",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
