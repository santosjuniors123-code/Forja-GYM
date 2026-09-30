package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.example.data.local.entities.ExerciseEntity
import com.example.data.model.ActiveSet
import com.example.data.utils.FormatUtils
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.ForjaSecondaryButton
import com.example.ui.components.RestTimerSection
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun ActiveWorkoutScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val activeSession by viewModel.activeWorkout.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()
    val prAlert by viewModel.prAlert.collectAsState()
    val restSeconds by viewModel.restSecondsRemaining.collectAsState()
    val isRestRunning by viewModel.isRestTimerRunning.collectAsState()
    val lastCompletedSummary by viewModel.lastCompletedWorkoutSummary.collectAsState()

    var showFinishDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSwapExerciseDialog by remember { mutableStateOf(false) }
    var showInstructionsDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showRemoveCurrentExerciseDialog by remember { mutableStateOf(false) }

    // Intercept back button to confirm cancellation / partial saving
    BackHandler {
        if (activeSession != null) {
            showCancelDialog = true
        } else {
            viewModel.navigateBack()
        }
    }

    // CELEBRATION MODAL: 🔥 TREINO CONCLUÍDO! (Requirement 14)
    if (lastCompletedSummary != null) {
        val completed = lastCompletedSummary!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissCelebrationModal() },
            containerColor = ForgeCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = ForgeOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TREINO CONCLUÍDO!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = ForgeOrange
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Parabéns, atleta! Mais um treino forjado com sucesso.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF141419))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Data:", color = TextSecondaryDark, fontSize = 12.sp)
                                Text(completed.dateString, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Treino:", color = TextSecondaryDark, fontSize = 12.sp)
                                Text(completed.workoutName, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Duração:", color = TextSecondaryDark, fontSize = 12.sp)
                                Text(
                                    FormatUtils.formatSecondsToFullTimer(completed.durationSeconds),
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark,
                                    fontSize = 12.sp
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Séries Concluídas:", color = TextSecondaryDark, fontSize = 12.sp)
                                Text("${completed.totalSets}", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Carga Total:", color = TextSecondaryDark, fontSize = 12.sp)
                                Text("${completed.totalVolumeKg.toInt()} kg", fontWeight = FontWeight.Black, color = ForgeOrange, fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissCelebrationModal() },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("celebration_view_history_btn")
                ) {
                    Text("VER HISTÓRICO", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (activeSession == null) {
        if (lastCompletedSummary == null) {
            EmptyState(
                title = "Nenhum treino em andamento",
                description = "Selecione um plano de treino para iniciar sua sessão.",
                actionText = "Ver Treinos",
                onAction = { onNavigate(Screen.WORKOUTS) }
            )
        }
        return
    }

    val session = activeSession!!
    val currentExerciseIndex = session.currentExerciseIndex
    val currentExercise = session.exercises.getOrNull(currentExerciseIndex)

    // Finish Dialog (Requirement 21)
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            containerColor = ForgeCard,
            title = {
                Text(
                    text = "Finalizar Treino?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    fontSize = 18.sp
                )
            },
            text = {
                val totalCompletedSets = session.exercises.sumOf { ex -> ex.sets.count { it.isCompleted } }
                val totalVolume = session.exercises.sumOf { ex ->
                    ex.sets.filter { it.isCompleted }.sumOf { (it.weightKg * it.reps).toDouble() }
                }
                Column {
                    Text(
                        text = "Deseja concluir a sessão agora e salvar os resultados?",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tempo total: ${FormatUtils.formatSecondsToFullTimer(session.elapsedSeconds)}\n" +
                                "Séries completadas: $totalCompletedSets\n" +
                                "Volume estimado: ${totalVolume.toInt()} kg",
                        fontSize = 13.sp,
                        color = ForgeOrange,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeGreen),
                    modifier = Modifier.testTag("confirm_finish_workout_btn")
                ) {
                    Text("Salvar e Concluir", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Continuar Treinando", color = TextSecondaryDark)
                }
            }
        )
    }

    // Cancel Dialog: "Deseja salvar o que foi realizado até agora? [Salvar] [Descartar]" (Requirement 21)
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = ForgeCard,
            title = { Text("Cancelar Treino?", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 18.sp) },
            text = {
                Text(
                    "Deseja salvar o que foi realizado até agora ou descartar esta sessão?",
                    color = TextSecondaryDark,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelWorkout(savePartial = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Salvar Realizado", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showCancelDialog = false
                            viewModel.cancelWorkout(savePartial = false)
                        }
                    ) {
                        Text("Descartar", color = ForgeRed, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Continuar", color = TextSecondaryDark)
                    }
                }
            }
        )
    }

    // Swap Exercise Dialog
    if (showSwapExerciseDialog && currentExercise != null) {
        val alternatives = allExercises.filter { currentExercise.alternativeIds.contains(it.id) }
        AlertDialog(
            onDismissRequest = { showSwapExerciseDialog = false },
            containerColor = ForgeCard,
            title = {
                Text(
                    text = "Substituir Exercício",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Escolha um substituto para ${currentExercise.name}:",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (alternatives.isEmpty()) {
                        Text(
                            text = "Nenhuma alternativa pré-definida disponível para este exercício.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    } else {
                        alternatives.forEach { alt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ForgeCardElevated)
                                    .border(1.dp, ForgeBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.changeExercise(currentExerciseIndex, alt.id)
                                        showSwapExerciseDialog = false
                                    }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = alt.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                                    Text(text = "${alt.muscleGroup} • ${alt.equipment}", fontSize = 11.sp, color = TextSecondaryDark)
                                }
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = ForgeOrange)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSwapExerciseDialog = false }) {
                    Text("Fechar", color = TextSecondaryDark)
                }
            }
        )
    }

    // In-Workout Add Exercise Dialog (Requirement 22)
    if (showAddExerciseDialog) {
        var query by remember { mutableStateOf("") }
        val filtered = remember(query, allExercises) {
            if (query.isBlank()) allExercises.take(15)
            else allExercises.filter { it.name.contains(query, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            containerColor = ForgeCardElevated,
            title = {
                Text("+ ADICIONAR AO TREINO", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextPrimaryDark)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Pesquisar exercício...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForgeOrange) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeOrange,
                            unfocusedBorderColor = ForgeBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsIndexed(filtered) { _, ex ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF16161D))
                                    .clickable {
                                        viewModel.addExerciseToActiveWorkout(ex)
                                        showAddExerciseDialog = false
                                    }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ex.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                                    Text("${ex.muscleGroup} • ${ex.equipment}", fontSize = 11.sp, color = TextSecondaryDark)
                                }
                                Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOrange)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }

    // Instructions Dialog
    if (showInstructionsDialog && currentExercise != null) {
        AlertDialog(
            onDismissRequest = { showInstructionsDialog = false },
            containerColor = ForgeCard,
            title = {
                Text(text = currentExercise.name, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text(text = "EXECUÇÃO CORRETA:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                    Text(text = currentExercise.instructions.ifBlank { "Execute o movimento com controle e amplitude total." }, fontSize = 13.sp, color = TextPrimaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "DICAS PRO:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeGreen)
                    Text(text = currentExercise.tips.ifBlank { "Mantenha a contração muscular no pico do movimento." }, fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ERROS COMUNS A EVITAR:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeRed)
                    Text(text = currentExercise.commonMistakes.ifBlank { "Evite usar impulso excessivo." }, fontSize = 13.sp, color = TextSecondaryDark)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInstructionsDialog = false }) {
                    Text("Entendido", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Confirmation for removing exercise in active workout
    if (showRemoveCurrentExerciseDialog && currentExercise != null) {
        AlertDialog(
            onDismissRequest = { showRemoveCurrentExerciseDialog = false },
            containerColor = ForgeCard,
            title = { Text("Remover Exercício?", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = { Text("Deseja remover \"${currentExercise.name}\" desta sessão de treino?", color = TextSecondaryDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeExerciseFromActiveWorkout(currentExerciseIndex)
                        showRemoveCurrentExerciseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeRed)
                ) {
                    Text("Remover", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveCurrentExerciseDialog = false }) {
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
            .testTag("active_workout_screen")
    ) {
        // Sticky Header: Workout general timer (Requirement 20) + pause + cancel + finish
        item {
            Spacer(modifier = Modifier.height(10.dp))

            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeOrange.copy(alpha = 0.5f),
                testTag = "active_workout_header_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TEMPO DE TREINO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = ForgeOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = FormatUtils.formatSecondsToFullTimer(session.elapsedSeconds),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark,
                                modifier = Modifier.testTag("workout_elapsed_time")
                            )
                            if (session.isPaused) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(PAUSADO)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeRed
                                )
                            }
                        }
                    }

                    // Action buttons: Pause, Cancel, Finish
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.toggleWorkoutPause() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ForgeBorder)
                                .testTag("pause_workout_btn")
                        ) {
                            Icon(
                                imageVector = if (session.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (session.isPaused) "Continuar" else "Pausar",
                                tint = TextPrimaryDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2B1414))
                                .testTag("cancel_workout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar",
                                tint = ForgeRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showFinishDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForgeGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("finish_workout_top_btn")
                        ) {
                            Text("FINALIZAR", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PR Alert if triggered
            AnimatedVisibility(visible = prAlert != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF331E06))
                        .border(1.dp, ForgeOrange, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ForgeOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = prAlert ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Exercise Tabs Carousel + Add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScrollableTabRow(
                    selectedTabIndex = currentExerciseIndex,
                    containerColor = ForgeCard,
                    contentColor = ForgeOrange,
                    edgePadding = 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    session.exercises.forEachIndexed { index, ex ->
                        val allCompleted = ex.sets.isNotEmpty() && ex.sets.all { it.isCompleted }
                        Tab(
                            selected = currentExerciseIndex == index,
                            onClick = { viewModel.setCurrentExerciseIndex(index) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (allCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Concluído",
                                            tint = ForgeGreen,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = "${index + 1}. ${ex.name.take(12)}",
                                        fontWeight = if (currentExerciseIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // In-workout Add Exercise Button (Requirement 22)
                IconButton(
                    onClick = { showAddExerciseDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF26180E))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar exercício", tint = ForgeOrange, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Exercise Details Box
            if (currentExercise != null) {
                ForjaCard(
                    backgroundColor = ForgeCard,
                    borderColor = ForgeBorder,
                    testTag = "current_exercise_details_card"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EXERCÍCIO ${currentExerciseIndex + 1} DE ${session.exercises.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForgeOrange,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentExercise.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${currentExercise.muscleGroup} • Tipo: ${currentExercise.exerciseType}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showInstructionsDialog = true }) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "Instruções", tint = ForgeOrange)
                            }
                            IconButton(onClick = { showSwapExerciseDialog = true }) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Substituir", tint = TextSecondaryDark)
                            }
                            if (session.exercises.size > 1) {
                                IconButton(onClick = { showRemoveCurrentExerciseDialog = true }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover", tint = ForgeRed)
                                }
                            }
                        }
                    }

                    // PREVIOUS PERFORMANCE (Requirement 16 & 17)
                    if (currentExercise.previousPerformance != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF191924))
                                .border(1.dp, Color(0xFF333348), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "📊 HISTÓRICO ANTERIOR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF818CF8),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = currentExercise.previousPerformance,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "💡 Progressão de carga: deseja manter ou registrar uma nova carga para superar sua marca?",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }

                    // OBSERVATIONS FIELD (Requirement 23)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = currentExercise.notes,
                        onValueChange = { newNotes ->
                            viewModel.updateActiveExerciseNotes(currentExerciseIndex, newNotes)
                        },
                        label = { Text("Observações deste exercício (ex: sentiu desconforto, pegada fechada...)") },
                        placeholder = { Text("Registrar anotação para o histórico...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeOrange,
                            unfocusedBorderColor = ForgeBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedLabelColor = ForgeOrange,
                            unfocusedLabelColor = TextSecondaryDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rest Timer Section (Requirement 19)
            RestTimerSection(
                secondsRemaining = restSeconds,
                isRunning = isRestRunning,
                onStartTimer = { s -> viewModel.startRestTimer(s) },
                onStopTimer = { viewModel.stopRestTimer() },
                onAdjustTimer = { delta -> viewModel.adjustRestTimer(delta) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SÉRIES & CARGAS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Sets List for current exercise (Requirement 18)
        if (currentExercise != null) {
            itemsIndexed(currentExercise.sets) { setIndex, set ->
                SetRowItem(
                    set = set,
                    setIndex = setIndex,
                    totalSets = currentExercise.sets.size,
                    exerciseType = currentExercise.exerciseType,
                    onComplete = { w, r, notes ->
                        viewModel.completeSet(currentExerciseIndex, setIndex, w, r, notes)
                    },
                    onDelete = {
                        viewModel.removeSetFromExercise(currentExerciseIndex, setIndex)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                ForjaOutlinedButton(
                    text = "+ ADICIONAR MAIS UMA SÉRIE",
                    icon = Icons.Default.Add,
                    onClick = { viewModel.addSetToExercise(currentExerciseIndex) },
                    testTag = "add_set_button"
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Navigation between exercises
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentExerciseIndex > 0) {
                        ForjaSecondaryButton(
                            text = "← Anterior",
                            onClick = { viewModel.setCurrentExerciseIndex(currentExerciseIndex - 1) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (currentExerciseIndex < session.exercises.size - 1) {
                        ForjaPrimaryButton(
                            text = "Próximo Exercício →",
                            onClick = { viewModel.setCurrentExerciseIndex(currentExerciseIndex + 1) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        ForjaPrimaryButton(
                            text = "CONCLUIR TREINO",
                            onClick = { showFinishDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun SetRowItem(
    set: ActiveSet,
    setIndex: Int,
    totalSets: Int,
    exerciseType: String,
    onComplete: (weight: Float, reps: Int, notes: String) -> Unit,
    onDelete: () -> Unit
) {
    var weightText by remember(set.weightKg) { mutableStateOf(set.weightKg.toInt().toString()) }
    var repsText by remember(set.reps) { mutableStateOf(set.reps.toString()) }
    var notesText by remember(set.notes) { mutableStateOf(set.notes) }

    val isDone = set.isCompleted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDone) Color(0xFF132219) else ForgeCard)
            .border(1.dp, if (isDone) ForgeGreen else ForgeBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Set label: Série 1/4 (Requirement 18)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDone) ForgeGreen else ForgeBorder),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${set.setNumber}/$totalSets",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isDone) Color.White else TextPrimaryDark
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (exerciseType == "Cardio") {
            // Duration & Distance for Cardio
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "DURAÇÃO (MIN)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = activeSetTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            // Weight Input
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "CARGA (KG)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = activeSetTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("weight_input_${setIndex + 1}")
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Reps Input
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "REPS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = activeSetTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().testTag("reps_input_${setIndex + 1}")
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Complete Set Button (Requirement 18)
        Button(
            onClick = {
                val w = weightText.toFloatOrNull() ?: set.weightKg
                val r = repsText.toIntOrNull() ?: set.reps
                onComplete(w, r, notesText)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDone) ForgeGreen else ForgeOrange
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("complete_set_btn_${setIndex + 1}")
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Concluir série",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (isDone) "FEITA" else "CONCLUIR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun activeSetTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    cursorColor = ForgeOrange
)
