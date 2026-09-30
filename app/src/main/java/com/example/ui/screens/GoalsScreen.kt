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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
fun GoalsScreen(
    viewModel: ForjaViewModel
) {
    val goals by viewModel.goals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("TREINOS") }
        var targetValue by remember { mutableStateOf("4") }
        var currentValue by remember { mutableStateOf("0") }
        var unit by remember { mutableStateOf("dias/sem") }
        var deadline by remember { mutableStateOf("Semanal") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = ForgeCard,
            title = { Text("Criar Nova Meta", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título da Meta (ex: Treinar 4x por semana)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = goalTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = targetValue,
                        onValueChange = { targetValue = it },
                        label = { Text("Valor Alvo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        colors = goalTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidade (ex: kg, reps, ml, dias/sem)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = goalTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Prazo / Periodicidade") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = goalTextFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val t = targetValue.toFloatOrNull() ?: 1f
                        val c = currentValue.toFloatOrNull() ?: 0f
                        if (title.isNotBlank()) {
                            viewModel.addGoal(title, type, t, c, unit, deadline)
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_goal_btn")
                ) {
                    Text("Salvar Meta", color = ForgeOrange, fontWeight = FontWeight.Bold)
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
            .testTag("goals_screen")
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
                        text = "METAS & OBJETIVOS",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Diretrizes para manter o ritmo e disciplina",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }

                TextButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Criar Meta", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (goals.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhuma meta definida",
                    description = "Crie metas de frequência, carga ou hidratação para manter seu foco constante.",
                    actionText = "Criar Minha Primeira Meta",
                    onAction = { showAddDialog = true }
                )
            }
        } else {
            items(goals) { goal ->
                val progress = (goal.currentValue / goal.targetValue).coerceIn(0f, 1f)
                val isDone = goal.isCompleted || progress >= 1f

                ForjaCard(
                    backgroundColor = if (isDone) Color(0xFF14241B) else ForgeCard,
                    borderColor = if (isDone) ForgeGreen else ForgeBorder,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) ForgeGreen else ForgeOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = if (isDone) Color.White else ForgeOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = goal.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                                Text(text = "Prazo: ${goal.deadline}", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                        }

                        IconButton(onClick = { viewModel.deleteGoal(goal.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progresso: ${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDone) ForgeGreen else ForgeOrange
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimaryDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (isDone) ForgeGreen else ForgeOrange,
                        trackColor = ForgeBorder
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick increment buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ForgeCardElevated)
                                .clickable {
                                    viewModel.updateGoalProgress(goal, goal.currentValue + 1f)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+1 Progresso", fontSize = 11.sp, color = ForgeOrange, fontWeight = FontWeight.Bold)
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

@Composable
private fun goalTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    cursorColor = ForgeOrange
)
