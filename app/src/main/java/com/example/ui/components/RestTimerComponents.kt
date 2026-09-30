package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.utils.FormatUtils
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun RestTimerSection(
    secondsRemaining: Int,
    isRunning: Boolean,
    onStartTimer: (Int) -> Unit,
    onStopTimer: () -> Unit,
    onAdjustTimer: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ForgeCardElevated)
            .border(1.dp, if (isRunning) ForgeOrange else ForgeBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("rest_timer_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) ForgeOrange.copy(alpha = 0.2f) else ForgeBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isRunning) ForgeOrange else TextSecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CRONÔMETRO DE DESCANSO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isRunning) "Descansando..." else "Selecione o intervalo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isRunning) ForgeGreen else TextPrimaryDark
                    )
                }
            }

            if (isRunning) {
                IconButton(
                    onClick = onStopTimer,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF331D1D))
                        .testTag("stop_rest_timer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancelar descanso",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = isRunning,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = FormatUtils.formatSecondsToTime(secondsRemaining),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = ForgeOrange,
                    modifier = Modifier.testTag("rest_timer_digits")
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (secondsRemaining.toFloat() / 180f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ForgeOrange,
                    trackColor = ForgeBorder
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onAdjustTimer(-15) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ForgeBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "-15s", tint = TextPrimaryDark)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Ajustar ±15s",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = { onAdjustTimer(15) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ForgeBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "+15s", tint = TextPrimaryDark)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Preset buttons: 30s, 45s, 60s, 90s, 120s, 180s
        Text(
            text = "Intervalos Rápidos",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val presets = listOf(30, 45, 60, 90, 120, 180)
            presets.forEach { seconds ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRunning && secondsRemaining == seconds) ForgeOrange else ForgeBorder)
                        .clickable { onStartTimer(seconds) }
                        .padding(vertical = 8.dp)
                        .testTag("preset_rest_${seconds}s"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${seconds}s",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRunning && secondsRemaining == seconds) Color.White else TextPrimaryDark
                    )
                }
            }
        }
    }
}
