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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.ui.components.EmptyState
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.components.SimpleLineChart
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
import kotlin.math.abs

@Composable
fun ProgressScreen(
    viewModel: ForjaViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Peso, 1: Medidas, 2: Fotos

    val profile by viewModel.userProfile.collectAsState()
    val weightRecords by viewModel.weightRecords.collectAsState()
    val measurements by viewModel.bodyMeasurements.collectAsState()
    val photos by viewModel.evolutionPhotos.collectAsState()

    var showAddWeightDialog by remember { mutableStateOf(false) }
    var showAddMeasurementDialog by remember { mutableStateOf(false) }
    var showAddPhotoDialog by remember { mutableStateOf(false) }

    // Dialog: Add Weight
    if (showAddWeightDialog) {
        var weightInput by remember { mutableStateOf(profile?.currentWeightKg?.toString() ?: "80.0") }
        var noteInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddWeightDialog = false },
            containerColor = ForgeCard,
            title = { Text("Registrar Peso", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("weight_modal_input"),
                        colors = progressTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Observação (ex: jejum pós-treino)") },
                        modifier = Modifier.fillMaxWidth().testTag("weight_modal_note"),
                        colors = progressTextFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val w = weightInput.toFloatOrNull()
                        if (w != null && w > 0f) {
                            viewModel.addWeightRecord(w, noteInput)
                            showAddWeightDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_weight_btn")
                ) {
                    Text("Salvar", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWeightDialog = false }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }

    // Dialog: Add Measurements
    if (showAddMeasurementDialog) {
        var rightArm by remember { mutableStateOf("38.5") }
        var leftArm by remember { mutableStateOf("38.5") }
        var chest by remember { mutableStateOf("105") }
        var waist by remember { mutableStateOf("86") }
        var abdomen by remember { mutableStateOf("88") }
        var hips by remember { mutableStateOf("101") }
        var rightThigh by remember { mutableStateOf("61") }
        var leftThigh by remember { mutableStateOf("61") }
        var calves by remember { mutableStateOf("38.5") }

        AlertDialog(
            onDismissRequest = { showAddMeasurementDialog = false },
            containerColor = ForgeCard,
            title = { Text("Registrar Medidas Corporais", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    item {
                        MeasurementField(label = "Braço Direito (cm)", value = rightArm, onValueChange = { rightArm = it })
                        MeasurementField(label = "Braço Esquerdo (cm)", value = leftArm, onValueChange = { leftArm = it })
                        MeasurementField(label = "Tórax / Peito (cm)", value = chest, onValueChange = { chest = it })
                        MeasurementField(label = "Cintura (cm)", value = waist, onValueChange = { waist = it })
                        MeasurementField(label = "Abdômen (cm)", value = abdomen, onValueChange = { abdomen = it })
                        MeasurementField(label = "Quadril (cm)", value = hips, onValueChange = { hips = it })
                        MeasurementField(label = "Coxa Direita (cm)", value = rightThigh, onValueChange = { rightThigh = it })
                        MeasurementField(label = "Coxa Esquerda (cm)", value = leftThigh, onValueChange = { leftThigh = it })
                        MeasurementField(label = "Panturrilhas (cm)", value = calves, onValueChange = { calves = it })
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addMeasurement(
                            rightArm = rightArm.toFloatOrNull() ?: 0f,
                            leftArm = leftArm.toFloatOrNull() ?: 0f,
                            chest = chest.toFloatOrNull() ?: 0f,
                            waist = waist.toFloatOrNull() ?: 0f,
                            abdomen = abdomen.toFloatOrNull() ?: 0f,
                            hips = hips.toFloatOrNull() ?: 0f,
                            rightThigh = rightThigh.toFloatOrNull() ?: 0f,
                            leftThigh = leftThigh.toFloatOrNull() ?: 0f,
                            calves = calves.toFloatOrNull() ?: 0f
                        )
                        showAddMeasurementDialog = false
                    },
                    modifier = Modifier.testTag("save_measurements_btn")
                ) {
                    Text("Salvar", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMeasurementDialog = false }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }

    // Dialog: Add Photo
    if (showAddPhotoDialog) {
        var angle by remember { mutableStateOf("Frontal") }
        var photoNote by remember { mutableStateOf("Foto mensal de evolução") }

        AlertDialog(
            onDismissRequest = { showAddPhotoDialog = false },
            containerColor = ForgeCard,
            title = { Text("Registrar Foto de Evolução", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    Text("Selecione o ângulo da foto:", fontSize = 13.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Frontal", "Lateral", "Costas").forEach { a ->
                            val isSel = angle == a
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ForgeOrange else ForgeCardElevated)
                                    .clickable { angle = a }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(a, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSel) Color.White else TextPrimaryDark)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = photoNote,
                        onValueChange = { photoNote = it },
                        label = { Text("Legenda / Observação") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = progressTextFieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addEvolutionPhoto(angle, "placeholder_photo_uri", photoNote)
                        showAddPhotoDialog = false
                    },
                    modifier = Modifier.testTag("save_photo_btn")
                ) {
                    Text("Registrar", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPhotoDialog = false }) {
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
            .testTag("progress_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PROGRESSO CORPORAL",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Acompanhamento de peso, circunferências e fotos de antes/depois",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ForgeCard,
                contentColor = ForgeOrange,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Peso", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Medidas", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Fotos", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        when (selectedTab) {
            // TAB 0: PESO
            0 -> {
                item {
                    val initialWeight = profile?.initialWeightKg ?: 80f
                    val currentWeight = profile?.currentWeightKg ?: 80f
                    val targetWeight = profile?.targetWeightKg ?: 75f
                    val weightLost = initialWeight - currentWeight
                    val weightRemaining = currentWeight - targetWeight

                    ForjaCard(
                        backgroundColor = ForgeCardElevated,
                        borderColor = ForgeOrange.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "METAS DE PESO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("PESO INICIAL", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                                Text("${String.format("%.1f", initialWeight)} kg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PESO ATUAL", fontSize = 10.sp, color = ForgeOrange, fontWeight = FontWeight.Bold)
                                Text("${String.format("%.1f", currentWeight)} kg", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("OBJETIVO", fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Bold)
                                Text("${String.format("%.1f", targetWeight)} kg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Feedback (Rule 16 requirement)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1B261E))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (weightLost >= 0) "✅ Você perdeu ${String.format("%.1f", weightLost)} kg desde o início"
                                    else "📈 Você ganhou ${String.format("%.1f", abs(weightLost))} kg desde o início",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (weightRemaining <= 0) "🎉 Parabéns! Você bateu seu peso objetivo!"
                                    else "🎯 Faltam ${String.format("%.1f", weightRemaining)} kg para alcançar sua meta",
                                    fontSize = 12.sp,
                                    color = TextPrimaryDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (weightRecords.size >= 2) {
                        Text(
                            text = "EVOLUÇÃO DO PESO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SimpleLineChart(points = weightRecords.map { it.weightKg })
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HISTÓRICO DE PESAGENS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark,
                            letterSpacing = 0.5.sp
                        )
                        TextButton(onClick = { showAddWeightDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Novo Registro", color = ForgeOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (weightRecords.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Nenhum peso registrado",
                            description = "Adicione pesagens para montar o gráfico de evolução.",
                            actionText = "Registrar Peso",
                            onAction = { showAddWeightDialog = true }
                        )
                    }
                } else {
                    items(weightRecords.reversed()) { record ->
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
                                    Icon(imageVector = Icons.Default.MonitorWeight, contentDescription = null, tint = ForgeOrange)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("${record.weightKg} kg", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimaryDark)
                                        Text("${record.dateString} ${record.timeString} • ${record.note.ifBlank { "Sem notas" }}", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteWeightRecord(record.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: MEDIDAS
            1 -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HISTÓRICO DE MEDIDAS (CM)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark,
                            letterSpacing = 0.5.sp
                        )
                        TextButton(onClick = { showAddMeasurementDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nova Medição", color = ForgeOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (measurements.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Nenhuma medida registrada",
                            description = "Monitore o crescimento de braços, tórax, pernas e diminuição de cintura.",
                            actionText = "Registrar Medidas",
                            onAction = { showAddMeasurementDialog = true }
                        )
                    }
                } else {
                    items(measurements) { m ->
                        ForjaCard(
                            backgroundColor = ForgeCard,
                            borderColor = ForgeBorder,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Text(
                                text = "Medição realizada em: ${m.dateString}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForgeOrange
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MeasurementPill("Braço D", "${m.rightArm}cm")
                                MeasurementPill("Braço E", "${m.leftArm}cm")
                                MeasurementPill("Tórax", "${m.chest}cm")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MeasurementPill("Cintura", "${m.waist}cm")
                                MeasurementPill("Abdômen", "${m.abdomen}cm")
                                MeasurementPill("Quadril", "${m.hips}cm")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MeasurementPill("Coxa D", "${m.rightThigh}cm")
                                MeasurementPill("Coxa E", "${m.leftThigh}cm")
                                MeasurementPill("Panturrilha", "${m.calves}cm")
                            }
                        }
                    }
                }
            }

            // TAB 2: FOTOS DE EVOLUÇÃO
            2 -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FOTOS DE EVOLUÇÃO (PRIVADAS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryDark,
                            letterSpacing = 0.5.sp
                        )
                        TextButton(onClick = { showAddPhotoDialog = true }) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nova Foto", color = ForgeOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Antes & Depois Comparison Frame
                    ForjaCard(
                        backgroundColor = ForgeCardElevated,
                        borderColor = ForgeOrange.copy(alpha = 0.4f)
                    ) {
                        Text(
                            text = "COMPARATIVO ANTES / DEPOIS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PhotoSlot(title = "ANTES (INÍCIO)", date = "Foto 1", modifier = Modifier.weight(1f))
                            PhotoSlot(title = "DEPOIS (ATUAL)", date = "Foto Recente", modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "GALERIA REGISTRADA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (photos.isEmpty()) {
                    item {
                        EmptyState(
                            title = "Nenhuma foto adicionada",
                            description = "Adicione fotos de frente, lado e costas para registrar sua transformação com privacidade.",
                            actionText = "Tirar / Adicionar Foto",
                            onAction = { showAddPhotoDialog = true }
                        )
                    }
                } else {
                    items(photos) { photo ->
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
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ForgeCardElevated),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = ForgeOrange)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = "Ângulo: ${photo.angle}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                        Text(text = "${photo.dateString} • ${photo.note}", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteEvolutionPhoto(photo.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Excluir", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
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
private fun MeasurementField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = progressTextFieldColors()
    )
}

@Composable
private fun MeasurementPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextSecondaryDark, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
    }
}

@Composable
private fun PhotoSlot(title: String, date: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF15151B))
            .border(1.dp, ForgeBorder, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Image, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
            Text(date, fontSize = 9.sp, color = TextSecondaryDark)
        }
    }
}

@Composable
private fun progressTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    cursorColor = ForgeOrange
)
