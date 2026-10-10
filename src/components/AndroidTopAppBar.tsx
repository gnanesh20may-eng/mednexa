import React from 'react';
import { Bell, Code2, RefreshCw, Languages, ShieldCheck } from 'lucide-react';
import { TabType } from '../types';

interface AndroidTopAppBarProps {
  activeTab: TabType;
  unreadCount: number;
  onOpenNotifications: () => void;
  onOpenComposeCode: () => void;
  currentLanguage: 'en' | 'ta';
  onToggleLanguage: () => void;
  isSyncing: boolean;
  onTriggerSync: () => void;
}

export const AndroidTopAppBar: React.FC<AndroidTopAppBarProps> = ({
  unreadCount,
  onOpenNotifications,
  onOpenComposeCode,
  currentLanguage,
  onToggleLanguage,
  isSyncing,
  onTriggerSync,
}) => {
  return (
    <header className="bg-teal-800 text-white px-4 py-3 shadow-md relative z-20">
      <div className="flex items-center justify-between gap-2">
        {/* Left: PHC Info */}
        <div className="flex items-center gap-2.5 min-w-0">
          <div className="w-9 h-9 rounded-xl bg-teal-700/80 border border-teal-500/30 flex items-center justify-center text-teal-100 flex-shrink-0 shadow-inner">
            <ShieldCheck className="w-5 h-5 text-emerald-300" />
          </div>
          <div className="min-w-0">
            <div className="flex items-center gap-1.5">
              <h1 className="text-sm font-bold tracking-tight text-white truncate">
                {currentLanguage === 'ta' ? 'கள்ளக்குறிச்சி ஆரம்ப சுகாதார நிலையம்' : 'Kallakurichi Taluk PHC'}
              </h1>
              <span className="inline-block w-2 h-2 rounded-full bg-emerald-400 animate-pulse flex-shrink-0" title="Live Synced" />
            </div>
            <p className="text-[11px] text-teal-200/90 truncate">
              {currentLanguage === 'ta' ? 'கிராமப்புற பிரிவு 4 · தமிழ்நாடு' : 'Rural Block 4 · Kallakurichi District'}
            </p>
          </div>
        </div>

        {/* Right actions */}
        <div className="flex items-center gap-1 flex-shrink-0">
          {/* Sync status */}
          <button
            onClick={onTriggerSync}
            title="Sync with District Central Warehouse"
            className="p-1.5 rounded-lg text-teal-100 hover:bg-teal-700 active:bg-teal-600 transition-colors"
            aria-label="Sync with District"
          >
            <RefreshCw className={`w-4 h-4 ${isSyncing ? 'animate-spin text-emerald-300' : ''}`} />
          </button>

          {/* Language toggle */}
          <button
            onClick={onToggleLanguage}
            title="Switch Language (English / தமிழ்)"
            className="flex items-center gap-1 px-2 py-1 rounded-lg text-xs font-medium text-teal-100 bg-teal-700/60 hover:bg-teal-700 transition-colors border border-teal-600/40"
          >
            <Languages className="w-3.5 h-3.5 text-teal-300" />
            <span className="font-semibold text-[11px]">{currentLanguage === 'en' ? 'தமிழ்' : 'EN'}</span>
          </button>

          {/* Compose Code Viewer */}
          <button
            onClick={onOpenComposeCode}
            title="Inspect Jetpack Compose Kotlin Code"
            className="p-1.5 rounded-lg text-teal-100 hover:bg-teal-700 active:bg-teal-600 transition-colors"
            aria-label="Inspect Compose Code"
          >
            <Code2 className="w-4 h-4 text-emerald-300" />
          </button>

          {/* Notification Bell */}
          <button
            onClick={onOpenNotifications}
            title="Notifications & Alerts"
            className="p-1.5 rounded-lg text-teal-100 hover:bg-teal-700 active:bg-teal-600 transition-colors relative"
            aria-label="Notifications"
          >
            <Bell className="w-4 h-4" />
            {unreadCount > 0 && (
              <span className="absolute top-1 right-1 w-2 h-2 rounded-full bg-rose-500 ring-2 ring-teal-800" />
            )}
          </button>
        </div>
      </div>
    </header>
  );
};
