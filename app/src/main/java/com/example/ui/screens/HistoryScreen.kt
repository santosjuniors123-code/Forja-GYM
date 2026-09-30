package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.data.utils.FormatUtils
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.navigation.Screen
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
fun HistoryScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filters = listOf("Todos", "Últimos 7 dias", "Últimos 30 dias")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("history_screen")
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
                        text = "HISTÓRICO DE TREINOS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${completedWorkouts.size} sessões completadas",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                TextButton(onClick = { onNavigate(Screen.CALENDAR) }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Calendário", color = ForgeOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { f ->
                    val isSel = selectedFilter == f
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedFilter = f },
                        label = { Text(f, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForgeOrange,
                            selectedLabelColor = Color.White,
                            containerColor = ForgeCard,
                            labelColor = TextSecondaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (completedWorkouts.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum treino no histórico",
                    description = "Quando você concluir suas sessões de treino, elas serão gravadas em detalhe aqui.",
                    actionText = "Iniciar Treino",
                    onAction = { onNavigate(Screen.WORKOUTS) }
                )
            }
        } else {
            items(completedWorkouts) { workout ->
                ForjaCard(
                    backgroundColor = ForgeCardElevated,
                    borderColor = ForgeBorder,
                    modifier = Modifier.padding(bottom = 10.dp),
                    testTag = "history_item_${workout.id}"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF26180E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = workout.workoutName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = workout.dateString,
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF132219))
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF141419))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("DURAÇÃO", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                            Text(FormatUtils.formatSecondsToDuration(workout.durationSeconds), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SÉRIES / REPS", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                            Text("${workout.totalSets} séries • ${workout.totalReps} reps", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("VOLUME TOTAL", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                            Text("${workout.totalVolumeKg.toInt()} kg", fontSize = 13.sp, fontWeight = FontWeight.Black, color = ForgeOrange)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
