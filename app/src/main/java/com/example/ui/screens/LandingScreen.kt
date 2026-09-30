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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ForjaCard
import com.example.ui.components.ForjaOutlinedButton
import com.example.ui.components.ForjaPrimaryButton
import com.example.ui.components.ForjaSecondaryButton
import com.example.ui.theme.ForgeBlack
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCardElevated
import com.example.ui.theme.ForgeGreen
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun LandingScreen(
    onStartNow: () -> Unit,
    onLogin: () -> Unit,
    onExploreDemo: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ForgeBlack)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp)
            .testTag("landing_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF24160E))
                .border(1.dp, ForgeOrange.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = null,
                tint = ForgeOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ALTA PERFORMANCE & PROGRESSÃO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeOrange,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ForgeOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "FORJA",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimaryDark,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "GYM",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = ForgeOrange,
                letterSpacing = 1.5.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Transforme seu treino em evolução.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "O aplicativo definitivo para atletas que levam a sério controle de carga, volume, hidratação, recordes e evolução corporal.",
            fontSize = 14.sp,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Actions
        ForjaPrimaryButton(
            text = "COMEÇAR AGORA",
            onClick = onStartNow,
            testTag = "landing_start_button"
        )

        Spacer(modifier = Modifier.height(10.dp))

        ForjaOutlinedButton(
            text = "JÁ TENHO UMA CONTA",
            onClick = onLogin,
            testTag = "landing_login_button"
        )

        Spacer(modifier = Modifier.height(10.dp))

        ForjaSecondaryButton(
            text = "⚡ TESTAR MODO DEMO COM DADOS REAIS",
            onClick = onExploreDemo,
            testTag = "landing_demo_button"
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Features Grid
        Text(
            text = "RECURSOS FORJADOS PARA O SUCESSO",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondaryDark,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(14.dp))

        FeatureCard(
            icon = Icons.Default.FitnessCenter,
            title = "Controle Rigoroso de Cargas",
            description = "Registre séries, repetições, peso real e compare com a última carga realizada instantaneamente."
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureCard(
            icon = Icons.Default.LocalFireDepartment,
            title = "Detecção Automática de PR",
            description = "Bata seus recordes e receba alertas imediatos de superação de carga e força máxima."
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureCard(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            title = "Evolução Corporal & Medidas",
            description = "Acompanhe peso, circunferências musculares e fotos Antes/Depois com segurança e privacidade."
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureCard(
            icon = Icons.Default.WaterDrop,
            title = "Hidratação & Metas Diárias",
            description = "Controle de ingestão com botões rápidos de 250ml a 1000ml e histórico diário."
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureCard(
            icon = Icons.Default.Shield,
            title = "60+ Exercícios com Alternativas",
            description = "Instruções biomecânicas corretas, erros comuns e substituição inteligente se faltar equipamento."
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    ForjaCard(
        backgroundColor = ForgeCardElevated,
        borderColor = ForgeBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF26180E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ForgeOrange,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
