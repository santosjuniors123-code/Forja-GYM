package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
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
import com.example.ui.components.ForjaCard
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBlue
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun CalendarScreen(
    viewModel: ForjaViewModel
) {
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()
    val weightRecords by viewModel.weightRecords.collectAsState()
    val cardioRecords by viewModel.cardioRecords.collectAsState()

    var selectedDaySummary by remember { mutableStateOf<String?>(null) }
    var selectedDayData by remember { mutableStateOf<List<String>>(emptyList()) }

    val trainedDates = completedWorkouts.map { it.dateString }.toSet()

    if (selectedDaySummary != null) {
        AlertDialog(
            onDismissRequest = { selectedDaySummary = null },
            containerColor = ForgeCard,
            title = {
                Text(
                    text = "Resumo do Dia $selectedDaySummary",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column {
                    if (selectedDayData.isEmpty()) {
                        Text("Nenhuma atividade registrada nesta data (Dia de descanso/recuperação).", color = TextSecondaryDark, fontSize = 13.sp)
                    } else {
                        selectedDayData.forEach { item ->
                            Text("• $item", color = TextPrimaryDark, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDaySummary = null }) {
                    Text("OK", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("calendar_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CALENDÁRIO DE ATIVIDADES",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Consistência e histórico diário de treinos, peso e cardio",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Card
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SETEMBRO 2026", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimaryDark)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ForgeOrange))
                        Text(" Treinado", fontSize = 11.sp, color = TextSecondaryDark, modifier = Modifier.padding(start = 4.dp, end = 8.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ForgeBorder))
                        Text(" Descanso", fontSize = 11.sp, color = TextSecondaryDark, modifier = Modifier.padding(start = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days of week header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark,
                            modifier = Modifier.width(36.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 30 days grid (representing current month)
                val totalDays = 30
                val rows = (totalDays + 6) / 7

                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (c in 0 until 7) {
                            val dayNum = r * 7 + c + 1
                            if (dayNum <= totalDays) {
                                val dayStr = String.format("%02d", dayNum)
                                val dateStr = "2026-09-$dayStr"
                                val isTrained = trainedDates.contains(dateStr)

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isTrained) ForgeOrange else ForgeCard)
                                        .border(1.dp, if (isTrained) ForgeOrange else ForgeBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            val activities = mutableListOf<String>()
                                            val wForDay = completedWorkouts.filter { it.dateString == dateStr }
                                            wForDay.forEach { activities.add("Treino: ${it.workoutName} (${it.totalSets} séries, ${it.totalVolumeKg.toInt()}kg)") }
                                            val pForDay = weightRecords.filter { it.dateString == dateStr }
                                            pForDay.forEach { activities.add("Pesagem: ${it.weightKg} kg") }
                                            val cForDay = cardioRecords.filter { it.dateString == dateStr }
                                            cForDay.forEach { activities.add("Cardio: ${it.activityType} ${it.durationMinutes}min (${it.estimatedCalories} kcal)") }

                                            selectedDaySummary = "$dayStr/09/2026"
                                            selectedDayData = activities
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        fontWeight = if (isTrained) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (isTrained) Color.White else TextPrimaryDark
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Dica: Toque em qualquer data para abrir o resumo das atividades realizadas.", fontSize = 12.sp, color = TextSecondaryDark)
        }
    }
}
