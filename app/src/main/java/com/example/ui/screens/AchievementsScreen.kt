package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
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
fun AchievementsScreen(
    viewModel: ForjaViewModel
) {
    val achievements by viewModel.achievements.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val unlockedCount = achievements.count { it.isUnlocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("achievements_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CONQUISTAS DA FORJA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "$unlockedCount de ${achievements.size} medalhas desbloqueadas",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Streak card
            ForjaCard(
                backgroundColor = ForgeCardElevated,
                borderColor = ForgeOrange.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E190E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = ForgeOrange,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "SEQUÊNCIA DE FOGO (STREAK)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeOrange,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${profile?.currentStreak ?: 0}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = " dias consecutivos",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }
                        Text(
                            text = "Recorde histórico: ${profile?.bestStreak ?: 0} dias",
                            fontSize = 12.sp,
                            color = ForgeYellow
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        items(achievements) { ach ->
            val isUnlocked = ach.isUnlocked

            ForjaCard(
                backgroundColor = if (isUnlocked) Color(0xFF1B1B24) else ForgeCard,
                borderColor = if (isUnlocked) ForgeOrange.copy(alpha = 0.5f) else ForgeBorder,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) ForgeOrange.copy(alpha = 0.2f) else ForgeBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isUnlocked) ForgeYellow else TextSecondaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ach.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isUnlocked) TextPrimaryDark else TextSecondaryDark
                            )
                            if (isUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF132219))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("DESBLOQUEADO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ForgeGreen)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = ach.description,
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                        if (ach.unlockedDate != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Conquistado em: ${ach.unlockedDate}",
                                fontSize = 10.sp,
                                color = ForgeOrange
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
