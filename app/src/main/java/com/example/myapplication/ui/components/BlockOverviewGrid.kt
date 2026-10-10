package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.StockAnalysis
import com.example.myapplication.core.StockStatus
import com.example.myapplication.ui.theme.NotoSansTamilFontFamily
import com.example.myapplication.ui.theme.StatusAmber
import com.example.myapplication.ui.theme.StatusGreen
import com.example.myapplication.ui.theme.StatusRed

/**
 * Screen 6: Block Officer Supervisory Grid View across all 4 PHCs.
 * Shows status aggregated colours and at-risk count per health centre.
 */
@Composable
fun BlockOverviewGrid(
    analyses: List<StockAnalysis>,
    modifier: Modifier = Modifier
) {
    val centres = analyses.map { it.record.centre }.distinct()

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Block Health Centre Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "வட்டார சுகாதார நிலையங்கள்",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = NotoSansTamilFontFamily,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF57C00)
                ) {
                    Text(
                        text = "SIMULATED VIEW",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                centres.forEach { centreName ->
                    val centreAnalyses = analyses.filter { it.record.centre == centreName }
                    val redCount = centreAnalyses.count { it.status == StockStatus.AT_RISK }
                    val amberCount = centreAnalyses.count { it.status == StockStatus.SPEED_UP || it.status == StockStatus.ACCELERATING }
                    val greenCount = centreAnalyses.count { it.status == StockStatus.OK }

                    CentreOverviewRow(
                        centreName = centreName,
                        redCount = redCount,
                        amberCount = amberCount,
                        greenCount = greenCount
                    )
                }
            }
        }
    }
}

@Composable
private fun CentreOverviewRow(
    centreName: String,
    redCount: Int,
    amberCount: Int,
    greenCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (redCount > 0) Color(0xFFFFEBEE) else Color(0xFFFAFAFA)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = centreName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (redCount > 0) {
                    CountChip(count = redCount, label = "Risk", color = StatusRed)
                }
                if (amberCount > 0) {
                    CountChip(count = amberCount, label = "Surge", color = StatusAmber)
                }
                CountChip(count = greenCount, label = "OK", color = StatusGreen)
            }
        }
    }
}

@Composable
private fun CountChip(count: Int, label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color
    ) {
        Text(
            text = "$count $label",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
