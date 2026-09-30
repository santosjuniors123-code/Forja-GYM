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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ForjaCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.SimpleLineChart
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBlue
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun ReportsScreen(
    viewModel: ForjaViewModel
) {
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()
    val weightRecords by viewModel.weightRecords.collectAsState()
    val personalRecords by viewModel.personalRecords.collectAsState()
    val cardioRecords by viewModel.cardioRecords.collectAsState()

    var selectedPeriod by remember { mutableStateOf("30 dias") }
    val periods = listOf("7 dias", "30 dias", "90 dias", "6 meses", "1 ano")

    val totalWorkouts = completedWorkouts.size
    val totalVolume = completedWorkouts.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
    val totalCardioMin = cardioRecords.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("reports_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RELATÓRIOS & ANALYTICS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Visão executiva da sua evolução de cargas, assiduidade e medidas",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Period Selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(periods) { p ->
                    val isSel = selectedPeriod == p
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedPeriod = p },
                        label = { Text(p, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForgeOrange,
                            selectedLabelColor = Color.White,
                            containerColor = ForgeCardElevated,
                            labelColor = TextSecondaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Executive Summary
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeOrange.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "RESUMO CONSOLIDADO • $selectedPeriod",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForgeOrange,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TREINOS", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("$totalWorkouts", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VOLUME MOVIMENTADO", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("${totalVolume.toInt()} kg", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("RECORDES (PR)", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("${personalRecords.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ForgeOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart 1: Weight Evolution
            SectionHeader(title = "PROGRESSÃO DE PESO")
            if (weightRecords.size >= 2) {
                SimpleLineChart(points = weightRecords.map { it.weightKg })
            } else {
                Text("Registros insuficientes para gráfico no período selecionado.", fontSize = 12.sp, color = TextSecondaryDark)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart 2: Workout Volume
            SectionHeader(title = "DISTRIBUIÇÃO DE VOLUME")
            if (completedWorkouts.isNotEmpty()) {
                val chartData = completedWorkouts.take(5).reversed().map {
                    it.dateString.takeLast(5) to it.totalVolumeKg
                }
                SimpleBarChart(data = chartData, barUnit = "kg")
            } else {
                Text("Nenhuma sessão concluída no período.", fontSize = 12.sp, color = TextSecondaryDark)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
