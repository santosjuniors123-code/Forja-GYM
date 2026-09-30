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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.WorkoutPlanEntity
import com.example.data.local.entities.WorkoutScheduleEntity
import com.example.data.model.DayOfWeekPt
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.navigation.Screen
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.ForgeRed
import com.example.ui.theme.ForgeYellow
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun WeekScheduleScreen(
    viewModel: ForjaViewModel,
    onNavigateBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val weeklySchedule by viewModel.weeklySchedule.collectAsState()
    val workoutPlans by viewModel.workoutPlans.collectAsState()
    val todayDayNumber = remember { viewModel.getTodayDayOfWeek() }

    // Dialog state for day workout change
    var dayToConfigure by remember { mutableStateOf<WorkoutScheduleEntity?>(null) }
    // Dialog for moving workout or handling missed
    var dayToReschedule by remember { mutableStateOf<WorkoutScheduleEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("week_schedule_screen")
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
                        text = "MINHA SEMANA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Programação semanal de treinos e descansos",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1710))
                    .border(1.dp, ForgeOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "ORGANIZAÇÃO PERIODIZADA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeOrange,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Defina qual treino você fará em cada dia da semana. O app selecionará automaticamente o treino certo no Dashboard conforme o dia!",
                        fontSize = 12.sp,
                        color = TextPrimaryDark,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        val fullWeekList = (1..7).map { dayNum ->
            weeklySchedule.find { it.dayOfWeek == dayNum } ?: WorkoutScheduleEntity(
                dayOfWeek = dayNum,
                workoutId = null,
                workoutName = "Dia de Descanso",
                isRestDay = true,
                status = "DESCANSO"
            )
        }

        items(fullWeekList) { dayEntry ->
            val dayPt = DayOfWeekPt.fromDayNumber(dayEntry.dayOfWeek)
            val isToday = dayEntry.dayOfWeek == todayDayNumber

            ForjaCard(
                backgroundColor = if (isToday) ForgeCardElevated else ForgeCard,
                borderColor = if (isToday) ForgeOrange else ForgeBorder,
                modifier = Modifier.padding(bottom = 10.dp),
                testTag = "schedule_day_${dayEntry.dayOfWeek}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        // Day Tag badge
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isToday) ForgeOrange else Color(0xFF202029)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayPt.shortName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isToday) Color.White else TextPrimaryDark
                                )
                                if (isToday) {
                                    Text(
                                        text = "HOJE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = dayPt.fullName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) ForgeOrange else TextSecondaryDark,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(status = dayEntry.status, isRest = dayEntry.isRestDay)
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = if (dayEntry.isRestDay) "😴 DIA DE DESCANSO" else (dayEntry.workoutName ?: "Treino Programado"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dayEntry.isRestDay) TextSecondaryDark else TextPrimaryDark
                            )
                        }
                    }

                    // Action buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { dayToConfigure = dayEntry }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Alterar Treino",
                                tint = ForgeOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (!dayEntry.isRestDay) {
                            IconButton(
                                onClick = { dayToReschedule = dayEntry }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Mover ou Remanejar",
                                    tint = TextSecondaryDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal to configure / change workout for a specific day
    if (dayToConfigure != null) {
        val entry = dayToConfigure!!
        val dayPt = DayOfWeekPt.fromDayNumber(entry.dayOfWeek)

        AlertDialog(
            onDismissRequest = { dayToConfigure = null },
            containerColor = ForgeCardElevated,
            title = {
                Text(
                    text = "PROGRAMAR ${dayPt.fullName.uppercase()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Selecione o treino para este dia ou defina como descanso:",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )

                    // Option: Descanso
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (entry.isRestDay) Color(0xFF26180E) else Color(0xFF141419))
                            .clickable {
                                viewModel.setScheduleDay(entry.dayOfWeek, null, "Dia de Descanso", true)
                                dayToConfigure = null
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bed, contentDescription = null, tint = ForgeOrange)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("😴 Dia de Descanso", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                            Text("Sem treino programado", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                    }

                    // List of all user workouts
                    workoutPlans.forEach { plan ->
                        val isCurrent = entry.workoutId == plan.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) Color(0xFF26180E) else Color(0xFF141419))
                                .clickable {
                                    viewModel.setScheduleDay(entry.dayOfWeek, plan.id, plan.name, false)
                                    dayToConfigure = null
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ForgeOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(plan.code, fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                                Text(plan.targetMuscles, fontSize = 11.sp, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { dayToConfigure = null }) {
                    Text("Fechar", color = TextSecondaryDark)
                }
            }
        )
    }

    // Modal: Reschedule / Missed workout options
    if (dayToReschedule != null) {
        val entry = dayToReschedule!!
        val dayPt = DayOfWeekPt.fromDayNumber(entry.dayOfWeek)
        var targetDayNumber by remember { mutableStateOf(if (entry.dayOfWeek < 7) entry.dayOfWeek + 1 else 1) }

        AlertDialog(
            onDismissRequest = { dayToReschedule = null },
            containerColor = ForgeCardElevated,
            title = {
                Text(
                    text = "REMANEJAR TREINO",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Opções para o treino de ${dayPt.fullName} (${entry.workoutName}):",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )

                    // Option: Mover para outro dia
                    Text("MOVER PARA OUTRO DIA:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeOrange)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..7).forEach { dNum ->
                            val dPt = DayOfWeekPt.fromDayNumber(dNum)
                            val isSel = targetDayNumber == dNum
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) ForgeOrange else Color(0xFF16161D))
                                    .clickable { targetDayNumber = dNum }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = dPt.shortName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextSecondaryDark
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.rescheduleDayWorkout(entry.dayOfWeek, targetDayNumber, "MOVE")
                            dayToReschedule = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeOrange),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Confirmar Mudança para ${DayOfWeekPt.fromDayNumber(targetDayNumber).shortName}")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Option: Marcar como perdido
                    Button(
                        onClick = {
                            viewModel.rescheduleDayWorkout(entry.dayOfWeek, targetDayNumber, "MARK_LOST")
                            dayToReschedule = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeRed),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Marcar como Perdido")
                    }

                    // Option: Pular treino (vira descanso)
                    TextButton(
                        onClick = {
                            viewModel.rescheduleDayWorkout(entry.dayOfWeek, targetDayNumber, "SKIP")
                            dayToReschedule = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pular e Marcar como Descanso", color = TextSecondaryDark)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { dayToReschedule = null }) {
                    Text("Cancelar", color = TextSecondaryDark)
                }
            }
        )
    }
}

@Composable
private fun StatusBadge(status: String, isRest: Boolean) {
    val (label, bg, fg) = when {
        isRest -> Triple("DESCANSO", Color(0xFF1A1A22), TextSecondaryDark)
        status == "REALIZADO" -> Triple("CONCLUÍDO", Color(0xFF122818), ForgeGreen)
        status == "PERDIDO" -> Triple("PERDIDO", Color(0xFF281313), ForgeRed)
        else -> Triple("PROGRAMADO", Color(0xFF26180E), ForgeOrange)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}
