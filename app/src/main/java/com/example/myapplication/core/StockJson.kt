package com.example.myapplication.core

import org.json.JSONArray
import org.json.JSONObject

/**
 * Pure Kotlin utilities for JSON encoding and decoding of stock domain data structures.
 */
object StockJson {

    fun encodeRecords(records: List<StockRecord>): String {
        val array = JSONArray()
        records.forEach { rec ->
            val obj = JSONObject().apply {
                put("centre", rec.centre)
                put("medicine", rec.medicine)
                put("stock", rec.stock)
                put("dailyUseLast7", rec.dailyUseLast7)
                put("dailyUsePrior7", rec.dailyUsePrior7)
                put("expiryDate", rec.expiryDate)
                put("importance", rec.importance)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun decodeRecords(json: String): List<StockRecord> {
        if (json.isBlank()) return emptyList()
        val array = JSONArray(json)
        val list = mutableListOf<StockRecord>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val dailyLast7 = obj.optDouble("dailyUseLast7", obj.optDouble("dailyUse", 0.0))
            val dailyPrior7 = obj.optDouble("dailyUsePrior7", obj.optDouble("dailyUsePrevious7", dailyLast7))
            list.add(
                StockRecord(
                    centre = obj.getString("centre"),
                    medicine = obj.getString("medicine"),
                    stock = obj.getInt("stock"),
                    dailyUseLast7 = dailyLast7,
                    dailyUsePrior7 = dailyPrior7,
                    expiryDate = obj.optString("expiryDate", "2026-12-31"),
                    importance = obj.optInt("importance", 2)
                )
            )
        }
        return list
    }

    fun encodeProposals(proposals: List<TransferProposal>): String {
        val array = JSONArray()
        proposals.forEach { prop ->
            val obj = JSONObject().apply {
                put("id", prop.id)
                put("status", prop.status.name)
                put("timestamp", prop.timestamp)
                if (prop.approvedBy != null) {
                    put("approvedBy", prop.approvedBy)
                } else {
                    put("approvedBy", JSONObject.NULL)
                }

                val tr = JSONObject().apply {
                    put("medicine", prop.transfer.medicine)
                    put("fromCentre", prop.transfer.fromCentre)
                    put("toCentre", prop.transfer.toCentre)
                    put("quantity", prop.transfer.quantity)
                    put("distanceKm", prop.transfer.distanceKm)
                    put("donorScore", prop.transfer.donorScore)
                }
                put("transfer", tr)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun decodeProposals(json: String): List<TransferProposal> {
        if (json.isBlank()) return emptyList()
        val array = JSONArray(json)
        val list = mutableListOf<TransferProposal>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val trObj = obj.getJSONObject("transfer")
            val transfer = Transfer(
                medicine = trObj.getString("medicine"),
                fromCentre = trObj.getString("fromCentre"),
                toCentre = trObj.getString("toCentre"),
                quantity = trObj.getInt("quantity"),
                distanceKm = trObj.optDouble("distanceKm", 0.0),
                donorScore = trObj.optDouble("donorScore", 0.0)
            )

            val approvedBy = if (obj.isNull("approvedBy")) null else obj.getString("approvedBy")

            list.add(
                TransferProposal(
                    id = obj.getString("id"),
                    transfer = transfer,
                    status = TransferApprovalStatus.valueOf(obj.getString("status")),
                    timestamp = obj.getLong("timestamp"),
                    approvedBy = approvedBy
                )
            )
        }
        return list
    }

    fun encodeLedger(entries: List<LedgerEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            val obj = JSONObject().apply {
                put("id", entry.id)
                put("eventType", entry.eventType.name)
                put("centre", entry.centre)
                put("medicine", entry.medicine)
                put("quantity", entry.quantity)
                put("senderCentre", entry.senderCentre ?: "")
                put("receiverCentre", entry.receiverCentre ?: "")
                put("distanceKm", entry.distanceKm ?: 0.0)
                put("timestamp", entry.timestamp)
                put("dateFormatted", entry.dateFormatted)
                put("actor", entry.actor)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun decodeLedger(json: String): List<LedgerEntry> {
        if (json.isBlank()) return emptyList()
        val array = JSONArray(json)
        val list = mutableListOf<LedgerEntry>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                LedgerEntry(
                    id = obj.getString("id"),
                    eventType = LedgerEventType.valueOf(obj.getString("eventType")),
                    centre = obj.getString("centre"),
                    medicine = obj.getString("medicine"),
                    quantity = obj.getInt("quantity"),
                    senderCentre = obj.optString("senderCentre", "").ifEmpty { null },
                    receiverCentre = obj.optString("receiverCentre", "").ifEmpty { null },
                    distanceKm = obj.optDouble("distanceKm", 0.0),
                    timestamp = obj.getLong("timestamp"),
                    dateFormatted = obj.optString("dateFormatted", ""),
                    actor = obj.optString("actor", "Pharmacist")
                )
            )
        }
        return list
    }
}
