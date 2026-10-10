import React from 'react';
import { Home, Sparkles, Navigation2, Pill, MoreHorizontal } from 'lucide-react';
import { TabType } from '../types';

interface AndroidBottomNavProps {
  activeTab: TabType;
  onSelectTab: (tab: TabType) => void;
  language: 'en' | 'ta';
  criticalAlertCount: number;
}

export const AndroidBottomNav: React.FC<AndroidBottomNavProps> = ({
  activeTab,
  onSelectTab,
  language,
  criticalAlertCount,
}) => {
  const tabs: Array<{
    id: TabType;
    labelEn: string;
    labelTa: string;
    icon: React.ComponentType<{ className?: string }>;
    badge?: number;
  }> = [
    {
      id: 'home',
      labelEn: 'Home',
      labelTa: 'முகப்பு',
      icon: Home,
    },
    {
      id: 'insights',
      labelEn: 'AI Insights',
      labelTa: 'AI கணிப்புகள்',
      icon: Sparkles,
      badge: criticalAlertCount > 0 ? criticalAlertCount : undefined,
    },
    {
      id: 'rescue',
      labelEn: 'Rescue Map',
      labelTa: 'மீட்பு வரைபடம்',
      icon: Navigation2,
    },
    {
      id: 'inventory',
      labelEn: 'Inventory',
      labelTa: 'சரக்கு இருப்பு',
      icon: Pill,
    },
    {
      id: 'more',
      labelEn: 'More',
      labelTa: 'கூடுதல்',
      icon: MoreHorizontal,
    },
  ];

  return (
    <nav
      className="bg-white border-t border-slate-200/80 px-2 py-1.5 shadow-[0_-2px_10px_rgba(0,0,0,0.04)] z-30 flex-shrink-0"
      aria-label="Bottom Navigation"
    >
      <div className="flex items-center justify-around max-w-md mx-auto">
        {tabs.map((tab) => {
          const isActive = activeTab === tab.id;
          const Icon = tab.icon;
          return (
            <button
              key={tab.id}
              onClick={() => onSelectTab(tab.id)}
              className="flex flex-col items-center justify-center flex-1 py-1 focus:outline-none transition-colors group select-none relative"
            >
              <div
                className={`relative px-4 py-1 rounded-full transition-all duration-200 ${
                  isActive
                    ? 'bg-teal-100 text-teal-800 font-semibold'
                    : 'text-slate-500 hover:text-slate-800'
                }`}
              >
                <Icon
                  className={`w-5 h-5 transition-transform duration-150 ${
                    isActive ? 'scale-110 text-teal-800' : 'text-slate-600'
                  }`}
                />
                {tab.badge && tab.badge > 0 && (
                  <span className="absolute -top-1 -right-0.5 min-w-4 h-4 px-1 rounded-full bg-rose-600 text-white text-[10px] font-bold flex items-center justify-center shadow-sm">
                    {tab.badge}
                  </span>
                )}
              </div>
              <span
                className={`text-[11px] mt-0.5 tracking-tight transition-colors ${
                  isActive
                    ? 'text-teal-900 font-bold'
                    : 'text-slate-600 font-medium'
                }`}
              >
                {language === 'ta' ? tab.labelTa : tab.labelEn}
              </span>
            </button>
          );
        })}
      </div>
    </nav>
  );
};
