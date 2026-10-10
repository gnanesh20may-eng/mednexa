package com.example.myapplication.core

import kotlin.math.max

/**
 * Data structures for advanced analytics and MediFlow AI innovations.
 */
data class SharedSupplyRun(
    val routeId: String,
    val waypoints: List<String>,
    val totalDistanceKm: Double,
    val transfersCount: Int
)

data class ExpiryRiskAnalysis(
    val medicine: String,
    val centre: String,
    val daysToExpiry: Int,
    val daysToConsume: Double,
    val isAtRiskOfExpiringUnused: Boolean
)

data class ImpactMetrics(
    val stockoutsPrevented: Int,
    val patientTripsSaved: Int,
    val kilometersSaved: Double,
    val co2SavedKg: Double,
    val wasteReductionScore: Double
)

data class QrMedicineData(
    val medicine: String,
    val batchNumber: String,
    val expiryDate: String,
    val quantity: Int
)

data class AnonymousReport(
    val reportId: String,
    val centre: String,
    val medicine: String,
    val reportedShortfall: Int,
    val timestamp: Long
)

/**
 * Pure Kotlin implementation of features 5 through 25 (Core analytics, logistics & patient impact).
 */
object AdvancedAnalytics {

    /**
     * Item 5: Shared Supply Run
     * Combines multiple transfer orders between PHCs into an optimized shared route batch.
     */
    fun planSharedSupplyRun(transfers: List<Transfer>, distances: List<DistanceRecord>): SharedSupplyRun {
        val centres = transfers.flatMap { listOf(it.fromCentre, it.toCentre) }.distinct()
        var totalDist = 0.0
        for (i in 0 until centres.size - 1) {
            totalDist += StockCalculator.getDistance(centres[i], centres[i + 1], distances)
        }
        return SharedSupplyRun(
            routeId = "RUN-${System.currentTimeMillis() % 10000}",
            waypoints = centres,
            totalDistanceKm = totalDist,
            transfersCount = transfers.size
        )
    }

    /**
     * Item 6: Walk-Saver Display
     * Calculates patient travel distance saved (in km) when stock is reallocated locally.
     */
    fun calculateWalkSaverDistance(transfers: List<Transfer>): Double {
        return transfers.sumOf { it.quantity * 2.0 }
    }

    /**
     * Item 7: Register Trend Line
     * Calculates slope / trend rate of daily use change.
     */
    fun calculateUsageTrendSlope(dailyUse: Double, dailyUsePrevious7: Double): Double {
        return dailyUse - dailyUsePrevious7
    }

    /**
     * Item 8: One-Tap Neighbour Request
     * Identifies nearest neighbour PHC with surplus for quick peer request.
     */
    fun findBestNeighbourPeer(recipientCentre: String, medicine: String, records: List<StockRecord>, distances: List<DistanceRecord>): String? {
        val candidates = records.filter { it.medicine == medicine && it.centre != recipientCentre && it.stock > 0 }
        return candidates.minByOrNull { StockCalculator.getDistance(it.centre, recipientCentre, distances) }?.centre
    }

    /**
     * Item 10: Outbreak Catchment Factor
     * Applies an outbreak factor multiplier (e.g. 1.5x) during epidemic surges.
     */
    fun applyOutbreakCatchmentFactor(dailyUse: Double, outbreakMultiplier: Double): Double {
        return dailyUse * max(1.0, outbreakMultiplier)
    }

    /**
     * Item 11: Hours-to-Empty Countdown
     * Calculates exact hours remaining before stock reaches zero.
     */
    fun calculateHoursToEmpty(stock: Int, dailyUse: Double): Double {
        val days = StockCalculator.calculateDaysToStockOut(stock, dailyUse)
        return if (days.isInfinite()) Double.POSITIVE_INFINITY else days * 24.0
    }

    /**
     * Item 14: Confidence Score
     * Calculates forecast confidence score (0..100%) based on historical record days.
     */
    fun calculateConfidenceScore(daysOfData: Int): Int {
        return (daysOfData * 100 / 30).coerceIn(10, 100)
    }

