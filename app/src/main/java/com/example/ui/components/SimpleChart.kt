package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeBorder
import com.example.ui.theme.ForgeCard
import com.example.ui.theme.ForgeOrange
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun SimpleBarChart(
    data: List<Pair<String, Float>>,
    accentColor: Color = ForgeOrange,
    modifier: Modifier = Modifier,
    barUnit: String = ""
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(ForgeCard, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Sem registros suficientes para gráfico", color = TextSecondaryDark, fontSize = 12.sp)
        }
        return
    }

    val maxVal = (data.maxOfOrNull { it.second } ?: 1f).coerceAtLeast(1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ForgeCard, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barCount = data.size
            val barSpacing = 12f
            val totalSpacing = barSpacing * (barCount - 1)
            val barWidth = ((canvasWidth - totalSpacing) / barCount).coerceAtLeast(10f)

            data.forEachIndexed { index, pair ->
                val barHeight = ((pair.second / maxVal) * (canvasHeight - 20f)).coerceAtLeast(8f)
                val x = index * (barWidth + barSpacing)
                val y = canvasHeight - barHeight

                // Draw background bar track
                drawRoundRect(
                    color = ForgeBorder,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, canvasHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Draw filled bar
                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { pair ->
                Text(
                    text = pair.first,
                    fontSize = 10.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SimpleLineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = ForgeOrange
) {
    if (points.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(ForgeCard, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Registre mais pontos para visualizar a linha", color = TextSecondaryDark, fontSize = 12.sp)
        }
        return
    }

    val minVal = points.minOrNull() ?: 0f
    val maxVal = points.maxOrNull() ?: 100f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (points.size - 1)

        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = (value - minVal) / range
            val y = height - (normalizedY * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw dots on each point
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val normalizedY = (value - minVal) / range
            val y = height - (normalizedY * height)
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = lineColor,
                radius = 2.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
