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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.example.data.utils.FormatUtils
import com.example.ui.components.ForjaCard
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeYellow
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun CalculatorsScreen(
    viewModel: ForjaViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 1RM, 1: IMC, 2: TMB & Gasto

    // 1RM Inputs
    var rmWeight by remember { mutableStateOf("100") }
    var rmReps by remember { mutableStateOf("8") }

    // IMC Inputs
    var imcWeight by remember(profile) { mutableStateOf((profile?.currentWeightKg ?: 80f).toString()) }
    var imcHeight by remember(profile) { mutableStateOf((profile?.heightCm ?: 178f).toString()) }

    // TMB Inputs
    var tmbWeight by remember(profile) { mutableStateOf((profile?.currentWeightKg ?: 80f).toString()) }
    var tmbHeight by remember(profile) { mutableStateOf((profile?.heightCm ?: 178f).toString()) }
    var tmbAge by remember(profile) { mutableStateOf((profile?.age ?: 26).toString()) }
    var isMale by remember(profile) { mutableStateOf(profile?.gender != "Feminino") }
    var trainingDays by remember(profile) { mutableIntStateOf(profile?.availableDays ?: 5) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("calculators_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CALCULADORAS DA FORJA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Ferramentas científicas e estimativas biométricas para potencializar seu treino",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ForgeCard,
                contentColor = ForgeOrange,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("1RM Máx", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("IMC", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("TMB / TDEE", fontWeight = FontWeight.Bold) })
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        when (selectedTab) {
            // 1RM Epley
            0 -> {
                item {
                    val weight = rmWeight.toFloatOrNull() ?: 0f
                    val reps = rmReps.toIntOrNull() ?: 1
                    val calculated1RM = FormatUtils.calculate1RMEpley(weight, reps)

                    ForjaCard(
                        backgroundColor = ForgeCardElevated,
                        borderColor = ForgeOrange.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "ESTIMATIVA DE 1RM (FÓRMULA DE EPLEY)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Fórmula: 1RM = Carga × (1 + Reps / 30). Permite calcular sua força máxima de 1 repetição com segurança sem risco de lesão.",
                            fontSize = 12.sp,
                            color = TextSecondaryDark,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = rmWeight,
                                onValueChange = { rmWeight = it },
                                label = { Text("Carga realizada (kg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                            OutlinedTextField(
                                value = rmReps,
                                onValueChange = { rmReps = it },
                                label = { Text("Repetições feitas") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF26180E))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("SUA 1RM ESTIMADA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                                Text(
                                    text = "${String.format("%.1f", calculated1RM)} kg",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimaryDark
                                )
                                Text("Estimativa aproximada para levantamento único", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Percentages Breakdown
                        Text("TABELA DE INTENSIDADE DA CARGA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(95 to "1-2 reps (Força)", 90 to "3-4 reps (Força)", 80 to "8-10 reps (Hipertrofia)", 70 to "12-15 reps (Resistência)").forEach { (pct, label) ->
                            val load = calculated1RM * (pct / 100f)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$pct% • $label", fontSize = 12.sp, color = TextSecondaryDark)
                                Text("${String.format("%.1f", load)} kg", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                            }
                        }
                    }
                }
            }

            // IMC
            1 -> {
                item {
                    val w = imcWeight.toFloatOrNull() ?: 0f
                    val h = imcHeight.toFloatOrNull() ?: 0f
                    val imc = FormatUtils.calculateBMI(w, h)

                    val (classification, color) = when {
                        imc < 18.5f -> "Abaixo do peso" to ForgeYellow
                        imc < 25f -> "Peso normal / Eutrófico" to ForgeGreen
                        imc < 30f -> "Sobrepeso (atenção à massa muscular)" to ForgeOrange
                        else -> "Obesidade (atenção à massa muscular)" to Color(0xFFEF4444)
                    }

                    ForjaCard(
                        backgroundColor = ForgeCardElevated,
                        borderColor = ForgeBorder
                    ) {
                        Text(
                            text = "ÍNDICE DE MASSA CORPORAL (IMC)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aviso: O IMC não diferencia massa muscular de gordura. Para atletas de musculação com alta massa magra, o valor pode superestimar sobrepeso.",
                            fontSize = 11.sp,
                            color = TextSecondaryDark,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = imcWeight,
                                onValueChange = { imcWeight = it },
                                label = { Text("Peso (kg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                            OutlinedTextField(
                                value = imcHeight,
                                onValueChange = { imcHeight = it },
                                label = { Text("Altura (cm)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ForgeCard)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("RESULTADO DO IMC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                                Text(
                                    text = String.format("%.1f", imc),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = classification,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                        }
                    }
                }
            }

            // TMB & TDEE
            2 -> {
                item {
                    val w = tmbWeight.toFloatOrNull() ?: 0f
                    val h = tmbHeight.toFloatOrNull() ?: 0f
                    val age = tmbAge.toIntOrNull() ?: 25
                    val bmr = FormatUtils.calculateBMR(w, h, age, isMale)
                    val tdee = FormatUtils.calculateTDEE(bmr, trainingDays)

                    ForjaCard(
                        backgroundColor = ForgeCardElevated,
                        borderColor = ForgeBorder
                    ) {
                        Text(
                            text = "METABOLISMO BASAL (TMB) & GASTO DIÁRIO (TDEE)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fórmula científica de Mifflin-St Jeor. Informamos claramente que estes valores são estimativas metabólicas.",
                            fontSize = 11.sp,
                            color = TextSecondaryDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = tmbWeight,
                                onValueChange = { tmbWeight = it },
                                label = { Text("Peso (kg)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                            OutlinedTextField(
                                value = tmbHeight,
                                onValueChange = { tmbHeight = it },
                                label = { Text("Altura (cm)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                            OutlinedTextField(
                                value = tmbAge,
                                onValueChange = { tmbAge = it },
                                label = { Text("Idade") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = calcTextFieldColors()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ForgeCard)
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("TMB (REPOUSO)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                                    Text("${bmr.toInt()} kcal", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimaryDark)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF26180E))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("GASTO DIÁRIO TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                                    Text("${tdee.toInt()} kcal", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("RECOMENDAÇÕES PARA SEU OBJETIVO:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Hipertrofia (Superávit +300kcal): ${(tdee + 300).toInt()} kcal/dia", fontSize = 12.sp, color = ForgeGreen)
                        Text("• Manutenção (Equilíbrio calórico): ${tdee.toInt()} kcal/dia", fontSize = 12.sp, color = TextPrimaryDark)
                        Text("• Emagrecimento (Déficit -400kcal): ${(tdee - 400).coerceAtLeast(1200f).toInt()} kcal/dia", fontSize = 12.sp, color = ForgeOrange)
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
private fun calcTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ForgeOrange,
    unfocusedBorderColor = ForgeBorder,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    cursorColor = ForgeOrange
)
