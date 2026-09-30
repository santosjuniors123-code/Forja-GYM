package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.components.SectionHeader
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
fun CardioScreen(
    viewModel: ForjaViewModel
) {
    val cardioRecords by viewModel.cardioRecords.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalMinutes = cardioRecords.sumOf { it.durationMinutes }
    val totalCalories = cardioRecords.sumOf { it.estimatedCalories }
    val totalDistance = cardioRecords.sumOf { it.distanceKm.toDouble() }.toFloat()

    if (showAddDialog) {
        var selectedType by remember { mutableStateOf("Corrida") }
        var durationInput by remember { mutableStateOf("30") }
        var distanceInput by remember { mutableStateOf("4.5") }
        var caloriesInput by remember { mutableStateOf("300") }
        var notesInput by remember { mutableStateOf("Pós-treino de musculação") }

        val types = listOf("Corrida", "Caminhada", "Bicicleta", "Elíptico", "Escada", "HIIT", "Outro")

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = ForgeCard,
            title = { Text("Registrar Treino de Cardio", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    Text("Modalidade:", fontSize = 12.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        types.take(4).forEach { t ->
                            val isSel = selectedType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ForgeOrange else ForgeCardElevated)
                                    .clickable { selectedType = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextPrimaryDark)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        types.drop(4).forEach { t ->
                            val isSel = selectedType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ForgeOrange else ForgeCardElevated)
                                    .clickable { selectedType = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(t, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextPrimaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = durationInput,
                        onValueChange = { durationInput = it },
                        label = { Text("Duração (minutos)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = cardioTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = distanceInput,
                        onValueChange = { distanceInput = it },
                        label = { Text("Distância (km) - opcional") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = cardioTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = caloriesInput,
                        onValueChange = { caloriesInput = it },
                        label = { Text("Calorias estimadas (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = cardioTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Observação") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = cardioTextFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dur = durationInput.toIntOrNull() ?: 30
                        val dist = distanceInput.toFloatOrNull() ?: 0f
                        val cal = caloriesInput.toIntOrNull() ?: 250
                        viewModel.addCardio(selectedType, dur, dist, cal, notesInput)
                        showAddDialog = false
                    },
                    modifier = Modifier.testTag("save_cardio_btn")
                ) {
                    Text("Salvar", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("cardio_screen")
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
                        text = "MÓDULO DE CARDIO",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Corrida, esteira, bike, escada e condicionamento metabólico",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                TextButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Totals
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeGreen.copy(alpha = 0.5f)
            ) {
                Text("RESUMO CARDIOVASCULAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeGreen, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TEMPO TOTAL", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("$totalMinutes min", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DISTÂNCIA", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.1f", totalDistance)} km", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("CALORIAS GASTAS", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                        Text("$totalCalories kcal", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ForgeOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeader(title = "HISTÓRICO DE SESSÕES")
        }

        if (cardioRecords.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum cardio registrado",
                    description = "Adicione sessões de corrida, esteira ou bicicleta para monitorar calorias e condicionamento.",
                    actionText = "Registrar Cardio",
                    onAction = { showAddDialog = true }
                )
            }
        } else {
            items(cardioRecords) { record ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF132219)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = ForgeGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(record.activityType, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                Text("${record.dateString} • ${record.durationMinutes} min • ${record.distanceKm} km", fontSize = 12.sp, color = TextSecondaryDark)
                            }
                        }

                        Text("${record.estimatedCalories} kcal", fontWeight = FontWeight.Black, fontSize = 15.sp, color = ForgeOrange)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun cardioTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    cursorColor = ForgeOrange
)
