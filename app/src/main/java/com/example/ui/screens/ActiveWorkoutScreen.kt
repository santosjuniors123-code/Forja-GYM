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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    var showFinishDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSwapExerciseDialog by remember { mutableStateOf(false) }
    var showInstructionsDialog by remember { mutableStateOf(false) }

    // Intercept back button to avoid accidental loss of workout
    BackHandler {
        showCancelDialog = true
    }

    if (activeSession == null) {
        EmptyState(
            title = "Nenhum treino em andamento",
            description = "Selecione um plano de treino na aba Treinos para iniciar sua sessão.",
            actionText = "Ver Treinos",
            onAction = { onNavigate(Screen.WORKOUTS) }
        )
        return
    }

    val session = activeSession!!
    val currentExerciseIndex = session.currentExerciseIndex
    val currentExercise = session.exercises.getOrNull(currentExerciseIndex)

    // Finish Dialog
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
                        text = "Parabéns pelo treino forjado!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeOrange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tempo total: ${FormatUtils.formatSecondsToDuration(session.elapsedSeconds)}\n" +
                                "Séries completadas: $totalCompletedSets\n" +
                                "Volume estimado: ${totalVolume.toInt()} kg",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
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

    // Cancel Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = ForgeCard,
            title = { Text("Deseja sair do treino?", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Text(
                    "Você pode pausar e continuar depois, ou cancelar descartando as séries não salvas.",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelWorkout()
                    },
                    modifier = Modifier.testTag("confirm_cancel_workout_btn")
                ) {
                    Text("Cancelar Treino", color = ForgeRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Permanecer no Treino", color = ForgeOrange)
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
                        text = "Não tem ${currentExercise.name}? Escolha uma alternativa equivalente preservando suas séries:",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

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
                                    .padding(12.dp)
                                    .padding(bottom = 6.dp),
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
            confirmButton = {
                TextButton(onClick = { showSwapExerciseDialog = false }) {
                    Text("Fechar", color = TextSecondaryDark)
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
                    Text(text = currentExercise.instructions, fontSize = 13.sp, color = TextPrimaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "DICAS PRO:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeGreen)
                    Text(text = currentExercise.tips, fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ERROS COMUNS A EVITAR:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeRed)
                    Text(text = currentExercise.commonMistakes, fontSize = 13.sp, color = TextSecondaryDark)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInstructionsDialog = false }) {
                    Text("Entendido", color = ForgeOrange, fontWeight = FontWeight.Bold)
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
        // Sticky Header: Workout general timer + pause + finish
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
                            text = session.workoutName.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 0.5.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = FormatUtils.formatSecondsToTime(session.elapsedSeconds),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark,
                                modifier = Modifier.testTag("workout_elapsed_time")
                            )
                            if (session.isPaused) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(PAUSADO)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeRed
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.toggleWorkoutPause() },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(ForgeBorder)
                                .testTag("pause_workout_btn")
                        ) {
                            Icon(
                                imageVector = if (session.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = "Pausar",
                                tint = TextPrimaryDark
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showFinishDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForgeGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("finish_workout_top_btn")
                        ) {
                            Text("FINALIZAR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Exercise Tabs Carousel
            ScrollableTabRow(
                selectedTabIndex = currentExerciseIndex,
                containerColor = ForgeCard,
                contentColor = ForgeOrange,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                session.exercises.forEachIndexed { index, ex ->
                    val allCompleted = ex.sets.all { it.isCompleted }
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
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "${index + 1}. ${ex.name.take(14)}...",
                                    fontWeight = if (currentExerciseIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
                                text = "${currentExercise.muscleGroup} • Equipamento: ${currentExercise.equipment}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }

                        Row {
                            IconButton(onClick = { showInstructionsDialog = true }) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "Instruções", tint = ForgeOrange)
                            }
                            IconButton(onClick = { showSwapExerciseDialog = true }) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Substituir", tint = TextSecondaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF16161B))
                            .clickable { showSwapExerciseDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Não tem esse equipamento? Ver alternativas",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ForgeOrange
                        )
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rest Timer Card
            RestTimerSection(
                secondsRemaining = restSeconds,
                isRunning = isRestRunning,
                onStartTimer = { s -> viewModel.startRestTimer(s) },
                onStopTimer = { viewModel.stopRestTimer() },
                onAdjustTimer = { delta -> viewModel.adjustRestTimer(delta) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SÉRIES & CARGAS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Sets List for current exercise
        if (currentExercise != null) {
            itemsIndexed(currentExercise.sets) { setIndex, set ->
                SetRowItem(
                    set = set,
                    setIndex = setIndex,
                    onComplete = { w, r ->
                        viewModel.completeSet(currentExerciseIndex, setIndex, w, r)
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
                            text = "← Exercício Anterior",
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
    set: com.example.data.model.ActiveSet,
    setIndex: Int,
    onComplete: (weight: Float, reps: Int) -> Unit
) {
    var weightText by remember(set.weightKg) { mutableStateOf(set.weightKg.toString()) }
    var repsText by remember(set.reps) { mutableStateOf(set.reps.toString()) }

    val isDone = set.isCompleted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDone) Color(0xFF132219) else ForgeCard)
            .border(1.dp, if (isDone) ForgeGreen else ForgeBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Set label
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDone) ForgeGreen else ForgeBorder),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${set.setNumber}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isDone) Color.White else TextPrimaryDark
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Weight Input
        Column(modifier = Modifier.width(90.dp)) {
            Text(text = "CARGA (KG)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = activeSetTextFieldColors(),
                modifier = Modifier.fillMaxWidth().testTag("weight_input_${setIndex + 1}")
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Reps Input
        Column(modifier = Modifier.width(80.dp)) {
            Text(text = "REPETIÇÕES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
            OutlinedTextField(
                value = repsText,
                onValueChange = { repsText = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = activeSetTextFieldColors(),
                modifier = Modifier.fillMaxWidth().testTag("reps_input_${setIndex + 1}")
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Complete Button
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDone) ForgeGreen else ForgeOrange)
                .clickable {
                    val w = weightText.toFloatOrNull() ?: set.weightKg
                    val r = repsText.toIntOrNull() ?: set.reps
                    onComplete(w, r)
                }
                .testTag("complete_set_btn_${setIndex + 1}"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Concluir série",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
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
