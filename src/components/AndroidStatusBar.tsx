import React, { useState, useEffect } from 'react';
import { Wifi, BatteryMedium, Signal } from 'lucide-react';

export const AndroidStatusBar: React.FC = () => {
  const [time, setTime] = useState<string>('09:41');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      const hours = now.getHours().toString().padStart(2, '0');
      const minutes = now.getMinutes().toString().padStart(2, '0');
      setTime(`${hours}:${minutes}`);
    };
    updateTime();
    const interval = setInterval(updateTime, 30000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="w-full bg-teal-900 text-teal-100 px-5 pt-2 pb-1.5 flex items-center justify-between text-xs font-semibold tracking-tight select-none z-30">
      <div className="flex items-center gap-1.5">
        <span className="font-mono text-[11px] tracking-normal">{time}</span>
        <span className="text-[10px] text-teal-300 font-normal">· MediFlow</span>
      </div>
      <div className="flex items-center gap-2 text-teal-200">
        <span className="text-[10px] font-medium tracking-wide">5G</span>
        <Signal className="w-3 h-3 text-teal-200" />
        <Wifi className="w-3 h-3 text-teal-200" />
        <div className="flex items-center gap-0.5">
          <span className="text-[10px] font-mono">94%</span>
          <BatteryMedium className="w-3.5 h-3.5 text-emerald-300" />
        </div>
      </div>
    </div>
  );
};
