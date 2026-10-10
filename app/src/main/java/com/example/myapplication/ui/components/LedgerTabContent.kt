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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.myapplication.core.LedgerEntry
import com.example.myapplication.core.LedgerEventType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ledger tab content displaying full transaction audit trail and "Why did this centre run out?" 30-day investigation view.
 */
@Composable
fun LedgerTabContent(
    ledgerEntries: List<LedgerEntry>,
    audit30DayEntries: List<LedgerEntry>,
    centres: List<String>,
    medicines: List<String>,
    selectedAuditCentre: String,
    selectedAuditMedicine: String,
    onAuditFilterChanged: (centre: String, medicine: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: "Why did this centre run out?" 30-day view
        WhyDidItRunOutCard(
            audit30DayEntries = audit30DayEntries,
            centres = centres,
            medicines = medicines,
            selectedCentre = selectedAuditCentre,
            selectedMedicine = selectedAuditMedicine,
            onFilterChanged = onAuditFilterChanged
        )

        // Section 2: Full Audit Ledger
        FullAuditLedgerCard(ledgerEntries = ledgerEntries)
    }
}

@Composable
private fun WhyDidItRunOutCard(
    audit30DayEntries: List<LedgerEntry>,
    centres: List<String>,
    medicines: List<String>,
    selectedCentre: String,
    selectedMedicine: String,
    onFilterChanged: (centre: String, medicine: String) -> Unit
) {
    var centreDropdownExpanded by remember { mutableStateOf(false) }
    var medicineDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔍", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Why did this centre run out?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )
            }
            Text(
                text = "Inspect all dispenses and stock transfers over the last 30 days.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown filters for audit inspection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { centreDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedCentre, maxLines = 1)
                    }
                    DropdownMenu(
                        expanded = centreDropdownExpanded,
                        onDismissRequest = { centreDropdownExpanded = false }
                    ) {
                        centres.forEach { centre ->
                            DropdownMenuItem(
                                text = { Text(centre) },
                                onClick = {
                                    onFilterChanged(centre, selectedMedicine)
                                    centreDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { medicineDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedMedicine, maxLines = 1)
                    }
                    DropdownMenu(
                        expanded = medicineDropdownExpanded,
                        onDismissRequest = { medicineDropdownExpanded = false }
                    ) {
                        medicines.forEach { medicine ->
                            DropdownMenuItem(
                                text = { Text(medicine) },
                                onClick = {
                                    onFilterChanged(selectedCentre, medicine)
                                    medicineDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (audit30DayEntries.isEmpty()) {
                Text(
                    text = "No dispenses or transfers recorded for $selectedMedicine at $selectedCentre in the last 30 days.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    audit30DayEntries.forEach { entry ->
                        LedgerItemCard(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun FullAuditLedgerCard(ledgerEntries: List<LedgerEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📜", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transaction Audit Ledger",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${ledgerEntries.size} Entries",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (ledgerEntries.isEmpty()) {
                Text(
                    text = "No transactions logged yet. Dispense stock or approve transfers to record transactions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ledgerEntries.forEach { entry ->
                        LedgerItemCard(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun LedgerItemCard(entry: LedgerEntry) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val timeFormatted = dateFormat.format(Date(entry.timestamp))

    val (typeLabel, badgeBg, badgeText) = when (entry.eventType) {
        LedgerEventType.APPROVED_TRANSFER -> Triple("TRANSFER", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        LedgerEventType.DISPENSE -> Triple("DISPENSE", Color(0xFFFFEBEE), Color(0xFFC62828))
        LedgerEventType.REPLENISHMENT -> Triple("REPLENISHMENT", Color(0xFFE3F2FD), Color(0xFF1565C0))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = typeLabel,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeText,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${entry.medicine} • ${entry.quantity} units",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            when (entry.eventType) {
                LedgerEventType.APPROVED_TRANSFER -> {
                    Text(
                        text = "Sender: ${entry.senderCentre} ➔ Receiver: ${entry.receiverCentre}" +
                                if (entry.distanceKm != null && entry.distanceKm > 0) " (${entry.distanceKm} km)" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
                LedgerEventType.DISPENSE -> {
                    Text(
                        text = "Dispensed at: ${entry.centre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
                LedgerEventType.REPLENISHMENT -> {
                    Text(
                        text = "Replenished stock at: ${entry.centre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Text(
                text = "Logged by: ${entry.actor}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
