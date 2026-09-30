package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CompletedSetEntity
import com.example.data.utils.FormatUtils
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun WorkoutHistoryDetailScreen(
    viewModel: ForjaViewModel,
    onNavigateBack: () -> Unit
) {
    val workout by viewModel.selectedCompletedWorkout.collectAsState()
    val completedSets by viewModel.selectedCompletedWorkoutSets.collectAsState()

    if (workout == null) {
        EmptyState(
            title = "Nenhum treino selecionado",
            description = "Selecione uma sessão na tela de Histórico para ver os detalhes.",
            actionText = "Voltar ao Histórico",
            onAction = onNavigateBack
        )
        return
    }

    val currentWorkout = workout!!
    val groupedSets = remember(completedSets) {
        completedSets.groupBy { it.exerciseName }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("workout_history_detail_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = ForgeOrange
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DETALHES DO TREINO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = currentWorkout.dateString,
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Workout Overview Card
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeOrange.copy(alpha = 0.7f),
                testTag = "history_workout_overview_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = currentWorkout.workoutName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimaryDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = ForgeOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Realizado em ${currentWorkout.dateString}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF132819))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CONCLUÍDO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF141419))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DURAÇÃO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                        Text(
                            text = FormatUtils.formatSecondsToFullTimer(currentWorkout.durationSeconds),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SÉRIES / REPS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                        Text(
                            text = "${currentWorkout.totalSets} séries • ${currentWorkout.totalReps} reps",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("CARGA TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                        Text(
                            text = "${currentWorkout.totalVolumeKg.toInt()} kg",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = ForgeOrange
                        )
                    }
                }

                if (currentWorkout.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Observações: ${currentWorkout.notes}",
                        fontSize = 12.sp,
                        color = Color(0xFFC7A280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "EXERCÍCIOS REALIZADOS (${groupedSets.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (groupedSets.isEmpty()) {
            item {
                Text(
                    text = "Nenhum detalhe de série registrado para esta sessão.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(groupedSets.entries.toList()) { entry ->
                val exerciseName = entry.key
                val sets = entry.value

                ForjaCard(
                    backgroundColor = ForgeCard,
                    borderColor = ForgeBorder,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF26180E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = ForgeOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = exerciseName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        }

                        Text(
                            text = "${sets.size} séries",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ForgeOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sets Breakdown
                    sets.forEach { s ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF141419))
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .padding(bottom = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Série ${s.setNumber}:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (s.exerciseType == "Cardio") {
                                    Text(
                                        text = "${s.durationSeconds / 60}m • ${s.distanceKm}km",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                } else if (s.exerciseType == "Isometria" || s.exerciseType == "Por Tempo") {
                                    Text(
                                        text = "${s.durationSeconds}s sustentação",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                } else {
                                    Text(
                                        text = "${s.reps} reps — ${s.weightKg} kg",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (s.isPR) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF331E06))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = ForgeOrange,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "RECORDE!",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = ForgeOrange
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Concluída",
                                    tint = ForgeGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (s.notes.isNotBlank()) {
                            Text(
                                text = "Nota: ${s.notes}",
                                fontSize = 11.sp,
                                color = Color(0xFFC7A280),
                                modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
