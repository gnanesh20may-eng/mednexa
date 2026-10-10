package com.example.myapplication.core

import java.util.UUID

/**
 * Event type for transaction ledger logs.
 */
enum class LedgerEventType {
    APPROVED_TRANSFER,
    DISPENSE,
    REPLENISHMENT
}

/**
 * Audit log entry recording stock movement transactions.
 *
 * @property id Unique entry identifier.
 * @property eventType Type of event (APPROVED_TRANSFER, DISPENSE, REPLENISHMENT).
 * @property centre Primary centre impacted by transaction.
 * @property medicine Name of the medicine.
 * @property quantity Units moved or dispensed.
 * @property senderCentre Sender centre (for transfers).
 * @property receiverCentre Receiver centre (for transfers).
 * @property distanceKm Road distance in km (for transfers).
 * @property timestamp Unix epoch timestamp in milliseconds.
 * @property dateFormatted Formatted date string (e.g. YYYY-MM-DD HH:mm).
 * @property actor Pharmacist or system actor performing the action.
 */
data class LedgerEntry(
    val id: String = UUID.randomUUID().toString(),
    val eventType: LedgerEventType,
    val centre: String,
    val medicine: String,
    val quantity: Int,
    val senderCentre: String? = null,
    val receiverCentre: String? = null,
    val distanceKm: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String = "",
    val actor: String = "Pharmacist"
)
