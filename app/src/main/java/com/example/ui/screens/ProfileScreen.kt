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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.SectionHeader
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
fun ProfileScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val completedWorkouts by viewModel.completedWorkouts.collectAsState()
    val prs by viewModel.personalRecords.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    val name = profile?.name?.ifBlank { "Atleta Forja" } ?: "Atleta Forja"

    if (showEditDialog && profile != null) {
        var editName by remember { mutableStateOf(profile!!.name) }
        var editAge by remember { mutableStateOf(profile!!.age.toString()) }
        var editHeight by remember { mutableStateOf(profile!!.heightCm.toString()) }
        var editWeight by remember { mutableStateOf(profile!!.currentWeightKg.toString()) }
        var editTargetWeight by remember { mutableStateOf(profile!!.targetWeightKg.toString()) }
        var editDays by remember { mutableStateOf(profile!!.availableDays.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = ForgeCard,
            title = { Text("Editar Perfil do Atleta", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = profileTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editAge,
                            onValueChange = { editAge = it },
                            label = { Text("Idade") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = profileTextFieldColors()
                        )
                        OutlinedTextField(
                            value = editHeight,
                            onValueChange = { editHeight = it },
                            label = { Text("Altura (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = profileTextFieldColors()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editWeight,
                            onValueChange = { editWeight = it },
                            label = { Text("Peso Atual (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = profileTextFieldColors()
                        )
                        OutlinedTextField(
                            value = editTargetWeight,
                            onValueChange = { editTargetWeight = it },
                            label = { Text("Meta (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = profileTextFieldColors()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding(
                            name = editName,
                            age = editAge.toIntOrNull() ?: profile!!.age,
                            gender = profile!!.gender,
                            heightCm = editHeight.toFloatOrNull() ?: profile!!.heightCm,
                            currentWeightKg = editWeight.toFloatOrNull() ?: profile!!.currentWeightKg,
                            targetWeightKg = editTargetWeight.toFloatOrNull() ?: profile!!.targetWeightKg,
                            mainGoal = profile!!.mainGoal,
                            trainingLevel = profile!!.trainingLevel,
                            availableDays = editDays.toIntOrNull() ?: profile!!.availableDays
                        )
                        showEditDialog = false
                    },
                    modifier = Modifier.testTag("save_profile_btn")
                ) {
                    Text("Salvar Alterações", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
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
            .testTag("profile_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Header
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeOrange.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ForgeOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${profile?.mainGoal ?: "Hipertrofia"} • Atleta Forja",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                            if (profile?.isDemoUser == true) {
                                Text(
                                    text = "⚡ CONTA DEMO / DEMONSTRAÇÃO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeOrange
                                )
                            }
                        }
                    }

                    IconButton(onClick = { showEditDialog = true }, modifier = Modifier.testTag("edit_profile_btn")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = ForgeOrange)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileStat(label = "Treinos Feitos", value = "${completedWorkouts.size}")
                    ProfileStat(label = "Streak Atual", value = "${profile?.currentStreak ?: 0}d")
                    ProfileStat(label = "Recordes (PR)", value = "${prs.size}")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Physical and Training Details
            SectionHeader(title = "DADOS CORPORAIS & ROTINA")

            ForjaCard(backgroundColor = ForgeCard, borderColor = ForgeBorder) {
                ProfileDetailRow(label = "Idade", value = "${profile?.age ?: 25} anos")
                ProfileDetailRow(label = "Sexo", value = profile?.gender ?: "Masculino")
                ProfileDetailRow(label = "Altura", value = "${profile?.heightCm?.toInt() ?: 175} cm")
                ProfileDetailRow(label = "Peso Atual", value = "${profile?.currentWeightKg ?: 80f} kg")
                ProfileDetailRow(label = "Peso Objetivo", value = "${profile?.targetWeightKg ?: 75f} kg")
                ProfileDetailRow(label = "Objetivo Principal", value = profile?.mainGoal ?: "Hipertrofia")
                ProfileDetailRow(label = "Nível de Treinamento", value = profile?.trainingLevel ?: "Intermediário")
                ProfileDetailRow(label = "Frequência Semanal", value = "${profile?.availableDays ?: 5} dias / semana")
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Secondary Quick Links
            SectionHeader(title = "CENTRAL DE FERRAMENTAS")

            ForjaOutlinedButton(
                text = "CONQUISTAS E MEDALHAS",
                icon = Icons.Default.EmojiEvents,
                onClick = { onNavigate(Screen.ACHIEVEMENTS) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ForjaOutlinedButton(
                text = "CONFIGURAÇÕES DO APP",
                icon = Icons.Default.Settings,
                onClick = { onNavigate(Screen.SETTINGS) }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
        Text(text = label, fontSize = 11.sp, color = TextSecondaryDark)
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondaryDark)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
    }
}

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    cursorColor = ForgeOrange
)
