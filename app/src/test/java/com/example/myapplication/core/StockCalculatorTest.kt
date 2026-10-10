package com.example.myapplication.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StockCalculatorTest {

    @Test
    fun testDaysToStockOut_normalAndZeroUsage() {
        val days1 = StockCalculator.calculateDaysToStockOut(stock = 100, dailyUse = 5.0)
        assertEquals(20.0, days1, 0.001)

        val days2 = StockCalculator.calculateDaysToStockOut(stock = 4, dailyUse = 3.0)
        assertEquals(1.333333, days2, 0.0001)

        val daysZero = StockCalculator.calculateDaysToStockOut(stock = 50, dailyUse = 0.0)
        assertEquals(Double.POSITIVE_INFINITY, daysZero, 0.0)

        val daysNeg = StockCalculator.calculateDaysToStockOut(stock = 50, dailyUse = -2.0)
        assertEquals(Double.POSITIVE_INFINITY, daysNeg, 0.0)
    }

    @Test
    fun testBurnRate_normalZeroAndNegative() {
        assertEquals(5.0, StockCalculator.calculateBurnRate(5.0), 0.001)
        assertEquals(0.0, StockCalculator.calculateBurnRate(0.0), 0.001)
        assertEquals(0.0, StockCalculator.calculateBurnRate(-3.0), 0.001)
    }

    @Test
    fun testSpeedUpAlert_doublingTrend() {
        assertTrue(StockCalculator.isSpeedUp(dailyUseLast7 = 10.0, dailyUsePrior7 = 5.0))
        assertTrue(StockCalculator.isSpeedUp(dailyUseLast7 = 15.0, dailyUsePrior7 = 5.0))
        assertFalse(StockCalculator.isSpeedUp(dailyUseLast7 = 8.0, dailyUsePrior7 = 5.0))
        assertFalse(StockCalculator.isSpeedUp(dailyUseLast7 = 10.0, dailyUsePrior7 = 0.0))
    }

    @Test
    fun testSpeedUpAlert_flagsEvenWhenStockLooksSafe() {
        val record = StockRecord(
            centre = "PHC Thirupparankundram",
            medicine = "Ciprofloxacin 500mg",
            stock = 300,
            dailyUseLast7 = 10.0,
            dailyUsePrior7 = 5.0
        )

        val analysis = StockCalculator.analyzeRecord(record, daysUntilIndent = 20, safetyDays = 14)

        assertFalse(analysis.isAtRisk)
        assertTrue(analysis.isSpeedUp)
        assertEquals(StockStatus.SPEED_UP, analysis.status)
        assertEquals(10.0, analysis.burnRate, 0.001)
        assertEquals(30.0, analysis.daysToStockOut, 0.001)
    }

    @Test
    fun testTransferCappingAndAllocation() {
        val records = listOf(
            StockRecord("PHC Keelavasal", "Paracetamol syrup", stock = 4, dailyUseLast7 = 3.0, dailyUsePrior7 = 3.0),
            StockRecord("PHC Thirupparankundram", "Paracetamol syrup", stock = 100, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0),
            StockRecord("PHC Tirumangalam", "Paracetamol syrup", stock = 80, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0),
            StockRecord("PHC Melur", "Paracetamol syrup", stock = 120, dailyUseLast7 = 3.0, dailyUsePrior7 = 3.0)
        )

        val distances = listOf(
            DistanceRecord("PHC Keelavasal", "PHC Melur", 9.0),
            DistanceRecord("PHC Keelavasal", "PHC Thirupparankundram", 12.0)
        )

        val plan = StockCalculator.calculateTransfersAndIndents(records, distances, daysUntilIndent = 20, safetyDays = 14)

        // Melur is 9 km (<= 10 km limit), while Thirupparankundram is 12 km (> 10 km limit). Melur sends 56 units.
        assertEquals(1, plan.transfers.size)
        val transfer = plan.transfers.first()
        assertEquals("Paracetamol syrup", transfer.medicine)
        assertEquals("PHC Melur", transfer.fromCentre)
        assertEquals("PHC Keelavasal", transfer.toCentre)
        assertEquals(56, transfer.quantity)
    }

    @Test
    fun testTransferMultipleDonorsCapping() {
        val records = listOf(
            StockRecord("Recipient", "Amoxicillin", stock = 0, dailyUseLast7 = 2.5, dailyUsePrior7 = 2.5),
            StockRecord("DonorSmall", "Amoxicillin", stock = 58, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0),
            StockRecord("DonorBig", "Amoxicillin", stock = 68, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0)
        )

        val distances = listOf(
            DistanceRecord("DonorSmall", "Recipient", 5.0),
            DistanceRecord("DonorBig", "Recipient", 5.0)
        )

        val plan = StockCalculator.calculateTransfersAndIndents(records, distances, daysUntilIndent = 20, safetyDays = 14)

        assertEquals(2, plan.transfers.size)
        assertEquals("DonorBig", plan.transfers[0].fromCentre)
        assertEquals("Recipient", plan.transfers[0].toCentre)
        assertEquals(40, plan.transfers[0].quantity)

        assertEquals("DonorSmall", plan.transfers[1].fromCentre)
        assertEquals("Recipient", plan.transfers[1].toCentre)
        assertEquals(10, plan.transfers[1].quantity)
    }

    @Test
    fun testNoTransferToSelf() {
        val records = listOf(
            StockRecord("PHC Keelavasal", "Paracetamol syrup", stock = 4, dailyUseLast7 = 3.0, dailyUsePrior7 = 3.0)
        )

        val transfers = StockCalculator.calculateTransfers(records, daysUntilIndent = 20, safetyDays = 14)
        assertTrue(transfers.isEmpty())
    }

    @Test
    fun testDistanceWeightedDonorRanking_nearSmallSurplusBeatsFarLargeSurplus() {
        val records = listOf(
            StockRecord("Recipient", "Paracetamol", stock = 0, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0),
            StockRecord("NearDonor", "Paracetamol", stock = 48, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0), // Surplus = 20
            StockRecord("FarDonor", "Paracetamol", stock = 88, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0)   // Surplus = 60
        )

        val distances = listOf(
            DistanceRecord("NearDonor", "Recipient", 2.0), // Score = 20 / 2.0 = 10.0
            DistanceRecord("FarDonor", "Recipient", 10.0)   // Score = 60 / 10.0 = 6.0
        )

        val plan = StockCalculator.calculateTransfersAndIndents(records, distances, daysUntilIndent = 20, safetyDays = 14)

        assertEquals(2, plan.transfers.size)
        assertEquals("NearDonor", plan.transfers[0].fromCentre)
        assertEquals(20, plan.transfers[0].quantity)
        assertEquals(2.0, plan.transfers[0].distanceKm, 0.001)

        assertEquals("FarDonor", plan.transfers[1].fromCentre)
        assertEquals(20, plan.transfers[1].quantity)
    }

    @Test
    fun testRemainderOnlyDistrictIndent_fallbackWhenNearbySurplusExhausted() {
        val records = listOf(
            StockRecord("Recipient", "Amoxicillin", stock = 0, dailyUseLast7 = 2.5, dailyUsePrior7 = 2.5),
            StockRecord("NearbyDonor", "Amoxicillin", stock = 58, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0)
        )

        val distances = listOf(
            DistanceRecord("NearbyDonor", "Recipient", 8.0)
        )

        val plan = StockCalculator.calculateTransfersAndIndents(records, distances, daysUntilIndent = 20, safetyDays = 14)

        assertEquals(1, plan.transfers.size)
        assertEquals("NearbyDonor", plan.transfers[0].fromCentre)
        assertEquals(30, plan.transfers[0].quantity)

        assertEquals(1, plan.districtIndents.size)
        val districtIndent = plan.districtIndents.first()
        assertEquals("Amoxicillin", districtIndent.medicine)
        assertEquals("Recipient", districtIndent.centre)
        assertEquals(20, districtIndent.quantity)
        assertTrue(districtIndent.reason.contains("Remainder shortfall of 20 units"))
    }

    @Test
    fun testDistrictIndent_notCreatedWhenNearbySurplusCoversFullShortfall() {
        val records = listOf(
            StockRecord("Recipient", "Amoxicillin", stock = 0, dailyUseLast7 = 1.0, dailyUsePrior7 = 1.0),
            StockRecord("NearbyDonor", "Amoxicillin", stock = 58, dailyUseLast7 = 2.0, dailyUsePrior7 = 2.0)
        )

        val distances = listOf(DistanceRecord("NearbyDonor", "Recipient", 5.0))

        val plan = StockCalculator.calculateTransfersAndIndents(records, distances, daysUntilIndent = 20, safetyDays = 14)

        assertEquals(1, plan.transfers.size)
        assertEquals(20, plan.transfers[0].quantity)
        assertTrue(plan.districtIndents.isEmpty())
    }

    @Test
    fun testAtRisk_thresholdEvaluation() {
        assertTrue(StockCalculator.isAtRisk(daysToStockOut = 1.33, daysUntilIndent = 20))
        assertTrue(StockCalculator.isAtRisk(daysToStockOut = 19.9, daysUntilIndent = 20))
        assertFalse(StockCalculator.isAtRisk(daysToStockOut = 20.0, daysUntilIndent = 20))
        assertFalse(StockCalculator.isAtRisk(daysToStockOut = 30.0, daysUntilIndent = 20))
        assertFalse(StockCalculator.isAtRisk(daysToStockOut = Double.POSITIVE_INFINITY, daysUntilIndent = 20))
    }

    @Test
    fun testSurplus_calculationAndZeroFloor() {
        assertEquals(72, StockCalculator.calculateSurplus(stock = 100, dailyUse = 2.0, safetyDays = 14))
        assertEquals(78, StockCalculator.calculateSurplus(stock = 120, dailyUse = 3.0, safetyDays = 14))
        assertEquals(0, StockCalculator.calculateSurplus(stock = 4, dailyUse = 3.0, safetyDays = 14))
    }

    @Test
    fun testShortfall_calculation() {
        assertEquals(56, StockCalculator.calculateShortfall(stock = 4, dailyUse = 3.0, daysUntilIndent = 20))
        assertEquals(10, StockCalculator.calculateShortfall(stock = 10, dailyUse = 1.0, daysUntilIndent = 20))
        assertEquals(0, StockCalculator.calculateShortfall(stock = 100, dailyUse = 2.0, daysUntilIndent = 20))
    }

    @Test
    fun testAcceleration_rateOfChangeAlert() {
        assertTrue(StockCalculator.isAccelerating(dailyUseLast7 = 5.0, dailyUsePrior7 = 3.0))
        assertTrue(StockCalculator.isAccelerating(dailyUseLast7 = 12.0, dailyUsePrior7 = 6.0))
        assertFalse(StockCalculator.isAccelerating(dailyUseLast7 = 12.0, dailyUsePrior7 = 10.0))
        assertFalse(StockCalculator.isAccelerating(dailyUseLast7 = 5.0, dailyUsePrior7 = 0.0))
    }
}
