# StockSafe 🏥💊

**StockSafe** is an offline-first Android application designed for primary health centres (PHCs) in a block to prevent medicine stock-outs. It acts as an early warning system for pharmacists and medical officers by detecting imminent stock-outs, watching how fast stock is falling (burn rate and speed-up acceleration), and recommending stock reallocations from nearby surplus health centres before ordering from the district store.

---

## 🛠️ Build Setup

Set only `ANDROID_USER_HOME=C:\Users\GNANESHWER\.android` (and do **NOT** set `ANDROID_PREFS_ROOT`).
To run unit tests and build the application APK from the project root:
```cmd
.\gradlew.bat test assembleDebug
```

---

## 🎬 3-Minute Live Demo Walkthrough Script

Follow these steps to demonstrate StockSafe in under 3 minutes:

1. **Initial Red Risk Alert & Burn-Rate Meter (0:00 - 0:45)**:
   - Launch StockSafe or tap **Reset Seed**.
   - Observe **Paracetamol syrup at PHC Keelavasal** highlighted in **RED (AT RISK)** with only **1.3 days left** ($\text{stock} = 4$, $\text{burn rate} = 3.0/\text{day}$).
2. **Speed-Up & Unexpected Demand Alerts (0:45 - 1:15)**:
   - Check the **Alerts** panel. Observe ORS sachets flagged in **AMBER (SPEED UP)** due to consumption doubling week-over-week.
3. **Smart Donor Match from Melur (1:15 - 2:00)**:
   - Navigate to the **Transfers** tab.
   - Observe the sideways transfer recommendation: **"Move 56 x Paracetamol syrup from PHC Melur to PHC Keelavasal"**.
   - *Constraint check*: PHC Melur (9.0 km) is selected as the donor because it is within 10 km and holds 78 surplus units above its 14-day safety buffer. PHC Thirupparankundram (12 km) is excluded by the 10 km limit rule.
4. **Pharmacist Approval & Live Reallocation (2:00 - 2:30)**:
   - Enter the approving pharmacist's name (*Pharmacist Ramesh*) and tap **Approve**.
   - See Melur stock adjust to 64 units and Keelavasal stock increase to 60 units. The red stock-out alert clears immediately!
5. **Impact Card Summary (2:30 - 3:00)**:
   - Review the **Impact Card**: **56 units reallocated**, **18 km saved**, **1 stock-out prevented**, **18 warning days gained**, and **0 expired units wasted**.

---

## 🗺️ Feature Implementation Status Matrix (45 Items)

All 45 features are categorized and explicitly tagged as `WORKING`, `SIMULATED`, or `ROADMAP` in `FEATURES.md` and `CHECKLIST.md`.

### 🟢 WORKING FEATURES (1–17) — 100% Offline Core & UI

| # | Feature | Status | Description / Evidence |
| :-: | :--- | :-: | :--- |
| 1 | Dashboard | `[WORKING]` | Status-coloured cards for all PHCs & medicines. |
| 2 | Days-left figure | `[WORKING]` | Days remaining calculation per medicine per centre. |
| 3 | Burn-rate meter | `[WORKING]` | Primary metric showing units lost/consumed per day. |
| 4 | Speed-up alert | `[WORKING]` | Flags medicines whose consumption rate doubled week-over-week. |
| 5 | Unexpected demand alert | `[WORKING]` | Flags medicines where today's use is > 2x 7-day average. |
| 6 | Smart donor matching | `[WORKING]` | Matches donors within 10 km, surplus buffer, and near-expiry priority. |
| 7 | Multi-PHC transfer plan | `[WORKING]` | Combines transfer proposals from eligible nearby PHCs. |
| 8 | Remainder-only district indent | `[WORKING]` | Fallback district indent for shortfalls nearby PHCs cannot cover. |
| 9 | Emergency score & priority | `[WORKING]` | Weighted risk score ranking urgency across all centres. |
| 10 | Confidence indicator | `[WORKING]` | Forecast confidence rating ("High", "Medium", "Low"). |
| 11 | Record dispense | `[WORKING]` | Subtracts stock live and updates risk status immediately. |
| 12 | Transfer ledger | `[WORKING]` | Immutable audit log of sender, receiver, km, time, and pharmacist. |
| 13 | Audit log | `[WORKING]` | Complete log of stock changes, dispenses, and replenishments. |
| 14 | Daily one-click report | `[WORKING]` | Printable/copyable summary report text generator. |
| 15 | Anonymous shortage report | `[WORKING]` | Shortage submission form without patient data collection. |
| 16 | Settings | `[WORKING]` | Configurable sliders for indent days and safety buffer days. |
| 17 | Impact card | `[WORKING]` | Metrics quantifying km saved, stock-outs prevented, and warning days gained. |

