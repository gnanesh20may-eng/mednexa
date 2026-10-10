package com.example.myapplication.core

/**
 * Calculated analysis result for a single stock record.
 */
data class StockAnalysis(
    val record: StockRecord,
    val daysToStockOut: Double,
    val burnRate: Double,
    val isAtRisk: Boolean,
    val isAccelerating: Boolean,
    val isSpeedUp: Boolean,
    val isUnexpectedDemand: Boolean,
    val emergencyScore: Double,
    val confidenceRating: String,
    val status: StockStatus,
    val surplus: Int,
    val shortfall: Int
)
