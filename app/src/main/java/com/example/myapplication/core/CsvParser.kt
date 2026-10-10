package com.example.myapplication.core

/**
 * Pure Kotlin utility for parsing CSV string data into StockRecord and DistanceRecord instances.
 */
object CsvParser {

    /**
     * Parses stock CSV content.
     * Expected CSV header format: centre,medicine,stock,daily_use_last7,daily_use_prior7,expiry_date,importance
     */
    fun parse(csvContent: String): List<StockRecord> {
        val lines = csvContent.lines()
        if (lines.isEmpty()) return emptyList()

        val records = mutableListOf<StockRecord>()

        val dataLines = if (lines.first().contains("centre", ignoreCase = true)) {
            lines.drop(1)
        } else {
            lines
        }

        for (line in dataLines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val tokens = trimmed.split(",").map { it.trim() }
            if (tokens.size >= 4) {
                val centre = tokens[0]
                val medicine = tokens[1]
                val stock = tokens[2].toIntOrNull() ?: 0
                val dailyUseLast7 = tokens[3].toDoubleOrNull() ?: 0.0
                val dailyUsePrior7 = if (tokens.size >= 5) tokens[4].toDoubleOrNull() ?: dailyUseLast7 else dailyUseLast7
                val expiryDate = if (tokens.size >= 6) tokens[5] else "2026-12-31"
                val importance = if (tokens.size >= 7) tokens[6].toIntOrNull() ?: 2 else 2

                records.add(
                    StockRecord(
                        centre = centre,
                        medicine = medicine,
                        stock = stock,
                        dailyUseLast7 = dailyUseLast7,
                        dailyUsePrior7 = dailyUsePrior7,
                        expiryDate = expiryDate,
                        importance = importance
                    )
                )
            }
        }

        return records
    }

    /**
     * Parses distances CSV content.
     * Expected CSV header format: centre_a,centre_b,km
     */
    fun parseDistances(csvContent: String): List<DistanceRecord> {
        val lines = csvContent.lines()
        if (lines.isEmpty()) return emptyList()

        val distances = mutableListOf<DistanceRecord>()

        val dataLines = if (lines.first().contains("centre", ignoreCase = true) || lines.first().contains("from", ignoreCase = true)) {
            lines.drop(1)
        } else {
            lines
        }

        for (line in dataLines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val tokens = trimmed.split(",").map { it.trim() }
            if (tokens.size >= 3) {
                val from = tokens[0]
                val to = tokens[1]
                val dist = tokens[2].toDoubleOrNull() ?: 10.0
                distances.add(DistanceRecord(fromCentre = from, toCentre = to, distanceKm = dist))
            }
        }

        return distances
    }
}
