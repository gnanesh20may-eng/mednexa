import React, { useState } from 'react';
import { X, Plus, ShieldCheck, Thermometer } from 'lucide-react';
import { MedicineItem } from '../types';

interface AddMedicineModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddMedicine: (newMedicine: MedicineItem) => void;
}

export const AddMedicineModal: React.FC<AddMedicineModalProps> = ({
  isOpen,
  onClose,
  onAddMedicine,
}) => {
  if (!isOpen) return null;

  const [name, setName] = useState('');
  const [genericName, setGenericName] = useState('');
  const [category, setCategory] = useState<MedicineItem['category']>('Antibiotic');
  const [form, setForm] = useState<MedicineItem['form']>('Tablet');
  const [batchNumber, setBatchNumber] = useState(`BATCH-TN-2026-${Math.floor(100 + Math.random() * 900)}`);
  const [stock, setStock] = useState(100);
  const [minSafetyStock, setMinSafetyStock] = useState(30);
  const [unit, setUnit] = useState('tablets');
  const [expiryDate, setExpiryDate] = useState('2027-08-30');
  const [coldChainRequired, setColdChainRequired] = useState(false);
  const [rackLocation, setRackLocation] = useState('Bay 1, Shelf A');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    // Calculate approximate days until expiry
    const expiry = new Date(expiryDate);
    const today = new Date();
    const diffTime = expiry.getTime() - today.getTime();
    const daysUntilExpiry = Math.max(1, Math.ceil(diffTime / (1000 * 60 * 60 * 24)));

    // Importance mapping by category
    const importanceMap: Record<MedicineItem['category'], number> = {
      'Emergency Antidote': 5,
      'Vaccine': 4,
      'Antibiotic': 4,
      'Analgesic': 3,
      'Chronic Care': 3,
      'Maternal Care': 2,
      'Hydration': 1,
    };

    // Generate 30-day consumption history based on category and stock
    const consumptionHistoryLength = 30;
    const consumptionHistory = [];
    for (let i = 0; i < consumptionHistoryLength; i++) {
      const base = importanceMap[category] * 10;
      const variation = Math.floor(Math.random() * 6); // 0-5 variation
      consumptionHistory.push(base + variation);
    }

    const newMed: MedicineItem = {
      id: `med-${Date.now()}`,
      name: name.trim(),
      genericName: genericName.trim() || name.trim(),
      category,
      form,
      batchNumber,
      stock: Number(stock) || 0,
      minSafetyStock: Number(minSafetyStock) || 0,
      unit,
      expiryDate,
      daysUntilExpiry,
      coldChainRequired,
      coldChainTemp: coldChainRequired ? '2°C - 8°C (Monitored)' : undefined,
      rackLocation,
      isRescueCandidate: daysUntilExpiry < 90,
      surplusUnits: 0,
      consumptionHistory,
      importance: importanceMap[category],
    };

    onAddMedicine(newMed);
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-fade-in">
      <div className="bg-white w-full max-w-md rounded-2xl shadow-2xl border border-slate-100 overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="bg-teal-800 text-white px-5 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Plus className="w-5 h-5 text-emerald-300" />
            <div>
              <h2 className="text-base font-bold">Add Medicine Batch</h2>
              <p className="text-xs text-teal-200">Log new stock into PHC Pharmacy Ledger</p>
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
        <form onSubmit={handleSubmit} className="p-5 overflow-y-auto space-y-3.5 text-sm text-slate-800 flex-1">
          <div>
            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
              Brand / Medicine Name *
            </label>
            <input
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. Ciprofloxacin 500mg"
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-sm focus:ring-2 focus:ring-teal-500 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
              Generic Chemical Name / Formulation
            </label>
            <input
              type="text"
              value={genericName}
              onChange={(e) => setGenericName(e.target.value)}
              placeholder="e.g. Ciprofloxacin Hydrochloride IP"
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-sm focus:ring-2 focus:ring-teal-500 focus:outline-none"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Category
              </label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value as any)}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-2 text-xs focus:ring-2 focus:ring-teal-500 focus:outline-none"
              >
                <option value="Antibiotic">Antibiotic</option>
                <option value="Analgesic">Analgesic</option>
                <option value="Emergency Antidote">Emergency Antidote</option>
                <option value="Vaccine">Vaccine</option>
                <option value="Chronic Care">Chronic Care</option>
                <option value="Maternal Care">Maternal Care</option>
                <option value="Hydration">Hydration</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Dosage Form
              </label>
              <select
                value={form}
                onChange={(e) => {
                  const f = e.target.value as any;
                  setForm(f);
                  if (f === 'Tablet') setUnit('tablets');
                  else if (f === 'Injection') setUnit('vials');
                  else if (f === 'Syrup/Suspension') setUnit('bottles');
                  else if (f === 'Sachet') setUnit('sachets');
                  else if (f === 'Ampoule') setUnit('ampoules');
                  else if (f === 'Lyophilized') setUnit('vials');
                }}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-2.5 py-2 text-xs focus:ring-2 focus:ring-teal-500 focus:outline-none"
              >
                <option value="Tablet">Tablet</option>
                <option value="Injection">Injection</option>
                <option value="Syrup/Suspension">Syrup / Suspension</option>
                <option value="Sachet">Sachet</option>
                <option value="Ampoule">Ampoule</option>
                <option value="Lyophilized">Lyophilized Vial</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Batch Number
              </label>
              <input
                type="text"
                required
                value={batchNumber}
                onChange={(e) => setBatchNumber(e.target.value)}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-mono focus:ring-2 focus:ring-teal-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Storage Rack Location
              </label>
              <input
                type="text"
                value={rackLocation}
                onChange={(e) => setRackLocation(e.target.value)}
                placeholder="e.g. Bay 2, Shelf B"
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs focus:ring-2 focus:ring-teal-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Current Quantity
              </label>
              <input
                type="number"
                min="0"
                value={stock}
                onChange={(e) => setStock(parseInt(e.target.value) || 0)}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-sm font-semibold focus:ring-2 focus:ring-teal-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
                Min. Safety Buffer
              </label>
              <input
                type="number"
                min="0"
                value={minSafetyStock}
                onChange={(e) => setMinSafetyStock(parseInt(e.target.value) || 0)}
                className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-sm font-semibold focus:ring-2 focus:ring-teal-500 focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-600 uppercase tracking-wider mb-1">
              Expiry Date
            </label>
            <input
              type="date"
              required
              value={expiryDate}
              onChange={(e) => setExpiryDate(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-sm focus:ring-2 focus:ring-teal-500 focus:outline-none"
            />
          </div>

          <div className="p-3 bg-slate-50 border border-slate-200/80 rounded-xl flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Thermometer className="w-4 h-4 text-sky-600" />
              <div>
                <p className="text-xs font-bold text-slate-800">Cold Chain Storage (2°C - 8°C)</p>
                <p className="text-[11px] text-slate-500">Requires dedicated ILR (Ice Lined Refrigerator)</p>
              </div>
            </div>
            <input
              type="checkbox"
              checked={coldChainRequired}
              onChange={(e) => setColdChainRequired(e.target.checked)}
              className="w-4 h-4 text-teal-600 rounded-sm focus:ring-teal-500"
            />
          </div>

          {/* Action buttons */}
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
              <Plus className="w-3.5 h-3.5" />
              <span>Save to Ledger</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
