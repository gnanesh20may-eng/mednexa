package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.StockStatus
import com.example.myapplication.ui.theme.NotoSansTamilFontFamily
import com.example.myapplication.ui.theme.StatusAmber
import com.example.myapplication.ui.theme.StatusGreen
import com.example.myapplication.ui.theme.StatusRed

private data class StatusInfo(
    val icon: ImageVector,
    val textEn: String,
    val textTa: String,
    val color: Color
)

/**
 * Accessible Status Badge Component: Always combines Icon + Color + Text (English and Tamil).
 * Adheres to Step 2 accessibility guidelines (never color alone).
 */
@Composable
fun StatusBadge(
    status: StockStatus,
    modifier: Modifier = Modifier,
    showTamilText: Boolean = false
) {
    val info = when (status) {
        StockStatus.AT_RISK -> StatusInfo(
            Icons.Default.Warning,
            "AT RISK",
            "அபாயம்",
            StatusRed
        )
        StockStatus.SPEED_UP -> StatusInfo(
            Icons.Default.Info,
            "SPEED UP (2x)",
            "வேக அதிகரிப்பு",
            StatusAmber
        )
        StockStatus.ACCELERATING -> StatusInfo(
            Icons.Default.Info,
            "ACCELERATING",
            "அதிகரிப்பு",
            StatusAmber
        )
        StockStatus.OK -> StatusInfo(
            Icons.Default.CheckCircle,
            "OK",
            "சரியானது",
            StatusGreen
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = info.color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = info.icon,
                contentDescription = info.textEn,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (showTamilText) "${info.textEn} (${info.textTa})" else info.textEn,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = NotoSansTamilFontFamily,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
