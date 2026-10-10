import React, { useState } from 'react';
import {
  Sparkles,
  AlertTriangle,
  Info,
  Sliders,
  TrendingDown,
  Clock,
  ArrowRight,
  ShieldAlert,
  Send,
  Calendar,
  Layers,
  ThermometerSnowflake,
  CheckCircle2
} from 'lucide-react';
import { AIStockoutRisk, MedicineItem, PHCCenter } from '../types';

interface InsightsScreenProps {
  aiRisks: AIStockoutRisk[];
  medicines: MedicineItem[];
  phcs: PHCCenter[];
  onOpenTransfer: (med?: MedicineItem, phc?: PHCCenter, type?: 'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT') => void;
  language: 'en' | 'ta';
}

export const InsightsScreen: React.FC<InsightsScreenProps> = ({
  aiRisks,
  medicines,
  phcs,
  onOpenTransfer,
  language,
}) => {
  // Seasonal simulation state
  const [simulationScenario, setSimulationScenario] = useState<'BASELINE' | 'MONSOON' | 'HARVEST' | 'HEATWAVE'>('HARVEST');

  // Adjust risk factors based on selected simulation scenario
  const getAdjustedRisks = () => {
    return aiRisks.map((risk) => {
      let days = risk.daysToStockout;
      let level = risk.riskLevel;

      if (simulationScenario === 'HARVEST') {
        if (risk.medicineName.includes('Snake Venom')) {
          days = 2;
          level = 'CRITICAL';
        }
      } else if (simulationScenario === 'MONSOON') {
        if (risk.medicineName.includes('Amoxicillin') || risk.medicineName.includes('ORS')) {
          days = 2;
          level = 'CRITICAL';
        }
      } else if (simulationScenario === 'HEATWAVE') {
        if (risk.medicineName.includes('ORS')) {
          days = 1;
          level = 'CRITICAL';
        }
      }

      return {
        ...risk,
        daysToStockout: days,
        riskLevel: level,
      };
    });
  };

  const adjustedRisks = getAdjustedRisks();

  // Expiry waste candidates
  const expiringExcess = medicines.filter((m) => m.daysUntilExpiry <= 60 && m.stock > 10);

  return (
    <div className="p-4 space-y-4 pb-20">
      {/* Required Demo / Illustrative Disclaimer Banner */}
      <div className="bg-amber-50 border border-amber-200/80 rounded-2xl p-3.5 text-xs text-amber-900 shadow-xs flex items-start gap-2.5">
        <Info className="w-4 h-4 text-amber-700 flex-shrink-0 mt-0.5" />
        <div className="space-y-0.5 leading-relaxed">
          <p className="font-bold text-amber-900">
            {language === 'ta'
              ? 'விளக்கக் கணிப்பு மாதிரி (Demo AI Projections)'
              : 'Illustrative Demo Predictions · Explanatory AI Model'}
          </p>
          <p className="text-amber-800 text-[11px]">
            {language === 'ta'
              ? 'இந்த முன்னறிவிப்புகள் பருவ கால நோய் மாதிரித் தரவுகளைக் கொண்டு விளக்கத்திற்காக மட்டுமே கணக்கிடப்பட்டுள்ளன. உண்மையான மருத்துவ முடிவுகளுக்கு பதிவு பெற்ற மருந்தாளரின் உறுதிப்படுத்தல் தேவை.'
              : 'Predictions are illustrative demo projections based on rural epidemiological demand models (seasonal rainfall, harvest patterns, logistics delivery intervals). Not for real-world clinical dosage decisions.'}
          </p>
        </div>
      </div>

      {/* Screen Title & Subtitle */}
      <div>
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-xl bg-teal-100 text-teal-800 flex items-center justify-center">
            <Sparkles className="w-4 h-4" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-900">
              {language === 'ta' ? 'AI கையிருப்பு தீர்வு & கணிப்புகள்' : 'AI Stockout & Expiry Insights'}
            </h2>
            <p className="text-xs text-slate-500">
              {language === 'ta'
                ? 'பருவகால நோய் போக்குகள் மற்றும் சரக்கு காலாவதி தடுப்பு'
                : 'Epidemiological surge forecasting & circular medicine rescue'}
            </p>
          </div>
        </div>
      </div>

      {/* Interactive Seasonal Demand Shift Simulator */}
      <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sliders className="w-4 h-4 text-teal-700" />
            <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
              {language === 'ta' ? 'பருவகால உருவகப்படுத்துதல்' : 'Simulate Seasonal Disease Shift'}
            </h3>
          </div>
          <span className="text-[10px] text-teal-700 font-bold bg-teal-50 px-2 py-0.5 rounded-full">
            Interactive Test
          </span>
        </div>

        <div className="grid grid-cols-2 gap-2 text-xs">
          <button
            onClick={() => setSimulationScenario('HARVEST')}
            className={`p-2.5 rounded-xl border text-left transition-all ${
              simulationScenario === 'HARVEST'
                ? 'bg-teal-50/80 border-teal-500 text-teal-900 font-bold shadow-xs'
                : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
            }`}
          >
            <p className="font-bold text-slate-900">🌾 Paddy Harvest</p>
            <p className="text-[10px] text-slate-500 mt-0.5">+380% Snakebite Risk</p>
          </button>

          <button
            onClick={() => setSimulationScenario('MONSOON')}
            className={`p-2.5 rounded-xl border text-left transition-all ${
              simulationScenario === 'MONSOON'
                ? 'bg-teal-50/80 border-teal-500 text-teal-900 font-bold shadow-xs'
                : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
            }`}
          >
            <p className="font-bold text-slate-900">🌧️ Monsoon Flood</p>
            <p className="text-[10px] text-slate-500 mt-0.5">+65% Pediatric URTI</p>
          </button>

          <button
            onClick={() => setSimulationScenario('HEATWAVE')}
            className={`p-2.5 rounded-xl border text-left transition-all ${
              simulationScenario === 'HEATWAVE'
                ? 'bg-teal-50/80 border-teal-500 text-teal-900 font-bold shadow-xs'
                : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
            }`}
          >
            <p className="font-bold text-slate-900">☀️ Dry Heatwave</p>
            <p className="text-[10px] text-slate-500 mt-0.5">+80% Dehydration ORS</p>
          </button>

          <button
            onClick={() => setSimulationScenario('BASELINE')}
            className={`p-2.5 rounded-xl border text-left transition-all ${
              simulationScenario === 'BASELINE'
                ? 'bg-teal-50/80 border-teal-500 text-teal-900 font-bold shadow-xs'
                : 'bg-slate-50 border-slate-200 text-slate-600 hover:bg-slate-100'
            }`}
          >
            <p className="font-bold text-slate-900">📊 Baseline Normal</p>
            <p className="text-[10px] text-slate-500 mt-0.5">Steady Standard Rate</p>
          </button>
        </div>
      </div>

      {/* Stockout Risk Explanations Section */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wider">
            {language === 'ta' ? 'கையிருப்பு தீர்வு அபாய முன்னறிவிப்பு' : 'Predicted Stockout Risks & Explanations'}
          </h3>
          <span className="text-xs text-slate-500">
            {adjustedRisks.length} {language === 'ta' ? 'மருந்துகள்' : 'Medicines Tracked'}
          </span>
        </div>

        {adjustedRisks.map((risk) => {
          const med = medicines.find((m) => m.id === risk.medicineId);
          const isCritical = risk.riskLevel === 'CRITICAL';
          const isHigh = risk.riskLevel === 'HIGH';

          return (
            <div
              key={risk.id}
              className={`bg-white rounded-2xl border p-4 shadow-xs space-y-3 transition-all ${
                isCritical
                  ? 'border-rose-300 ring-1 ring-rose-200'
                  : isHigh
                  ? 'border-amber-300'
                  : 'border-slate-200'
              }`}
            >
              {/* Header with Risk Level and Days */}
              <div className="flex items-start justify-between gap-2">
                <div>
                  <div className="flex items-center gap-1.5 flex-wrap">
                    <span
                      className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                        isCritical
                          ? 'bg-rose-600 text-white'
                          : isHigh
                          ? 'bg-amber-600 text-white'
                          : 'bg-teal-700 text-white'
                      }`}
                    >
                      {risk.riskLevel} RISK
                    </span>
                    <span className="text-[11px] font-semibold text-slate-500">
                      {risk.confidence}% Model Confidence
                    </span>
                  </div>
                  <h4 className="text-sm font-bold text-slate-900 mt-1">
                    {risk.medicineName}
                  </h4>
                  <p className="text-[11px] text-slate-500">
                    {risk.genericName}
                  </p>
                </div>

                <div className="text-right flex-shrink-0 bg-slate-50 px-2.5 py-1.5 rounded-xl border border-slate-100">
                  <p
                    className={`text-base font-extrabold ${
                      isCritical ? 'text-rose-600' : isHigh ? 'text-amber-600' : 'text-teal-700'
                    }`}
                  >
                    {risk.daysToStockout} Days
                  </p>
                  <p className="text-[10px] text-slate-500 font-medium">Until Zero Stock</p>
                </div>
              </div>

              {/* Primary Cause Alert Box */}
              <div className="bg-slate-50 rounded-xl p-3 border border-slate-100 text-xs text-slate-800 space-y-1">
                <p className="font-bold text-slate-900 flex items-center gap-1.5">
                  <ShieldAlert className="w-3.5 h-3.5 text-amber-600" />
                  <span>Primary Trigger: {risk.primaryCause}</span>
                </p>
                <p className="text-slate-600 text-[11px]">{risk.seasonalFactorName}</p>
              </div>

              {/* Clear Explanations: 3 Factors */}
              <div className="space-y-1.5">
                <p className="text-[11px] font-bold text-slate-700 uppercase tracking-wide">
                  Why the AI flagged this risk:
                </p>
                <ul className="space-y-1.5 text-xs text-slate-600">
                  {risk.factors.map((factor, idx) => (
                    <li key={idx} className="flex items-start gap-2 leading-relaxed">
                      <span className="w-1.5 h-1.5 rounded-full bg-teal-600 mt-1.5 flex-shrink-0" />
                      <span>{factor}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {/* Recommended Action & Transfer CTA */}
              <div className="pt-2 border-t border-slate-100 flex items-center justify-between gap-2 flex-wrap">
                <div className="text-xs text-emerald-800 font-medium">
                  💡 <span className="font-bold">Recommended:</span> {risk.suggestedSourcePHC ? `Rescue from ${risk.suggestedSourcePHC}` : 'Reorder buffer'}
                </div>
                <button
                  onClick={() => {
                    const sourcePHC = phcs.find((p) => p.name === risk.suggestedSourcePHC) || phcs[0];
                    onOpenTransfer(med, sourcePHC, 'REQUEST_DEFICIT');
                  }}
                  className="px-3.5 py-1.5 rounded-xl bg-teal-700 hover:bg-teal-800 active:bg-teal-900 text-white text-xs font-bold flex items-center gap-1 shadow-xs transition-colors"
                >
                  <Send className="w-3.5 h-3.5" />
                  <span>Initiate Rescue Request</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* AI Expiry Waste Prevention Strategy */}
      <div className="bg-white rounded-2xl border border-slate-100 p-4 shadow-xs space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Clock className="w-4 h-4 text-emerald-700" />
            <h3 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
              {language === 'ta' ? 'கழிவுத் தடுப்பு மறுபகிர்வு' : 'AI Expiry Waste Prevention Strategy'}
            </h3>
          </div>
          <span className="text-[10px] text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded-full">
            Circular Healthcare
          </span>
        </div>

        <p className="text-xs text-slate-600 leading-relaxed">
          The AI calculates each drug's <strong>local consumption burn rate</strong> vs <strong>days until expiry</strong>. Medicines that cannot be consumed locally before expiration are matched to high-volume neighboring hospitals.
        </p>

        <div className="space-y-3">
          {expiringExcess.map((item) => (
            <div
              key={item.id}
              className="p-3.5 rounded-xl bg-emerald-50/60 border border-emerald-100 space-y-2 text-xs text-emerald-950"
            >
              <div className="flex items-start justify-between">
                <div>
                  <h4 className="font-bold text-slate-900">{item.name}</h4>
                  <p className="text-[11px] text-slate-600">
                    Batch: {item.batchNumber} · Current Stock: {item.stock} {item.unit}
                  </p>
                </div>
                <span className="text-[11px] font-bold text-amber-700 bg-amber-100/80 px-2 py-0.5 rounded-md">
                  Expires in {item.daysUntilExpiry} days
                </span>
              </div>

              <div className="bg-white/80 rounded-lg p-2.5 border border-emerald-200/50 text-[11px] space-y-1">
                <p className="font-bold text-emerald-900">
                  ⚡ AI Match: Local burn rate is only {Math.round(item.stock * 0.25)} {item.unit}/month.
                </p>
                <p className="text-slate-600">
                  Ulundurpet Taluk Hospital has an active patient surge and can utilize {item.surplusUnits || 45} {item.unit} immediately, saving approximately ₹6,750 from expiry discard.
                </p>
              </div>

              <div className="flex justify-end">
                <button
                  onClick={() => {
                    const ulundurpet = phcs.find((p) => p.name.includes('Ulundurpet'));
                    onOpenTransfer(item, ulundurpet, 'DISPATCH_SURPLUS');
                  }}
                  className="px-3 py-1.5 rounded-lg bg-teal-800 hover:bg-teal-900 text-white font-bold text-xs flex items-center gap-1 transition-colors"
                >
                  <ArrowRight className="w-3.5 h-3.5" />
                  <span>Transfer Surplus to Ulundurpet</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
