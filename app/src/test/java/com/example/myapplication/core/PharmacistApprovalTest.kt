package com.example.myapplication.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PharmacistApprovalTest {

    private val initialRecords = listOf(
        StockRecord("PHC Melur", "Paracetamol syrup", stock = 120, dailyUseLast7 = 3.0, dailyUsePrior7 = 3.0),
        StockRecord("PHC Keelavasal", "Paracetamol syrup", stock = 4, dailyUseLast7 = 3.0, dailyUsePrior7 = 3.0)
    )

    private val sampleProposal = TransferProposal(
        id = "PROP-1",
        transfer = Transfer(
            medicine = "Paracetamol syrup",
            fromCentre = "PHC Melur",
            toCentre = "PHC Keelavasal",
            quantity = 56,
            distanceKm = 9.0,
            donorScore = 8.66
        ),
        status = TransferApprovalStatus.PENDING
    )

    @Test
    fun testApproveWithValidPharmacistName_updatesStatusAndStock() {
        val (updatedProposal, updatedRecords) = StockCalculator.processDecision(
            records = initialRecords,
            proposal = sampleProposal,
            action = ApprovalAction.APPROVE,
            pharmacistName = "Pharmacist Ramesh"
        )

        assertEquals(TransferApprovalStatus.APPROVED, updatedProposal.status)
        assertEquals("Pharmacist Ramesh", updatedProposal.approvedBy)

        val melurRecord = updatedRecords.first { it.centre == "PHC Melur" }
        assertEquals(64, melurRecord.stock)

        val keelavasalRecord = updatedRecords.first { it.centre == "PHC Keelavasal" }
        assertEquals(60, keelavasalRecord.stock)
    }

    @Test
    fun testApproveWithBlankPharmacistName_remainsPendingAndStockUnchanged() {
        val (updatedProposal, updatedRecords) = StockCalculator.processDecision(
            records = initialRecords,
            proposal = sampleProposal,
            action = ApprovalAction.APPROVE,
            pharmacistName = "   "
        )

        assertEquals(TransferApprovalStatus.PENDING, updatedProposal.status)
        assertNull(updatedProposal.approvedBy)

        val melurRecord = updatedRecords.first { it.centre == "PHC Melur" }
        assertEquals(120, melurRecord.stock)

        val keelavasalRecord = updatedRecords.first { it.centre == "PHC Keelavasal" }
        assertEquals(4, keelavasalRecord.stock)
    }

    @Test
    fun testRejectProposal_marksRejectedAndLeavesStockUnchanged() {
        val (updatedProposal, updatedRecords) = StockCalculator.processDecision(
            records = initialRecords,
            proposal = sampleProposal,
            action = ApprovalAction.REJECT,
            pharmacistName = "Pharmacist Ramesh"
        )

        assertEquals(TransferApprovalStatus.REJECTED, updatedProposal.status)

        val melurRecord = updatedRecords.first { it.centre == "PHC Melur" }
        assertEquals(120, melurRecord.stock)

        val keelavasalRecord = updatedRecords.first { it.centre == "PHC Keelavasal" }
        assertEquals(4, keelavasalRecord.stock)
    }
}
