import React, { useState } from 'react';
import { X, Send, Truck, AlertTriangle, ShieldCheck, Thermometer } from 'lucide-react';
import { MedicineItem, PHCCenter, RescueTransfer } from '../types';

interface TransferModalProps {
  isOpen: boolean;
  onClose: () => void;
  medicine?: MedicineItem | null;
  targetPHC?: PHCCenter | null;
  availablePHCs: PHCCenter[];
  availableMedicines: MedicineItem[];
  transferType: 'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT';
  onConfirmTransfer: (transfer: Omit<RescueTransfer, 'id' | 'requestedAt'>) => void;
}

export const TransferModal: React.FC<TransferModalProps> = ({
  isOpen,
  onClose,
  medicine,
  targetPHC,
  availablePHCs,
  availableMedicines,
  transferType,
  onConfirmTransfer,
}) => {
  if (!isOpen) return null;

  const [selectedMedId, setSelectedMedId] = useState<string>(medicine?.id || availableMedicines[0]?.id || '');
  const [selectedPHCId, setSelectedPHCId] = useState<string>(targetPHC?.id || availablePHCs[0]?.id || '');
  const [quantity, setQuantity] = useState<number>(medicine?.surplusUnits || medicine?.deficitUnits || 10);
  const [vehicle, setVehicle] = useState<'2-Wheeler Health Courier' | 'Ambulance Shuttle' | 'PHC Logistics Van'>('2-Wheeler Health Courier');
  const [notes, setNotes] = useState<string>('Standard cold-box dispatch protocol with thermal logger.');

  const currentMed = availableMedicines.find((m) => m.id === selectedMedId) || medicine;
  const currentTargetPHC = availablePHCs.find((p) => p.id === selectedPHCId) || targetPHC;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentMed || !currentTargetPHC) return;

    const isDispatch = transferType === 'DISPATCH_SURPLUS';
    const fromPHC = isDispatch ? 'Kallakurichi Taluk PHC' : currentTargetPHC.name;
    const toPHC = isDispatch ? currentTargetPHC.name : 'Kallakurichi Taluk PHC';

    onConfirmTransfer({
      medicineName: currentMed.name,
      batchNumber: currentMed.batchNumber,
      quantity: Number(quantity) || 1,
      unit: currentMed.unit,
      fromPHC,
      toPHC,
      distanceKm: currentTargetPHC.distanceKm,
      etaMinutes: Math.round(currentTargetPHC.travelTimeMin * 1.1),
      status: 'DISPATCHED',
      coldChain: currentMed.coldChainRequired,
      tempReading: currentMed.coldChainRequired ? '3.6°C (Verified)' : undefined,
      vehicleType: vehicle,
      driverName: 'Murugan / Kannan (District Health Courier)',
      driverPhone: '+91 98421 11200',
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-fade-in">
      <div className="bg-white w-full max-w-md rounded-2xl shadow-2xl border border-slate-100 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="bg-teal-800 text-white px-5 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Truck className="w-5 h-5 text-emerald-300" />
            <div>
              <h2 className="text-base font-bold">
                {transferType === 'DISPATCH_SURPLUS' ? 'Dispatch Surplus Medicine' : 'Request Medicine Transfer'}
              </h2>
              <p className="text-xs text-teal-200">
                {transferType === 'DISPATCH_SURPLUS' ? 'Rescue near-expiry stock from waste' : 'Emergency stock replenishment'}
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-teal-200 hover:text-white hover:bg-teal-700 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content Form */}
        <form onSubmit={handleSubmit} className="p-5 overflow-y-auto space-y-4 text-sm text-slate-800 flex-1">
          {/* Medicine Selection */}
          <div>
            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
              Select Medicine
            </label>
            <select
              value={selectedMedId}
              onChange={(e) => setSelectedMedId(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2.5 text-sm font-medium focus:ring-2 focus:ring-teal-500 focus:outline-none"
            >
              {availableMedicines.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name} ({m.stock} {m.unit} in stock)
                </option>
              ))}
            </select>
            {currentMed?.coldChainRequired && (
              <div className="mt-1.5 flex items-center gap-1.5 text-xs text-sky-700 bg-sky-50 px-2.5 py-1 rounded-lg border border-sky-100">
                <Thermometer className="w-3.5 h-3.5 flex-shrink-0" />
                <span>Cold-Chain Required: 2°C - 8°C insulated vaccine carrier box will be dispatched.</span>
              </div>
            )}
          </div>

          {/* Partner PHC Selection */}
          <div>
            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
              {transferType === 'DISPATCH_SURPLUS' ? 'Destination PHC (Recipient)' : 'Source PHC (Supplier)'}
            </label>
            <select
              value={selectedPHCId}
              onChange={(e) => setSelectedPHCId(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2.5 text-sm font-medium focus:ring-2 focus:ring-teal-500 focus:outline-none"
            >
              {availablePHCs.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} ({p.distanceKm} km · ~{p.travelTimeMin} mins)
                </option>
              ))}
            </select>
          </div>

          {/* Quantity & Units */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Transfer Quantity
              </label>
              <div className="relative">
                <input
                  type="number"
                  min="1"
                  max="1000"
                  value={quantity}
                  onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-sm font-semibold focus:ring-2 focus:ring-teal-500 focus:outline-none"
                />
                <span className="absolute right-3 top-2.5 text-xs text-slate-500 font-medium">
                  {currentMed?.unit || 'units'}
                </span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Transport Vehicle
              </label>
              <select
                value={vehicle}
                onChange={(e) => setVehicle(e.target.value as any)}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-2 text-xs font-medium focus:ring-2 focus:ring-teal-500 focus:outline-none"
              >
                <option value="2-Wheeler Health Courier">2-Wheeler Courier</option>
                <option value="Ambulance Shuttle">Ambulance Shuttle</option>
                <option value="PHC Logistics Van">PHC Logistics Van</option>
              </select>
            </div>
          </div>

          {/* Dispatch Notice info */}
          <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-100 text-xs text-emerald-900 space-y-1">
            <div className="flex items-center gap-1.5 font-bold text-emerald-800">
              <ShieldCheck className="w-4 h-4" />
              <span>Inter-PHC Transfer Protocol</span>
            </div>
            <p className="leading-relaxed">
              Transfer is digitally recorded in the Tamil Nadu Medical Services Corporation (TNMSC) local sub-ledger. Digital proof of handover is logged on delivery.
            </p>
          </div>

          {/* Buttons */}
          <div className="pt-2 flex items-center justify-end gap-2.5">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-xl text-slate-600 hover:bg-slate-100 font-medium text-xs transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2.5 rounded-xl bg-teal-700 hover:bg-teal-800 active:bg-teal-900 text-white font-semibold text-xs flex items-center gap-1.5 shadow-md shadow-teal-700/20 transition-colors"
            >
              <Send className="w-3.5 h-3.5" />
              <span>Confirm & Dispatch</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
