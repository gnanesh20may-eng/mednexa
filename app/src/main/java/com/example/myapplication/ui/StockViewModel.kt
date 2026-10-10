package com.example.myapplication.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.core.ApprovalAction
import com.example.myapplication.core.CsvParser
import com.example.myapplication.core.DistanceRecord
import com.example.myapplication.core.DistrictIndent
import com.example.myapplication.core.LedgerEntry
import com.example.myapplication.core.LedgerEventType
import com.example.myapplication.core.LedgerManager
import com.example.myapplication.core.StockAnalysis
import com.example.myapplication.core.StockCalculator
import com.example.myapplication.core.StockRecord
import com.example.myapplication.core.StockStatus
import com.example.myapplication.core.TransferApprovalStatus
import com.example.myapplication.core.TransferProposal
import com.example.myapplication.data.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for StockSafe holding stock records, distance matrix, transfer proposals, district indents, and ledger logs.
 */
data class StockUiState(
    val analyses: List<StockAnalysis> = emptyList(),
    val filteredAnalyses: List<StockAnalysis> = emptyList(),
    val alerts: List<StockAnalysis> = emptyList(),
    val proposals: List<TransferProposal> = emptyList(),
    val districtIndents: List<DistrictIndent> = emptyList(),
    val ledgerEntries: List<LedgerEntry> = emptyList(),
    val audit30DayEntries: List<LedgerEntry> = emptyList(),
    val centres: List<String> = emptyList(),
    val medicines: List<String> = emptyList(),
    val daysUntilIndent: Int = StockCalculator.DEFAULT_DAYS_UNTIL_INDENT,
    val safetyDays: Int = StockCalculator.DEFAULT_SAFETY_DAYS,
    val selectedCentre: String? = null,
    val selectedMedicine: String? = null,
    val selectedAuditCentre: String = "PHC Keelavasal",
    val selectedAuditMedicine: String = "Paracetamol syrup",
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

/**
 * ViewModel managing StockSafe state, persistence, pharmacist approvals, and audit ledger logs.
 */
class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val _records = MutableStateFlow<List<StockRecord>>(emptyList())
    private val _distances = MutableStateFlow<List<DistanceRecord>>(emptyList())
    private val _proposals = MutableStateFlow<List<TransferProposal>>(emptyList())
    private val _ledgerEntries = MutableStateFlow<List<LedgerEntry>>(emptyList())

    private val _daysUntilIndent = MutableStateFlow(StockCalculator.DEFAULT_DAYS_UNTIL_INDENT)
    private val _safetyDays = MutableStateFlow(StockCalculator.DEFAULT_SAFETY_DAYS)

    private val _selectedCentre = MutableStateFlow<String?>(null)
    private val _selectedMedicine = MutableStateFlow<String?>(null)

    private val _selectedAuditCentre = MutableStateFlow("PHC Keelavasal")
    private val _selectedAuditMedicine = MutableStateFlow("Paracetamol syrup")

    private val _searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * Loads stock records, distances, proposals, and ledger from local persistence or seed assets.
     */
    fun loadData() {
        val context = getApplication<Application>()

        val savedRecords = StockRepository.loadStockRecords(context)
        val savedDistances = StockRepository.loadDistances(context)
        val savedProposals = StockRepository.loadProposals(context)
        val savedLedger = StockRepository.loadLedgerEntries(context)
        val savedSettings = StockRepository.loadSettings(context)

        if (savedSettings != null) {
            _daysUntilIndent.value = savedSettings.first
            _safetyDays.value = savedSettings.second
        }

        if (savedDistances != null) {
            _distances.value = savedDistances
        } else {
            loadDistancesFromAsset()
        }

        if (savedRecords != null) {
            _records.value = savedRecords
        } else {
            loadRecordsFromAsset()
        }

        if (savedLedger != null) {
            _ledgerEntries.value = savedLedger
        }

        if (savedProposals != null) {
            _proposals.value = savedProposals
        } else {
            generateFreshProposals()
        }

        recalculateUiState()
    }

    private fun loadRecordsFromAsset() {
        try {
            val assetManager = getApplication<Application>().assets
            val csvText = assetManager.open("seed.csv").bufferedReader().use { it.readText() }
            val parsedRecords = CsvParser.parse(csvText)
            _records.value = parsedRecords
            StockRepository.saveStockRecords(getApplication(), parsedRecords)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDistancesFromAsset() {
        try {
            val assetManager = getApplication<Application>().assets
            val csvText = assetManager.open("distances.csv").bufferedReader().use { it.readText() }
            val parsedDistances = CsvParser.parseDistances(csvText)
            _distances.value = parsedDistances
            StockRepository.saveDistances(getApplication(), parsedDistances)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateFreshProposals() {
        val plan = StockCalculator.calculateTransfersAndIndents(
            records = _records.value,
            distances = _distances.value,
            daysUntilIndent = _daysUntilIndent.value,
            safetyDays = _safetyDays.value
        )

        val freshProposals = plan.transfers.map { tr ->
            TransferProposal(
                transfer = tr,
                status = TransferApprovalStatus.PENDING
            )
        }

        _proposals.value = freshProposals
        StockRepository.saveProposals(getApplication(), freshProposals)
    }

    fun setDaysUntilIndent(value: Int) {
        val clamped = value.coerceIn(1, 100)
        _daysUntilIndent.value = clamped
        StockRepository.saveSettings(getApplication(), clamped, _safetyDays.value)
        generateFreshProposals()
        recalculateUiState()
    }

    fun setSafetyDays(value: Int) {
        val clamped = value.coerceIn(1, 100)
        _safetyDays.value = clamped
        StockRepository.saveSettings(getApplication(), _daysUntilIndent.value, clamped)
        generateFreshProposals()
        recalculateUiState()
    }

    fun setCentreFilter(centre: String?) {
        _selectedCentre.value = centre
        recalculateUiState()
    }

    fun setMedicineFilter(medicine: String?) {
        _selectedMedicine.value = medicine
        recalculateUiState()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        recalculateUiState()
    }

    fun setAuditFilter(centre: String, medicine: String) {
        _selectedAuditCentre.value = centre
        _selectedAuditMedicine.value = medicine
        recalculateUiState()
    }

    /**
     * Pharmacist approves a proposed transfer:
     * - Uses StockCalculator.processDecision to evaluate decision and update stock
     * - Records entry in audit transaction ledger
     */
    fun approveTransfer(proposalId: String, pharmacistName: String) {
        val currentProposals = _proposals.value.toMutableList()
        val index = currentProposals.indexOfFirst { it.id == proposalId }
        if (index == -1) return

        val targetProposal = currentProposals[index]
        val (updatedProposal, updatedRecords) = StockCalculator.processDecision(
            records = _records.value,
            proposal = targetProposal,
            action = ApprovalAction.APPROVE,
            pharmacistName = pharmacistName
        )

        if (updatedProposal == targetProposal) return

        currentProposals[index] = updatedProposal
        _proposals.value = currentProposals
        _records.value = updatedRecords

        val ledgerEntry = LedgerManager.createTransferEntry(updatedProposal, pharmacistName)
        _ledgerEntries.update { listOf(ledgerEntry) + it }

        val context = getApplication<Application>()
        StockRepository.saveStockRecords(context, _records.value)
        StockRepository.saveProposals(context, _proposals.value)
        StockRepository.saveLedgerEntries(context, _ledgerEntries.value)

        recalculateUiState()
    }

    /**
     * Pharmacist rejects a proposed transfer:
     * - Uses StockCalculator.processDecision to mark proposal REJECTED
     * - Leaves stock levels completely unchanged
     */
    fun rejectTransfer(proposalId: String) {
        val currentProposals = _proposals.value.toMutableList()
        val index = currentProposals.indexOfFirst { it.id == proposalId }
        if (index == -1) return

        val targetProposal = currentProposals[index]
        val (updatedProposal, updatedRecords) = StockCalculator.processDecision(
            records = _records.value,
            proposal = targetProposal,
            action = ApprovalAction.REJECT,
            pharmacistName = ""
        )

        if (updatedProposal == targetProposal) return

        currentProposals[index] = updatedProposal
        _proposals.value = currentProposals
        _records.value = updatedRecords

        StockRepository.saveProposals(getApplication(), _proposals.value)

        recalculateUiState()
    }

    /**
     * Record a dispense action, regenerate pending proposals live, and log to audit ledger.
     */
    fun dispenseStock(centre: String, medicine: String, quantity: Int = 1) {
        _records.update { currentRecords ->
            currentRecords.map { rec ->
                if (rec.centre == centre && rec.medicine == medicine) {
                    val newStock = (rec.stock - quantity).coerceAtLeast(0)
                    rec.copy(stock = newStock)
                } else {
                    rec
                }
            }
        }

        val dispenseEntry = LedgerManager.createDispenseEntry(centre, medicine, quantity)
        _ledgerEntries.update { listOf(dispenseEntry) + it }

        generateFreshProposals()

        val context = getApplication<Application>()
        StockRepository.saveStockRecords(context, _records.value)
        StockRepository.saveLedgerEntries(context, _ledgerEntries.value)

        recalculateUiState()
    }

    /**
     * Add stock (replenishment), regenerate pending proposals live, and log to audit ledger.
     */
    fun addStock(centre: String, medicine: String, quantity: Int) {
        _records.update { currentRecords ->
            currentRecords.map { rec ->
                if (rec.centre == centre && rec.medicine == medicine) {
                    rec.copy(stock = rec.stock + quantity)
                } else {
                    rec
                }
            }
        }

        val replenishEntry = LedgerEntry(
            eventType = LedgerEventType.REPLENISHMENT,
            centre = centre,
            medicine = medicine,
            quantity = quantity
        )
        _ledgerEntries.update { listOf(replenishEntry) + it }

        generateFreshProposals()

        val context = getApplication<Application>()
        StockRepository.saveStockRecords(context, _records.value)
        StockRepository.saveLedgerEntries(context, _ledgerEntries.value)

        recalculateUiState()
    }

    /**
     * Clears local storage and resets everything to original seed data.
     */
    fun resetToSeed() {
        val context = getApplication<Application>()
        StockRepository.clearAll(context)
        _ledgerEntries.value = emptyList()
        _daysUntilIndent.value = StockCalculator.DEFAULT_DAYS_UNTIL_INDENT
        _safetyDays.value = StockCalculator.DEFAULT_SAFETY_DAYS
        loadRecordsFromAsset()
        loadDistancesFromAsset()
        generateFreshProposals()
        recalculateUiState()
    }

    private fun recalculateUiState() {
        val recordsList = _records.value
        val distancesList = _distances.value
        val indentDays = _daysUntilIndent.value
        val sDays = _safetyDays.value
        val centreFilter = _selectedCentre.value
        val medFilter = _selectedMedicine.value
        val query = _searchQuery.value.trim().lowercase()

        val allAnalyses = recordsList.map { rec ->
            StockCalculator.analyzeRecord(rec, daysUntilIndent = indentDays, safetyDays = sDays)
        }

        val alertsList = allAnalyses.filter { it.status != StockStatus.OK }
            .sortedBy { it.daysToStockOut }

        val plan = StockCalculator.calculateTransfersAndIndents(
            records = recordsList,
            distances = distancesList,
            daysUntilIndent = indentDays,
            safetyDays = sDays
        )

        val uniqueCentres = recordsList.map { it.centre }.distinct().sorted()
        val uniqueMedicines = recordsList.map { it.medicine }.distinct().sorted()

        val filtered = allAnalyses.filter { analysis ->
            val matchesCentre = centreFilter == null || analysis.record.centre == centreFilter
            val matchesMed = medFilter == null || analysis.record.medicine == medFilter
            val matchesSearch = query.isEmpty() ||
                    analysis.record.centre.lowercase().contains(query) ||
                    analysis.record.medicine.lowercase().contains(query)

            matchesCentre && matchesMed && matchesSearch
        }

        val auditEntries = LedgerManager.get30DayHistoryForCentreAndMedicine(
            entries = _ledgerEntries.value,
            centre = _selectedAuditCentre.value,
            medicine = _selectedAuditMedicine.value
        )

        _uiState.value = StockUiState(
            analyses = allAnalyses,
            filteredAnalyses = filtered,
            alerts = alertsList,
            proposals = _proposals.value,
            districtIndents = plan.districtIndents,
            ledgerEntries = _ledgerEntries.value,
            audit30DayEntries = auditEntries,
            centres = uniqueCentres,
            medicines = uniqueMedicines,
            daysUntilIndent = indentDays,
            safetyDays = sDays,
            selectedCentre = centreFilter,
            selectedMedicine = medFilter,
            selectedAuditCentre = _selectedAuditCentre.value,
            selectedAuditMedicine = _selectedAuditMedicine.value,
            searchQuery = _searchQuery.value,
            isLoading = false
        )
    }
}
