package com.example.myapplication.core

/**
 * Represents a suggested stock transfer recommendation from a surplus centre to an at-risk centre.
 *
 * @property medicine Name of the medicine to transfer.
 * @property fromCentre Name of the donor health centre.
 * @property toCentre Name of the recipient health centre.
 * @property quantity Number of units recommended for transfer.
 * @property distanceKm Road distance between donor and recipient in kilometers.
 * @property donorScore Score used for ranking donors: surplus / distanceKm.
 */
data class Transfer(
    val medicine: String,
    val fromCentre: String,
    val toCentre: String,
    val quantity: Int,
    val distanceKm: Double = 0.0,
    val donorScore: Double = 0.0
)
