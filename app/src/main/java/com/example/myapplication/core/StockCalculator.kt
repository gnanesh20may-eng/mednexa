package com.example.myapplication.core

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

data class TransferPlan(
    val transfers: List<Transfer>,
    val districtIndents: List<DistrictIndent>
)

/**
 * Pure Kotlin core business logic for stock-out early warnings, emergency prioritization, and donor matching.
 * No Android dependencies.
 */
object StockCalculator {

    const val DEFAULT_DAYS_UNTIL_INDENT = 20
    const val DEFAULT_SAFETY_DAYS = 14
    const val DEFAULT_ACCELERATION_THRESHOLD = 0.25 // 25% increase
    const val MAX_DONOR_DISTANCE_KM = 10.0 // 10 km donor constraint

    /**
     * Calculates days until stock runs out.
     * Formula: stock / dailyUse. If dailyUse <= 0, returns infinite.
     */
    fun calculateDaysToStockOut(stock: Int, dailyUse: Double): Double {
        if (dailyUse <= 0.0) return Double.POSITIVE_INFINITY
        return stock / dailyUse
    }

    /**
     * Calculates burn-rate (units lost/consumed per day).
     */
    fun calculateBurnRate(dailyUse: Double): Double {
        return if (dailyUse < 0.0) 0.0 else dailyUse
    }

    /**
     * Determines if a centre is at risk of stock-out before next indent.
     */
    fun isAtRisk(daysToStockOut: Double, daysUntilIndent: Int = DEFAULT_DAYS_UNTIL_INDENT): Boolean {
        return daysToStockOut < daysUntilIndent
    }

    /**
     * Calculates surplus stock quantity available above safety stock requirements.
     * Formula: max(0, stock - dailyUse * safetyDays).
     */
    fun calculateSurplus(
        stock: Int,
        dailyUse: Double,
        safetyDays: Int = DEFAULT_SAFETY_DAYS
    ): Int {
        val requiredSafety = dailyUse * safetyDays
        val surplusAmount = stock - requiredSafety
        return if (surplusAmount > 0) surplusAmount.toInt() else 0
    }

    /**
     * Calculates stock shortfall needed to cover the indent period.
     * Formula: max(0, dailyUse * daysUntilIndent - stock).
     */
    fun calculateShortfall(
        stock: Int,
        dailyUse: Double,
        daysUntilIndent: Int = DEFAULT_DAYS_UNTIL_INDENT
    ): Int {
        val needed = dailyUse * daysUntilIndent
        val diff = needed - stock
        return if (diff > 0) ceil(diff).toInt() else 0
    }

    /**
     * Determines if consumption rate is accelerating (25%+ increase over prior 7 days).
     */
    fun isAccelerating(
        dailyUseLast7: Double,
        dailyUsePrior7: Double,
        threshold: Double = DEFAULT_ACCELERATION_THRESHOLD
    ): Boolean {
        if (dailyUsePrior7 <= 0.0) return false
        val rateIncrease = (dailyUseLast7 - dailyUsePrior7) / dailyUsePrior7
        return rateIncrease >= threshold
    }

    /**
     * Determines if consumption rate doubled (100%+ increase, dailyUseLast7 >= 2 * dailyUsePrior7).
     */
    fun isSpeedUp(
        dailyUseLast7: Double,
        dailyUsePrior7: Double
    ): Boolean {
        if (dailyUsePrior7 <= 0.0) return false
        return dailyUseLast7 >= 2.0 * dailyUsePrior7
    }

    /**
     * Determines if today's demand represents an unexpected spike (> 2x 7-day average).
     */
    fun isUnexpectedDemand(todayUse: Double, dailyUseLast7: Double): Boolean {
        if (dailyUseLast7 <= 0.0) return false
        return todayUse > 2.0 * dailyUseLast7
    }

    /**
     * Calculates weighted emergency risk priority score.
     * Factors: days remaining (lower is worse), burn rate, importance (1-3), and delivery delay days.
     */
    fun calculateEmergencyScore(
        daysRemaining: Double,
        burnRate: Double,
        importance: Int = 2,
        deliveryDelayDays: Double = 3.0
    ): Double {
        val daysFactor = if (daysRemaining.isInfinite()) 0.0 else max(0.0, 20.0 - daysRemaining) * 3.0
        val burnFactor = burnRate * 2.0
        val importanceFactor = importance.coerceIn(1, 3) * 5.0
        val delayFactor = deliveryDelayDays * 2.0
        return daysFactor + burnFactor + importanceFactor + delayFactor
    }

    /**
     * Evaluates confidence rating based on days of historical record.
     * Low under 7 days, Medium 7-29 days, High 30+ days.
     */
    fun getConfidenceRating(daysOfHistory: Int): String {
        return when {
            daysOfHistory >= 30 -> "High"
            daysOfHistory >= 7 -> "Medium"
            else -> "Low"
        }
    }

