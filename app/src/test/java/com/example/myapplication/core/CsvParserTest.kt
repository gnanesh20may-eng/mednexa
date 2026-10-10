package com.example.myapplication.core

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvParserTest {

    @Test
    fun testParseCsvStock() {
        val csv = """
            centre,medicine,stock,daily_use_last7,daily_use_prior7,expiry_date,importance
            PHC Keelavasal,Paracetamol syrup,4,3.0,3.0,2025-12-31,3
            PHC Melur,Paracetamol syrup,120,3.0,3.0,2025-09-30,3
        """.trimIndent()

        val records = CsvParser.parse(csv)
        assertEquals(2, records.size)
        assertEquals("PHC Keelavasal", records[0].centre)
        assertEquals("Paracetamol syrup", records[0].medicine)
        assertEquals(4, records[0].stock)
        assertEquals(3.0, records[0].dailyUseLast7, 0.001)
        assertEquals(3.0, records[0].dailyUsePrior7, 0.001)
    }

    @Test
    fun testParseCsvDistances() {
        val csv = """
            centre_a,centre_b,km
            PHC Keelavasal,PHC Melur,9.0
            PHC Keelavasal,PHC Thirupparankundram,12.0
        """.trimIndent()

        val distances = CsvParser.parseDistances(csv)
        assertEquals(2, distances.size)
        assertEquals("PHC Keelavasal", distances[0].fromCentre)
        assertEquals("PHC Melur", distances[0].toCentre)
        assertEquals(9.0, distances[0].distanceKm, 0.001)
    }
}
