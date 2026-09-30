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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
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
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.SimpleBarChart
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun VolumeScreen(
    viewModel: ForjaViewModel
) {
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()

    val totalVolumeAllTime = completedWorkouts.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
    val totalSetsAllTime = completedWorkouts.sumOf { it.totalSets }
    val totalRepsAllTime = completedWorkouts.sumOf { it.totalReps }

    val recentWorkoutsChartData = completedWorkouts.take(6).reversed().map {
        val shortName = it.workoutName.replace("Treino ", "").take(8)
        shortName to it.totalVolumeKg
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("volume_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VOLUME DE TREINO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Fórmula biomecânica: Volume Total = Séries × Repetições × Carga",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metric Summary Card
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeOrange.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "TONELAGEM TOTAL FORJADA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForgeOrange,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%,.0f", totalVolumeAllTime),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = " kg movimentados",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SÉRIES TOTAIS", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("$totalSetsAllTime", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("REPETIÇÕES", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("$totalRepsAllTime", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("MÉDIA / TREINO", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        val avg = if (completedWorkouts.isNotEmpty()) (totalVolumeAllTime / completedWorkouts.size).toInt() else 0
                        Text("$avg kg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (recentWorkoutsChartData.isNotEmpty()) {
                SectionHeader(title = "EVOLUÇÃO DO VOLUME POR TREINO")
                SimpleBarChart(data = recentWorkoutsChartData, barUnit = "kg")
                Spacer(modifier = Modifier.height(20.dp))
            }

            SectionHeader(title = "VOLUME POR SESSÃO")
        }

        if (completedWorkouts.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhuma sessão registrada",
                    description = "Conclua treinos para calcular seu volume total e evolução semanal."
                )
            }
        } else {
            items(completedWorkouts) { workout ->
                ForjaCard(
                    backgroundColor = ForgeCard,
                    borderColor = ForgeBorder,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = workout.workoutName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                            Text(text = "${workout.dateString} • ${workout.totalSets} séries • ${workout.totalReps} reps", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${workout.totalVolumeKg.toInt()} kg",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ForgeOrange
                            )
                            Text(text = "volume da sessão", fontSize = 10.sp, color = TextSecondaryDark)
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
