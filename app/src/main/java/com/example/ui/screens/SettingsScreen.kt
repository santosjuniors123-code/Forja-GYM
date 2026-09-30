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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.ForgeRed
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.ForjaViewModel

@Composable
fun SettingsScreen(
    viewModel: ForjaViewModel,
    onNavigate: (Screen) -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var useKg by remember { mutableStateOf(true) }
    var showFirebaseGuideDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val profile by viewModel.userProfile.collectAsState()

    // Firebase Config Guidance Dialog
    if (showFirebaseGuideDialog) {
        AlertDialog(
            onDismissRequest = { showFirebaseGuideDialog = false },
            containerColor = ForgeCard,
            title = {
                Text(
                    text = "Configuração do Firebase Cloud",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "O FORJAGYM já conta com arquitetura completa e pronta para Firebase Auth, Firestore e Storage.",
                        fontSize = 13.sp,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Para ativar o sincronismo em nuvem definitivo em produção:\n" +
                                "1. Crie um projeto no Firebase Console (firebase.google.com)\n" +
                                "2. Adicione o aplicativo Android com o pacote: com.aistudio.forjagym.xrtqvk\n" +
                                "3. Baixe o arquivo google-services.json e adicione-o na pasta /app do projeto\n" +
                                "4. O app detectará e ativará automaticamente o Firestore e Storage!",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFirebaseGuideDialog = false }) {
                    Text("Entendido", color = ForgeOrange, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = ForgeCard,
            title = { Text("Deseja sair da conta?", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
            text = { Text("Seus treinos gravados localmente no banco Room continuarão seguros.", color = TextSecondaryDark, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeRed),
                    modifier = Modifier.testTag("confirm_logout_btn")
                ) {
                    Text("Sair da Conta", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
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
            .testTag("settings_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CONFIGURAÇÕES",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Preferências do sistema, unidades, privacidade e status de sincronização",
                fontSize = 12.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Appearance & Preferences
            SectionHeader(title = "PREFERÊNCIAS")

            ForjaCard(backgroundColor = ForgeCard, borderColor = ForgeBorder) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tema Escuro Premium", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                        Text("Padrão Dark estilizado ForjaGym", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                    Switch(
                        checked = true,
                        onCheckedChange = null, // Fixed dark theme per design guidelines
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ForgeOrange)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Notificações & Avisos de Descanso", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                        Text("Vibração ao término do cronômetro", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ForgeOrange)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Unidade de Carga", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                        Text(if (useKg) "Quilogramas (kg)" else "Libras (lbs)", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                    Switch(
                        checked = useKg,
                        onCheckedChange = { useKg = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ForgeOrange)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Firebase Cloud Integration Section
            SectionHeader(title = "SINCRONISMO & NUVEM")

            ForjaCard(backgroundColor = ForgeCardElevated, borderColor = ForgeBorder) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = ForgeGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Banco Local Room Ativo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                        Text("Persistência offline instantânea habilitada", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ForjaOutlinedButton(
                    text = "INSTRUÇÕES PARA ATIVAR FIREBASE CLOUD",
                    onClick = { showFirebaseGuideDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Future Admin Module Section
            SectionHeader(title = "ADMINISTRAÇÃO")

            ForjaCard(backgroundColor = ForgeCard, borderColor = ForgeBorder) {
                Text(
                    text = "Módulo Preparado para Gestão",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryDark
                )
                Text(
                    text = "A arquitetura está pronta para painel administrativo, controle de planos, assinaturas e novos exercícios.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                ForjaOutlinedButton(
                    text = "VER PAINEL ADMINISTRATIVO (PREVIEW)",
                    onClick = { onNavigate(Screen.ADMIN) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Mode & Reset
            SectionHeader(title = "DADOS DE DEMONSTRAÇÃO")

            ForjaCard(backgroundColor = ForgeCard, borderColor = ForgeBorder) {
                Text(
                    text = "Recarregar Dados Demo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Popula o aplicativo com histórico completo de pesagens, treinos realizados, PRs e medidas para testes.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                ForjaPrimaryButton(
                    text = "CARREGAR DADOS DEMO",
                    onClick = {
                        viewModel.loadDemoMode()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A1515)),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("logout_button")
            ) {
                Text("SAIR DA CONTA", color = ForgeRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
