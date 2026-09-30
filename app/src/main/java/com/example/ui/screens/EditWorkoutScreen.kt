package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entities.WorkoutExerciseEntity
import com.example.data.model.ConfiguredSet
import com.example.data.model.CustomSetsParser
import com.example.data.model.ExerciseType
import com.example.data.model.MuscleGroup
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
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
fun EditWorkoutScreen(
    viewModel: ForjaViewModel,
    onNavigateBack: () -> Unit
) {
    val existingPlan by viewModel.selectedWorkoutPlanForEdit.collectAsState()
    val exercises by viewModel.editingWorkoutExercises.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    var workoutName by remember(existingPlan) { mutableStateOf(existingPlan?.name ?: "Novo Treino") }
    var workoutCode by remember(existingPlan) { mutableStateOf(existingPlan?.code ?: "A") }
    var workoutDescription by remember(existingPlan) { mutableStateOf(existingPlan?.description ?: "") }
    var targetMuscles by remember(existingPlan) { mutableStateOf(existingPlan?.targetMuscles ?: "Peito + Tríceps") }
    var estimatedMinutes by remember(existingPlan) { mutableIntStateOf(existingPlan?.estimatedMinutes ?: 60) }

    // Dialog state
    var showAddExerciseModal by remember { mutableStateOf(false) }
    var showCreateCustomExerciseDialog by remember { mutableStateOf(false) }
    var exerciseToDeleteIndex by remember { mutableStateOf<Int?>(null) }
    var exerciseToEditConfig by remember { mutableStateOf<Pair<WorkoutExerciseEntity, Int>?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("edit_workout_screen")
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
                        text = if (existingPlan == null) "CRIAR TREINO" else "EDITAR TREINO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Configure séries, cargas, descanso e ordem",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Workout Details Card
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeBorder,
                testTag = "workout_metadata_card"
            ) {
                Text(
                    text = "INFORMAÇÕES DO TREINO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForgeOrange,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = workoutCode,
                        onValueChange = { workoutCode = it.take(6) },
                        label = { Text("Código / Letra") },
                        placeholder = { Text("Ex: A") },
                        modifier = Modifier.width(110.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeOrange,
                            unfocusedBorderColor = ForgeBorder,
                            focusedLabelColor = ForgeOrange,
                            unfocusedLabelColor = TextSecondaryDark,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedContainerColor = Color(0xFF141419),
                            unfocusedContainerColor = Color(0xFF141419)
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { workoutName = it },
                        label = { Text("Nome do Treino") },
                        placeholder = { Text("Ex: Treino A — Peito + Tríceps") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeOrange,
                            unfocusedBorderColor = ForgeBorder,
                            focusedLabelColor = ForgeOrange,
                            unfocusedLabelColor = TextSecondaryDark,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedContainerColor = Color(0xFF141419),
                            unfocusedContainerColor = Color(0xFF141419)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetMuscles,
                    onValueChange = { targetMuscles = it },
                    label = { Text("Grupos Musculares / Foco") },
                    placeholder = { Text("Ex: Peito, Tríceps, Deltoide Anterior") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeOrange,
                        unfocusedBorderColor = ForgeBorder,
                        focusedLabelColor = ForgeOrange,
                        unfocusedLabelColor = TextSecondaryDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedContainerColor = Color(0xFF141419),
                        unfocusedContainerColor = Color(0xFF141419)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = workoutDescription,
                    onValueChange = { workoutDescription = it },
                    label = { Text("Descrição / Observações Gerais") },
                    placeholder = { Text("Instruções sobre este plano...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeOrange,
                        unfocusedBorderColor = ForgeBorder,
                        focusedLabelColor = ForgeOrange,
                        unfocusedLabelColor = TextSecondaryDark,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedContainerColor = Color(0xFF141419),
                        unfocusedContainerColor = Color(0xFF141419)
                    ),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Exercise List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXERCÍCIOS (${exercises.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark,
                    letterSpacing = 0.5.sp
                )

                Button(
                    onClick = { showAddExerciseModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_exercise_to_workout_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ ADICIONAR EXERCÍCIO", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (exercises.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum exercício adicionado",
                    description = "Clique em '+ ADICIONAR EXERCÍCIO' para selecionar da biblioteca ou criar um novo exercício.",
                    actionText = "+ Adicionar da Biblioteca",
                    onAction = { showAddExerciseModal = true }
                )
            }
        } else {
            itemsIndexed(exercises) { index, exercise ->
                ExerciseConfigCard(
                    exercise = exercise,
                    index = index,
                    totalCount = exercises.size,
                    onMoveUp = { viewModel.moveEditingExercise(index, index - 1) },
                    onMoveDown = { viewModel.moveEditingExercise(index, index + 1) },
                    onEdit = { exerciseToEditConfig = Pair(exercise, index) },
                    onDuplicate = { viewModel.duplicateEditingExercise(index) },
                    onDelete = { exerciseToDeleteIndex = index }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Save Workout Button
            ForjaPrimaryButton(
                text = "SALVAR TREINO",
                icon = Icons.Default.Check,
                onClick = {
                    viewModel.saveWorkout(
                        name = workoutName.ifBlank { "Treino $workoutCode" },
                        description = workoutDescription,
                        code = workoutCode.ifBlank { "A" },
                        targetMuscles = targetMuscles.ifBlank { "Geral" },
                        estimatedMinutes = estimatedMinutes
                    ) {
                        onNavigateBack()
                    }
                },
                testTag = "save_workout_btn"
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Confirmation dialog before deleting exercise
    if (exerciseToDeleteIndex != null) {
        val index = exerciseToDeleteIndex!!
        val exName = exercises.getOrNull(index)?.exerciseName ?: "este exercício"
        AlertDialog(
            onDismissRequest = { exerciseToDeleteIndex = null },
            containerColor = ForgeCard,
            title = {
                Text("Remover Exercício?", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Tem certeza que deseja remover \"$exName\" deste treino?\n(O histórico de execuções anteriores não será apagado).",
                    color = TextSecondaryDark,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeEditingExercise(index)
                        exerciseToDeleteIndex = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Remover", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { exerciseToDeleteIndex = null }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }

    // Modal / Dialog for Editing Specific Exercise Sets & Values
    if (exerciseToEditConfig != null) {
        val (exercise, index) = exerciseToEditConfig!!
        EditExerciseDetailDialog(
            exercise = exercise,
            onDismiss = { exerciseToEditConfig = null },
            onSave = { updated ->
                viewModel.updateEditingExercise(updated, index)
                exerciseToEditConfig = null
            }
        )
    }

    // Modal: Library Exercise Picker
    if (showAddExerciseModal) {
        AddExerciseModal(
            allExercises = allExercises,
            onDismiss = { showAddExerciseModal = false },
            onSelect = { selectedExercise ->
                viewModel.addExerciseToEditingWorkout(
                    exercise = selectedExercise,
                    sets = selectedExercise.defaultSets,
                    reps = selectedExercise.defaultReps,
                    weightKg = 20f,
                    restSeconds = selectedExercise.defaultRestSeconds,
                    exerciseType = selectedExercise.exerciseType
                )
                showAddExerciseModal = false
            },
            onCreateCustom = {
                showAddExerciseModal = false
                showCreateCustomExerciseDialog = true
            }
        )
    }

    // Dialog: Create Custom Exercise
    if (showCreateCustomExerciseDialog) {
        CreateCustomExerciseDialog(
            onDismiss = { showCreateCustomExerciseDialog = false },
            onCreate = { name, group, equip, type ->
                viewModel.createCustomExercise(name, group, equip, type) { created ->
                    viewModel.addExerciseToEditingWorkout(
                        exercise = created,
                        sets = 3,
                        reps = 10,
                        weightKg = 20f,
                        restSeconds = 60,
                        exerciseType = type
                    )
                }
                showCreateCustomExerciseDialog = false
            }
        )
    }
}

@Composable
private fun ExerciseConfigCard(
    exercise: WorkoutExerciseEntity,
    index: Int,
    totalCount: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val configuredSets = remember(exercise.customSetsJson) {
        CustomSetsParser.fromJson(
            exercise.customSetsJson,
            defaultSets = exercise.sets,
            defaultReps = exercise.reps,
            defaultWeight = exercise.weightKg
        )
    }

    ForjaCard(
        backgroundColor = ForgeCard,
        borderColor = ForgeBorder,
        testTag = "exercise_item_${index}"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                // Reorder buttons
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = index > 0,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Mover para cima",
                            tint = if (index > 0) ForgeOrange else TextSecondaryDark.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${index + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = ForgeOrange
                    )
                    IconButton(
                        onClick = onMoveDown,
                        enabled = index < totalCount - 1,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Mover para baixo",
                            tint = if (index < totalCount - 1) ForgeOrange else TextSecondaryDark.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = exercise.exerciseName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF26180E))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = exercise.exerciseType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ForgeOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when (exercise.exerciseType) {
                        "Cardio" -> {
                            Text(
                                text = "Duração: ${exercise.durationSeconds / 60}m • Distância: ${exercise.distanceKm}km • Descanso: ${exercise.restSeconds}s",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                        "Isometria", "Por Tempo" -> {
                            Text(
                                text = "${exercise.sets} séries • ${exercise.durationSeconds}s cada • Descanso: ${exercise.restSeconds}s",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                        "Por Distância" -> {
                            Text(
                                text = "${exercise.sets} séries • ${exercise.distanceKm}km cada",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                        else -> {
                            Text(
                                text = "${exercise.sets} séries × ${exercise.reps} reps • ${exercise.weightKg} kg • Descanso: ${exercise.restSeconds}s",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    if (exercise.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Obs: \"${exercise.notes}\"",
                            fontSize = 11.sp,
                            color = Color(0xFFC7A280)
                        )
                    }
                }
            }

            // Action Buttons: EDITAR, DUPLICAR, REMOVER
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ForgeOrange, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDuplicate, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicar", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = ForgeRed, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Preview of individual sets if multiple or custom
        if (configuredSets.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF141419))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                configuredSets.take(5).forEach { cs ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF202028))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "S${cs.setNumber}: ${cs.reps}r × ${cs.weightKg.toInt()}kg",
                            fontSize = 10.sp,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                if (configuredSets.size > 5) {
                    Text(
                        text = "+${configuredSets.size - 5}",
                        fontSize = 10.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

// Dialog for editing full parameters of an exercise within the workout
@Composable
private fun EditExerciseDetailDialog(
    exercise: WorkoutExerciseEntity,
    onDismiss: () -> Unit,
    onSave: (WorkoutExerciseEntity) -> Unit
) {
    var setsCount by remember { mutableIntStateOf(exercise.sets) }
    var defaultReps by remember { mutableIntStateOf(exercise.reps) }
    var defaultWeight by remember { mutableStateOf(exercise.weightKg.toString()) }
    var restSeconds by remember { mutableIntStateOf(exercise.restSeconds) }
    var durationSec by remember { mutableIntStateOf(exercise.durationSeconds) }
    var distanceKm by remember { mutableStateOf(exercise.distanceKm.toString()) }
    var notes by remember { mutableStateOf(exercise.notes) }
    var exerciseType by remember { mutableStateOf(exercise.exerciseType) }

    var individualSets by remember {
        mutableStateOf(
            CustomSetsParser.fromJson(
                exercise.customSetsJson,
                defaultSets = exercise.sets,
                defaultReps = exercise.reps,
                defaultWeight = exercise.weightKg
            )
        )
    }

    val typeOptions = listOf("Musculação", "Cardio", "Isometria", "Por Tempo", "Por Distância")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ForgeCardElevated,
        title = {
            Column {
                Text(
                    text = exercise.exerciseName,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Configuração completa do exercício",
                    fontSize = 12.sp,
                    color = ForgeOrange
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("TIPO DE EXERCÍCIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        typeOptions.take(3).forEach { t ->
                            FilterChip(
                                selected = exerciseType == t,
                                onClick = { exerciseType = t },
                                label = { Text(t, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ForgeOrange,
                                    selectedLabelColor = Color.White,
                                    containerColor = ForgeCard,
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        typeOptions.drop(3).forEach { t ->
                            FilterChip(
                                selected = exerciseType == t,
                                onClick = { exerciseType = t },
                                label = { Text(t, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ForgeOrange,
                                    selectedLabelColor = Color.White,
                                    containerColor = ForgeCard,
                                    labelColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                item {
                    // Global parameters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = setsCount.toString(),
                            onValueChange = {
                                val s = it.toIntOrNull() ?: 1
                                setsCount = s.coerceIn(1, 20)
                                // Adjust individual sets list
                                val current = individualSets.toMutableList()
                                if (setsCount > current.size) {
                                    val last = current.lastOrNull()
                                    for (i in (current.size + 1)..setsCount) {
                                        current.add(ConfiguredSet(setNumber = i, reps = last?.reps ?: defaultReps, weightKg = last?.weightKg ?: (defaultWeight.toFloatOrNull() ?: 20f)))
                                    }
                                } else if (setsCount < current.size) {
                                    while (current.size > setsCount) current.removeAt(current.size - 1)
                                }
                                individualSets = current
                            },
                            label = { Text("Séries") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeOrange,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )

                        OutlinedTextField(
                            value = restSeconds.toString(),
                            onValueChange = { restSeconds = it.toIntOrNull() ?: 60 },
                            label = { Text("Descanso (s)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeOrange,
                                unfocusedBorderColor = ForgeBorder,
                                focusedTextColor = TextPrimaryDark,
                                unfocusedTextColor = TextPrimaryDark
                            )
                        )
                    }
                }

                // If Musculação: show sets breakdown
                if (exerciseType == "Musculação") {
                    item {
                        Text(
                            text = "CONFIGURAR CADA SÉRIE INDIVIDUALMENTE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    itemsIndexed(individualSets) { setIdx, cSet ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141419))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Série ${setIdx + 1}:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark,
                                modifier = Modifier.width(60.dp)
                            )

                            OutlinedTextField(
                                value = cSet.reps.toString(),
                                onValueChange = {
                                    val r = it.toIntOrNull() ?: 10
                                    val copy = individualSets.toMutableList()
                                    copy[setIdx] = cSet.copy(reps = r)
                                    individualSets = copy
                                },
                                label = { Text("Reps", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ForgeOrange,
                                    unfocusedBorderColor = ForgeBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )

                            OutlinedTextField(
                                value = cSet.weightKg.toInt().toString(),
                                onValueChange = {
                                    val w = it.toFloatOrNull() ?: 20f
                                    val copy = individualSets.toMutableList()
                                    copy[setIdx] = cSet.copy(weightKg = w)
                                    individualSets = copy
                                },
                                label = { Text("Carga (kg)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1.2f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ForgeOrange,
                                    unfocusedBorderColor = ForgeBorder,
                                    focusedTextColor = TextPrimaryDark,
                                    unfocusedTextColor = TextPrimaryDark
                                )
                            )
                        }
                    }
                } else if (exerciseType == "Cardio") {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = (durationSec / 60).toString(),
                                onValueChange = { durationSec = (it.toIntOrNull() ?: 10) * 60 },
                                label = { Text("Duração (min)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeOrange)
                            )
                            OutlinedTextField(
                                value = distanceKm,
                                onValueChange = { distanceKm = it },
                                label = { Text("Distância (km)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeOrange)
                            )
                        }
                    }
                } else {
                    item {
                        OutlinedTextField(
                            value = durationSec.toString(),
                            onValueChange = { durationSec = it.toIntOrNull() ?: 30 },
                            label = { Text("Duração por série (segundos)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeOrange)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações do Exercício") },
                        placeholder = { Text("Ex: Usar pegada fechada, sentir contração...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeOrange,
                            unfocusedBorderColor = ForgeBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = exercise.copy(
                        exerciseType = exerciseType,
                        sets = setsCount,
                        reps = individualSets.firstOrNull()?.reps ?: defaultReps,
                        weightKg = individualSets.firstOrNull()?.weightKg ?: (defaultWeight.toFloatOrNull() ?: 20f),
                        restSeconds = restSeconds,
                        durationSeconds = durationSec,
                        distanceKm = distanceKm.toFloatOrNull() ?: 0f,
                        notes = notes,
                        customSetsJson = CustomSetsParser.toJson(individualSets)
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondaryDark)
            }
        }
    )
}

// Modal: Select exercise from library or create custom
@Composable
private fun AddExerciseModal(
    allExercises: List<ExerciseEntity>,
    onDismiss: () -> Unit,
    onSelect: (ExerciseEntity) -> Unit,
    onCreateCustom: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("Todos") }

    val muscleGroups = listOf("Todos", "Peito", "Costas", "Ombros", "Bíceps", "Tríceps", "Quadríceps", "Posterior", "Glúteos", "Panturrilhas", "Abdômen", "Cardio")

    val filtered = remember(allExercises, searchQuery, selectedGroup) {
        allExercises.filter { ex ->
            val matchName = searchQuery.isBlank() || ex.name.contains(searchQuery, ignoreCase = true)
            val matchGroup = selectedGroup == "Todos" || ex.muscleGroup.equals(selectedGroup, ignoreCase = true)
            matchName && matchGroup
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ForgeCardElevated,
        title = {
            Column {
                Text(
                    text = "ADICIONAR EXERCÍCIO",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Pesquise pelo nome ou crie um personalizado",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Pesquisar exercício...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForgeOrange) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeOrange,
                        unfocusedBorderColor = ForgeBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedContainerColor = Color(0xFF141419),
                        unfocusedContainerColor = Color(0xFF141419)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Create Custom Exercise Option Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF26180E))
                        .border(1.dp, ForgeOrange.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .clickable { onCreateCustom() }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "+ CRIAR EXERCÍCIO PERSONALIZADO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange
                        )
                        Text(
                            text = "Não achou? Crie com nome e tipo customizados",
                            fontSize = 10.sp,
                            color = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Exercise List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(filtered) { _, exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF16161C))
                                .clickable { onSelect(exercise) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exercise.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "${exercise.muscleGroup} • ${exercise.equipment}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                            Icon(Icons.Default.Add, contentDescription = "Selecionar", tint = ForgeOrange, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = TextSecondaryDark)
            }
        }
    )
}

// Dialog: Create new custom exercise
@Composable
private fun CreateCustomExerciseDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, group: String, equip: String, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("Peito") }
    var equip by remember { mutableStateOf("Halteres") }
    var type by remember { mutableStateOf("Musculação") }

    val groups = listOf("Peito", "Costas", "Ombros", "Bíceps", "Tríceps", "Quadríceps", "Posterior", "Glúteos", "Panturrilhas", "Abdômen", "Cardio")
    val types = listOf("Musculação", "Cardio", "Isometria", "Por Tempo", "Por Distância")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ForgeCardElevated,
        title = {
            Text("CRIAR EXERCÍCIO", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextPrimaryDark)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Exercício") },
                    placeholder = { Text("Ex: Supino Inclinado com Halteres") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeOrange,
                        unfocusedBorderColor = ForgeBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    singleLine = true
                )

                Text("GRUPO MUSCULAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groups.take(4).forEach { g ->
                        FilterChip(
                            selected = group == g,
                            onClick = { group = g },
                            label = { Text(g, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForgeOrange,
                                selectedLabelColor = Color.White,
                                containerColor = ForgeCard,
                                labelColor = TextSecondaryDark
                            )
                        )
                    }
                }

                Text("TIPO DE EXERCÍCIO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    types.take(3).forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForgeOrange,
                                selectedLabelColor = Color.White,
                                containerColor = ForgeCard,
                                labelColor = TextSecondaryDark
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), group, equip, type)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Adicionar ao Treino", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondaryDark)
            }
        }
    )
}
