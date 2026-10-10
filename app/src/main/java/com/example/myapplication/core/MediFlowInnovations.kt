package com.example.myapplication.core

import kotlin.math.min

data class MultiDonorTransfer(
    val medicine: String,
    val recipientCentre: String,
    val allocations: List<Pair<String, Int>>,
    val totalQuantity: Int
)

data class DeliveryTrackingInfo(
    val transferId: String,
    val status: String,
    val etaMinutes: Int
)

/**
 * Pure Kotlin implementation for MediFlow AI proposals (items 26 through 45).
 */
object MediFlowInnovations {

    fun predictSeasonalDemand(dailyUse: Double, seasonMultiplier: Double): Double {
        return dailyUse * seasonMultiplier.coerceAtLeast(0.5)
    }

    fun predictExpiryRiskDays(stock: Int, dailyUse: Double, shelfLifeDays: Int): Int {
        val daysToConsume = StockCalculator.calculateDaysToStockOut(stock, dailyUse)
        if (daysToConsume.isInfinite()) return -1
        return (shelfLifeDays - daysToConsume.toInt())
    }

    fun calculateSmartEmergencyScore(
        daysToStockOut: Double,
        burnRate: Double,
        distanceKm: Double,
        isEssential: Boolean
    ): Double {
        val urgencyFactor = if (daysToStockOut.isInfinite()) 0.0 else (100.0 / (daysToStockOut + 1.0))
        val essentialBonus = if (isEssential) 25.0 else 0.0
        val distPenalty = distanceKm * 0.5
        return (urgencyFactor + burnRate * 2.0 + essentialBonus - distPenalty).coerceAtLeast(0.0)
    }

    fun detectUnexpectedDemandSpike(dailyUse: Double, baselineUse: Double, thresholdMultiplier: Double = 1.5): Boolean {
        if (baselineUse <= 0.0) return false
        return dailyUse >= baselineUse * thresholdMultiplier
    }

    fun getAiConfidenceRating(dataPointsCount: Int): String {
        return when {
            dataPointsCount >= 30 -> "HIGH (95% Confidence)"
            dataPointsCount >= 14 -> "MEDIUM (75% Confidence)"
            else -> "LOW (Staff Verification Recommended)"
        }
    }

