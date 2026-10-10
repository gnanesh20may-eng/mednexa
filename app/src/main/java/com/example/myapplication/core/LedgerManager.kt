package com.example.myapplication.core

/**
 * Pure Kotlin helper for ledger filtering and stock movement audit analysis.
 */
object LedgerManager {

    const val MILLIS_IN_30_DAYS = 30L * 24L * 60L * 60L * 1000L

    /**
     * Creates an approved transfer ledger entry with complete audit metadata.
     */
    fun createTransferEntry(
        proposal: TransferProposal,
        approvingPharmacist: String = "Pharmacist"
    ): LedgerEntry {
        val transfer = proposal.transfer
        return LedgerEntry(
            eventType = LedgerEventType.APPROVED_TRANSFER,
            centre = transfer.toCentre,
            medicine = transfer.medicine,
            quantity = transfer.quantity,
            senderCentre = transfer.fromCentre,
            receiverCentre = transfer.toCentre,
            distanceKm = transfer.distanceKm,
            timestamp = proposal.timestamp,
            actor = approvingPharmacist
        )
    }

    /**
     * Creates a dispense event ledger entry.
     */
    fun createDispenseEntry(
        centre: String,
        medicine: String,
        quantity: Int,
        pharmacist: String = "Pharmacist"
    ): LedgerEntry {
        return LedgerEntry(
            eventType = LedgerEventType.DISPENSE,
            centre = centre,
            medicine = medicine,
            quantity = quantity,
            actor = pharmacist
        )
    }

    /**
     * Returns ledger entries for a specific health centre and medicine within the last 30 days.
     * Answers: "Why did this centre run out?"
     */
    fun get30DayHistoryForCentreAndMedicine(
        entries: List<LedgerEntry>,
        centre: String,
        medicine: String,
        currentTimeMs: Long = System.currentTimeMillis()
    ): List<LedgerEntry> {
        val cutoffTimestamp = currentTimeMs - MILLIS_IN_30_DAYS

        return entries.filter { entry ->
            val matchesCentre = entry.centre.equals(centre, ignoreCase = true) ||
                    (entry.senderCentre != null && entry.senderCentre.equals(centre, ignoreCase = true)) ||
                    (entry.receiverCentre != null && entry.receiverCentre.equals(centre, ignoreCase = true))

            val matchesMedicine = entry.medicine.equals(medicine, ignoreCase = true)
            val within30Days = entry.timestamp >= cutoffTimestamp

            matchesCentre && matchesMedicine && within30Days
        }.sortedByDescending { it.timestamp }
    }
}