### 🟡 SIMULATED FEATURES (18–26) — Interactive UI with Simulated Data

| # | Feature | Status | Description / Evidence |
| :-: | :--- | :-: | :--- |
| 18 | Seasonal disease forecast | `[SIMULATED]` | Pharmacist sets monsoon/fever demand multiplier slider. |
| 19 | Medicine expiry alerts | `[SIMULATED]` | Interactive preview of near-expiry medicine batches. |
| 20 | Fair distribution check | `[SIMULATED]` | Buffer check confirming donor PHC stays above safety days after transfer. |
| 21 | Multi-centre block officer view | `[SIMULATED]` | Supervisory view aggregating block-wide stock risk metrics. |
| 22 | Demand surge input by catchment | `[SIMULATED]` | Simulated village catchment demand surge controls. |
| 23 | Emergency transfer mode | `[SIMULATED]` | High-priority escalation trigger marking proposals as URGENT. |
| 24 | Pharmacist approval step | `[SIMULATED]` | Required pharmacist name input gating before transfer acceptance. |
| 25 | Offline mode & sync indicator | `[SIMULATED]` | Local DB state with animated simulated server sync indicator. |
| 26 | Barcode or QR entry | `[SIMULATED]` | Manual QR/barcode text entry parser. |

### 🔵 ROADMAP FEATURES (27–45) — Documented Future Roadmap

| # | Feature | Status | Description |
| :-: | :--- | :-: | :--- |
| 27 | Live route and delivery tracking | `[ROADMAP]` | Real-time GPS route tracking for vehicle deliveries. |
| 28 | WhatsApp and SMS alerts | `[ROADMAP]` | Automated SMS/WhatsApp notifications to pharmacists. |
| 29 | Tamil voice assistant | `[ROADMAP]` | Voice queries in Tamil for stock status checks. |
| 30 | Patient-facing SMS availability | `[ROADMAP]` | SMS stock inquiry system for patients. |
| 31 | Carbon-aware route combining | `[ROADMAP]` | Route batching for CO2 emission reduction. |
| 32 | Community impact dashboard | `[ROADMAP]` | Multi-block community impact analytics. |
| 33 | Equity priority scoring | `[ROADMAP]` | Priority weighting for remote PHCs. |
| 34 | Waste reduction score | `[ROADMAP]` | Long-term expiry waste tracking over time. |
| 35 | Auto-filled monthly indent form | `[ROADMAP]` | Automated PDF indent form generator. |
| 36 | Recall handling for medicines | `[ROADMAP]` | Batch withdrawal management system. |
| 37 | Patient queue estimate | `[ROADMAP]` | Queue estimation based on dispensing rate. |
| 38 | Stock-out heat map | `[ROADMAP]` | Spatial GIS heat map of block stock-out risks. |
| 39 | Fever-visit link | `[ROADMAP]` | Correlation of fever surveillance with Paracetamol use. |
| 40 | Dispense anomaly check | `[ROADMAP]` | Historical statistical anomaly detection. |
| 41 | Multi-language UI | `[ROADMAP]` | Tamil and English bilingual interface toggle. |
| 42 | Handover note | `[ROADMAP]` | Shift change pharmacist handover log. |
| 43 | Road-distance routing API | `[ROADMAP]` | Dynamic Google Maps API distance routing. |
| 44 | Two-centre pairing | `[ROADMAP]` | Persistent peer PHC pairing. |
| 45 | Live e-Aushadhi integration | `[ROADMAP]` | Direct API integration with DVDMS / e-Aushadhi. |

---

## 🧪 Unit Tests & Verification Results

```cmd
.\gradlew.bat test assembleDebug
```
- **61 Unit Tests Passed** across `StockCalculatorTest`, `PharmacistApprovalTest`, `LedgerTest`, `StockJsonTest`, `CsvParserTest`, and `InnovationsTest`.
