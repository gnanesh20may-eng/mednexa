export type TabType = 'home' | 'insights' | 'rescue' | 'inventory' | 'more';

export type RiskLevel = 'CRITICAL' | 'HIGH' | 'MODERATE' | 'LOW';

export interface MedicineItem {
  id: string;
  name: string;
  genericName: string;
  category: 'Antibiotic' | 'Analgesic' | 'Emergency Antidote' | 'Vaccine' | 'Chronic Care' | 'Maternal Care' | 'Hydration';
  form: 'Tablet' | 'Injection' | 'Syrup/Suspension' | 'Sachet' | 'Ampoule' | 'Lyophilized';
  batchNumber: string;
  stock: number;
  minSafetyStock: number;
  unit: string;
  expiryDate: string; // YYYY-MM-DD
  daysUntilExpiry: number;
  coldChainRequired: boolean;
  coldChainTemp?: string; // e.g. "2°C - 8°C"
  rackLocation: string;
  isRescueCandidate: boolean;
  surplusUnits?: number;
  deficitUnits?: number;
  consumptionHistory: number[]; // 30 days of daily consumption
  importance: number; // 1-5 scale, 5 = most critical
}

export interface PHCCenter {
  id: string;
  name: string;
  taluk: string;
  district: string;
  distanceKm: number;
  travelTimeMin: number;
  doctorInCharge: string;
  phone: string;
  status: 'ONLINE' | 'STANDBY';
  x: number; // percentage coordinate for map 0-100
  y: number; // percentage coordinate for map 0-100
  surplusList: Array<{ medicineName: string; quantity: number; expiryMonths: number }>;
  shortageList: Array<{ medicineName: string; quantityNeeded: number; urgency: 'HIGH' | 'MEDIUM' }>;
  stockLevels: Record<string, { stock: number; dailyConsumption: number; daysUntilStockout: number }>;
}

export interface AIStockoutRisk {
  id: string;
  medicineId: string;
  medicineName: string;
  genericName: string;
  riskLevel: RiskLevel;
  daysToStockout: number;
  currentStock: number;
  predictedRunoutDate: string;
  confidence: number;
  primaryCause: string;
  factors: string[];
  seasonalFactorName: string;
  recommendedAction: string;
  suggestedSourcePHC?: string;
}

export interface RescueTransfer {
  id: string;
  medicineName: string;
  batchNumber: string;
  quantity: number;
  unit: string;
  fromPHC: string;
  toPHC: string;
  distanceKm: number;
  etaMinutes: number;
  status: 'REQUESTED' | 'DISPATCHED' | 'IN_TRANSIT' | 'DELIVERED';
  coldChain: boolean;
  tempReading?: string;
  vehicleType: '2-Wheeler Health Courier' | 'Ambulance Shuttle' | 'PHC Logistics Van';
  requestedAt: string;
  driverName?: string;
  driverPhone?: string;
}

export interface NotificationAlert {
  id: string;
  title: string;
  message: string;
  timestamp: string;
  type: 'ALERT' | 'TRANSFER' | 'EXPIRY' | 'VOICE';
  read: boolean;
  actionScreen?: TabType;
}
