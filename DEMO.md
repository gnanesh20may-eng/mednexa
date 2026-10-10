# StockSafe 3-Minute Demonstration Script 🎬💊

This script guides you through demonstrating **StockSafe** to pharmacists, block medical officers, and district health authorities.

---

## ⏱️ Demonstration Steps (< 3 Minutes)

### 1. Initial State & Burn-Rate Meter (0:00 - 0:45)
- **Action**: Open StockSafe or tap **Reset Seed** in the top bar.
- **Observe**:
  - The Dashboard displays medicine cards across 4 PHCs (*PHC Keelavasal*, *PHC Thirupparankundram*, *PHC Tirumangalam*, *PHC Melur*).
  - Main metric on each card shows the **Burn Rate** (units lost/consumed per day, e.g. `3.0 units/day`) with current stock as secondary text (`Stock: 4 units`).
  - **Paracetamol syrup at PHC Keelavasal** is flagged in **RED (AT RISK)** with only **1.3 days left** ($\text{stock} = 4$, $\text{burn rate} = 3.0/\text{day}$).

### 2. Speed-Up & Unexpected Demand Alerts (0:45 - 1:15)
- **Action**: Review the **Active Alerts** panel at the top.
- **Observe**:
  - **Speed-Up Alert**: Highlights ORS sachets at PHC Keelavasal in **AMBER (ACCELERATING)** due to a 66% consumption surge week-over-week.
  - **Unexpected Demand Alert**: Warns when single-day consumption exceeds 2x the 7-day average.

### 3. Smart Donor Match & Sideways Transfer (1:15 - 2:00)
- **Action**: Navigate to the **Transfers** tab.
- **Observe**:
  - A sideways transfer proposal is generated: **"Move 56 x Paracetamol syrup from PHC Melur to PHC Keelavasal"**.
  - **Smart Donor Matching**: PHC Melur (9.0 km) is matched because it is within the 10 km distance constraint and holds sufficient surplus stock (78 units above its 14-day safety buffer). PHC Thirupparankundram (12.0 km) is excluded due to the 10 km limit.

### 4. Pharmacist Approval & Live Stock Update (2:00 - 2:30)
- **Action**: Type the approving pharmacist's name in the input field (*Pharmacist Ramesh*) and tap **Approve**.
- **Observe**:
  - Proposal status changes to **Approved**.
  - Stock levels recalculate live across the app:
    - PHC Melur stock drops from **120 to 64 units** (retaining its 42-unit safety buffer).
    - PHC Keelavasal stock increases from **4 to 60 units**.
  - The red stock-out alert for Paracetamol syrup clears immediately!

### 5. Impact Card Summary (2:30 - 3:00)
- **Action**: View the **Impact Card** on the Dashboard or Settings tab.
- **Observe**:
  - Quantifies real-world savings: **56 units reallocated**, **18 km saved**, **1 stock-out prevented**, **18 warning days gained**, and **0 expired units wasted**.
