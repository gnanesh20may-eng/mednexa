import React, { useState } from 'react';
import {
  MapPin,
  Truck,
  Navigation2,
  Info,
  Phone,
  Thermometer,
  ShieldCheck,
  Clock,
  ArrowRight,
  RotateCw,
  Send,
  CheckCircle2,
  Share2
} from 'lucide-react';
import { PHCCenter, RescueTransfer, MedicineItem } from '../types';

interface RescueMapScreenProps {
  phcs: PHCCenter[];
  transfers: RescueTransfer[];
  medicines: MedicineItem[];
  onOpenTransfer: (med?: MedicineItem, phc?: PHCCenter, type?: 'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT') => void;
  onUpdateTransferStatus: (transferId: string, newStatus: RescueTransfer['status']) => void;
  language: 'en' | 'ta';
}

export const RescueMapScreen: React.FC<RescueMapScreenProps> = ({
  phcs,
  transfers,
  medicines,
  onOpenTransfer,
  onUpdateTransferStatus,
  language,
}) => {
  const [selectedPHC, setSelectedPHC] = useState<PHCCenter | null>(phcs[0] || null);
  const [viewMode, setViewMode] = useState<'MAP' | 'DISPATCHES' | 'MATCHES'>('MAP');

  const basePHC = {
    name: 'Kallakurichi Taluk PHC (Base Station)',
    x: 48,
    y: 50,
  };

  return (
    <div className="p-4 space-y-4 pb-20">
      {/* Demo Map Notice */}
      <div className="bg-slate-100 border border-slate-200/80 rounded-2xl p-3 text-xs text-slate-700 shadow-xs flex items-start gap-2">
        <Info className="w-4 h-4 text-teal-700 flex-shrink-0 mt-0.5" />
        <p className="leading-relaxed">
          {language === 'ta'
            ? 'விளக்க வரைபட மாதிரி: கள்ளக்குறிச்சி வட்டார சுகாதார நிலையங்களின் மருந்து பரிமாற்ற கட்டமைப்பு. நேரடி ஜி.பி.எஸ் அல்ல; விளக்க மாதிரி.'
            : 'Illustrative map-style interface of rural PHC inter-facility medicine mesh in Kallakurichi Taluk. Demo coordinates for prototype evaluation.'}
        </p>
      </div>

      {/* Screen Title & Segmented Controls */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-base font-bold text-slate-900">
            {language === 'ta' ? 'மருந்து மீட்பு வரைபடம்' : 'Inter-PHC Rescue Map'}
          </h2>
          <p className="text-xs text-slate-500">
            {language === 'ta'
              ? 'அருகிலுள்ள 6 ஆரம்ப சுகாதார நிலையங்களின் இருப்பு பகிர்வு'
              : 'Decentralized peer-to-peer redistribution mesh'}
          </p>
        </div>

        {/* View mode toggle */}
        <div className="flex items-center gap-1 p-1 bg-slate-100 rounded-xl">
          <button
            onClick={() => setViewMode('MAP')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-colors ${
              viewMode === 'MAP' ? 'bg-white text-teal-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Map
          </button>
          <button
            onClick={() => setViewMode('DISPATCHES')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-colors flex items-center gap-1 ${
              viewMode === 'DISPATCHES' ? 'bg-white text-teal-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <span>Live Routes</span>
            <span className="w-4 h-4 rounded-full bg-teal-700 text-white text-[10px] flex items-center justify-center font-bold">
              {transfers.length}
            </span>
          </button>
          <button
            onClick={() => setViewMode('MATCHES')}
            className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-colors ${
              viewMode === 'MATCHES' ? 'bg-white text-teal-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            Matches
          </button>
        </div>
      </div>

      {viewMode === 'MAP' && (
        <div className="space-y-3">
          {/* Stylized Rural Map Container */}
          <div className="relative w-full h-72 sm:h-80 bg-gradient-to-b from-teal-950 via-slate-900 to-slate-950 rounded-2xl overflow-hidden border border-slate-700 shadow-inner select-none">
            {/* Map grid lines */}
            <div className="absolute inset-0 opacity-15 bg-[radial-gradient(#14b8a6_1px,transparent_1px)] [background-size:16px_16px]" />

            {/* Stylized Highway connection roads */}
            <svg className="absolute inset-0 w-full h-full pointer-events-none">
              {/* Roads from base PHC to other PHCs */}
              {phcs.map((phc) => (
                <g key={phc.id}>
                  {/* Road line */}
                  <line
                    x1={`${basePHC.x}%`}
                    y1={`${basePHC.y}%`}
                    x2={`${phc.x}%`}
                    y2={`${phc.y}%`}
                    stroke="rgba(45, 212, 191, 0.35)"
                    strokeWidth="2"
                    strokeDasharray="4 3"
                  />
                  {/* Moving dot representing active transfers */}
                  <circle
                    cx={`${(basePHC.x + phc.x) / 2}%`}
                    cy={`${(basePHC.y + phc.y) / 2}%`}
                    r="3.5"
                    fill="#34d399"
                    className="animate-ping"
                  />
                </g>
              ))}
            </svg>

            {/* Highway labels */}
            <div className="absolute top-3 left-4 text-[10px] font-mono text-teal-400/80 bg-teal-950/60 px-2 py-0.5 rounded border border-teal-800/40">
              NH-79 Corridor · Salem-Ulundurpet
            </div>

            {/* Central Base PHC Pin */}
            <div
              style={{ left: `${basePHC.x}%`, top: `${basePHC.y}%` }}
              className="absolute -translate-x-1/2 -translate-y-1/2 z-20 flex flex-col items-center group cursor-pointer"
            >
              <div className="relative">
                <div className="w-9 h-9 rounded-full bg-teal-500/30 animate-ping absolute inset-0" />
                <div className="w-9 h-9 rounded-full bg-teal-600 border-2 border-white text-white flex items-center justify-center shadow-lg">
                  <ShieldCheck className="w-5 h-5 text-emerald-300" />
                </div>
              </div>
              <span className="mt-1 px-2 py-0.5 rounded-md bg-teal-900/90 text-[10px] font-bold text-white border border-teal-500/50 shadow-md whitespace-nowrap">
                Base: Kallakurichi PHC
              </span>
            </div>

            {/* Partner PHC Pins */}
            {phcs.map((phc) => {
              const isSelected = selectedPHC?.id === phc.id;
              const hasSurplus = phc.surplusList.length > 0;
              return (
                <div
                  key={phc.id}
                  onClick={() => setSelectedPHC(phc)}
                  style={{ left: `${phc.x}%`, top: `${phc.y}%` }}
                  className="absolute -translate-x-1/2 -translate-y-1/2 z-20 flex flex-col items-center cursor-pointer transition-transform hover:scale-110"
                >
                  <div
                    className={`w-7 h-7 rounded-full flex items-center justify-center shadow-md transition-all ${
                      isSelected
                        ? 'bg-emerald-400 text-slate-950 ring-4 ring-emerald-400/30 scale-110'
                        : hasSurplus
                        ? 'bg-teal-600 text-white border border-teal-300'
                        : 'bg-slate-700 text-slate-300 border border-slate-500'
                    }`}
                  >
                    <MapPin className="w-4 h-4" />
                  </div>
                  <span
                    className={`mt-1 px-1.5 py-0.5 rounded text-[9px] font-bold transition-all whitespace-nowrap ${
                      isSelected
                        ? 'bg-white text-slate-950 shadow-md font-extrabold'
                        : 'bg-slate-900/80 text-teal-200 border border-slate-700/60'
                    }`}
                  >
                    {phc.name.replace(' Primary Health Centre', '').replace(' Hospital', '')} ({phc.distanceKm} km)
                  </span>
                </div>
              );
            })}

            {/* Map Legend */}
            <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between text-[10px] text-slate-300 bg-slate-950/80 backdrop-blur-xs px-3 py-1.5 rounded-xl border border-slate-800">
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-teal-600 border border-white"></span>
                Base PHC
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-400"></span>
                Surplus Ready
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2 h-0.5 bg-teal-400"></span>
                Rural Transit Route
              </span>
            </div>
          </div>

          {/* Selected PHC Bottom Sheet Card */}
          {selectedPHC && (
            <div className="bg-white rounded-2xl border border-slate-100 p-4 shadow-sm space-y-3">
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-sm font-bold text-slate-900">{selectedPHC.name}</h3>
                    <span className="text-[10px] font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md">
                      {selectedPHC.status}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 mt-0.5">
                    {selectedPHC.taluk} · In-Charge: {selectedPHC.doctorInCharge}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-extrabold text-teal-800">{selectedPHC.distanceKm} km</p>
                  <p className="text-[10px] text-slate-500 font-medium">~{selectedPHC.travelTimeMin} mins courier time</p>
                </div>
              </div>

              {/* Surplus & Shortage Balance */}
              <div className="grid grid-cols-2 gap-2 text-xs">
                {/* Surpluses they offer */}
                <div className="p-3 bg-emerald-50/70 rounded-xl border border-emerald-100">
                  <p className="font-bold text-emerald-900 flex items-center gap-1 text-[11px] mb-1.5">
                    <span>🟢 Surplus Available</span>
                  </p>
                  {selectedPHC.surplusList.length === 0 ? (
                    <p className="text-[11px] text-slate-500">None currently listed</p>
                  ) : (
                    <div className="space-y-1 text-[11px] text-slate-700">
                      {selectedPHC.surplusList.map((s, idx) => (
                        <div key={idx} className="flex justify-between font-medium">
                          <span className="truncate pr-1">{s.medicineName}</span>
                          <span className="font-bold text-emerald-800 flex-shrink-0">+{s.quantity}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* Shortages they need */}
                <div className="p-3 bg-rose-50/70 rounded-xl border border-rose-100">
                  <p className="font-bold text-rose-900 flex items-center gap-1 text-[11px] mb-1.5">
                    <span>🔴 Critical Shortages</span>
                  </p>
                  {selectedPHC.shortageList.length === 0 ? (
                    <p className="text-[11px] text-slate-500">Adequate stock</p>
                  ) : (
                    <div className="space-y-1 text-[11px] text-slate-700">
                      {selectedPHC.shortageList.map((s, idx) => (
                        <div key={idx} className="flex justify-between font-medium">
                          <span className="truncate pr-1">{s.medicineName}</span>
                          <span className="font-bold text-rose-700 flex-shrink-0">-{s.quantityNeeded}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              {/* Action buttons */}
              <div className="pt-2 flex items-center justify-end gap-2 border-t border-slate-100">
                <button
                  onClick={() => onOpenTransfer(undefined, selectedPHC, 'REQUEST_DEFICIT')}
                  className="px-3.5 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-800 font-semibold text-xs flex items-center gap-1.5 transition-colors"
                >
                  <Send className="w-3.5 h-3.5 text-teal-700" />
                  <span>Request Surplus Stock</span>
                </button>
                <button
                  onClick={() => onOpenTransfer(undefined, selectedPHC, 'DISPATCH_SURPLUS')}
                  className="px-3.5 py-2 rounded-xl bg-teal-700 hover:bg-teal-800 text-white font-semibold text-xs flex items-center gap-1.5 shadow-xs transition-colors"
                >
                  <Truck className="w-3.5 h-3.5 text-emerald-300" />
                  <span>Send Medicine Here</span>
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {viewMode === 'DISPATCHES' && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Active Inter-PHC Transit Routes ({transfers.length})
            </h3>
            <span className="text-xs text-slate-500">Live Cold-Chain Tracking</span>
          </div>

          <div className="space-y-3">
            {transfers.map((trf) => (
              <div
                key={trf.id}
                className="bg-white rounded-2xl border border-slate-100 p-4 shadow-xs space-y-3"
              >
                {/* Header */}
                <div className="flex items-start justify-between">
                  <div>
                    <span
                      className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                        trf.status === 'IN_TRANSIT'
                          ? 'bg-amber-100 text-amber-800'
                          : trf.status === 'DELIVERED'
                          ? 'bg-emerald-100 text-emerald-800'
                          : 'bg-teal-100 text-teal-800'
                      }`}
                    >
                      {trf.status.replace('_', ' ')}
                    </span>
                    <h4 className="text-sm font-bold text-slate-900 mt-1">{trf.medicineName}</h4>
                    <p className="text-[11px] text-slate-500 font-mono">
                      Batch: {trf.batchNumber} · {trf.quantity} {trf.unit}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-extrabold text-teal-800">
                      {trf.status === 'DELIVERED' ? 'Delivered' : `ETA ${trf.etaMinutes} mins`}
                    </p>
                    <p className="text-[10px] text-slate-400">{trf.distanceKm} km route</p>
                  </div>
                </div>

                {/* Route visualization */}
                <div className="p-3 bg-slate-50 rounded-xl border border-slate-100 flex items-center justify-between text-xs font-medium text-slate-700">
                  <div className="min-w-0 pr-2">
                    <p className="text-[10px] text-slate-400 uppercase font-bold">Origin</p>
                    <p className="font-bold text-slate-900 truncate">{trf.fromPHC}</p>
                  </div>
                  <ArrowRight className="w-4 h-4 text-teal-600 flex-shrink-0" />
                  <div className="min-w-0 pl-2 text-right">
                    <p className="text-[10px] text-slate-400 uppercase font-bold">Destination</p>
                    <p className="font-bold text-slate-900 truncate">{trf.toPHC}</p>
                  </div>
                </div>

                {/* Cold chain & Driver telemetry */}
                <div className="grid grid-cols-2 gap-2 text-[11px] text-slate-600">
                  {trf.coldChain && (
                    <div className="p-2 rounded-lg bg-sky-50 border border-sky-100 flex items-center gap-1.5 text-sky-900">
                      <Thermometer className="w-3.5 h-3.5 text-sky-600 flex-shrink-0" />
                      <span>Cold Chain: {trf.tempReading || '3.8°C (2-8°C OK)'}</span>
                    </div>
                  )}
                  <div className="p-2 rounded-lg bg-slate-50 border border-slate-100 flex items-center gap-1.5 text-slate-700">
                    <Truck className="w-3.5 h-3.5 text-slate-500 flex-shrink-0" />
                    <span className="truncate">{trf.vehicleType}</span>
                  </div>
                </div>

                {/* Driver phone and completion action */}
                <div className="pt-2 flex items-center justify-between text-xs border-t border-slate-100">
                  <div className="flex items-center gap-1.5 text-slate-500">
                    <Phone className="w-3 h-3 text-slate-400" />
                    <span>{trf.driverName || 'Health Courier'} ({trf.driverPhone || '+91 98421 11200'})</span>
                  </div>
                  {trf.status !== 'DELIVERED' && (
                    <button
                      onClick={() => onUpdateTransferStatus(trf.id, 'DELIVERED')}
                      className="px-2.5 py-1 rounded-lg bg-emerald-700 hover:bg-emerald-800 text-white font-bold text-[11px] flex items-center gap-1 transition-colors"
                    >
                      <CheckCircle2 className="w-3 h-3" />
                      <span>Mark Delivered</span>
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {viewMode === 'MATCHES' && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Smart Inter-PHC Matches
            </h3>
            <span className="text-xs text-teal-700 font-bold">Automatic Pairing</span>
          </div>

          <div className="bg-white rounded-2xl border border-slate-100 p-4 shadow-xs space-y-3">
            <div className="p-3 bg-teal-50/70 border border-teal-100 rounded-xl space-y-2 text-xs">
              <div className="flex items-center justify-between">
                <h4 className="font-bold text-teal-950">Match #1: Cefotaxime 1g Injection</h4>
                <span className="text-[10px] font-bold text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded-full">
                  High Priority
                </span>
              </div>
              <p className="text-slate-600">
                <strong>Kallakurichi PHC</strong> has 50 surplus vials expiring in 41 days. <strong>Ulundurpet Taluk Hospital</strong> has 0 stock with 45 units required for emergency inpatient ward.
              </p>
              <div className="flex justify-end pt-1">
                <button
                  onClick={() => {
                    const cef = medicines.find((m) => m.name.includes('Cefotaxime'));
                    const ulundurpet = phcs.find((p) => p.name.includes('Ulundurpet'));
                    onOpenTransfer(cef, ulundurpet, 'DISPATCH_SURPLUS');
                  }}
                  className="px-3 py-1.5 rounded-lg bg-teal-800 hover:bg-teal-900 text-white font-bold text-xs flex items-center gap-1"
                >
                  <Share2 className="w-3 h-3" />
                  <span>Execute 45 Vial Rescue</span>
                </button>
              </div>
            </div>

            <div className="p-3 bg-teal-50/70 border border-teal-100 rounded-xl space-y-2 text-xs">
              <div className="flex items-center justify-between">
                <h4 className="font-bold text-teal-950">Match #2: Anti-Rabies Vaccine (ARV)</h4>
                <span className="text-[10px] font-bold text-emerald-800 bg-emerald-100 px-2 py-0.5 rounded-full">
                  Cold-Chain Match
                </span>
              </div>
              <p className="text-slate-600">
                <strong>Chinnasalem PHC</strong> holds 24 surplus doses in active cold storage (expires in 14 months). <strong>Kallakurichi PHC</strong> has only 4 doses remaining for ongoing dog bite regimens.
              </p>
              <div className="flex justify-end pt-1">
                <button
                  onClick={() => {
                    const arv = medicines.find((m) => m.name.includes('Rabies'));
                    const chinna = phcs.find((p) => p.name.includes('Chinnasalem'));
                    onOpenTransfer(arv, chinna, 'REQUEST_DEFICIT');
                  }}
                  className="px-3 py-1.5 rounded-lg bg-teal-800 hover:bg-teal-900 text-white font-bold text-xs flex items-center gap-1"
                >
                  <Share2 className="w-3 h-3" />
                  <span>Request 10 Doses Transfer</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
