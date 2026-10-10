package com.example.myapplication.core

import java.util.UUID

/**
 * Wrapper for a suggested transfer with its approval lifecycle state.
 *
 * @property id Unique proposal identifier.
 * @property transfer Underlying transfer recommendation.
 * @property status Current approval status (PENDING, APPROVED, REJECTED).
 * @property timestamp Time proposal was generated or updated.
 * @property approvedBy Pharmacist name who approved/rejected (optional).
 */
data class TransferProposal(
    val id: String = UUID.randomUUID().toString(),
    val transfer: Transfer,
    val status: TransferApprovalStatus = TransferApprovalStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val approvedBy: String? = null
)
