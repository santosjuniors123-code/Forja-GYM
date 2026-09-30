package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExerciseEntity
import com.example.data.local.entities.WorkoutPlanEntity
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun WorkoutsScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val plans by viewModel.workoutPlans.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()
    val activeWorkout by viewModel.activeWorkout.collectAsState()

    val exerciseMap = remember(allExercises) { allExercises.associateBy { it.id } }

    var expandedPlanId by remember { mutableStateOf<String?>(plans.firstOrNull()?.id) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("workouts_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MEUS TREINOS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Planos periodizados da divisão A ao E",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }

                // Exercise Library Button
                ForjaOutlinedButton(
                    text = "BIBLIOTECA",
                    icon = Icons.Default.Book,
                    onClick = { onNavigate(Screen.EXERCISES) },
                    modifier = Modifier.width(150.dp),
                    testTag = "open_library_button"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(plans) { plan ->
            val isExpanded = expandedPlanId == plan.id
            val exerciseIds = plan.exerciseIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val planExercises = exerciseIds.mapNotNull { exerciseMap[it] }

            ForjaCard(
                backgroundColor = if (isExpanded) ForgeCardElevated else ForgeCard,
                borderColor = if (isExpanded) ForgeOrange.copy(alpha = 0.7f) else ForgeBorder,
                modifier = Modifier.padding(bottom = 12.dp),
                testTag = "workout_card_${plan.code}"
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedPlanId = if (isExpanded) null else plan.id
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForgeOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = plan.code,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = plan.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${plan.estimatedMinutes} min • ${planExercises.size} exercícios",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir",
                        tint = ForgeOrange
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        Text(
                            text = plan.description,
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "EXERCÍCIOS INCLUSOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        planExercises.forEachIndexed { index, exercise ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF141419))
                                    .clickable { viewModel.selectExercise(exercise) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${index + 1}.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = exercise.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimaryDark
                                        )
                                        Text(
                                            text = "${exercise.muscleGroup} • ${exercise.equipment}",
                                            fontSize = 11.sp,
                                            color = TextSecondaryDark
                                        )
                                    }
                                }

                                Text(
                                    text = "${exercise.defaultSets}x${exercise.defaultReps}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeOrange
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ForjaPrimaryButton(
                            text = "INICIAR ESTE TREINO",
                            icon = Icons.Default.PlayArrow,
                            onClick = {
                                viewModel.startWorkout(plan.id)
                            },
                            testTag = "start_workout_${plan.code}"
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