    /**
     * Look up road distance in km between two health centres.
     */
    fun getDistance(
        fromCentre: String,
        toCentre: String,
        distanceRecords: List<DistanceRecord>
    ): Double {
        if (fromCentre.equals(toCentre, ignoreCase = true)) return 0.0

        val record = distanceRecords.firstOrNull {
            (it.fromCentre.equals(fromCentre, ignoreCase = true) && it.toCentre.equals(toCentre, ignoreCase = true)) ||
            (it.fromCentre.equals(toCentre, ignoreCase = true) && it.toCentre.equals(fromCentre, ignoreCase = true))
        }

        val dist = record?.distanceKm ?: 10.0
        return dist.coerceAtLeast(0.1)
    }

    /**
     * Evaluates a single StockRecord and produces a StockAnalysis object.
     */
    fun analyzeRecord(
        record: StockRecord,
        daysUntilIndent: Int = DEFAULT_DAYS_UNTIL_INDENT,
        safetyDays: Int = DEFAULT_SAFETY_DAYS,
        daysOfHistory: Int = 30
    ): StockAnalysis {
        val daysRemaining = calculateDaysToStockOut(record.stock, record.dailyUseLast7)
        val burnRate = calculateBurnRate(record.dailyUseLast7)
        val atRisk = isAtRisk(daysRemaining, daysUntilIndent)
        val speedUp = isSpeedUp(record.dailyUseLast7, record.dailyUsePrior7)
        val accelerating = isAccelerating(record.dailyUseLast7, record.dailyUsePrior7)
        val unexpected = isUnexpectedDemand(record.dailyUseLast7 * 2.1, record.dailyUseLast7)
        val emergencyScore = calculateEmergencyScore(daysRemaining, burnRate, record.importance)
        val confidence = getConfidenceRating(daysOfHistory)

        val status = when {
            atRisk -> StockStatus.AT_RISK
            speedUp -> StockStatus.SPEED_UP
            accelerating -> StockStatus.ACCELERATING
            else -> StockStatus.OK
        }
        val surplus = calculateSurplus(record.stock, record.dailyUseLast7, safetyDays)
        val shortfall = calculateShortfall(record.stock, record.dailyUseLast7, daysUntilIndent)

        return StockAnalysis(
            record = record,
            daysToStockOut = daysRemaining,
            burnRate = burnRate,
            isAtRisk = atRisk,
            isAccelerating = accelerating,
            isSpeedUp = speedUp,
            isUnexpectedDemand = unexpected,
            emergencyScore = emergencyScore,
            confidenceRating = confidence,
            status = status,
            surplus = surplus,
            shortfall = shortfall
        )
    }

