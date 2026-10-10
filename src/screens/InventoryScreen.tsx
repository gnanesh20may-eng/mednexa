import React, { useState, useMemo } from 'react';
import {
  Search,
  Filter,
  Plus,
  ThermometerSnowflake,
  AlertTriangle,
  Clock,
  CheckCircle2,
  Share2,
  Minus,
  Layers,
  ArrowUpDown
} from 'lucide-react';
import { MedicineItem, PHCCenter } from '../types';

interface InventoryScreenProps {
  medicines: MedicineItem[];
  onOpenAddMedicine: () => void;
  onOpenTransfer: (med?: MedicineItem, phc?: PHCCenter, type?: 'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT') => void;
  onAdjustStock: (medicineId: string, delta: number) => void;
  language: 'en' | 'ta';
}

export const InventoryScreen: React.FC<InventoryScreenProps> = ({
  medicines,
  onOpenAddMedicine,
  onOpenTransfer,
  onAdjustStock,
  language,
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [filterType, setFilterType] = useState<'ALL' | 'LOW_STOCK' | 'NEAR_EXPIRY' | 'COLD_CHAIN'>('ALL');
  const [sortBy, setSortBy] = useState<'EXPIRY' | 'STOCK_ASC' | 'NAME'>('EXPIRY');

  // Filter & search logic
  const filteredMedicines = useMemo(() => {
    return medicines
      .filter((m) => {
        // Search
        const query = searchQuery.toLowerCase().trim();
        const matchesQuery =
          !query ||
          m.name.toLowerCase().includes(query) ||
          m.genericName.toLowerCase().includes(query) ||
          m.batchNumber.toLowerCase().includes(query) ||
          m.category.toLowerCase().includes(query);

        if (!matchesQuery) return false;

        // Filter chips
        if (filterType === 'LOW_STOCK') return m.stock <= m.minSafetyStock;
        if (filterType === 'NEAR_EXPIRY') return m.daysUntilExpiry <= 60;
        if (filterType === 'COLD_CHAIN') return m.coldChainRequired;
        return true;
      })
      .sort((a, b) => {
        if (sortBy === 'EXPIRY') return a.daysUntilExpiry - b.daysUntilExpiry;
        if (sortBy === 'STOCK_ASC') return a.stock - b.stock;
        if (sortBy === 'NAME') return a.name.localeCompare(b.name);
        return 0;
      });
  }, [medicines, searchQuery, filterType, sortBy]);

  return (
    <div className="p-4 space-y-4 pb-24">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-base font-bold text-slate-900">
            {language === 'ta' ? 'மருந்து சரக்கு இருப்பு' : 'PHC Pharmacy Inventory'}
          </h2>
          <p className="text-xs text-slate-500">
            {filteredMedicines.length} of {medicines.length} {language === 'ta' ? 'மருந்துகள்' : 'batches cataloged'}
          </p>
        </div>
        <button
          onClick={onOpenAddMedicine}
          className="px-3 py-1.5 rounded-xl bg-teal-700 hover:bg-teal-800 text-white font-semibold text-xs flex items-center gap-1 shadow-xs transition-colors"
        >
          <Plus className="w-3.5 h-3.5" />
          <span>{language === 'ta' ? 'மருந்து சேர்' : 'Add Batch'}</span>
        </button>
      </div>

      {/* Search Input */}
      <div className="relative">
        <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-3" />
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder={language === 'ta' ? 'மருந்து, உப்பு அல்லது பேட்ச் எண் தேடவும்...' : 'Search medicine name, generic salt, batch number...'}
          className="w-full bg-white border border-slate-200 rounded-xl pl-9 pr-4 py-2 text-xs font-medium focus:ring-2 focus:ring-teal-500 focus:outline-none placeholder:text-slate-400 shadow-xs"
        />
        {searchQuery && (
          <button
            onClick={() => setSearchQuery('')}
            className="absolute right-3 top-2.5 text-xs text-slate-400 hover:text-slate-600"
          >
            Clear
          </button>
        )}
      </div>

      {/* Filter Tabs / Segmented controls */}
      <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs">
        <button
          onClick={() => setFilterType('ALL')}
          className={`px-3 py-1.5 rounded-xl font-medium transition-colors whitespace-nowrap ${
            filterType === 'ALL'
              ? 'bg-teal-700 text-white shadow-xs'
              : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
          }`}
        >
          All ({medicines.length})
        </button>
        <button
          onClick={() => setFilterType('LOW_STOCK')}
          className={`px-3 py-1.5 rounded-xl font-medium transition-colors whitespace-nowrap flex items-center gap-1 ${
            filterType === 'LOW_STOCK'
              ? 'bg-rose-600 text-white shadow-xs'
              : 'bg-white text-rose-700 border border-rose-200 hover:bg-rose-50'
          }`}
        >
          <AlertTriangle className="w-3 h-3" />
          <span>Low Stock ({medicines.filter((m) => m.stock <= m.minSafetyStock).length})</span>
        </button>
        <button
          onClick={() => setFilterType('NEAR_EXPIRY')}
          className={`px-3 py-1.5 rounded-xl font-medium transition-colors whitespace-nowrap flex items-center gap-1 ${
            filterType === 'NEAR_EXPIRY'
              ? 'bg-amber-600 text-white shadow-xs'
              : 'bg-white text-amber-700 border border-amber-200 hover:bg-amber-50'
          }`}
        >
          <Clock className="w-3 h-3" />
          <span>Near Expiry ({medicines.filter((m) => m.daysUntilExpiry <= 60).length})</span>
        </button>
        <button
          onClick={() => setFilterType('COLD_CHAIN')}
          className={`px-3 py-1.5 rounded-xl font-medium transition-colors whitespace-nowrap flex items-center gap-1 ${
            filterType === 'COLD_CHAIN'
              ? 'bg-sky-600 text-white shadow-xs'
              : 'bg-white text-sky-700 border border-sky-200 hover:bg-sky-50'
          }`}
        >
          <ThermometerSnowflake className="w-3 h-3" />
          <span>Cold Chain (2-8°C)</span>
        </button>
      </div>

      {/* Sort Option bar */}
      <div className="flex items-center justify-between text-[11px] text-slate-500 px-1">
        <span>Showing {filteredMedicines.length} batches</span>
        <div className="flex items-center gap-1">
          <ArrowUpDown className="w-3 h-3 text-slate-400" />
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as any)}
            className="bg-transparent text-slate-700 font-semibold focus:outline-none cursor-pointer"
          >
            <option value="EXPIRY">Sort: Expiry (Soonest first)</option>
            <option value="STOCK_ASC">Sort: Stock (Lowest first)</option>
            <option value="NAME">Sort: Name (A to Z)</option>
          </select>
        </div>
      </div>

      {/* Medicine Cards List */}
      <div className="space-y-3">
        {filteredMedicines.length === 0 ? (
          <div className="bg-white rounded-2xl p-8 border border-slate-100 text-center space-y-2">
            <Layers className="w-8 h-8 text-slate-300 mx-auto" />
            <p className="text-sm font-bold text-slate-700">No medicines found</p>
            <p className="text-xs text-slate-500">Try adjusting your search query or active filter.</p>
          </div>
        ) : (
          filteredMedicines.map((item) => {
            const isLow = item.stock <= item.minSafetyStock;
            const isNearExpiry = item.daysUntilExpiry <= 60;
            const stockPct = Math.min(100, Math.round((item.stock / (item.minSafetyStock * 2)) * 100));

            return (
              <div
                key={item.id}
                className={`bg-white rounded-2xl p-4 border shadow-xs transition-all space-y-3 ${
                  isLow
                    ? 'border-rose-200 ring-1 ring-rose-100'
                    : isNearExpiry
                    ? 'border-amber-200'
                    : 'border-slate-100 hover:border-teal-200'
                }`}
              >
                {/* Header: Name and Status */}
                <div className="flex items-start justify-between gap-2">
                  <div className="min-w-0">
                    <div className="flex items-center gap-1.5 flex-wrap">
                      <span className="text-[10px] font-bold text-teal-800 bg-teal-50 px-2 py-0.5 rounded-md">
                        {item.category}
                      </span>
                      {item.coldChainRequired && (
                        <span className="text-[10px] font-bold text-sky-700 bg-sky-50 px-2 py-0.5 rounded-md flex items-center gap-0.5">
                          <ThermometerSnowflake className="w-3 h-3" />
                          2°C - 8°C
                        </span>
                      )}
                    </div>
                    <h3 className="text-sm font-bold text-slate-900 mt-1 truncate">{item.name}</h3>
                    <p className="text-[11px] text-slate-500 truncate">{item.genericName}</p>
                  </div>

                  {/* Stock Quantity */}
                  <div className="text-right flex-shrink-0">
                    <p
                      className={`text-base font-extrabold ${
                        isLow ? 'text-rose-600' : 'text-slate-900'
                      }`}
                    >
                      {item.stock} <span className="text-xs font-normal text-slate-500">{item.unit}</span>
                    </p>
                    <p className="text-[10px] text-slate-400">buffer: {item.minSafetyStock}</p>
                  </div>
                </div>

                {/* Batch & Expiry Row */}
                <div className="p-2.5 bg-slate-50 rounded-xl border border-slate-100 grid grid-cols-2 gap-2 text-xs">
                  <div>
                    <span className="text-[10px] text-slate-400 uppercase font-bold">Batch & Shelf</span>
                    <p className="font-mono text-[11px] font-bold text-slate-700 truncate">{item.batchNumber}</p>
                    <p className="text-[10px] text-slate-500">{item.rackLocation}</p>
                  </div>
                  <div>
                    <span className="text-[10px] text-slate-400 uppercase font-bold">Expiry Date</span>
                    <p
                      className={`text-[11px] font-bold ${
                        isNearExpiry ? 'text-amber-700 font-extrabold' : 'text-slate-700'
                      }`}
                    >
                      {item.expiryDate}
                    </p>
                    <p
                      className={`text-[10px] font-medium ${
                        isNearExpiry ? 'text-amber-600 font-bold' : 'text-slate-500'
                      }`}
                    >
                      {item.daysUntilExpiry} days remaining
                    </p>
                  </div>
                </div>

                {/* Visual Stock Level Bar */}
                <div className="space-y-1">
                  <div className="flex justify-between text-[10px] text-slate-500 font-medium">
                    <span>Stock Level</span>
                    <span>{isLow ? '⚠️ Below safety threshold' : '✓ Normal'}</span>
                  </div>
                  <div className="w-full bg-slate-100 rounded-full h-1.5 overflow-hidden">
                    <div
                      className={`h-full rounded-full ${
                        isLow ? 'bg-rose-500' : isNearExpiry ? 'bg-amber-500' : 'bg-teal-600'
                      }`}
                      style={{ width: `${Math.max(5, stockPct)}%` }}
                    />
                  </div>
                </div>

                {/* Interactive Action Controls */}
                <div className="pt-2 border-t border-slate-100 flex items-center justify-between gap-2">
                  {/* Quick Dispense / Adjust */}
                  <div className="flex items-center gap-1.5">
                    <button
                      onClick={() => onAdjustStock(item.id, -1)}
                      title="Log Dispensed (-1)"
                      disabled={item.stock <= 0}
                      className="w-7 h-7 rounded-lg bg-slate-100 hover:bg-slate-200 active:bg-slate-300 disabled:opacity-40 text-slate-700 flex items-center justify-center font-bold text-xs transition-colors"
                    >
                      <Minus className="w-3.5 h-3.5" />
                    </button>
                    <span className="text-xs font-semibold text-slate-600 px-1 select-none">
                      Dispense
                    </span>
                    <button
                      onClick={() => onAdjustStock(item.id, 5)}
                      title="Receive Restock (+5)"
                      className="w-7 h-7 rounded-lg bg-slate-100 hover:bg-slate-200 active:bg-slate-300 text-slate-700 flex items-center justify-center font-bold text-xs transition-colors"
                    >
                      <Plus className="w-3.5 h-3.5" />
                    </button>
                  </div>

                  {/* Inter-PHC Rescue Button */}
                  <button
                    onClick={() => onOpenTransfer(item, undefined, isNearExpiry ? 'DISPATCH_SURPLUS' : 'REQUEST_DEFICIT')}
                    className="px-3 py-1.5 rounded-xl bg-teal-50 hover:bg-teal-100 text-teal-800 font-bold text-xs flex items-center gap-1 border border-teal-200 transition-colors"
                  >
                    <Share2 className="w-3.5 h-3.5" />
                    <span>{isNearExpiry ? 'Rescue Share' : 'Request Stock'}</span>
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
