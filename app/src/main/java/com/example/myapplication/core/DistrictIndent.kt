package com.example.myapplication.core

/**
 * Represents a fallback indent request sent to the district medical store
 * for any shortfall remaining after exhausting nearby centre surplus stock.
 *
 * @property medicine Name of the medicine required.
 * @property centre Health centre requesting district support.
 * @property quantity Remainder quantity required from district store.
 * @property reason Explanation of why district indent was raised.
 */
data class DistrictIndent(
    val medicine: String,
    val centre: String,
    val quantity: Int,
    val reason: String
)
