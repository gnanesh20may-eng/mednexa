import React, { useState } from 'react';
import { TabType, MedicineItem, PHCCenter, AIStockoutRisk, RescueTransfer, NotificationAlert } from './types';
import {
  INITIAL_MEDICINES,
  CONNECTED_PHCS,
  INITIAL_AI_RISKS,
  INITIAL_TRANSFERS,
  INITIAL_NOTIFICATIONS,
} from './data/mockData';
import { AndroidStatusBar } from './components/AndroidStatusBar';
import { AndroidTopAppBar } from './components/AndroidTopAppBar';
import { AndroidBottomNav } from './components/AndroidBottomNav';
import { HomeScreen } from './screens/HomeScreen';
import { InsightsScreen } from './screens/InsightsScreen';
import { RescueMapScreen } from './screens/RescueMapScreen';
import { InventoryScreen } from './screens/InventoryScreen';
import { MoreScreen } from './screens/MoreScreen';
import { TransferModal } from './components/TransferModal';
import { AddMedicineModal } from './components/AddMedicineModal';
import { ComposeCodeModal } from './components/ComposeCodeModal';
import { Smartphone, Maximize2, Minimize2, Check, AlertCircle } from 'lucide-react';

export default function App() {
  // Navigation
  const [activeTab, setActiveTab] = useState<TabType>('home');
  const [language, setLanguage] = useState<'en' | 'ta'>('en');

  // Presentation Mode: Mobile Device View vs Fullscreen
  const [isMobileFrameView, setIsMobileFrameView] = useState<boolean>(true);

  // Core Application State with demo data
  const [medicines, setMedicines] = useState<MedicineItem[]>(INITIAL_MEDICINES);
  const [phcs, setPhcs] = useState<PHCCenter[]>(CONNECTED_PHCS);
  const [aiRisks, setAiRisks] = useState<AIStockoutRisk[]>(INITIAL_AI_RISKS);
  const [transfers, setTransfers] = useState<RescueTransfer[]>(INITIAL_TRANSFERS);
  const [notifications, setNotifications] = useState<NotificationAlert[]>(INITIAL_NOTIFICATIONS);

  // Sync animation state
  const [isSyncing, setIsSyncing] = useState<boolean>(false);

  // Modals state
  const [isTransferModalOpen, setIsTransferModalOpen] = useState(false);
  const [selectedTransferMed, setSelectedTransferMed] = useState<MedicineItem | null>(null);
  const [selectedTransferPHC, setSelectedTransferPHC] = useState<PHCCenter | null>(null);
  const [transferType, setTransferType] = useState<'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT'>('REQUEST_DEFICIT');

  const [isAddMedicineModalOpen, setIsAddMedicineModalOpen] = useState(false);
  const [isComposeCodeModalOpen, setIsComposeCodeModalOpen] = useState(false);

  // Toast feedback
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 3500);
  };

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
    // Find candidate donors that have this medicine in surplus and stock above their reserve
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

  // Multi-PHC Transfer Planning
  // If one donor can't cover the full shortage, split across several nearby PHCs
  const planMultiPHCTransfer = (
    medicineId: string,
    targetPHC: PHCCenter,
    quantity: number,
    allPHCs: PHCCenter[],
    medicine: MedicineItem
  ) => {
    // Find all donors that have this medicine with stock above their reserve
    const donors = allPHCs
      .filter((phc) => phc.id !== targetPHC.id)
      .map((phc) => {
        const stockData = phc.stockLevels[medicineId];
        if (!stockData) return null;
        const stockAboveReserve = stockData.stock - medicine.minSafetyStock;
        if (stockAboveReserve <= 0) return null; // Cannot donate
        return {
          phc,
          available: stockAboveReserve, // Max can donate without going into shortage
          daysOfStock: stockData.daysUntilStockout,
          transportTime: phc.distanceKm,
          priority: stockAboveReserve * 100 + stockData.daysUntilStockout * 10 - phc.distanceKm * 0.5,
        };
      })
      .filter((d: any) => d !== null)
      .sort((a, b) => (b as { priority: number }).priority - (a as { priority: number }).priority);

    // Allocate quantity across donors in priority order
    const allocation: { phc: PHCCenter; quantity: number }[] = [];
    let remaining = quantity;
    
    for (const donor of donors) {
      if (remaining <= 0) break;
      // Donate up to available stock or remaining need
      const donateAmount = Math.min(donor!.available, remaining);
      if (donateAmount > 0) {
        allocation.push({
          phc: donor!.phc!,
          quantity: donateAmount,
        });
        remaining -= donateAmount;
      }
    }

    // If we couldn't cover the full quantity, return partial allocation
    return {
      allocation,
      remainingShortage: remaining,
      totalDonors: donors.length,
    };
  };

  // Actions
  const handleTriggerSync = () => {
    setIsSyncing(true);
    setTimeout(() => {
      setIsSyncing(false);
      showToast(language === 'ta' ? 'மாவட்ட கிடங்குடன் ஒத்திசைவு நிறைவடைந்தது' : 'Central Warehouse ledger synced successfully');
    }, 1200);
  };

  const handleToggleLanguage = () => {
    setLanguage((prev) => (prev === 'en' ? 'ta' : 'en'));
  };

  const handleOpenTransfer = (
    med?: MedicineItem,
    phc?: PHCCenter,
    type: 'DISPATCH_SURPLUS' | 'REQUEST_DEFICIT' = 'REQUEST_DEFICIT'
  ) => {
    setSelectedTransferMed(med || medicines[0] || null);
    setSelectedTransferPHC(phc || phcs[0] || null);
    setTransferType(type);
    setIsTransferModalOpen(true);
  };

  const handleConfirmTransfer = (newTransferData: Omit<RescueTransfer, 'id' | 'requestedAt'>) => {
    const { fromPHC: donorId, toPHC: receiverId, medicineName, quantity, unit } = newTransferData;
    const donor = phcs.find((p) => p.id === donorId);
    const receiver = phcs.find((p) => p.id === receiverId);
    const medicine = medicines.find((m) => m.name === medicineName);
    
    if (!donor || !receiver || !medicine) {
      showToast('Invalid transfer data');
      return;
    }
    
    // Fair Distribution Check: confirm donor won't fall into shortage risk
    const wouldCause = wouldCauseShortage(donor, medicine.id, quantity, medicine);
    
    // Show before/after stock summary
    const donorStockBefore = donor.stockLevels[medicine.id]?.stock || 0;
    const donorStockAfter = wouldCause ? 0 : (donorStockBefore - quantity);
    const receiverStockBefore = receiver.stockLevels[medicine.id]?.stock || 0;
    const receiverStockAfter = receiverStockBefore + quantity;
    
    if (wouldCause) {
      showToast('⚠️ Cannot approve transfer: Donor PHC would fall into critical shortage (< 3 days stock)');
      return;
    }
    
    showToast(`Transfer approved! Donor: ${donorStockBefore} → ${donorStockAfter} ${medicine.unit}, Receiver: ${receiverStockBefore} → ${receiverStockAfter} ${unit}`);
    
    const newTransfer: RescueTransfer = {
      ...newTransferData,
      id: `trf-${Date.now().toString().slice(-4)}`,
      requestedAt: 'Just now',
    };

    setTransfers((prev) => [newTransfer, ...prev]);

    // Add alert notification
    const newNotif: NotificationAlert = {
      id: `notif-${Date.now()}`,
      title: `Transfer Dispatched: ${newTransfer.medicineName}`,
      message: `${newTransfer.quantity} ${newTransfer.unit} dispatched via ${newTransfer.vehicleType} to ${newTransfer.toPHC}.`,
      timestamp: 'Just now',
      type: 'TRANSFER',
      read: false,
      actionScreen: 'rescue',
    };
    setNotifications((prev) => [newNotif, ...prev]);
  };

  const handleUpdateTransferStatus = (transferId: string, newStatus: RescueTransfer['status']) => {
    setTransfers((prev) =>
      prev.map((t) => (t.id === transferId ? { ...t, status: newStatus } : t))
    );
    showToast('Transfer logged as delivered to destination PHC.');
  };

  const handleAddMedicine = (newMed: MedicineItem) => {
    setMedicines((prev) => [newMed, ...prev]);
    showToast(`Added ${newMed.name} to pharmacy ledger.`);
  };

  const handleAdjustStock = (medicineId: string, delta: number) => {
    setMedicines((prev) =>
      prev.map((m) => {
        if (m.id === medicineId) {
          const newStock = Math.max(0, m.stock + delta);
          return { ...m, stock: newStock };
        }
        return m;
      })
    );
    showToast(delta < 0 ? 'Logged 1 unit dispensed.' : `Added ${delta} units to stock.`);
  };

  const handleMarkNotificationRead = (id: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  };

  const handleClearAllNotifications = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    showToast('All notifications marked as read.');
  };

  const unreadCount = notifications.filter((n) => !n.read).length;
  const criticalAlertCount = aiRisks.filter((r) => r.riskLevel === 'CRITICAL' || r.riskLevel === 'HIGH').length;

  return (
    <div className="min-h-screen bg-slate-200/90 flex flex-col items-center justify-start py-0 sm:py-6 px-0 sm:px-4 font-sans text-slate-900 selection:bg-teal-500 selection:text-white">
      {/* Top Bar with Device Frame Switcher & Jetpack Compose Note */}
      <div className="w-full max-w-md mb-2 px-3 pt-2 sm:pt-0 flex items-center justify-between text-xs text-slate-600 select-none">
        <div className="flex items-center gap-1.5 font-bold text-slate-800">
          <Smartphone className="w-4 h-4 text-teal-700" />
          <span>MediFlow AI · Android Experience</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setIsMobileFrameView(!isMobileFrameView)}
            className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-white/90 hover:bg-white text-slate-700 font-semibold border border-slate-300 shadow-xs transition-colors"
          >
            {isMobileFrameView ? (
              <>
                <Maximize2 className="w-3.5 h-3.5 text-teal-700" />
                <span>Full Width</span>
              </>
            ) : (
              <>
                <Minimize2 className="w-3.5 h-3.5 text-teal-700" />
                <span>Mobile Shell</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Main Container / Mobile Device Frame */}
      <div
        className={`w-full bg-slate-50 flex flex-col transition-all duration-300 overflow-hidden relative shadow-2xl ${
          isMobileFrameView
            ? 'max-w-[430px] rounded-none sm:rounded-[40px] border-0 sm:border-8 sm:border-slate-850 h-screen sm:h-[880px]'
            : 'max-w-4xl rounded-2xl border border-slate-300 min-h-[90vh]'
        }`}
      >
        {/* Device Punch-Hole Camera (When in mobile frame) */}
        {isMobileFrameView && (
          <div className="hidden sm:block absolute top-2 left-1/2 -translate-x-1/2 w-4 h-4 rounded-full bg-slate-900 z-40 ring-1 ring-slate-800/80 pointer-events-none" />
        )}

        {/* Android Status Bar */}
        <AndroidStatusBar />

        {/* Top App Bar */}
        <AndroidTopAppBar
          activeTab={activeTab}
          unreadCount={unreadCount}
          onOpenNotifications={() => setActiveTab('more')}
          onOpenComposeCode={() => setIsComposeCodeModalOpen(true)}
          currentLanguage={language}
          onToggleLanguage={handleToggleLanguage}
          isSyncing={isSyncing}
          onTriggerSync={handleTriggerSync}
        />

        {/* Scrollable Screen Content */}
        <main className="flex-1 overflow-y-auto bg-slate-50 overscroll-contain">
          {activeTab === 'home' && (
            <HomeScreen
              medicines={medicines}
              phcs={phcs}
              aiRisks={aiRisks}
              transfers={transfers}
              onNavigateTab={(tab) => setActiveTab(tab)}
              onOpenTransfer={handleOpenTransfer}
              onOpenAddMedicine={() => setIsAddMedicineModalOpen(true)}
              onOpenVoiceAssistant={() => setActiveTab('more')}
              language={language}
            />
          )}

          {activeTab === 'insights' && (
            <InsightsScreen
              aiRisks={aiRisks}
              medicines={medicines}
              phcs={phcs}
              onOpenTransfer={handleOpenTransfer}
              language={language}
            />
          )}

          {activeTab === 'rescue' && (
            <RescueMapScreen
              phcs={phcs}
              transfers={transfers}
              medicines={medicines}
              onOpenTransfer={handleOpenTransfer}
              onUpdateTransferStatus={handleUpdateTransferStatus}
              language={language}
            />
          )}

          {activeTab === 'inventory' && (
            <InventoryScreen
              medicines={medicines}
              onOpenAddMedicine={() => setIsAddMedicineModalOpen(true)}
              onOpenTransfer={handleOpenTransfer}
              onAdjustStock={handleAdjustStock}
              language={language}
            />
          )}

          {activeTab === 'more' && (
            <MoreScreen
              notifications={notifications}
              onMarkNotificationRead={handleMarkNotificationRead}
              onClearAllNotifications={handleClearAllNotifications}
              onOpenComposeCode={() => setIsComposeCodeModalOpen(true)}
              language={language}
              onToggleLanguage={handleToggleLanguage}
            />
          )}
        </main>

        {/* Android Bottom Navigation */}
        <AndroidBottomNav
          activeTab={activeTab}
          onSelectTab={(tab) => setActiveTab(tab)}
          language={language}
          criticalAlertCount={criticalAlertCount}
        />

        {/* Android Home Navigation Bar Pill (When in mobile view) */}
        {isMobileFrameView && (
          <div className="hidden sm:flex justify-center py-1.5 bg-white select-none z-30">
            <div className="w-32 h-1 bg-slate-400/80 rounded-full" />
          </div>
        )}

        {/* Floating Toast Notification */}
        {toastMessage && (
          <div className="absolute bottom-18 left-4 right-4 bg-slate-900/95 text-white px-4 py-2.5 rounded-2xl shadow-xl text-xs font-medium z-50 flex items-center gap-2 border border-slate-700 animate-fade-in backdrop-blur-xs">
            <Check className="w-4 h-4 text-emerald-400 flex-shrink-0" />
            <span className="truncate">{toastMessage}</span>
          </div>
        )}
      </div>

      {/* Interactive Modals */}
      <TransferModal
        isOpen={isTransferModalOpen}
        onClose={() => setIsTransferModalOpen(false)}
        medicine={selectedTransferMed}
        targetPHC={selectedTransferPHC}
        availablePHCs={phcs}
        availableMedicines={medicines}
        transferType={transferType}
        onConfirmTransfer={handleConfirmTransfer}
      />

      <AddMedicineModal
        isOpen={isAddMedicineModalOpen}
        onClose={() => setIsAddMedicineModalOpen(false)}
        onAddMedicine={handleAddMedicine}
      />

      <ComposeCodeModal
        isOpen={isComposeCodeModalOpen}
        onClose={() => setIsComposeCodeModalOpen(false)}
      />
    </div>
  );
}
