package com.example.myapplication.core

import org.junit.Assert.assertEquals
import org.junit.Test

class StockJsonTest {

    @Test
    fun testRecordRoundtrip() {
        val records = listOf(
            StockRecord("PHC Keelavasal", "Paracetamol syrup", 4, 3.0, 3.0),
            StockRecord("PHC Melur", "Paracetamol syrup", 120, 3.0, 3.0)
        )

        val json = StockJson.encodeRecords(records)
        val decoded = StockJson.decodeRecords(json)

        assertEquals(2, decoded.size)
        assertEquals("PHC Keelavasal", decoded[0].centre)
        assertEquals(4, decoded[0].stock)
        assertEquals(3.0, decoded[0].dailyUseLast7, 0.001)
    }

    @Test
    fun testProposalRoundtrip() {
        val proposals = listOf(
            TransferProposal(
                id = "P1",
                transfer = Transfer("Paracetamol", "Melur", "Keelavasal", 56, 9.0, 8.66),
                status = TransferApprovalStatus.PENDING
            )
        )

        val json = StockJson.encodeProposals(proposals)
        val decoded = StockJson.decodeProposals(json)

        assertEquals(1, decoded.size)
        assertEquals("P1", decoded[0].id)
        assertEquals(56, decoded[0].transfer.quantity)
        assertEquals(TransferApprovalStatus.PENDING, decoded[0].status)
    }
}
