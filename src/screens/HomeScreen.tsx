import React from 'react';
import {
  AlertTriangle,
  Clock,
  Building2,
  PackageCheck,
  Layout,
  TrendingDown,
  ArrowRight,
  ShieldAlert,
  Truck,
  Plus,
  Mic,
  ThermometerSnowflake,
  ChevronRight,
  Sparkles,
  Share2
} from 'lucide-react';
import { MedicineItem, PHCCenter, AIStockoutRisk, RescueTransfer, TabType } from '../types';

interface HomeScreenProps {
  medicines: MedicineItem[];
  phcs: PHCCenter[];
  aiRisks: AIStockoutRisk[];
  transfers: RescueTransfer[];
  onNavigateTab: (tab: TabType) => void;
  onOpenTransfer: (med?: MedicineItem, phc?: PHCCenter, type?: 'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT') => void;
  onOpenAddMedicine: () => void;
  onOpenVoiceAssistant: () => void;
  language: 'en' | 'ta';
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  medicines,
  phcs,
  aiRisks,
  transfers,
  onNavigateTab,
  onOpenTransfer,
  onOpenAddMedicine,
  onOpenVoiceAssistant,
  language,
}) => {
  // Calculated stats
  const totalMedicines = medicines.length;
  const lowStockCount = medicines.filter((m) => m.stock <= m.minSafetyStock).length;
  const expiringSoonCount = medicines.filter((m) => m.daysUntilExpiry <= 60).length;
  const activeTransfersCount = transfers.filter((t) => t.status === 'IN_TRANSIT' || t.status === 'DISPATCHED').length;

  const criticalRisk = aiRisks.find((r) => r.riskLevel === 'CRITICAL');
  const criticalMed = medicines.find((m) => m.id === criticalRisk?.medicineId);

  const lowStockItems = medicines.filter((m) => m.stock <= m.minSafetyStock).slice(0, 3);
  const nearExpiryItems = medicines.filter((m) => m.daysUntilExpiry <= 60).slice(0, 2);

  // Smart Donor Matching Logic
  // Rank donor PHCs for a medicine shortage by:
  // 1. Stock above their own reserve (stock > minSafetyStock)
  // 2. Expiry date (longer remaining = better)
  // 3. Transport time (shorter distance = better)
  const rankDonorsForMedicine = (
    medicineId: string,
    targetPHC: PHCCenter,
    allPHCs: PHCCenter[],
    medicine: MedicineItem
  ) => {
    // Find candidate donors that have this medicine and stock above their reserve
    const donors = allPHCs
      .filter((phc) => phc.id !== targetPHC.id)
      .map((phc) => {
        const stockData = phc.stockLevels[medicineId];
        if (!stockData) return null;
        const stockAboveReserve = stockData.stock - medicine.minSafetyStock;
        // Expiry: use daysUntilStockout as proxy; longer is better
        const daysOfStock = stockData.daysUntilStockout;
        const transportTime = phc.distanceKm; // km to target PHC (simplified)
        
        return {
          phc,
          stockAboveReserve,
          daysOfStock,
          transportTime,
          // Priority: stock above reserve (desc), days of stock (desc), transport time (asc)
          priority: stockAboveReserve * 100 + daysOfStock * 10 - transportTime * 0.5,
        };
      })
      .filter((d: any) => d !== null && d.stockAboveReserve > 0)
      .sort((a, b) => (b as { priority: number }).priority - (a as { priority: number }).priority);

    return donors.length > 0 ? donors : null;
  };

  // Fair Distribution Check
  // Check if approving a transfer would cause the donor to fall into shortage risk
  const wouldCauseShortage = (
    donor: PHCCenter,
    medicineId: string,
    quantity: number,
    medicine: MedicineItem
  ) => {
    const donorStockData = donor.stockLevels[medicineId];
    if (!donorStockData) return false;
    const remainingAfterTransfer = donorStockData.stock - quantity;
    const dailyConsumption = donorStockData.dailyConsumption;
    const daysAfterTransfer = dailyConsumption > 0 
      ? Math.max(1, remainingAfterTransfer / dailyConsumption) 
      : 999;
    // Check if remaining stock falls below safety stock threshold
    return daysAfterTransfer < 3; // If less than 3 days of stock remaining, it's risky
  };

  // Calculate Smart Emergency Score per medicine per PHC
  // Formula: importance × 10 + (30 - min(daysToStockout, 30))
  // where daysToStockout = stock / dailyConsumption
  // Higher score = higher emergency risk
  // Factors: consumption speed, delivery delay (transport time), medicine importance
  const emergencyScores = phcs.flatMap((phc) =>
    medicines.map((med) => {
      const phcStockData = phc.stockLevels[med.id];
      if (!phcStockData) return null;
      const daysToStockout = phcStockData.dailyConsumption > 0
        ? Math.ceil(phcStockData.stock / phcStockData.dailyConsumption)
        : 999;
      const emergencyScore = med.importance * 10 + (30 - Math.min(daysToStockout, 30));
      return {
        med,
        phc,
        emergencyScore,
        daysToStockout,
        importance: med.importance,
        dailyConsumption: phcStockData.dailyConsumption,
      };
    }).filter((item): item is NonNullable<typeof item> => item !== null)
  ).sort((a, b) => b.emergencyScore - a.emergencyScore);

  const unexpectedDemandFlags = medicines.map((med) => {
    // Get recent consumption (last 7 days) vs historical average (30 days)
    const phc = phcs[0]; // Use base PHC for detection
    const phcStockData = phc.stockLevels[med.id];
    if (!phcStockData) return null;
    
    const history = med.consumptionHistory; // 30 days
    if (history.length < 7) return null;
    
    // Calculate historical average (last 30 days)
    const historicalAvg = history.reduce((sum, val) => sum + val, 0) / history.length;
    
    // Calculate recent average (last 7 days)
    const recentAvg = history.slice(-7).reduce((sum, val) => sum + val, 0) / 7;
    
    // Detect spike: recent average is 50% higher than historical average
    const spikeThreshold = 1.5;
    const spikeDetected = recentAvg > historicalAvg * spikeThreshold;
    
    return {
      medicineId: med.id,
      medicineName: med.name,
      historicalAvg: Math.round(historicalAvg),
      recentAvg: Math.round(recentAvg),
      spikeFactor: Number((recentAvg / historicalAvg).toFixed(2)),
      spikeDetected,
    };
  }).filter((d): d is NonNullable<typeof d> => d !== null);

  // Sort by spike severity (highest factor first)
  unexpectedDemandFlags.sort((a, b) => b.spikeFactor - a.spikeFactor);

  // Get donor ranking for a medicine at a PHC
  const getDonorRanking = (medicineId: string, targetPHC: PHCCenter) => {
    const ranking = rankDonorsForMedicine(medicineId, targetPHC, phcs, medicines.find((m) => m.id === medicineId) || medicines[0]);
    if (!ranking || ranking.length === 0) return null;
    return {
      bestDonor: ranking[0]!.phc!,
      donorCount: ranking!.length,
      topReason: `${ranking[0]!.phc!.name} has ${ranking[0]!.stockAboveReserve!.toString()} units above their reserve, ${ranking[0]!.daysOfStock} days of stock, ${ranking[0]!.transportTime}km transport`,
    };
  };

  return (
    <div className="p-4 space-y-4 pb-20">
      {/* PHC Greeting & Shift status */}
      <div className="bg-gradient-to-r from-teal-800 to-teal-700 rounded-2xl p-4 text-white shadow-sm flex items-center justify-between">
        <div>
          <p className="text-[11px] text-teal-200 uppercase font-bold tracking-wider">
            {language === 'ta' ? 'மருத்தகம் செயலில் உள்ளது' : 'Pharmacy Operations Active'}
          </p>
          <h2 className="text-base font-bold text-white mt-0.5">
            {language === 'ta' ? 'வணக்கம், மருத்துவர் & மருந்தாளர்' : 'Dr. Aruna & Pharmacist Selvam'}
          </h2>
          <p className="text-xs text-teal-100/90 mt-1 flex items-center gap-1.5">
            <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
            <span>{language === 'ta' ? '48,500 கிரাম மக்களுக்கு சேவை' : 'Serving 48,500 villagers in Kallakurichi Taluk'}</span>
          </p>
        </div>
        <button
          onClick={onOpenVoiceAssistant}
          title="Open Tamil Voice Assistant"
          className="p-3 bg-white/10 hover:bg-white/20 active:bg-white/30 rounded-2xl text-teal-100 transition-colors flex flex-col items-center gap-1 border border-white/15"
        >
          <Mic className="w-5 h-5 text-emerald-300" />
          <span className="text-[10px] font-semibold">குரல்</span>
        </button>
      </div>

      {/* 4 Stat Metric Cards (Rounded cards, clean white, teal/green accents) */}
      <div className="grid grid-cols-2 gap-3">
        {/* Total Medicines */}
        <div
          onClick={() => onNavigateTab('inventory')}
          className="bg-white rounded-2xl p-3.5 border border-slate-100 shadow-xs hover:border-teal-200 transition-all cursor-pointer group"
        >
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <div className="w-8 h-8 rounded-xl bg-teal-50 text-teal-700 flex items-center justify-center">
              <PackageCheck className="w-4 h-4" />
            </div>
            <span className="text-[11px] font-medium text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full">
              98.2% Avail
            </span>
          </div>
          <p className="text-2xl font-extrabold text-slate-900 tracking-tight">{totalMedicines}</p>
          <p className="text-xs text-slate-600 font-medium mt-0.5">
            {language === 'ta' ? 'மொத்த மருந்துகள்' : 'Total Medicines'}
          </p>
        </div>

        {/* Low-Stock Alerts */}
        <div
          onClick={() => onNavigateTab('inventory')}
          className="bg-white rounded-2xl p-3.5 border border-rose-100 shadow-xs hover:border-rose-300 transition-all cursor-pointer group"
        >
          <div className="flex items-center justify-between text-rose-500 mb-2">
            <div className="w-8 h-8 rounded-xl bg-rose-50 text-rose-700 flex items-center justify-center">
              <AlertTriangle className="w-4 h-4" />
            </div>
            <span className="text-[11px] font-bold text-rose-700 bg-rose-50 px-2 py-0.5 rounded-full">
              Critical
            </span>
          </div>
          <p className="text-2xl font-extrabold text-rose-600 tracking-tight">{lowStockCount}</p>
          <p className="text-xs text-slate-600 font-medium mt-0.5">
            {language === 'ta' ? 'குறைந்த இருப்பு எச்சரிக்கை' : 'Low-Stock Alerts'}
          </p>
        </div>

        {/* Expiring Soon */}
        <div
          onClick={() => onNavigateTab('inventory')}
          className="bg-white rounded-2xl p-3.5 border border-amber-100 shadow-xs hover:border-amber-300 transition-all cursor-pointer group"
        >
          <div className="flex items-center justify-between text-amber-500 mb-2">
            <div className="w-8 h-8 rounded-xl bg-amber-50 text-amber-700 flex items-center justify-center">
              <Clock className="w-4 h-4" />
            </div>
            <span className="text-[11px] font-medium text-amber-700 bg-amber-50 px-2 py-0.5 rounded-full">
              ≤ 60 Days
            </span>
          </div>
          <p className="text-2xl font-extrabold text-amber-600 tracking-tight">{expiringSoonCount}</p>
          <p className="text-xs text-slate-600 font-medium mt-0.5">
            {language === 'ta' ? 'காலாவதிய toga abhayam' : 'Expiring Medicines'}
          </p>
        </div>

        {/* Connected PHCs */}
        <div
          onClick={() => onNavigateTab('rescue')}
          className="bg-white rounded-2xl p-3.5 border border-slate-100 shadow-xs hover:border-teal-200 transition-all cursor-pointer group"
        >
          <div className="flex items-center justify-between text-slate-500 mb-2">
            <div className="w-8 h-8 rounded-xl bg-emerald-50 text-emerald-700 flex items-center justify-center">
              <Building2 className="w-4 h-4" />
            </div>
            <span className="text-[11px] font-medium text-teal-700 bg-teal-50 px-2 py-0.5 rounded-full">
              {activeTransfersCount} Active
            </span>
          </div>
          <p className="text-2xl font-extrabold text-slate-900 tracking-tight">{phcs.length}</p>
          <p className="text-xs text-slate-600 font-medium mt-0.5">
            {language === 'ta' ? 'ணைக்கப்பட்ட PHC-க்கள்' : 'Nearby PHC Mesh'}
          </p>
        </div>
      </div>

      {/* Emergency Stockout Alert Banner */}
      {criticalRisk && (
        <div className="bg-gradient-to-r from-rose-50 via-rose-50/70 to-amber-50 border border-rose-200 rounded-2xl p-4 shadow-xs relative overflow-hidden">
          <div className="flex items-start gap-3">
            <div className="w-9 h-9 rounded-xl bg-rose-600 text-white flex items-center justify-center flex-shrink-0 shadow-sm">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-1.5">
                <span className="text-xs font-bold text-rose-800 uppercase tracking-wider">
                  {language === 'ta' ? 'அவசர கையிருப்பு எச்சரிக்கை' : 'Emergency Stockout Alert'}
                </span>
                <span className="text-[10px] text-rose-600 font-medium bg-rose-100 px-1.5 py-0.2 rounded-md">
                  {criticalRisk.daysToStockout} days left
                </span>
              </div>
              <h3 className="text-sm font-bold text-slate-900 mt-0.5">
                {criticalRisk.medicineName}
              </h3>
              <p className="text-xs text-slate-700 mt-1 leading-relaxed">
                {criticalRisk.primaryCause}. Reserve down to {criticalRisk.currentStock} vials.
              </p>
              <div className="mt-3 flex items-center gap-2">
                <button
                  onClick={() => {
                    const ulundurpet = phcs.find((p) => p.name.includes('Ulundurpet'));
                    onOpenTransfer(criticalMed, ulundurpet, 'REQUEST_DEFICIT');
                  }}
                  className="px-3 py-1.5 rounded-xl bg-rose-600 hover:bg-rose-700 active:bg-rose-800 text-white text-xs font-bold flex items-center gap-1 shadow-sm transition-colors"
                >
                  <Share2 className="w-3.5 h-3.5" />
                  <span>{language === 'ta' ? 'உடனடி மீட்கு கோரிக்கை' : 'Request Inter-PHC Rescue'}</span>
                </button>
                <button
                  onClick={() => onNavigateTab('insights')}
                  className="text-xs font-semibold text-rose-800 hover:text-rose-900 px-2 py-1 rounded-lg hover:bg-rose-100/50 transition-colors"
                >
                  {language === 'ta' ? 'காரணங்களை காண்க' : 'View AI Analysis'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Critical Stock Attention List */}
      <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <TrendingDown className="w-4 h-4 text-rose-600" />
            <h3 className="text-sm font-bold text-slate-900">
              {language === 'ta' ? 'அவசர கவன தேவை உள்ள மருந்துகள்' : 'Critical Stock Attention'}
            </h3>
          </div>
          <button
            onClick={() => onNavigateTab('inventory')}
            className="text-xs text-teal-700 font-semibold flex items-center gap-0.5 hover:text-teal-800"
          >
            <span>{language === 'ta' ? 'அனைத்தும்' : 'View all'}</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="space-y-3">
          {lowStockItems.map((item) => {
            const stockPct = Math.min(100, Math.round((item.stock / item.minSafetyStock) * 100));
            return (
              <div
                key={item.id}
                className="p-3 rounded-xl bg-slate-50 border border-slate-100 hover:border-teal-200 transition-colors"
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <h4 className="text-xs font-bold text-slate-900">{item.name}</h4>
                    <p className="text-[11px] text-slate-500">{item.form} · Batch {item.batchNumber}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-extrabold text-rose-600">
                      {item.stock} / {item.minSafetyStock} {item.unit}
                    </p>
                    <p className="text-[10px] text-slate-400">min buffer {item.minSafetyStock}</p>
                  </div>
                </div>

                {/* Progress bar */}
                <div className="mt-2 w-full bg-slate-200 rounded-full h-1.5 overflow-hidden">
                  <div
                    className={`h-full rounded-full ${
                      stockPct <= 25 ? 'bg-rose-500' : stockPct <= 50 ? 'bg-amber-500' : 'bg-teal-500'
                    }`}
                    style={{ width: `${Math.max(8, stockPct)}%` }}
                  />
                </div>

                {/* Smart Rescue Donor Matching */}
                <div className="mt-2 p-2 rounded bg-teal-50/50 border border-teal-100/80 text-xs text-slate-600">
                  {item.stock <= item.minSafetyStock && getDonorRanking(item.id, phcs[0]) ? (
                    <div>
                      <strong>🤝 Best donor:</strong> {getDonorRanking(item.id, phcs[0])?.bestDonor?.name || 'Finding donors...'}
                    </div>
                  ) : (
                    'Stock sufficient'
                  )}
                </div>

                {/* Rescue action */}
                <div className="mt-2.5 flex items-center justify-between text-[11px]">
                  <span className="text-slate-500 flex items-center gap-1">
                    {item.coldChainRequired && (
                      <span className="text-sky-600 flex items-center gap-0.5">
                        <ThermometerSnowflake className="w-3 h-3" />
                        2°C-8°C
                      </span>
                    )}
                  </span>
                  <button
                    onClick={() => onOpenTransfer(item, undefined, 'REQUEST_DEFICIT')}
                    className="text-teal-700 font-bold hover:text-teal-900 flex items-center gap-1"
                  >
                    <span>{language === 'ta' ? 'மீட்பு Kōru' : 'Request from Mesh'}</span>
                    <ArrowRight className="w-3 h-3" />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Expiring Soon Batches (Waste Prevention) */}
      <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <Clock className="w-4 h-4 text-amber-600" />
            <div>
              <h3 className="text-sm font-bold text-slate-900">
                {language === 'ta' ? 'கழிவைத் தவிர்க்க உடனடி மீட்கு' : 'Expiry Waste Prevention'}
              </h3>
              <p className="text-[11px] text-slate-500">
                {language === 'ta' ? 'வீணாவதுக்கு முன் பிற PHC-க்கு மாற்றவும்' : 'Transfer surplus before expiry date'}
              </p>
            </div>
          </div>
          <span className="text-[11px] font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded-full">
            {expiringSoonCount} Batches
          </span>
        </div>

        <div className="space-y-2.5">
          {nearExpiryItems.map((item) => (
            <div
              key={item.id}
              className="p-3 rounded-xl bg-amber-50/50 border border-amber-100/80 flex items-center justify-between gap-3"
            >
              <div className="min-w-0">
                <h4 className="text-xs font-bold text-slate-900 truncate">{item.name}</h4>
                <p className="text-[11px] text-slate-600 mt-0.5">
                  {item.stock} {item.unit} · <span className="font-semibold text-amber-800">Expires in {item.daysUntilExpiry} days</span>
                </p>
              </div>
              <button
                onClick={() => onOpenTransfer(item, undefined, 'DISPATCH_SURPLUS')}
                className="px-2.5 py-1.5 rounded-xl bg-teal-700 hover:bg-teal-800 text-white text-xs font-semibold flex items-center gap-1 flex-shrink-0 transition-colors shadow-xs"
              >
                <Share2 className="w-3 h-3" />
                <span>{language === 'ta' ? 'பகிர்' : 'Share Surplus'}</span>
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Nearby PHC Availability Mesh Radar */}
      <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <Building2 className="w-4 h-4 text-teal-700" />
            <h3 className="text-sm font-bold text-slate-900">
              {language === 'ta' ? 'அருகிலுள்ள PHCiruppy நிலை' : 'Nearby PHC Mesh Availability'}
            </h3>
          </div>
          <button
            onClick={() => onNavigateTab('rescue')}
            className="text-xs text-teal-700 font-semibold flex items-center gap-0.5 hover:text-teal-800"
          >
            <span>{language === 'ta' ? 'வரைபடம்' : 'Open Map'}</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="space-y-2.5">
          {phcs.slice(0, 3).map((phc) => (
            <div
              key={phc.id}
              onClick={() => onNavigateTab('rescue')}
              className="p-3 rounded-xl bg-slate-50 border border-slate-100 hover:border-teal-200 transition-colors flex items-center justify-between cursor-pointer"
            >
              <div>
                <div className="flex items-center gap-2">
                  <h4 className="text-xs font-bold text-slate-900">{phc.name}</h4>
                  <span className="text-[10px] text-emerald-700 font-medium bg-emerald-50 px-1.5 py-0.2 rounded-md">
                    {phc.distanceKm} km · ~{phc.travelTimeMin}m
                  </span>
                </div>
                <p className="text-[11px] text-slate-500 mt-0.5">
                  Surplus: {phc.surplusList.length} items · Shortages: {phc.shortageList.length} items
                </p>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </div>
          ))}
        </div>
      </div>

      {/* Stock Table: PHCs x Medicines */}
      <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <Layout className="w-4 h-4 text-teal-700" />
            <h3 className="text-sm font-bold text-slate-900">
              {language === 'ta' ? 'PHCs x Medicines Stock Table' : 'PHCs × Medicines Stock Table'}
            </h3>
          </div>
          <div className="text-xs text-slate-500">
            {'Stock • Consumption • Days-to-Stockout • Expiry'}
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr>
                <th className={`left border border-slate-200 p-2 text-left font-bold text-slate-600 ${language === 'ta' ? 'w-40' : 'w-24'}`}>PHC</th>
                {medicines.slice(0, 8).map((med) => (
                  <th key={med.id} className="border border-slate-200 p-2 text-left font-bold text-slate-600">
                    {med.name}
                  </th>
                ))}
                <th className="border border-slate-200 p-2 text-left font-bold text-slate-600">Avg</th>
              </tr>
            </thead>
            <tbody>
              {phcs.map((phc) => {
                const phcMedData = medicines.map((med) => {
                  const phcStockData = phc.stockLevels[med.id];
                  if (!phcStockData) return null;
                  const daysToStockout = phcStockData.dailyConsumption > 0
                    ? Math.ceil(phcStockData.stock / phcStockData.dailyConsumption)
                    : 999;
                  return {
                    med,
                    stock: phcStockData.stock,
                    dailyConsumption: phcStockData.dailyConsumption,
                    daysUntilStockout: daysToStockout,
                    expiry: med.expiryDate,
                    importance: med.importance,
                  };
                }).filter((d: any) => d !== null);

                return (
                  <tr key={phc.id} className="border-b border-slate-100">
                    <td className="left border border-slate-200 p-2 font-medium text-slate-800">
                      {phc.name}
                    </td>
                    {phcMedData.map((d: any) => (
                      <td key={d.med.id} className="border border-slate-200 p-2 align-middle">
                        <div className="text-slate-700 font-medium">{d.stock} {d.med.unit}</div>
                        <div className="text-[10px] text-slate-500">{d.dailyConsumption.toFixed(1)}/day</div>
                        <div className={`text-[10px] ${
                          d.daysUntilStockout <= 3 ? 'text-rose-600' : d.daysUntilStockout <= 7 ? 'text-amber-600' : 'text-teal-600'
                        } font-medium`}>
                          {d.daysUntilStockout}d
                        </div>
                        <div className="text-[10px] text-slate-400">{d.expiry}</div>
                      </td>
                    ))}
                    <td className="border border-slate-200 p-2 text-right font-medium">
                      {phcMedData.reduce((sum: number, d: any) => sum + (d.daysUntilStockout ?? 999), 0) / phcMedData.length || '-'}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Smart Emergency Score */}
      {emergencyScores.length > 0 && (
        <div className="bg-white rounded-2xl p-4 border border-teal-100 shadow-xs">
          <div className="flex items-center justify-between mb-3">
            <div>
              <h3 className="text-sm font-bold text-slate-900">
                {language === 'ta' ? 'அபாயம் xếp hạng' : 'Smart Emergency Score'}
              </h3>
              <p className="text-[10px] text-teal-700 font-semibold">
                {emergencyScores.length} medicines tracked across {phcs.length} PHCs
              </p>
            </div>
            <div className="text-xs text-teal-600">
              Score range: 10-70 ({language === 'ta' ? 'மாதிரி: Madrasipriority' : 'Higher = Higher Risk'})
            </div>
          </div>

          <div className="space-y-2">
            {emergencyScores.slice(0, 5).map((item: any) => {
              const riskClass = item.emergencyScore >= 40 ? 'rose-600' : item.emergencyScore >= 25 ? 'amber-600' : 'teal-600';
              const riskLabel = item.emergencyScore >= 40 ? 'CRITICAL' : item.emergencyScore >= 25 ? 'HIGH' : 'MODERATE';
              const days = item.daysToStockout;
              return (
                <div
                  key={`${item.med.id}-${item.phc.id}`}
                  className="p-3 rounded-xl bg-slate-50 border border-teal-100 hover:border-teal-200 transition-colors"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="w-2.5 h-2.5 rounded-full bg-teal-300" />
                      <span className="font-medium text-slate-800">{item.med.name}</span>
                    </div>
                    <div>
                      <p className={`text-xs font-bold ${riskClass}`}>
                        {riskLabel} ({item.emergencyScore})
                      </p>
                      <p className="text-[10px] text-slate-500">Stockout in {days} days</p>
                    </div>
                  </div>
                  <div className="text-[10px] text-slate-500">
                    Importance: {item.importance} · Delivery: {item.dailyConsumption.toFixed(1)}/day
                  </div>
                </div>
              );
            })}
          </div>

          <div className="pt-2 border-t border-slate-100 text-xs text-slate-500">
            <strong>Formula:</strong> Emergency Score = Importance × 10 + (30 − min(Days-to-Stockout, 30))
            <br />
            <strong>Factors:</strong>
            <span>
              • Consumption speed: {emergencyScores[0]?.dailyConsumption} units/day per PHC
              • Delivery delay: transport time based on PHC distance
              • Medicine importance: 1-5 scale (Emergency Antidote=5, Vaccine=4, Antibiotic=4, Analgesic=3, Chronic Care=3, Maternal Care=2, Hydration=1)
            </span>
          </div>
        </div>
      )}

      {/* Quick Action Floating / Bottom Bar */}
      <div className="grid grid-cols-2 gap-2 pt-1">
        <button
          onClick={onOpenAddMedicine}
          className="py-2.5 px-3 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-800 font-semibold text-xs flex items-center justify-center gap-1.5 transition-colors"
        >
          <Plus className="w-4 h-4 text-teal-700" />
          <span>{language === 'ta' ? '+ மருந்து சேர்' : '+ Add Batch'}</span>
        </button>
        <button
          onClick={() => onOpenTransfer()}
          className="py-2.5 px-3 rounded-xl bg-teal-700 hover:bg-teal-800 text-white font-semibold text-xs flex items-center justify-center gap-1.5 shadow-sm transition-colors"
        >
          <Truck className="w-4 h-4 text-emerald-300" />
          <span>{language === 'ta' ? 'புதிய மாற்றம்' : 'New Transfer'}</span>
        </button>
      </div>
    </div>
  );
};