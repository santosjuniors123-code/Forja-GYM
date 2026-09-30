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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.ForjaSecondaryButton
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun OnboardingScreen(
    viewModel: ForjaViewModel
) {
    var step by remember { mutableIntStateOf(1) }

    // Step 1: Dados básicos
    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("26") }
    var selectedGender by remember { mutableStateOf("Masculino") }

    // Step 2: Medidas corporais iniciais
    var heightText by remember { mutableStateOf("178") }
    var currentWeightText by remember { mutableStateOf("82.0") }
    var targetWeightText by remember { mutableStateOf("78.0") }

    // Step 3: Foco e rotina
    var selectedGoal by remember { mutableStateOf("Hipertrofia") }
    var selectedLevel by remember { mutableStateOf("Intermediário (1-3 anos)") }
    var selectedDays by remember { mutableIntStateOf(5) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp)
            .testTag("onboarding_screen")
    ) {
        // Step progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONFIGURAÇÃO DA FORJA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeOrange,
                letterSpacing = 1.sp
            )
            Text(
                text = "Etapa $step de 3",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (step.toFloat() / 3f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = ForgeOrange,
            trackColor = ForgeBorder
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            1 -> {
                Text(
                    text = "Quem é você?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Personalizaremos todas as cargas, calorias e metas de acordo com seu perfil.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome completo ou apelido") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_name"),
                    colors = onboardingTextFieldColors()
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    label = { Text("Idade (anos)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_age"),
                    colors = onboardingTextFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sexo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val genders = listOf("Masculino", "Feminino", "Outro")
                    genders.forEach { g ->
                        val isSelected = selectedGender == g
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ForgeOrange else ForgeCard)
                                .border(1.dp, if (isSelected) ForgeOrange else ForgeBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedGender = g }
                                .padding(vertical = 12.dp)
                                .testTag("gender_$g"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = g,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else TextPrimaryDark
                            )
                        }
                    }
                }
            }

            2 -> {
                Text(
                    text = "Suas Métricas Físicas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Informações essenciais para cálculo do IMC, TMB e progressão de peso.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("Altura (cm) - Ex: 178") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_height"),
                    colors = onboardingTextFieldColors()
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = currentWeightText,
                    onValueChange = { currentWeightText = it },
                    label = { Text("Peso Atual (kg) - Ex: 82.5") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_current_weight"),
                    colors = onboardingTextFieldColors()
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = targetWeightText,
                    onValueChange = { targetWeightText = it },
                    label = { Text("Peso Objetivo (kg) - Ex: 78.0") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_target_weight"),
                    colors = onboardingTextFieldColors()
                )
            }

            3 -> {
                Text(
                    text = "Seus Objetivos & Rotina",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Defina sua meta principal e frequência semanal para estruturar seus treinos.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Objetivo Principal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                val goals = listOf(
                    "Emagrecimento" to "Queima de gordura e definição muscular",
                    "Hipertrofia" to "Ganho de massa muscular e volume",
                    "Recomposição Corporal" to "Queimar gordura e ganhar músculo juntos",
                    "Ganho de Força" to "Foco em progressão máxima de cargas",
                    "Manutenção" to "Saúde geral e condicionamento atlético"
                )

                goals.forEach { (title, desc) ->
                    val isSelected = selectedGoal == title
                    ForjaCard(
                        backgroundColor = if (isSelected) Color(0xFF26180E) else ForgeCard,
                        borderColor = if (isSelected) ForgeOrange else ForgeBorder,
                        onClick = { selectedGoal = title },
                        modifier = Modifier.padding(bottom = 8.dp),
                        testTag = "goal_${title.lowercase()}"
                    ) {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isSelected) ForgeOrange else TextPrimaryDark)
                        Text(text = desc, fontSize = 12.sp, color = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Nível de Treinamento",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                val levels = listOf(
                    "Iniciante (0-1 ano)",
                    "Intermediário (1-3 anos)",
                    "Avançado (3+ anos)"
                )
                levels.forEach { lvl ->
                    val isSelected = selectedLevel == lvl
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF26180E) else ForgeCard)
                            .border(1.dp, if (isSelected) ForgeOrange else ForgeBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedLevel = lvl }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .padding(bottom = 4.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = lvl,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) ForgeOrange else TextPrimaryDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Dias disponíveis para treino por semana",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (1..7).forEach { days ->
                        val isSelected = selectedDays == days
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ForgeOrange else ForgeCard)
                                .border(1.dp, if (isSelected) ForgeOrange else ForgeBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedDays = days }
                                .padding(vertical = 12.dp)
                                .testTag("days_$days"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$days",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White else TextPrimaryDark
                            )
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (step > 1) {
                ForjaSecondaryButton(
                    text = "Voltar",
                    onClick = {
                        step--
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "onboarding_back"
                )
            }

            ForjaPrimaryButton(
                text = if (step == 3) "FINALIZAR E FORJAR" else "CONTINUAR",
                onClick = {
                    if (step == 1) {
                        if (name.isBlank()) {
                            errorMessage = "Por favor, digite seu nome."
                            return@ForjaPrimaryButton
                        }
                        step = 2
                        errorMessage = null
                    } else if (step == 2) {
                        val height = heightText.toFloatOrNull()
                        val weight = currentWeightText.toFloatOrNull()
                        val target = targetWeightText.toFloatOrNull()
                        if (height == null || weight == null || target == null) {
                            errorMessage = "Preencha valores numéricos válidos para altura e pesos."
                            return@ForjaPrimaryButton
                        }
                        step = 3
                        errorMessage = null
                    } else if (step == 3) {
                        viewModel.completeOnboarding(
                            name = name,
                            age = ageText.toIntOrNull() ?: 25,
                            gender = selectedGender,
                            heightCm = heightText.toFloatOrNull() ?: 175f,
                            currentWeightKg = currentWeightText.toFloatOrNull() ?: 80f,
                            targetWeightKg = targetWeightText.toFloatOrNull() ?: 75f,
                            mainGoal = selectedGoal,
                            trainingLevel = selectedLevel,
                            availableDays = selectedDays
                        )
                    }
                },
                modifier = Modifier.weight(if (step > 1) 2f else 1f),
                testTag = "onboarding_next"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun onboardingTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    focusedLabelColor = ForgeOrange,
    unfocusedLabelColor = TextSecondaryDark,
    cursorColor = ForgeOrange
)
