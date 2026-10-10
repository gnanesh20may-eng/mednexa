package com.example.myapplication.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InnovationsTest {

    @Test
    fun testItem5_sharedSupplyRun() {
        val transfers = listOf(
            Transfer("Paracetamol", "PHC Melur", "PHC Keelavasal", 50, 30.0, 2.6)
        )
        val distances = listOf(DistanceRecord("PHC Melur", "PHC Keelavasal", 30.0))
        val run = AdvancedAnalytics.planSharedSupplyRun(transfers, distances)
        assertEquals(1, run.transfersCount)
        assertEquals(30.0, run.totalDistanceKm, 0.001)
    }

    @Test
    fun testItem6_walkSaverDisplay() {
        val transfers = listOf(Transfer("Paracetamol", "A", "B", 10))
        val distSaved = AdvancedAnalytics.calculateWalkSaverDistance(transfers)
        assertEquals(20.0, distSaved, 0.001)
    }

    @Test
    fun testItem7_registerTrendLine() {
        val slope = AdvancedAnalytics.calculateUsageTrendSlope(10.0, 6.0)
        assertEquals(4.0, slope, 0.001)
    }

    @Test
    fun testItem8_oneTapNeighbourRequest() {
        val records = listOf(
            StockRecord("PHC Keelavasal", "ORS", 0, 1.0, 1.0),
            StockRecord("PHC Thirupparankundram", "ORS", 50, 1.0, 1.0)
        )
        val distances = listOf(DistanceRecord("PHC Keelavasal", "PHC Thirupparankundram", 8.0))
        val neighbour = AdvancedAnalytics.findBestNeighbourPeer("PHC Keelavasal", "ORS", records, distances)
        assertEquals("PHC Thirupparankundram", neighbour)
    }

    @Test
    fun testItem10_outbreakCatchmentFactor() {
        val adjusted = AdvancedAnalytics.applyOutbreakCatchmentFactor(10.0, 1.5)
        assertEquals(15.0, adjusted, 0.001)
    }

    @Test
    fun testItem11_hoursToEmptyCountdown() {
        val hours = AdvancedAnalytics.calculateHoursToEmpty(10, 2.0)
        assertEquals(120.0, hours, 0.001)
    }

    @Test
    fun testItem14_confidenceScore() {
        val confidence = AdvancedAnalytics.calculateConfidenceScore(30)
        assertEquals(100, confidence)
    }

    @Test
    fun testItem15_savedByTransferReport() {
        val transfers = listOf(Transfer("ORS", "A", "B", 100))
        val report = AdvancedAnalytics.generateSavedByTransferReport(transfers)
        assertEquals(1, report.stockoutsPrevented)
        assertEquals(100, report.patientTripsSaved)
    }

    @Test
    fun testItem17_twoCentrePairing() {
        val distances = listOf(
            DistanceRecord("PHC Keelavasal", "PHC Thirupparankundram", 8.0),
            DistanceRecord("PHC Keelavasal", "PHC Melur", 30.0)
        )
        val partner = AdvancedAnalytics.findTwoCentrePairing("PHC Keelavasal", distances)
        assertEquals("PHC Thirupparankundram", partner)
    }

    @Test
    fun testItem18_surplusExpiryRace() {
        val record = StockRecord("PHC Keelavasal", "Zinc", 100, 1.0, 1.0)
        val risk = AdvancedAnalytics.analyzeExpiryRace(record, 50)
        assertTrue(risk.isAtRiskOfExpiringUnused)
    }

    @Test
    fun testItem19_patientQueueEstimate() {
        val patients = AdvancedAnalytics.estimateDailyPatientQueue(20.0, 2.0)
        assertEquals(10, patients)
    }

    @Test
    fun testItem20_stockOutHeatMap() {
        val recAtRisk = StockRecord("PHC A", "Drug1", 1, 5.0, 5.0)
        val analysis = StockCalculator.analyzeRecord(recAtRisk)
        val score = AdvancedAnalytics.calculateCentreHeatMapRiskScore(listOf(analysis))
        assertEquals(100.0, score, 0.001)
    }

    @Test
    fun testItem21_feverTrendLink() {
        assertTrue(AdvancedAnalytics.correlateFeverTrend(10.0, 30))
    }

    @Test
    fun testItem22_motherNotification() {
        assertTrue(AdvancedAnalytics.isMchEssentialDrug("Iron tablets"))
        assertFalse(AdvancedAnalytics.isMchEssentialDrug("Paracetamol"))
    }

    @Test
    fun testItem23_dispenseAnomalyCheck() {
        assertTrue(AdvancedAnalytics.isDispenseAnomaly(30, 5.0))
        assertFalse(AdvancedAnalytics.isDispenseAnomaly(10, 5.0))
    }

    @Test
    fun testItem25_pitchReadyImpactCard() {
        val metrics = ImpactMetrics(1, 10, 150.0, 18.0, 80.0)
        val text = AdvancedAnalytics.formatImpactSummaryCard(metrics)
        assertTrue(text.contains("1 Stockouts Prevented"))
    }

    @Test
    fun testItem26_seasonalDiseaseForecasting() {
        val demand = MediFlowInnovations.predictSeasonalDemand(10.0, 1.8)
        assertEquals(18.0, demand, 0.001)
    }

    @Test
    fun testItem27_medicineExpiryPrediction() {
        val daysLeft = MediFlowInnovations.predictExpiryRiskDays(100, 10.0, 60)
        assertEquals(50, daysLeft)
    }

    @Test
    fun testItem28_smartEmergencyScore() {
        val score = MediFlowInnovations.calculateSmartEmergencyScore(1.0, 5.0, 10.0, true)
        assertTrue(score > 0.0)
    }

    @Test
    fun testItem29_unexpectedDemandDetection() {
        assertTrue(MediFlowInnovations.detectUnexpectedDemandSpike(15.0, 8.0))
    }

    @Test
    fun testItem30_aiConfidenceIndicator() {
        val text = MediFlowInnovations.getAiConfidenceRating(35)
        assertTrue(text.startsWith("HIGH"))
    }

    @Test
    fun testItem31_smartDonorMatching() {
        val records = listOf(
            StockRecord("PHC Melur", "ORS", 100, 1.0, 1.0)
        )
        val distances = listOf(DistanceRecord("PHC Keelavasal", "PHC Melur", 10.0))
        val ranked = MediFlowInnovations.rankSmartDonors("PHC Keelavasal", "ORS", records, distances)
        assertEquals(1, ranked.size)
        assertEquals("PHC Melur", ranked[0].first)
    }

    @Test
    fun testItem32_multiPhcTransferPlanning() {
        val records = listOf(
            StockRecord("Donor1", "ORS", 50, 1.0, 1.0),
            StockRecord("Donor2", "ORS", 50, 1.0, 1.0)
        )
        val plan = MediFlowInnovations.calculateMultiPhcTransferPlan("Recipient", "ORS", 50, records)
        assertTrue(plan.allocations.isNotEmpty())
    }

    @Test
    fun testItem33_liveRouteAndDeliveryTracking() {
        val proposal = TransferProposal(
            transfer = Transfer("ORS", "A", "B", 10, 10.0),
            status = TransferApprovalStatus.APPROVED
        )
        val tracking = MediFlowInnovations.trackDeliveryRoute(proposal)
        assertEquals("IN_TRANSIT", tracking.status)
    }

    @Test
    fun testItem34_emergencyTransferMode() {
        assertTrue(MediFlowInnovations.isEmergencyEscalationRequired(1.5))
        assertFalse(MediFlowInnovations.isEmergencyEscalationRequired(5.0))
    }

    @Test
    fun testItem35_fairDistributionEngine() {
        assertTrue(MediFlowInnovations.isFairDonorBufferMaintained(100, 2.0, 20, 14))
    }

    @Test
    fun testItem36_tamilVoiceAssistant() {
        val rec = StockRecord("PHC A", "Paracetamol", 1, 5.0, 5.0)
        val analysis = StockCalculator.analyzeRecord(rec)
        val response = MediFlowInnovations.answerTamilVoiceQuery("எந்த மருந்து குறையும்?", listOf(analysis))
        assertTrue(response.contains("Paracetamol"))
    }

    @Test
    fun testItem37_offlineModeWithAutoSync() {
        val queue = MediFlowInnovations.prepareOfflineSyncQueue(listOf("DISPENSE_1"))
        assertEquals(1, queue.size)
    }

    @Test
    fun testItem38_whatsappSmsAlerts() {
        val proposal = TransferProposal(transfer = Transfer("ORS", "Donor", "Recipient", 20, 15.0))
        val alert = MediFlowInnovations.generateWhatsappSmsAlert(proposal)
        assertTrue(alert.contains("Move 20 units"))
    }

    @Test
    fun testItem39_barcodeQrScanning() {
        val qrData = MediFlowInnovations.parseBarcodeQrCode("Paracetamol|BATCH123|2026-12-31|50")
        assertNotNull(qrData)
        assertEquals("Paracetamol", qrData?.medicine)
        assertEquals(50, qrData?.quantity)
    }

    @Test
    fun testItem40_oneClickDailyReport() {
        val report = MediFlowInnovations.generateOneClickDailyReport(emptyList(), emptyList())
        assertTrue(report.contains("Daily Stock Summary Report"))
    }

    @Test
    fun testItem41_medicineWasteReductionScore() {
        val transfers = listOf(Transfer("ORS", "A", "B", 200))
        val score = MediFlowInnovations.calculateWasteReductionScore(transfers)
        assertEquals(20.0, score, 0.001)
    }

    @Test
    fun testItem42_carbonAwareTransferPlanning() {
        val transfers = listOf(Transfer("ORS", "A", "B", 10, 20.0))
        val co2 = MediFlowInnovations.calculateCarbonAwareSavings(transfers)
        assertEquals(3.0, co2, 0.001)
    }

    @Test
    fun testItem43_equityBasedPriority() {
        val priority = MediFlowInnovations.calculateEquityPriorityScore(true, 20)
        assertEquals(30.0, priority, 0.001)
    }

    @Test
    fun testItem44_communityImpactDashboard() {
        val summary = MediFlowInnovations.generateCommunityImpactSummary(emptyList(), emptyList())
        assertTrue(summary.contains("Community Impact"))
    }

    @Test
    fun testItem45_anonymousShortageReporting() {
        val report = MediFlowInnovations.createAnonymousShortageReport("PHC Keelavasal", "Paracetamol", 20)
        assertEquals("PHC Keelavasal", report.centre)
        assertEquals("Paracetamol", report.medicine)
        assertEquals(20, report.reportedShortfall)
    }
}
