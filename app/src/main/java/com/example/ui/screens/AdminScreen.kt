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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ForjaCard
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun AdminScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .padding(horizontal = 16.dp)
            .testTag("admin_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF26180E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = ForgeOrange)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "PAINEL ADMINISTRATIVO",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Arquitetura preparada para gestão comercial e de conteúdo",
                        fontSize = 12.sp,
                        color = TextSecondaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AdminModuleCard(
                icon = Icons.Default.FitnessCenter,
                title = "Gerenciamento de Exercícios & Vídeos",
                status = "Pronto para expansão",
                description = "Estrutura de dados preparada para inclusão de novos exercícios, mídias de vídeo, ângulos de câmera e alternativas personalizadas por academia."
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminModuleCard(
                icon = Icons.Default.Group,
                title = "Gestão de Usuários & Alunos",
                status = "Modelo de dados isolado",
                description = "Preparado para integração multi-tenant ou personal trainer: cada aluno com histórico, evolução e plano individualizado seguro no Firestore."
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminModuleCard(
                icon = Icons.Default.CardMembership,
                title = "Planos & Assinaturas Premium",
                status = "Fluxo preparado (sem cobrança ativa)",
                description = "Modelagem de planos PRO e VIP prevista para integração futura com Google Play Billing sem retrabalho de arquitetura."
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminModuleCard(
                icon = Icons.Default.Insights,
                title = "Estatísticas da Plataforma",
                status = "Métricas estruturadas",
                description = "Agregação de assiduidade, retenção de atletas e exercícios mais populares para otimização contínua da experiência."
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun AdminModuleCard(
    icon: ImageVector,
    title: String,
    status: String,
    description: String
) {
    ForjaCard(backgroundColor = ForgeCardElevated, borderColor = ForgeBorder) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ForgeCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = ForgeOrange, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimaryDark)
                    Text(status, fontSize = 11.sp, color = ForgeOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(description, fontSize = 12.sp, color = TextSecondaryDark, lineHeight = 18.sp)
    }
}
