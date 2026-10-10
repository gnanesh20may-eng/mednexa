package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.core.DistanceRecord
import com.example.myapplication.core.LedgerEntry
import com.example.myapplication.core.StockJson
import com.example.myapplication.core.StockRecord
import com.example.myapplication.core.TransferProposal
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local offline repository managing persistent storage of stock records, distances, transfer proposals,
 * and audit ledger entries using Android SharedPreferences and pure StockJson serialization.
 */
object StockRepository {

    private const val PREFS_NAME = "stock_safe_prefs"

    private const val KEY_RECORDS = "key_stock_records"
    private const val KEY_DISTANCES = "key_distances"
    private const val KEY_PROPOSALS = "key_proposals"
    private const val KEY_LEDGER = "key_ledger"
    private const val KEY_INDENT_DAYS = "key_indent_days"
    private const val KEY_SAFETY_DAYS = "key_safety_days"

    fun saveStockRecords(context: Context, records: List<StockRecord>) {
        val json = StockJson.encodeRecords(records)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_RECORDS, json)
            .apply()
    }

    fun loadStockRecords(context: Context): List<StockRecord>? {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_RECORDS, null) ?: return null

        return try {
            StockJson.decodeRecords(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveDistances(context: Context, distances: List<DistanceRecord>) {
        val array = JSONArray()
        distances.forEach { dist ->
            val obj = JSONObject().apply {
                put("fromCentre", dist.fromCentre)
                put("toCentre", dist.toCentre)
                put("distanceKm", dist.distanceKm)
            }
            array.put(obj)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_DISTANCES, array.toString())
            .apply()
    }

    fun loadDistances(context: Context): List<DistanceRecord>? {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DISTANCES, null) ?: return null

        return try {
            val array = JSONArray(json)
            val list = mutableListOf<DistanceRecord>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DistanceRecord(
                        fromCentre = obj.getString("fromCentre"),
                        toCentre = obj.getString("toCentre"),
                        distanceKm = obj.getDouble("distanceKm")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveProposals(context: Context, proposals: List<TransferProposal>) {
        val json = StockJson.encodeProposals(proposals)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PROPOSALS, json)
            .apply()
    }

    fun loadProposals(context: Context): List<TransferProposal>? {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PROPOSALS, null) ?: return null

        return try {
            StockJson.decodeProposals(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveLedgerEntries(context: Context, entries: List<LedgerEntry>) {
        val json = StockJson.encodeLedger(entries)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LEDGER, json)
            .apply()
    }

    fun loadLedgerEntries(context: Context): List<LedgerEntry>? {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LEDGER, null) ?: return null

        return try {
            StockJson.decodeLedger(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveSettings(context: Context, indentDays: Int, safetyDays: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_INDENT_DAYS, indentDays)
            .putInt(KEY_SAFETY_DAYS, safetyDays)
            .apply()
    }

    fun loadSettings(context: Context): Pair<Int, Int>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_INDENT_DAYS)) return null
        val indent = prefs.getInt(KEY_INDENT_DAYS, 20)
        val safety = prefs.getInt(KEY_SAFETY_DAYS, 14)
        return indent to safety
    }

    fun clearAll(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