    fun rankSmartDonors(
        recipientCentre: String,
        medicine: String,
        records: List<StockRecord>,
        distances: List<DistanceRecord>,
        safetyDays: Int = 14
    ): List<Pair<String, Double>> {
        return records
            .filter { it.medicine == medicine && it.centre != recipientCentre }
            .map { rec ->
                val surplus = StockCalculator.calculateSurplus(rec.stock, rec.dailyUseLast7, safetyDays)
                val dist = StockCalculator.getDistance(rec.centre, recipientCentre, distances)
                val smartScore = if (dist > 0) surplus * 2.0 / dist else surplus.toDouble()
                rec.centre to smartScore
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
    }

    fun calculateMultiPhcTransferPlan(
        recipientCentre: String,
        medicine: String,
        shortfall: Int,
        records: List<StockRecord>,
        safetyDays: Int = 14
    ): MultiDonorTransfer {
        var remainingShortfall = shortfall
        val allocations = mutableListOf<Pair<String, Int>>()

        val potentialDonors = records.filter { it.medicine == medicine && it.centre != recipientCentre }
        for (donor in potentialDonors) {
            if (remainingShortfall <= 0) break
            val donorSurplus = StockCalculator.calculateSurplus(donor.stock, donor.dailyUseLast7, safetyDays)
            if (donorSurplus > 0) {
                val take = min(donorSurplus, remainingShortfall)
                allocations.add(donor.centre to take)
                remainingShortfall -= take
            }
        }

        return MultiDonorTransfer(
            medicine = medicine,
            recipientCentre = recipientCentre,
            allocations = allocations,
            totalQuantity = allocations.sumOf { it.second }
        )
    }

    fun trackDeliveryRoute(proposal: TransferProposal): DeliveryTrackingInfo {
        val eta = (proposal.transfer.distanceKm * 3.0).toInt().coerceAtLeast(15)
        val status = when (proposal.status) {
            TransferApprovalStatus.APPROVED -> "IN_TRANSIT"
            TransferApprovalStatus.REJECTED -> "CANCELLED"
            TransferApprovalStatus.PENDING -> "PENDING_APPROVAL"
        }
        return DeliveryTrackingInfo(
            transferId = proposal.id,
            status = status,
            etaMinutes = eta
        )
    }

    fun isEmergencyEscalationRequired(daysToStockOut: Double): Boolean {
        return daysToStockOut < 2.0
    }

    fun isFairDonorBufferMaintained(donorStock: Int, donorDailyUse: Double, transferQty: Int, safetyDays: Int = 14): Boolean {
        val remainingStock = donorStock - transferQty
        val remainingDays = StockCalculator.calculateDaysToStockOut(remainingStock, donorDailyUse)
        return remainingDays >= safetyDays
    }

    fun answerTamilVoiceQuery(queryText: String, analyses: List<StockAnalysis>): String {
        val atRisk = analyses.filter { it.isAtRisk }
        return if (atRisk.isNotEmpty()) {
            "இந்த வாரம் கையிருப்பு குறையும் மருந்துகள்: ${atRisk.joinToString { it.record.medicine }}"
        } else {
            "அனைத்து மருந்துகளும் போதிய அளவில் உள்ளன."
        }
    }

    fun prepareOfflineSyncQueue(pendingMutations: List<String>): List<String> {
        return pendingMutations.map { "SYNC_ITEM: $it" }
    }

    fun generateWhatsappSmsAlert(proposal: TransferProposal): String {
        val tr = proposal.transfer
        return "StockSafe Alert: Move ${tr.quantity} units of ${tr.medicine} from ${tr.fromCentre} to ${tr.toCentre} (Dist: ${tr.distanceKm} km)."
    }

    fun parseBarcodeQrCode(qrText: String): QrMedicineData? {
        val tokens = qrText.split("|").map { it.trim() }
        if (tokens.size >= 4) {
            return QrMedicineData(
                medicine = tokens[0],
                batchNumber = tokens[1],
                expiryDate = tokens[2],
                quantity = tokens[3].toIntOrNull() ?: 1
            )
        }
        return null
    }

    fun generateOneClickDailyReport(analyses: List<StockAnalysis>, proposals: List<TransferProposal>): String {
        val atRiskCount = analyses.count { it.isAtRisk }
        val pendingCount = proposals.count { it.status == TransferApprovalStatus.PENDING }
        return "Daily Stock Summary Report: Total Medicines Analyzed = ${analyses.size}, At-Risk = $atRiskCount, Pending Transfers = $pendingCount."
    }

    fun calculateWasteReductionScore(transfers: List<Transfer>): Double {
        val reallocatedUnits = transfers.sumOf { it.quantity }
        return (reallocatedUnits * 100.0 / 1000.0).coerceAtMost(100.0)
    }

    fun calculateCarbonAwareSavings(transfers: List<Transfer>): Double {
        return transfers.sumOf { it.distanceKm * 0.15 }
    }

    fun calculateEquityPriorityScore(isRemotePhc: Boolean, shortfall: Int): Double {
        val remoteMultiplier = if (isRemotePhc) 1.5 else 1.0
        return shortfall * remoteMultiplier
    }

    fun generateCommunityImpactSummary(analyses: List<StockAnalysis>, transfers: List<Transfer>): String {
        val phcsCovered = analyses.map { it.record.centre }.distinct().size
        val unitsMoved = transfers.sumOf { it.quantity }
        return "Community Impact: $phcsCovered PHCs monitored | $unitsMoved medicine units reallocated locally."
    }

    fun createAnonymousShortageReport(centre: String, medicine: String, shortfallQty: Int): AnonymousReport {
        return AnonymousReport(
            reportId = "ANON-${System.currentTimeMillis() % 100000}",
            centre = centre,
            medicine = medicine,
            reportedShortfall = shortfallQty,
            timestamp = System.currentTimeMillis()
        )
    }
}
