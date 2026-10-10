package com.example.myapplication.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LedgerTest {

    @Test
    fun testCreateTransferLedgerEntry_recordsAllMetadata() {
        val transfer = Transfer(
            medicine = "Paracetamol syrup",
            fromCentre = "PHC Melur",
            toCentre = "PHC Keelavasal",
            quantity = 56,
            distanceKm = 30.0,
            donorScore = 2.6
        )

        val proposal = TransferProposal(
            transfer = transfer,
            status = TransferApprovalStatus.APPROVED,
            timestamp = 1700000000000L
        )

        val entry = LedgerManager.createTransferEntry(proposal, approvingPharmacist = "Pharmacist Anitha")

        assertEquals(LedgerEventType.APPROVED_TRANSFER, entry.eventType)
        assertEquals("PHC Keelavasal", entry.centre)
        assertEquals("PHC Melur", entry.senderCentre)
        assertEquals("PHC Keelavasal", entry.receiverCentre)
        assertEquals("Paracetamol syrup", entry.medicine)
        assertEquals(56, entry.quantity)
        assertEquals(30.0, entry.distanceKm ?: 0.0, 0.001)
        assertEquals("Pharmacist Anitha", entry.actor)
        assertEquals(1700000000000L, entry.timestamp)
    }

    @Test
    fun test30DayFilter_includesRecentAndExcludesOldEntries() {
        val now = 1000000000000L
        val twentyDaysAgo = now - (20L * 24L * 60L * 60L * 1000L)
        val fortyDaysAgo = now - (40L * 24L * 60L * 60L * 1000L)

        val recentDispense = LedgerEntry(
            eventType = LedgerEventType.DISPENSE,
            centre = "PHC Keelavasal",
            medicine = "Paracetamol syrup",
            quantity = 10,
            timestamp = twentyDaysAgo
        )

        val oldDispense = LedgerEntry(
            eventType = LedgerEventType.DISPENSE,
            centre = "PHC Keelavasal",
            medicine = "Paracetamol syrup",
            quantity = 15,
            timestamp = fortyDaysAgo
        )

        val entries = listOf(recentDispense, oldDispense)

        val filtered = LedgerManager.get30DayHistoryForCentreAndMedicine(
            entries = entries,
            centre = "PHC Keelavasal",
            medicine = "Paracetamol syrup",
            currentTimeMs = now
        )

        // Only the 20-day-old entry should be included; 40-day-old entry must be filtered out
        assertEquals(1, filtered.size)
        assertEquals(10, filtered[0].quantity)
        assertEquals(twentyDaysAgo, filtered[0].timestamp)
    }

    @Test
    fun testWhyDidThisCentreRunOut_filtersByCentreAndMedicine() {
        val now = System.currentTimeMillis()

        val item1 = LedgerEntry(
            eventType = LedgerEventType.DISPENSE,
            centre = "PHC Keelavasal",
            medicine = "Paracetamol syrup",
            quantity = 5,
            timestamp = now - 10000
        )

        val item2 = LedgerEntry(
            eventType = LedgerEventType.DISPENSE,
            centre = "PHC Melur", // Different centre
            medicine = "Paracetamol syrup",
            quantity = 10,
            timestamp = now - 20000
        )

        val item3 = LedgerEntry(
            eventType = LedgerEventType.DISPENSE,
            centre = "PHC Keelavasal",
            medicine = "ORS sachets", // Different medicine
            quantity = 8,
            timestamp = now - 30000
        )

        val entries = listOf(item1, item2, item3)

        val result = LedgerManager.get30DayHistoryForCentreAndMedicine(
            entries = entries,
            centre = "PHC Keelavasal",
            medicine = "Paracetamol syrup",
            currentTimeMs = now
        )

        assertEquals(1, result.size)
        assertEquals("PHC Keelavasal", result[0].centre)
        assertEquals("Paracetamol syrup", result[0].medicine)
        assertEquals(5, result[0].quantity)
    }
}
