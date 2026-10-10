package com.example.myapplication.core

/**
 * Represents road distance in kilometers between two health centres.
 */
data class DistanceRecord(
    val fromCentre: String,
    val toCentre: String,
    val distanceKm: Double
)
