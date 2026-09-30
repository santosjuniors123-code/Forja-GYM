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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
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
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeYellow
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun RecordsScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    val prs by viewModel.personalRecords.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("records_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ForgeYellow.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ForgeYellow, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MEUS RECORDES (PR)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Suas maiores cargas conquistadas na Forja",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (prs.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum recorde registrado ainda",
                    description = "Conclua séries com carga nos seus treinos para que o ForjaGym detecte seus recordes automaticamente.",
                    actionText = "Iniciar um Treino",
                    onAction = { onNavigate(Screen.WORKOUTS) }
                )
            }
        } else {
            items(prs) { pr ->
                ForjaCard(
                    backgroundColor = ForgeCardElevated,
                    borderColor = ForgeOrange.copy(alpha = 0.4f),
                    modifier = Modifier.padding(bottom = 10.dp),
                    testTag = "pr_card_${pr.exerciseId}"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF26180E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = pr.exerciseName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Batido em: ${pr.dateString}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${pr.maxWeightKg} kg",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = ForgeOrange
                            )
                            Text(
                                text = "${pr.repsAtMaxWeight} reps máximas",
                                fontSize = 11.sp,
                                color = TextSecondaryDark,
                                fontWeight = FontWeight.SemiBold
                            )
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
