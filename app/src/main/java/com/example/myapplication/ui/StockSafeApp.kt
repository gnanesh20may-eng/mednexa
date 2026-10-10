package com.example.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.core.AdvancedAnalytics
import com.example.myapplication.ui.components.AlertsPanel
import com.example.myapplication.ui.components.AnonymousShortageDialog
import com.example.myapplication.ui.components.BlockOverviewGrid
import com.example.myapplication.ui.components.DailyReportDialog
import com.example.myapplication.ui.components.DashboardList
import com.example.myapplication.ui.components.ImpactCard
import com.example.myapplication.ui.components.LedgerTabContent
import com.example.myapplication.ui.components.MedicineDetailView
import com.example.myapplication.ui.components.SettingsSection
import com.example.myapplication.ui.components.SimulatedFeaturesPanel
import com.example.myapplication.ui.components.TransfersPanel
import com.example.myapplication.ui.theme.NotoSansTamilFontFamily
import com.example.myapplication.ui.theme.StockSafeTheme
import kotlinx.coroutines.launch

/**
 * Main Composable layout for StockSafe wrapped in StockSafeTheme.
 * Provides live Snackbar feedback for approvals and dispenses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockSafeApp(viewModel: StockViewModel) {
    StockSafeTheme {
        val uiState by viewModel.uiState.collectAsState()
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        var showDailyReportDialog by remember { mutableStateOf(false) }
        var showAnonymousReportDialog by remember { mutableStateOf(false) }

        val impactMetrics = remember(uiState.proposals, uiState.analyses) {
            AdvancedAnalytics.generateSavedByTransferReport(
                uiState.proposals.map { it.transfer }
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "StockSafe 🏥",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "வட்டார மருந்து இருப்பு பாதுகாப்பு அமைப்பு",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = NotoSansTamilFontFamily,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        OutlinedButton(
                            onClick = { showDailyReportDialog = true },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text("Daily Report", color = MaterialTheme.colorScheme.onPrimary)
                        }
                        OutlinedButton(
                            onClick = { showAnonymousReportDialog = true },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text("Report Shortage", color = MaterialTheme.colorScheme.onPrimary)
                        }
                        OutlinedButton(
                            onClick = { viewModel.resetToSeed() },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Reset Seed", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // PrimaryTabRow: Dashboard, Alerts, Transfers, Ledger, Block View, Simulated (18–26), Settings
                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Dashboard") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Alerts (${uiState.alerts.size})") }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Transfers (${uiState.proposals.size})") }
                    )
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        text = { Text("Ledger (${uiState.ledgerEntries.size})") }
                    )
                    Tab(
                        selected = selectedTabIndex == 4,
                        onClick = { selectedTabIndex = 4 },
                        text = { Text("Block View") }
                    )
                    Tab(
                        selected = selectedTabIndex == 5,
                        onClick = { selectedTabIndex = 5 },
                        text = { Text("Simulated (18–26)") }
                    )
                    Tab(
                        selected = selectedTabIndex == 6,
                        onClick = { selectedTabIndex = 6 },
                        text = { Text("Settings") }
                    )
                }

                if (selectedTabIndex == 0) {
                    FilterBar(
                        searchQuery = uiState.searchQuery,
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        selectedCentre = uiState.selectedCentre,
                        allCentres = uiState.centres,
                        onCentreSelected = { viewModel.setCentreFilter(it) },
                        selectedMedicine = uiState.selectedMedicine,
                        allMedicines = uiState.medicines,
                        onMedicineSelected = { viewModel.setMedicineFilter(it) }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            ImpactCard(metrics = impactMetrics)
                            AlertsPanel(alerts = uiState.alerts)

                            val atRiskKeelavasal = uiState.analyses.firstOrNull {
                                it.record.centre == "PHC Keelavasal" && it.record.medicine == "Paracetamol syrup"
                            }
                            if (atRiskKeelavasal != null) {
                                MedicineDetailView(analysis = atRiskKeelavasal)
                            }

                            DashboardList(
                                analyses = uiState.filteredAnalyses,
                                onDispenseStock = { centre, med, qty ->
                                    viewModel.dispenseStock(centre, med, qty)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Dispensed $qty units of $med at $centre.")
                                    }
                                },
                                onAddStock = { centre, med, qty ->
                                    viewModel.addStock(centre, med, qty)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Added $qty units of $med at $centre.")
                                    }
                                }
                            )
                        }
                        1 -> {
                            AlertsPanel(alerts = uiState.alerts)
                        }
                        2 -> {
                            TransfersPanel(
                                proposals = uiState.proposals,
                                districtIndents = uiState.districtIndents,
                                onApproveTransfer = { id, name ->
                                    viewModel.approveTransfer(id, name)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Transfer $id approved by $name.")
                                    }
                                },
                                onRejectTransfer = { id ->
                                    viewModel.rejectTransfer(id)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Transfer $id rejected.")
                                    }
                                }
                            )
                        }
                        3 -> {
                            LedgerTabContent(
                                ledgerEntries = uiState.ledgerEntries,
                                audit30DayEntries = uiState.audit30DayEntries,
                                centres = uiState.centres,
                                medicines = uiState.medicines,
                                selectedAuditCentre = uiState.selectedAuditCentre,
                                selectedAuditMedicine = uiState.selectedAuditMedicine,
                                onAuditFilterChanged = { centre, med ->
                                    viewModel.setAuditFilter(centre, med)
                                }
                            )
                        }
                        4 -> {
                            BlockOverviewGrid(analyses = uiState.analyses)
                        }
                        5 -> {
                            SimulatedFeaturesPanel()
                        }
                        6 -> {
                            SettingsSection(
                                daysUntilIndent = uiState.daysUntilIndent,
                                safetyDays = uiState.safetyDays,
                                onDaysUntilIndentChanged = { viewModel.setDaysUntilIndent(it) },
                                onSafetyDaysChanged = { viewModel.setSafetyDays(it) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.resetToSeed()
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Seed stock data reset successfully.")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reset Stock Data & Clear Persistence")
                            }
                        }
                    }
                }
            }
        }

        if (showDailyReportDialog) {
            DailyReportDialog(
                analyses = uiState.analyses,
                proposals = uiState.proposals,
                onDismiss = { showDailyReportDialog = false }
            )
        }

        if (showAnonymousReportDialog) {
            AnonymousShortageDialog(
                allCentres = uiState.centres,
                allMedicines = uiState.medicines,
                onSubmitReport = { centre, med, qty ->
                    viewModel.addStock(centre, med, qty)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Anonymous shortage report submitted for $med at $centre.")
                    }
                },
                onDismiss = { showAnonymousReportDialog = false }
            )
        }
    }
}

@Composable
private fun FilterBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedCentre: String?,
    allCentres: List<String>,
    onCentreSelected: (String?) -> Unit,
    selectedMedicine: String?,
    allMedicines: List<String>,
    onMedicineSelected: (String?) -> Unit
) {
    var centreMenuExpanded by remember { mutableStateOf(false) }
    var medicineMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search health centre or medicine...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { centreMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedCentre ?: "All Centres",
                        maxLines = 1
                    )
                }
                DropdownMenu(
                    expanded = centreMenuExpanded,
                    onDismissRequest = { centreMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Centres") },
                        onClick = {
                            onCentreSelected(null)
                            centreMenuExpanded = false
                        }
                    )
                    allCentres.forEach { centre ->
                        DropdownMenuItem(
                            text = { Text(centre) },
                            onClick = {
                                onCentreSelected(centre)
                                centreMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { medicineMenuExpanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedMedicine ?: "All Medicines",
                        maxLines = 1
                    )
                }
                DropdownMenu(
                    expanded = medicineMenuExpanded,
                    onDismissRequest = { medicineMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Medicines") },
                        onClick = {
                            onMedicineSelected(null)
                            medicineMenuExpanded = false
                        }
                    )
                    allMedicines.forEach { medicine ->
                        DropdownMenuItem(
                            text = { Text(medicine) },
                            onClick = {
                                onMedicineSelected(medicine)
                                medicineMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
