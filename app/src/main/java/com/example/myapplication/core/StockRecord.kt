package com.example.myapplication.core

/**
 * Represents a raw stock record for a medicine at a specific health centre.
 *
 * @property centre The name of the primary health centre (PHC).
 * @property medicine The name of the medicine.
 * @property stock Current stock quantity in units.
 * @property dailyUseLast7 Average daily consumption in the last 7 days.
 * @property dailyUsePrior7 Average daily consumption in the 7 days prior to the last 7 days.
 * @property expiryDate Expiry date string (YYYY-MM-DD).
 * @property importance Medicine criticality ranking (1 = Normal, 2 = High, 3 = Critical).
 */
data class StockRecord(
    val centre: String,
    val medicine: String,
    val stock: Int,
    val dailyUseLast7: Double,
    val dailyUsePrior7: Double,
    val expiryDate: String = "2026-12-31",
    val importance: Int = 2
)
