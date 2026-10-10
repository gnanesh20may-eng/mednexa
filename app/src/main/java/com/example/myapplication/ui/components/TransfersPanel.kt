package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.myapplication.core.DistrictIndent
import com.example.myapplication.core.TransferApprovalStatus
import com.example.myapplication.core.TransferProposal
import com.example.myapplication.ui.theme.NotoSansTamilFontFamily
import com.example.myapplication.ui.theme.StatusRed

/**
 * Screen 4: Transfers Panel with Proposal Cards, Red Border on Urgent Transfers, and Pharmacist Approval gating.
 */
@Composable
fun TransfersPanel(
    proposals: List<TransferProposal>,
    districtIndents: List<DistrictIndent> = emptyList(),
    onApproveTransfer: (proposalId: String, pharmacistName: String) -> Unit = { _, _ -> },
    onRejectTransfer: (proposalId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (proposals.isEmpty() && districtIndents.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No Pending Stock Transfers Required",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "All primary health centres hold adequate safety buffer stock.",
                    style = MaterialTheme.typography.bodyMedium,
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
        if (proposals.isNotEmpty()) {
            Text(
                text = "Sideways Transfer Proposals (${proposals.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            proposals.forEach { proposal ->
                ProposalCard(
                    proposal = proposal,
                    onApprove = { name -> onApproveTransfer(proposal.id, name) },
                    onReject = { onRejectTransfer(proposal.id) }
                )
            }
        }

        if (districtIndents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Fallback District Indents (${districtIndents.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StatusRed
            )

            districtIndents.forEach { indent ->
                DistrictIndentCard(indent = indent)
            }
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: TransferProposal,
    onApprove: (pharmacistName: String) -> Unit,
    onReject: () -> Unit
) {
    val tr = proposal.transfer
    var pharmacistName by remember { mutableStateOf("Pharmacist Ramesh") }
    val isUrgent = tr.quantity >= 50 || tr.distanceKm < 10.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isUrgent && proposal.status == TransferApprovalStatus.PENDING) BorderStroke(2.dp, StatusRed) else BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with distance & urgent indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tr.medicine,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isUrgent && proposal.status == TransferApprovalStatus.PENDING) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = StatusRed
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Urgent",
                                tint = Color.White,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            Text(
                                text = "URGENT",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Transfer route details: Donor -> Recipient, Distance & Quantity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "From Donor: ${tr.fromCentre}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "To Recipient: ${tr.toCentre}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusRed
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${tr.quantity} Units",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Distance: %.1f km".format(tr.distanceKm),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Pharmacist approval input field
            if (proposal.status == TransferApprovalStatus.PENDING) {
                OutlinedTextField(
                    value = pharmacistName,
                    onValueChange = { pharmacistName = it },
                    label = {
                        Text(
                            text = "Pharmacist Name (Required / மருந்தாளுநர் பெயர்)",
                            fontFamily = NotoSansTamilFontFamily
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onApprove(pharmacistName) },
                        enabled = pharmacistName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve Transfer")
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject")
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (proposal.status == TransferApprovalStatus.APPROVED) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = "Status: ${proposal.status} by ${proposal.approvedBy ?: "System"}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (proposal.status == TransferApprovalStatus.APPROVED) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
private fun DistrictIndentCard(indent: DistrictIndent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "${indent.medicine} • ${indent.centre}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StatusRed
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Indent Quantity: ${indent.quantity} units requested from District Store",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Reason: ${indent.reason}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}
