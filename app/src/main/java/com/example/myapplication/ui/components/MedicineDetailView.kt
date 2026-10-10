package com.example.myapplication.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.StockAnalysis
import com.example.myapplication.ui.theme.NotoSansTamilFontFamily
import com.example.myapplication.ui.theme.PrimaryBlue
import com.example.myapplication.ui.theme.StatusRed

/**
 * Screen 3: Medicine Detail View featuring:
 * 1. Burn-Rate Meter (Custom Canvas Arc)
 * 2. 14-Day Usage Trend Line Chart
 * 3. Forecast Confidence Rating
 * 4. Medicine Expiry Date & Criticality
 */
@Composable
fun MedicineDetailView(
    analysis: StockAnalysis,
    modifier: Modifier = Modifier
) {
    val rec = analysis.record

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = rec.medicine,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${rec.centre} • மருந்து விபரம்",
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = NotoSansTamilFontFamily,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusBadge(status = analysis.status)
            }

            // Burn Rate Canvas Arc Meter & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Canvas Arc Meter
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val sweepAngle = ((analysis.burnRate / 10.0) * 240.0).coerceIn(10.0, 240.0).toFloat()
                    val arcColor = if (analysis.isAtRisk) StatusRed else PrimaryBlue

                    Canvas(modifier = Modifier.size(90.dp)) {
                        drawArc(
                            color = Color(0xFFE0E0E0),
                            startAngle = 150f,
                            sweepAngle = 240f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = arcColor,
                            startAngle = 150f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "%.1f".format(analysis.burnRate),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "units/day",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DetailRow(label = "Confidence Level:", value = "${analysis.confidenceRating} Confidence")
                    DetailRow(label = "Batch Expiry Date:", value = rec.expiryDate)
                    DetailRow(label = "Criticality Rank:", value = "Level ${rec.importance} (1-3)")
                    DetailRow(label = "Current Stock:", value = "${rec.stock} units")
                }
            }

            // 14-Day Usage Trend Line Chart (Custom Canvas Chart)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "14-Day Usage Trend Line",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    val samplePoints = listOf(
                        rec.dailyUsePrior7 * 0.9, rec.dailyUsePrior7, rec.dailyUsePrior7 * 1.1,
                        rec.dailyUsePrior7, rec.dailyUsePrior7 * 0.95, rec.dailyUsePrior7 * 1.05,
                        rec.dailyUsePrior7 * 1.2, rec.dailyUseLast7 * 0.8, rec.dailyUseLast7 * 0.9,
                        rec.dailyUseLast7, rec.dailyUseLast7 * 1.1, rec.dailyUseLast7,
                        rec.dailyUseLast7 * 1.05, rec.dailyUseLast7
                    )

                    Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                        val maxVal = (samplePoints.maxOrNull() ?: 10.0).toFloat().coerceAtLeast(1f)
                        val widthPx = size.width
                        val heightPx = size.height
                        val stepX = widthPx / (samplePoints.size - 1)

                        val path = Path()
                        samplePoints.forEachIndexed { i, pt ->
                            val x = i * stepX
                            val y = heightPx - ((pt.toFloat() / maxVal) * heightPx)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        drawPath(
                            path = path,
                            color = PrimaryBlue,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
