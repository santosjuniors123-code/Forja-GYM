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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.entities.WorkoutPlanEntity
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.ForjaSecondaryButton
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeRed
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

    val exerciseMap = remember(allExercises) { allExercises.associateBy { it.id } }
    var expandedPlanId by remember(plans) { mutableStateOf(plans.firstOrNull()?.id) }

    var planToDelete by remember { mutableStateOf<WorkoutPlanEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("workouts_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
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
                        text = "Crie, edite e personalize seus planos",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }

                // Library shortcut button
                ForjaOutlinedButton(
                    text = "EXERCÍCIOS",
                    icon = Icons.Default.Book,
                    onClick = { onNavigate(Screen.EXERCISES) },
                    modifier = Modifier.width(130.dp),
                    testTag = "open_library_button"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Actions: "+ CRIAR TREINO" & "📅 MINHA SEMANA"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.openCreateWorkout() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("create_workout_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ CRIAR TREINO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { onNavigate(Screen.WEEK_SCHEDULE) },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeCardElevated),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .height(44.dp)
                        .testTag("open_week_schedule_btn")
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("MINHA SEMANA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimaryDark)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        if (plans.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum treino cadastrado",
                    description = "Crie seus próprios treinos personalizados (A, B, C, D, E ou com o nome que desejar).",
                    actionText = "+ Criar Primeiro Treino",
                    onAction = { viewModel.openCreateWorkout() }
                )
            }
        } else {
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
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedPlanId = if (isExpanded) null else plan.id
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ForgeOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = plan.code,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
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
                                    text = "${plan.estimatedMinutes} min • ${plan.targetMuscles}",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        // Actions: Edit & Expand
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.openEditWorkout(plan) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Treino",
                                    tint = ForgeOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expandir",
                                tint = TextSecondaryDark
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            if (plan.description.isNotBlank()) {
                                Text(
                                    text = plan.description,
                                    fontSize = 13.sp,
                                    color = TextSecondaryDark,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Exercise List Header
                            Text(
                                text = "EXERCÍCIOS INCLUSOS (${planExercises.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForgeOrange,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (planExercises.isEmpty()) {
                                Text(
                                    text = "Nenhum exercício configurado ainda. Clique em 'Editar Treino' para adicionar.",
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            } else {
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
                                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
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
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons: Edit, Duplicate, Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.openEditWorkout(plan) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26180E)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Editar", color = ForgeOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.duplicateWorkout(plan.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E26)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Duplicar", color = TextPrimaryDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { planToDelete = plan },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1414)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = ForgeRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Excluir", color = ForgeRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Start workout button
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
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Confirmation dialog before deleting a workout
    if (planToDelete != null) {
        val plan = planToDelete!!
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            containerColor = ForgeCardElevated,
            title = {
                Text("Excluir Treino?", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Tem certeza que deseja excluir o \"${plan.name}\"?\n(O histórico de treinos concluídos anteriormente será preservado).",
                    color = TextSecondaryDark,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWorkout(plan.id)
                        planToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Excluir", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToDelete = null }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }
}