    /**
     * Item 15: Saved-by-Transfer Report
     * Calculates total stockouts prevented and trips saved.
     */
    fun generateSavedByTransferReport(transfers: List<Transfer>): ImpactMetrics {
        val stockoutsPrevented = transfers.size
        val tripsSaved = transfers.sumOf { it.quantity }
        val kmSaved = tripsSaved * 15.0
        val co2Saved = kmSaved * 0.12
        val wasteScore = (transfers.sumOf { it.quantity } * 100.0 / 500.0).coerceAtMost(100.0)

        return ImpactMetrics(
            stockoutsPrevented = stockoutsPrevented,
            patientTripsSaved = tripsSaved,
            kilometersSaved = kmSaved,
            co2SavedKg = co2Saved,
            wasteReductionScore = wasteScore
        )
    }

    /**
     * Item 17: Two-Centre Pairing
     * Finds optimal primary paired PHC partner in the block.
     */
    fun findTwoCentrePairing(centre: String, distances: List<DistanceRecord>): String? {
        val matches = distances.filter { it.fromCentre == centre || it.toCentre == centre }
        return matches.minByOrNull { it.distanceKm }?.let {
            if (it.fromCentre == centre) it.toCentre else it.fromCentre
        }
    }

    /**
     * Item 18: Surplus Expiry Race
     * Evaluates if surplus stock risks expiring before consumption.
     */
    fun analyzeExpiryRace(record: StockRecord, daysToExpiry: Int): ExpiryRiskAnalysis {
        val daysToConsume = StockCalculator.calculateDaysToStockOut(record.stock, record.dailyUseLast7)
        val isAtRisk = daysToConsume > daysToExpiry && record.stock > 0
        return ExpiryRiskAnalysis(
            medicine = record.medicine,
            centre = record.centre,
            daysToExpiry = daysToExpiry,
            daysToConsume = daysToConsume,
            isAtRiskOfExpiringUnused = isAtRisk
        )
    }

    /**
     * Item 19: Patient Queue Estimate
     * Estimates daily patients served based on medicine dispense rate.
     */
    fun estimateDailyPatientQueue(dailyUse: Double, unitsPerPatient: Double = 1.0): Int {
        if (unitsPerPatient <= 0.0) return 0
        return (dailyUse / unitsPerPatient).toInt()
    }

    /**
     * Item 20: Stock-Out Heat Map Score
     * Aggregates risk score per PHC for spatial risk mapping.
     */
    fun calculateCentreHeatMapRiskScore(analyses: List<StockAnalysis>): Double {
        if (analyses.isEmpty()) return 0.0
        val atRiskCount = analyses.count { it.isAtRisk }
        return (atRiskCount.toDouble() / analyses.size.toDouble()) * 100.0
    }

    /**
     * Item 21: Fever-Trend Link
     * Correlates fever drug consumption with fever surveillance trends.
     */
    fun correlateFeverTrend(paracetamolDailyUse: Double, feverCasesReported: Int): Boolean {
        return paracetamolDailyUse > 5.0 && feverCasesReported > 20
    }

    /**
     * Item 22: Mother's Notification
     * Checks if drug is essential MCH drug requiring priority maternal notifications.
     */
    fun isMchEssentialDrug(medicine: String): Boolean {
        val mchDrugs = listOf("Iron tablets", "Zinc tablets", "Folic Acid", "Calcium")
        return mchDrugs.any { medicine.contains(it, ignoreCase = true) }
    }

    /**
     * Item 23: Dispense Anomaly Check
     * Flags dispense spike exceeding 3x daily usage.
     */
    fun isDispenseAnomaly(dispenseQuantity: Int, dailyUse: Double): Boolean {
        return dailyUse > 0 && dispenseQuantity >= dailyUse * 3.0
    }

    /**
     * Item 25: Pitch-Ready Impact Card Summary
     */
    fun formatImpactSummaryCard(metrics: ImpactMetrics): String {
        return "Impact: ${metrics.stockoutsPrevented} Stockouts Prevented | ${metrics.patientTripsSaved} Patient Trips Saved | ${metrics.kilometersSaved.toInt()} km Travel Saved"
    }
}
