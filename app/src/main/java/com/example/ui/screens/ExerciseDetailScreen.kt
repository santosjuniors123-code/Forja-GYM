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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.ForgeRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun ExerciseDetailScreen(
    viewModel: ForjaViewModel
) {
    val exercise by viewModel.selectedExercise.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    if (exercise == null) {
        EmptyState(
            title = "Nenhum exercício selecionado",
            description = "Retorne à biblioteca e selecione um exercício para ver os detalhes."
        )
        return
    }

    val ex = exercise!!
    val altIds = ex.alternativeIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val alternatives = allExercises.filter { altIds.contains(it.id) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("exercise_detail_screen")
    ) {
        // Header card with Media Placeholder
        ForjaCard(
            backgroundColor = ForgeCardElevated,
            borderColor = ForgeBorder
        ) {
            // Media placeholder banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E26)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ForgeOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.OndemandVideo,
                            contentDescription = null,
                            tint = ForgeOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "VÍDEO / DEMONSTRAÇÃO 3D",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeOrange,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = ex.mediaPlaceholderLabel,
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ex.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${ex.muscleGroup} • Equipamento: ${ex.equipment} • Nível: ${ex.level}",
                        fontSize = 13.sp,
                        color = ForgeOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = { viewModel.toggleFavorite(ex.id, ex.isFavorite) }) {
                    Icon(
                        imageVector = if (ex.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (ex.isFavorite) Color(0xFFEF4444) else TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Presets metrics: Sets, Reps, Rest
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(label = "Séries Padrão", value = "${ex.defaultSets} séries", icon = Icons.Default.Bookmark, modifier = Modifier.weight(1f))
                MetricPill(label = "Reps Padrão", value = "${ex.defaultReps} reps", icon = Icons.Default.Repeat, modifier = Modifier.weight(1f))
                MetricPill(label = "Descanso", value = "${ex.defaultRestSeconds}s", icon = Icons.Default.Timer, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preparation
        InstructionBox(
            title = "PREPARAÇÃO & POSTURA INICIAL",
            description = ex.preparation,
            accentColor = ForgeOrange,
            icon = Icons.Default.FitnessCenter
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Execution
        InstructionBox(
            title = "EXECUÇÃO CORRETA PASSO A PASSO",
            description = ex.execution,
            accentColor = ForgeGreen,
            icon = Icons.Default.CheckCircle
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Common Mistakes
        InstructionBox(
            title = "ERROS COMUNS A EVITAR",
            description = ex.commonMistakes,
            accentColor = ForgeRed,
            icon = Icons.Default.Warning
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pro Tips
        InstructionBox(
            title = "DICAS PRO DA FORJA",
            description = ex.tips,
            accentColor = Color(0xFFF59E0B),
            icon = Icons.Default.Lightbulb
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Alternatives Section
        SectionHeader(title = "EXERCÍCIOS ALTERNATIVOS")

        if (alternatives.isEmpty()) {
            ForjaCard(backgroundColor = ForgeCard) {
                Text(
                    text = "Nenhuma alternativa cadastrada para este exercício.",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
            }
        } else {
            alternatives.forEach { alt ->
                ForjaCard(
                    backgroundColor = ForgeCard,
                    borderColor = ForgeBorder,
                    modifier = Modifier.padding(bottom = 8.dp),
                    onClick = { viewModel.selectExercise(alt) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = alt.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "${alt.muscleGroup} • ${alt.equipment}",
                                fontSize = 12.sp,
                                color = TextSecondaryDark
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Ver alternativa",
                            tint = ForgeOrange
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF14141A))
            .border(1.dp, ForgeBorder, RoundedCornerShape(10.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
            Text(text = label, fontSize = 10.sp, color = TextSecondaryDark)
        }
    }
}

@Composable
private fun InstructionBox(
    title: String,
    description: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ForjaCard(
        backgroundColor = ForgeCard,
        borderColor = accentColor.copy(alpha = 0.3f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            color = TextPrimaryDark,
            lineHeight = 19.sp
        )
    }
}
