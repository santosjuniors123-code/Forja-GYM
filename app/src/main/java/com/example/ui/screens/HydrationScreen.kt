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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBlue
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun HydrationScreen(
    viewModel: ForjaViewModel
) {
    val todayRecord by viewModel.todayHydration.collectAsState()
    val recentHydration by viewModel.recentHydration.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val consumed = todayRecord?.consumedMl ?: 0
    val target = todayRecord?.targetMl ?: (profile?.dailyWaterGoalMl ?: 3000)
    val percentage = if (target > 0) ((consumed.toFloat() / target.toFloat()) * 100).toInt() else 0
    val progress = (consumed.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("hydration_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CONTROLE DE HIDRATAÇÃO",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Água é combustível para hipertrofia, recuperação e síntese proteica",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Hydration Circle Card
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeBlue.copy(alpha = 0.6f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(ForgeBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(130.dp),
                            color = ForgeBlue,
                            trackColor = ForgeBorder,
                            strokeWidth = 10.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = ForgeBlue, modifier = Modifier.size(28.dp))
                            Text(
                                text = "$percentage%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$consumed ml consumidos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Meta diária: $target ml",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )

                    if (consumed >= target) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🎉 Meta batida hoje! Corpo 100% hidratado.",
                            color = ForgeGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ADICIONAR ÁGUA RAPIDAMENTE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Buttons: +250ml, +500ml, +750ml, +1000ml
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(250, 500, 750, 1000).forEach { ml ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ForgeCard)
                            .border(1.dp, ForgeBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.addWater(ml) }
                        .padding(vertical = 14.dp)
                        .testTag("add_water_${ml}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForgeBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "+$ml",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TextPrimaryDark
                            )
                            Text(text = "ml", fontSize = 10.sp, color = TextSecondaryDark)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HISTÓRICO RECENTE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryDark,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (recentHydration.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhum histórico registrado",
                    description = "Seus dias anteriores de ingestão hídrica aparecerão aqui."
                )
            }
        } else {
            items(recentHydration) { record ->
                val dayProgress = if (record.targetMl > 0) (record.consumedMl.toFloat() / record.targetMl.toFloat()).coerceIn(0f, 1f) else 0f
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
                        Column {
                            Text(text = record.dateString, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimaryDark)
                            Text(text = "${record.consumedMl} / ${record.targetMl} ml", fontSize = 12.sp, color = TextSecondaryDark)
                        }
                        Text(
                            text = "${(dayProgress * 100).toInt()}%",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (dayProgress >= 1f) ForgeGreen else ForgeBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { dayProgress },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = if (dayProgress >= 1f) ForgeGreen else ForgeBlue,
                        trackColor = ForgeBorder
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
