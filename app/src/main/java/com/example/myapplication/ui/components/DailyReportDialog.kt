package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.MediFlowInnovations
import com.example.myapplication.core.StockAnalysis
import com.example.myapplication.core.TransferProposal

/**
 * Working Feature 14: Daily One-Click Printable Text Report Generator Dialog.
 */
@Composable
fun DailyReportDialog(
    analyses: List<StockAnalysis>,
    proposals: List<TransferProposal>,
    onDismiss: () -> Unit
) {
    val reportText = MediFlowInnovations.generateOneClickDailyReport(analyses, proposals)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Daily One-Click Summary Report (Working #14)")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = reportText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDismiss) {
                    Text("Copy / Print Report")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