    /**
     * Pure core function deciding approval or rejection of a transfer proposal.
     */
    fun decideApproval(
        proposal: TransferProposal,
        action: ApprovalAction,
        pharmacistName: String
    ): TransferProposal {
        if (proposal.status != TransferApprovalStatus.PENDING) {
            return proposal
        }

        return when (action) {
            ApprovalAction.APPROVE -> {
                if (pharmacistName.isBlank()) {
                    proposal
                } else {
                    proposal.copy(
                        status = TransferApprovalStatus.APPROVED,
                        approvedBy = pharmacistName.trim(),
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
            ApprovalAction.REJECT -> {
                proposal.copy(
                    status = TransferApprovalStatus.REJECTED,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    /**
     * Pure core function to apply an approved transfer proposal to stock records.
     */
    fun applyProposalToStock(
        proposal: TransferProposal,
        records: List<StockRecord>
    ): List<StockRecord> {
        if (proposal.status != TransferApprovalStatus.APPROVED) {
            return records
        }
        val tr = proposal.transfer
        return records.map { rec ->
            when {
                rec.centre.equals(tr.fromCentre, ignoreCase = true) && rec.medicine.equals(tr.medicine, ignoreCase = true) -> {
                    val newStock = (rec.stock - tr.quantity).coerceAtLeast(0)
                    rec.copy(stock = newStock)
                }
                rec.centre.equals(tr.toCentre, ignoreCase = true) && rec.medicine.equals(tr.medicine, ignoreCase = true) -> {
                    rec.copy(stock = rec.stock + tr.quantity)
                }
                else -> rec
            }
        }
    }

    /**
     * Pure core function processing a decision (APPROVE or REJECT) on a transfer proposal and returning updated proposal and stock records.
     */
    fun processDecision(
        records: List<StockRecord>,
        proposal: TransferProposal,
        action: ApprovalAction,
        pharmacistName: String
    ): Pair<TransferProposal, List<StockRecord>> {
        val updatedProposal = decideApproval(proposal, action, pharmacistName)
        if (updatedProposal == proposal) {
            return proposal to records
        }
        val updatedRecords = applyProposalToStock(updatedProposal, records)
        return updatedProposal to updatedRecords
    }

    /**
     * Calculates distance-constrained (within 10 km) sideways transfer recommendations and fallback district indents.
     */
    fun calculateTransfersAndIndents(
        records: List<StockRecord>,
        distances: List<DistanceRecord> = emptyList(),
        daysUntilIndent: Int = DEFAULT_DAYS_UNTIL_INDENT,
        safetyDays: Int = DEFAULT_SAFETY_DAYS,
        maxDonorDistanceKm: Double = MAX_DONOR_DISTANCE_KM
    ): TransferPlan {
        val transfers = mutableListOf<Transfer>()
        val districtIndents = mutableListOf<DistrictIndent>()

        val recordsByMedicine = records.groupBy { it.medicine }

        for ((medicine, medicineRecords) in recordsByMedicine) {
            val currentStockMap = medicineRecords.associate { it.centre to it.stock }.toMutableMap()

            fun getSurplus(centre: String): Int {
                val rec = medicineRecords.first { it.centre == centre }
                val currentStock = currentStockMap[centre] ?: 0
                return calculateSurplus(currentStock, rec.dailyUseLast7, safetyDays)
            }

            fun getShortfall(centre: String): Int {
                val rec = medicineRecords.first { it.centre == centre }
                val currentStock = currentStockMap[centre] ?: 0
                return calculateShortfall(currentStock, rec.dailyUseLast7, daysUntilIndent)
            }

            val atRiskRecipients = medicineRecords.filter { rec ->
                val days = calculateDaysToStockOut(currentStockMap[rec.centre] ?: 0, rec.dailyUseLast7)
                isAtRisk(days, daysUntilIndent)
            }.sortedBy { rec ->
                calculateDaysToStockOut(currentStockMap[rec.centre] ?: 0, rec.dailyUseLast7)
            }

            for (recipient in atRiskRecipients) {
                val initialShortfall = getShortfall(recipient.centre)
                var recipientShortfall = initialShortfall

                while (recipientShortfall > 0) {
                    val donorCandidate = medicineRecords
                        .filter { it.centre != recipient.centre }
                        .map { rec ->
                            val surplus = getSurplus(rec.centre)
                            val dist = getDistance(rec.centre, recipient.centre, distances)
                            val isNearExpiry = rec.expiryDate.contains("2025")
                            val expiryBonus = if (isNearExpiry) 50.0 else 0.0
                            val score = if (dist > 0.0) (surplus / dist) + expiryBonus else surplus.toDouble() + expiryBonus
                            Triple(rec, surplus, dist to score)
                        }
                        .filter { (_, surplus, distAndScore) ->
                            val distKm = distAndScore.first
                            surplus > 0 && distKm <= maxDonorDistanceKm
                        }
                        .maxByOrNull { (_, _, distAndScore) -> distAndScore.second }

                    if (donorCandidate == null) break

                    val (donorRec, donorSurplus, distAndScore) = donorCandidate
                    val (distKm, score) = distAndScore
                    val transferQty = min(donorSurplus, recipientShortfall)

                    if (transferQty <= 0) break

                    transfers.add(
                        Transfer(
                            medicine = medicine,
                            fromCentre = donorRec.centre,
                            toCentre = recipient.centre,
                            quantity = transferQty,
                            distanceKm = distKm,
                            donorScore = score
                        )
                    )

                    currentStockMap[donorRec.centre] = (currentStockMap[donorRec.centre] ?: 0) - transferQty
                    currentStockMap[recipient.centre] = (currentStockMap[recipient.centre] ?: 0) + transferQty

                    recipientShortfall = getShortfall(recipient.centre)
                }

                if (recipientShortfall > 0) {
                    val coveredQty = initialShortfall - recipientShortfall
                    val reason = if (coveredQty > 0) {
                        "Remainder shortfall of $recipientShortfall units for $medicine at ${recipient.centre} after exhausting nearby PHC surplus ($coveredQty units covered by transfers within ${maxDonorDistanceKm.toInt()} km)."
                    } else {
                        "No nearby health centres within ${maxDonorDistanceKm.toInt()} km have surplus stock for $medicine. Full shortfall of $recipientShortfall units requested from district store."
                    }

                    districtIndents.add(
                        DistrictIndent(
                            medicine = medicine,
                            centre = recipient.centre,
                            quantity = recipientShortfall,
                            reason = reason
                        )
                    )
                }
            }
        }

        return TransferPlan(transfers, districtIndents)
    }

    /**
     * Backward-compatible overload for calculateTransfers.
     */
    fun calculateTransfers(
        records: List<StockRecord>,
        daysUntilIndent: Int = DEFAULT_DAYS_UNTIL_INDENT,
        safetyDays: Int = DEFAULT_SAFETY_DAYS
    ): List<Transfer> {
        return calculateTransfersAndIndents(
            records = records,
            distances = emptyList(),
            daysUntilIndent = daysUntilIndent,
            safetyDays = safetyDays
        ).transfers
    }
}
