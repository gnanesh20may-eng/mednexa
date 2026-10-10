package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.MediFlowInnovations

/**
 * Working Feature 15: Anonymous Shortage Reporting Form without patient data collection.
 */
@Composable
fun AnonymousShortageDialog(
    allCentres: List<String>,
    allMedicines: List<String>,
    onSubmitReport: (centre: String, medicine: String, quantity: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCentre by remember { mutableStateOf(allCentres.firstOrNull() ?: "PHC Keelavasal") }
    var selectedMedicine by remember { mutableStateOf(allMedicines.firstOrNull() ?: "Paracetamol syrup") }
    var quantityText by remember { mutableStateOf("20") }
    var submittedConfirmation by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Anonymous Shortage Report (Working #15)")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Report stock shortage securely without patient or personal identity tracking.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = selectedCentre,
                    onValueChange = { newValue: String -> selectedCentre = newValue },
                    label = { Text(text = "Health Centre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedMedicine,
                    onValueChange = { newValue: String -> selectedMedicine = newValue },
                    label = { Text(text = "Shortage Medicine") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { newValue: String -> quantityText = newValue.filter { char: Char -> char.isDigit() } },
                    label = { Text(text = "Reported Shortfall Quantity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (submittedConfirmation) {
                    Text(
                        text = "✓ Anonymous report submitted successfully!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityText.toIntOrNull() ?: 0
                    if (qty > 0) {
                        val report = MediFlowInnovations.createAnonymousShortageReport(selectedCentre, selectedMedicine, qty)
                        onSubmitReport(report.centre, report.medicine, report.reportedShortfall)
                        submittedConfirmation = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Submit Anonymous Report")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
