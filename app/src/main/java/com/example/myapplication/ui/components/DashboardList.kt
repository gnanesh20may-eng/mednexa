package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.StockAnalysis
import com.example.myapplication.core.StockStatus

/**
 * Dashboard listing all health centre medicine stock records with status indicators and live dispense controls.
 */
@Composable
fun DashboardList(
    analyses: List<StockAnalysis>,
    onDispenseStock: (centre: String, medicine: String, quantity: Int) -> Unit,
    onAddStock: (centre: String, medicine: String, quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (analyses.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No stock records match the selected filters.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        analyses.forEach { analysis ->
            StockCardItem(
                analysis = analysis,
                onDispenseStock = onDispenseStock,
                onAddStock = onAddStock
            )
        }
    }
}

@Composable
private fun StockCardItem(
    analysis: StockAnalysis,
    onDispenseStock: (centre: String, medicine: String, quantity: Int) -> Unit,
    onAddStock: (centre: String, medicine: String, quantity: Int) -> Unit
) {
    val rec = analysis.record

    val (statusText, statusBgColor, statusTextColor) = when (analysis.status) {
        StockStatus.AT_RISK -> Triple("AT RISK", Color(0xFFD32F2F), Color.White)
        StockStatus.SPEED_UP -> Triple("SPEED UP (2x)", Color(0xFFE65100), Color.White)
        StockStatus.ACCELERATING -> Triple("ACCELERATING", Color(0xFFF57C00), Color.White)
        StockStatus.OK -> Triple("OK", Color(0xFF388E3C), Color.White)
    }

    val daysText = if (analysis.daysToStockOut.isInfinite()) {
        "∞ Days"
    } else {
        "%.1f Days".format(analysis.daysToStockOut)
    }

    var showDispenseDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Centre, Medicine, and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rec.centre,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = rec.medicine,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECEFF1)
                    ) {
                        Text(
                            text = "${analysis.confidenceRating} Conf.",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = statusBgColor
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Metric: Units lost per day as main number, with stock count as secondary text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricTile(
                    label = "Burn Rate (Daily Use)",
                    value = "${analysis.burnRate} units/day",
                    secondaryText = "Stock: ${rec.stock} units",
                    modifier = Modifier.weight(1f)
                )

                MetricTile(
                    label = "Days Remaining",
                    value = daysText,
                    secondaryText = "Emergency Score: %.1f".format(analysis.emergencyScore),
                    highlightColor = when (analysis.status) {
                        StockStatus.AT_RISK -> Color(0xFFD32F2F)
                        StockStatus.SPEED_UP -> Color(0xFFE65100)
                        StockStatus.ACCELERATING -> Color(0xFFF57C00)
                        StockStatus.OK -> Color(0xFF2E7D32)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details: Unexpected Demand or Acceleration surge info or Shortfall/Surplus details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (analysis.isAtRisk && analysis.shortfall > 0) {
                    Text(
                        text = "Shortfall: ${analysis.shortfall} units needed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                } else if (analysis.surplus > 0) {
                    Text(
                        text = "Surplus: ${analysis.surplus} units available",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Buffer Stock Balanced",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (analysis.isUnexpectedDemand) {
                    Text(
                        text = "⚠️ Unexpected Demand Spike",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                } else if (analysis.isSpeedUp) {
                    Text(
                        text = "🚀 SPEED-UP: Doubled week-over-week",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold
                    )
                } else if (analysis.isAccelerating) {
                    Text(
                        text = "⚡ Surge: Prev 7d avg was ${rec.dailyUsePrior7}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF57C00),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Quick Dispense (-1 unit) and Manage Stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onDispenseStock(rec.centre, rec.medicine, 1) },
                    enabled = rec.stock > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dispense 1 Unit")
                }

                OutlinedButton(
                    onClick = { showDispenseDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Manage Stock")
                }
            }
        }
    }

    if (showDispenseDialog) {
        DispenseManageDialog(
            centre = rec.centre,
            medicine = rec.medicine,
            currentStock = rec.stock,
            onDispense = { qty ->
                onDispenseStock(rec.centre, rec.medicine, qty)
                showDispenseDialog = false
            },
            onAdd = { qty ->
                onAddStock(rec.centre, rec.medicine, qty)
                showDispenseDialog = false
            },
            onDismiss = { showDispenseDialog = false }
        )
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    secondaryText: String? = null,
    modifier: Modifier = Modifier,
    highlightColor: Color? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = highlightColor ?: MaterialTheme.colorScheme.onSurface
        )
        if (secondaryText != null) {
            Text(
                text = secondaryText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DispenseManageDialog(
    centre: String,
    medicine: String,
    currentStock: Int,
    onDispense: (Int) -> Unit,
    onAdd: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var quantityText by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Manage Stock: $medicine")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$centre • Current Stock: $currentStock units",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { newValue: String ->
                        quantityText = newValue.filter { char: Char -> char.isDigit() }
                    },
                    label = { Text(text = "Quantity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val qty = quantityText.toIntOrNull() ?: 0
                        if (qty > 0) onDispense(qty)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Dispense")
                }
                Button(
                    onClick = {
                        val qty = quantityText.toIntOrNull() ?: 0
                        if (qty > 0) onAdd(qty)
                    }
                ) {
                    Text("Add Stock")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
