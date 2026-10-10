# StockSafe Innovations Checklist

## Core analytics and early warnings (1–25)

- [x] 1. burn-rate meter — Evidence: `StockCalculator.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 2. days-left figure — Evidence: `StockCalculator.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 3. speed-up alert — Evidence: `StockCalculator.kt`, `StockStatus.kt`, `AlertsPanel.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 4. unexpected demand alert — Evidence: `StockCalculator.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 5. smart donor matching — Evidence: `StockCalculator.kt`, `Transfer.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 6. multi-PHC transfer plan — Evidence: `StockCalculator.kt`, `StockViewModel.kt`, `TransfersPanel.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 7. remainder-only district indent — Evidence: `StockCalculator.kt`, `DistrictIndent.kt`, `TransfersPanel.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 8. emergency score & priority — Evidence: `StockCalculator.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 9. confidence indicator — Evidence: `StockCalculator.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 10. record dispense — Evidence: `StockViewModel.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 11. transfer ledger — Evidence: `LedgerManager.kt`, `LedgerEntry.kt`, `LedgerTabContent.kt` (Test: `LedgerTest`) `[WORKING]`
- [x] 12. audit log — Evidence: `LedgerManager.kt`, `LedgerTabContent.kt` (Test: `LedgerTest`) `[WORKING]`
- [x] 13. daily one-click report — Evidence: `MediFlowInnovations.kt`, `DailyReportDialog.kt` (Test: `InnovationsTest`) `[WORKING]`
- [x] 14. anonymous shortage report — Evidence: `MediFlowInnovations.kt`, `AnonymousShortageDialog.kt` (Test: `InnovationsTest`) `[WORKING]`
- [x] 15. settings controls — Evidence: `StockViewModel.kt`, `SettingsSection.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [x] 16. impact card — Evidence: `AdvancedAnalytics.kt`, `ImpactCard.kt` (Test: `InnovationsTest`) `[WORKING]`
- [x] 17. Dashboard view — Evidence: `StockSafeApp.kt`, `DashboardList.kt` (Test: `StockCalculatorTest`) `[WORKING]`
- [~] 18. seasonal disease forecast — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`
- [~] 19. medicine expiry alerts — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`
- [~] 20. fair distribution check — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`
- [~] 21. multi-centre block officer view — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`
- [~] 22. demand surge input by catchment — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`
- [~] 23. emergency transfer mode — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`
- [~] 24. pharmacist approval step — Evidence: `StockCalculator.kt`, `TransfersPanel.kt` (Test: `PharmacistApprovalTest`) `[SIMULATED]`
- [~] 25. offline mode & sync indicator — Evidence: `StockRepository.kt`, `SimulatedFeaturesPanel.kt` (Test: `StockJsonTest`) `[SIMULATED]`
- [~] 26. barcode or QR entry — Evidence: `MediFlowInnovations.kt`, `SimulatedFeaturesPanel.kt` (Test: `InnovationsTest`) `[SIMULATED]`

## Roadmap innovations (27–45)

- [ ] 27. Live route and delivery tracking `[ROADMAP]`
- [ ] 28. WhatsApp and SMS alerts `[ROADMAP]`
- [ ] 29. Tamil voice assistant `[ROADMAP]`
- [ ] 30. Patient-facing SMS availability check `[ROADMAP]`
- [ ] 31. Carbon-aware route combining `[ROADMAP]`
- [ ] 32. Community impact dashboard `[ROADMAP]`
- [ ] 33. Equity priority scoring `[ROADMAP]`
- [ ] 34. Waste reduction score `[ROADMAP]`
- [ ] 35. Auto-filled monthly indent form `[ROADMAP]`
- [ ] 36. Recall handling for withdrawn medicines `[ROADMAP]`
- [ ] 37. Patient queue estimate `[ROADMAP]`
- [ ] 38. Stock-out heat map `[ROADMAP]`
- [ ] 39. Fever-visit link `[ROADMAP]`
- [ ] 40. Dispense anomaly check `[ROADMAP]`
- [ ] 41. Multi-language UI `[ROADMAP]`
- [ ] 42. Handover note `[ROADMAP]`
- [ ] 43. Road-distance routing API `[ROADMAP]`
- [ ] 44. Two-centre pairing `[ROADMAP]`
- [ ] 45. Live integration with e-Aushadhi data `[ROADMAP]`

Total: 17 [WORKING], 9 [SIMULATED], 19 [ROADMAP]
