package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import com.example.myapplication.core.MediFlowInnovations
import com.example.myapplication.core.QrMedicineData

/**
 * Interactive UI panel showcasing SIMULATED features (items 18–26) with working simulated controls.
 */
@Composable
fun SimulatedFeaturesPanel(
    modifier: Modifier = Modifier
) {
    var seasonMultiplier by remember { mutableStateOf(1.5f) }
    var qrInput by remember { mutableStateOf("Paracetamol|BATCH456|2026-11-30|100") }
    var parsedQrData by remember { mutableStateOf<QrMedicineData?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        shape = RoundedCornerShape(12.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🧪", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simulated Features Suite (18–26)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF57C00)
                ) {
                    Text(
                        text = "SIMULATED UI",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Feature 18: Seasonal Forecast Slider
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Feature 18: Seasonal Disease Forecast (Monsoon/Fever Multiplier: %.1fx)".format(seasonMultiplier),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE65100)
                )
                Slider(
                    value = seasonMultiplier,
                    onValueChange = { seasonMultiplier = it },
                    valueRange = 1.0f..3.0f
                )
            }

            // Feature 25: Simulated Sync Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Feature 25: Offline Local DB Sync Status",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "🟢 Synced (Offline First)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }

            // Feature 26: Barcode/QR Parsing
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Feature 26: Barcode / QR Code Parsing Entry",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = qrInput,
                    onValueChange = { newValue: String -> qrInput = newValue },
                    label = { Text(text = "QR Code String") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        parsedQrData = MediFlowInnovations.parseBarcodeQrCode(qrInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Parse QR Code Batch")
                }

                if (parsedQrData != null) {
                    Text(
                        text = "Parsed: ${parsedQrData?.medicine} • Batch: ${parsedQrData?.batchNumber} • Expiry: ${parsedQrData?.expiryDate} • Qty: ${parsedQrData?.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
