# StockSafe Features Matrix

All features are categorized and tagged with their current implementation status: `WORKING`, `SIMULATED`, or `ROADMAP`.

---

## 🟢 WORKING FEATURES (1–17)

Fully functional, operating 100% offline in app core and UI:

1. **Dashboard**: All centres and medicines with status colours, burn rate as main number, stock secondary, and accessible status badges. `[WORKING]`
2. **Days-Left Figure**: Days remaining until stock-out per medicine per centre. `[WORKING]`
3. **Burn-Rate Meter**: Units lost/consumed per day shown as the primary metric with a Canvas Arc meter in Medicine Details. `[WORKING]`
4. **Speed-Up Alert**: Flags medicines whose consumption rate increased by 25%+ week-over-week. `[WORKING]`
5. **Unexpected Demand Alert**: Flags medicines where today's use is more than 2x the 7-day average. `[WORKING]`
6. **Smart Donor Matching**: Donor selection constrained by 10 km distance, surplus, and near-expiry batch prioritization. `[WORKING]`
7. **Multi-PHC Transfer Plan**: Combines sideways transfer proposals from eligible nearby PHCs. `[WORKING]`
8. **Remainder-Only District Indent**: Calculates fallback indents for shortfalls that nearby PHCs cannot cover, displaying explicit reasons. `[WORKING]`
9. **Emergency Score & Priority List**: Weighted risk score ranking centres and medicines by urgency. `[WORKING]`
10. **Confidence Indicator**: Rating indicator ("High", "Medium", "Low") based on days of historical record. `[WORKING]`
11. **Record Dispense**: Subtracts units from stock and recalculates analyses, alerts, and transfers live with Snackbar feedback. `[WORKING]`
12. **Transfer Ledger**: Immutable audit log of transfers recording sender, receiver, distance, quantity, timestamp, and pharmacist name. `[WORKING]`
13. **Audit Log**: Audit trail of every stock change, dispense, replenishment, and alert. `[WORKING]`
14. **Daily One-Click Report**: Printable/copyable daily summary text report generator. `[WORKING]`
15. **Anonymous Shortage Report Form**: Privacy-preserving health worker shortage submission form with free-text notes and no patient data. `[WORKING]`
16. **Settings Controls**: Sliders for configuring days until indent and safety buffer days. `[WORKING]`
17. **Impact Card**: Summary metrics quantifying km saved, stock-outs prevented, expired units avoided, and warning days gained. `[WORKING]`

---

## 🟡 SIMULATED FEATURES (18–26)

Working interactive UI components using simulated/fake data, clearly labelled with SIMULATED badges:

18. **Seasonal Disease Forecast**: Pharmacist sets monsoon/fever demand multiplier slider to simulate seasonal demand spikes. `[SIMULATED]`
19. **Medicine Expiry Alerts**: Interactive preview of near-expiry medicine batches. `[SIMULATED]`
20. **Fair Distribution Check**: Visual safety buffer check confirming donor PHCs retain required safety days after transfers. `[SIMULATED]`
21. **Multi-Centre View for Block Officer**: Block Officer supervisory grid view (`BlockOverviewGrid`) aggregating risk metrics across all 4 PHCs. `[SIMULATED]`
22. **Demand Surge Input by Village Catchment**: Simulated village catchment surge input controls. `[SIMULATED]`
23. **Emergency Transfer Mode**: High-priority escalation trigger marking transfer proposals with a prominent red border as URGENT. `[SIMULATED]`
24. **Pharmacist Approval Step**: Required pharmacist name input field and gating before any transfer is accepted. `[SIMULATED]`
25. **Offline Mode & Sync Indicator**: Local offline database state with animated simulated server sync indicator. `[SIMULATED]`
26. **Barcode / QR Code Entry**: Manual QR/barcode text entry parser (camera scan marked as roadmap). `[SIMULATED]`

---

## 🔵 ROADMAP FEATURES (27–45)

Documented for future development (no production code):

27. **Live Route & Delivery Tracking** `[ROADMAP]`
28. **WhatsApp & SMS Alerts** `[ROADMAP]`
29. **Tamil Voice Assistant** `[ROADMAP]`
30. **Patient-Facing SMS Availability Check** `[ROADMAP]`
31. **Carbon-Aware Route Combining** `[ROADMAP]`
32. **Community Impact Dashboard Across Blocks** `[ROADMAP]`
33. **Equity Priority Scoring for Remote PHCs** `[ROADMAP]`
34. **Waste Reduction Score Over Time** `[ROADMAP]`
35. **Auto-Filled Monthly Indent Form** `[ROADMAP]`
36. **Recall Handling for Withdrawn Medicines** `[ROADMAP]`
37. **Patient Queue Estimate** `[ROADMAP]`
38. **Stock-Out Heat Map of the Block** `[ROADMAP]`
39. **Fever-Visit Link to Paracetamol Use** `[ROADMAP]`
40. **Dispense Anomaly Check Against History** `[ROADMAP]`
41. **Multi-Language UI (Tamil and English)** `[ROADMAP]`
42. **Handover Note at Shift Change** `[ROADMAP]`
43. **Road-Distance Routing from Maps API** `[ROADMAP]`
44. **Two-Centre Pairing with Shared Tracking** `[ROADMAP]`
45. **Live Integration with e-Aushadhi Data** `[ROADMAP]`
