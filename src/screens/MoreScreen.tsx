import React, { useState } from 'react';
import {
  FileText,
  Leaf,
  Bell,
  Mic,
  Settings,
  Code2,
  ChevronRight,
  ArrowLeft,
  Volume2,
  CheckCircle2,
  Download,
  Share2,
  ShieldCheck,
  AlertTriangle,
  Smartphone,
  Radio,
  RefreshCw,
  Send
} from 'lucide-react';
import { NotificationAlert, MedicineItem } from '../types';
import { TAMIL_VOICE_PRESETS } from '../data/mockData';

interface MoreScreenProps {
  notifications: NotificationAlert[];
  onMarkNotificationRead: (id: string) => void;
  onClearAllNotifications: () => void;
  onOpenComposeCode: () => void;
  language: 'en' | 'ta';
  onToggleLanguage: () => void;
  initialSubPage?: 'menu' | 'reports' | 'sustainability' | 'notifications' | 'tamil_voice' | 'settings';
}

export const MoreScreen: React.FC<MoreScreenProps> = ({
  notifications,
  onMarkNotificationRead,
  onClearAllNotifications,
  onOpenComposeCode,
  language,
  onToggleLanguage,
  initialSubPage = 'menu',
}) => {
  const [subPage, setSubPage] = useState<'menu' | 'reports' | 'sustainability' | 'notifications' | 'tamil_voice' | 'settings'>(initialSubPage);

  // Voice assistant state
  const [isListening, setIsListening] = useState(false);
  const [voiceQuery, setVoiceQuery] = useState('');
  const [voiceResponse, setVoiceResponse] = useState<{ tamil: string; english: string } | null>(null);
  const [isSpeaking, setIsSpeaking] = useState(false);

  // Offline mode state
  const [offlineEnabled, setOfflineEnabled] = useState(true);
  const [offlineQueueCount, setOfflineQueueCount] = useState(0);

  const unreadNotifs = notifications.filter((n) => !n.read).length;

  // Speak aloud via SpeechSynthesis API
  const handleSpeak = (text: string) => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.rate = 0.95;
      setIsSpeaking(true);
      utterance.onend = () => setIsSpeaking(false);
      utterance.onerror = () => setIsSpeaking(false);
      window.speechSynthesis.speak(utterance);
    }
  };

  const handleSelectPresetQuery = (preset: typeof TAMIL_VOICE_PRESETS[0]) => {
    setVoiceQuery(preset.queryTamil);
    setIsListening(true);
    setTimeout(() => {
      setIsListening(false);
      setVoiceResponse({
        tamil: preset.responseTamil,
        english: preset.responseEnglish,
      });
      handleSpeak(preset.responseTamil);
    }, 600);
  };

  const handleSimulateCustomMic = () => {
    setIsListening(true);
    setTimeout(() => {
      setIsListening(false);
      const defaultPreset = TAMIL_VOICE_PRESETS[0];
      setVoiceQuery(defaultPreset.queryTamil);
      setVoiceResponse({
        tamil: defaultPreset.responseTamil,
        english: defaultPreset.responseEnglish,
      });
      handleSpeak(defaultPreset.responseTamil);
    }, 1200);
  };

  return (
    <div className="p-4 space-y-4 pb-20">
      {/* Subpage Header if not in menu */}
      {subPage !== 'menu' && (
        <div className="flex items-center gap-2 mb-2">
          <button
            onClick={() => setSubPage('menu')}
            className="p-1.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <h2 className="text-sm font-bold text-slate-900 capitalize">
            {subPage === 'tamil_voice' ? 'Tamil Voice Assistant' : subPage}
          </h2>
        </div>
      )}

      {/* Main Menu view */}
      {subPage === 'menu' && (
        <div className="space-y-4">
          {/* Header Card */}
          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-slate-900">
                {language === 'ta' ? 'கூடுதல் சேவைகள்' : 'Operations & Intelligence'}
              </h2>
              <p className="text-xs text-slate-500">
                {language === 'ta' ? 'அறிக்கைகள், குரல் உதவியாளர் மற்றும் அமைப்புகள்' : 'Healthcare reports, AI voice, sustainability & settings'}
              </p>
            </div>
            <div className="w-10 h-10 rounded-2xl bg-teal-50 border border-teal-100 flex items-center justify-center text-teal-800">
              <ShieldCheck className="w-5 h-5" />
            </div>
          </div>

          {/* Menu Items Grid / List */}
          <div className="bg-white rounded-2xl border border-slate-100 shadow-xs divide-y divide-slate-100 overflow-hidden">
            {/* 1. Reports */}
            <button
              onClick={() => setSubPage('reports')}
              className="w-full p-4 flex items-center justify-between hover:bg-slate-50 transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-teal-50 text-teal-800 flex items-center justify-center">
                  <FileText className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-xs font-bold text-slate-900">
                    {language === 'ta' ? 'அறிக்கைகள் & தரவு பகுப்பாய்வு' : 'Reports & Analytics'}
                  </h3>
                  <p className="text-[11px] text-slate-500">
                    {language === 'ta' ? 'கையிருப்பு தீர்வு தவிர்ப்பு & விநியோகம்' : '94.2% stockouts prevented, audit ledger'}
                  </p>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </button>

            {/* 2. Sustainability */}
            <button
              onClick={() => setSubPage('sustainability')}
              className="w-full p-4 flex items-center justify-between hover:bg-slate-50 transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-800 flex items-center justify-center">
                  <Leaf className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-xs font-bold text-slate-900">
                    {language === 'ta' ? 'பசுமை மருத்துவம் & கழிவு சேமிப்பு' : 'Sustainability & Waste Prevention'}
                  </h3>
                  <p className="text-[11px] text-slate-500">
                    {language === 'ta' ? '₹1.48 லட்சம் மதிப்புள்ள மருந்துகள் காப்பாற்றப்பட்டன' : '₹1,48,500 medicines diverted from landfill'}
                  </p>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </button>

            {/* 3. Notifications */}
            <button
              onClick={() => setSubPage('notifications')}
              className="w-full p-4 flex items-center justify-between hover:bg-slate-50 transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-rose-50 text-rose-800 flex items-center justify-center relative">
                  <Bell className="w-5 h-5" />
                  {unreadNotifs > 0 && (
                    <span className="w-2 h-2 rounded-full bg-rose-600 absolute top-2 right-2 ring-2 ring-white" />
                  )}
                </div>
                <div>
                  <h3 className="text-xs font-bold text-slate-900">
                    {language === 'ta' ? 'அறிவிப்புகள் & விழிப்பூட்டல்கள்' : 'Alerts & Notifications'}
                  </h3>
                  <p className="text-[11px] text-slate-500">
                    {unreadNotifs} {language === 'ta' ? 'புதிய எச்சரிக்கைகள்' : 'unread notifications'}
                  </p>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </button>

            {/* 4. Tamil Voice Assistant */}
            <button
              onClick={() => setSubPage('tamil_voice')}
              className="w-full p-4 flex items-center justify-between hover:bg-slate-50 transition-colors text-left bg-gradient-to-r from-teal-50/40 to-transparent"
            >
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-teal-800 text-emerald-300 flex items-center justify-center shadow-xs">
                  <Mic className="w-5 h-5" />
                </div>
                <div>
                  <div className="flex items-center gap-1.5">
                    <h3 className="text-xs font-bold text-slate-900">
                      தமிழ் குரல் உதவியாளர் (Tamil Voice AI)
                    </h3>
                    <span className="text-[9px] font-bold text-teal-800 bg-teal-100 px-1.5 py-0.2 rounded">
                      Audio
                    </span>
                  </div>
                  <p className="text-[11px] text-slate-500">
                    Hands-free voice queries in தமிழ் for rural pharmacists
                  </p>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </button>

            {/* 5. Settings */}
            <button
              onClick={() => setSubPage('settings')}
              className="w-full p-4 flex items-center justify-between hover:bg-slate-50 transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-slate-100 text-slate-700 flex items-center justify-center">
                  <Settings className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-xs font-bold text-slate-900">
                    {language === 'ta' ? 'அமைப்புகள் & ஆஃப்லைன் ஒத்திசைவு' : 'Settings & Offline Sync'}
                  </h3>
                  <p className="text-[11px] text-slate-500">
                    PHC credentials, sync queue, language preferences
                  </p>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400" />
            </button>
          </div>

          {/* Jetpack Compose Kotlin Code Inspector Banner */}
          <div className="bg-slate-900 rounded-2xl p-4 text-white shadow-md flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-teal-500/20 text-teal-300 flex items-center justify-center border border-teal-500/30">
                <Code2 className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-xs font-bold text-white flex items-center gap-1.5">
                  <span>Android Jetpack Compose Code</span>
                  <span className="text-[10px] bg-teal-800 text-teal-200 px-1.5 py-0.2 rounded font-mono">
                    Kotlin
                  </span>
                </h3>
                <p className="text-[11px] text-slate-400">
                  Inspect native Android UI architecture for this app
                </p>
              </div>
            </div>
            <button
              onClick={onOpenComposeCode}
              className="px-3 py-1.5 rounded-xl bg-teal-600 hover:bg-teal-500 text-white font-bold text-xs transition-colors flex items-center gap-1"
            >
              <span>View Code</span>
              <ChevronRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      )}

      {/* Subpage 1: Reports */}
      {subPage === 'reports' && (
        <div className="space-y-4">
          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
            <h3 className="text-sm font-bold text-slate-900">Quarterly PHC Medicine Performance</h3>
            <div className="grid grid-cols-2 gap-3">
              <div className="p-3 bg-teal-50 rounded-xl border border-teal-100">
                <p className="text-[11px] text-slate-600">Stockouts Prevented</p>
                <p className="text-xl font-extrabold text-teal-900 mt-0.5">94.2%</p>
                <p className="text-[10px] text-teal-700 mt-0.5">38 incidents averted</p>
              </div>
              <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-100">
                <p className="text-[11px] text-slate-600">Waste Prevented</p>
                <p className="text-xl font-extrabold text-emerald-900 mt-0.5">₹1,48,500</p>
                <p className="text-[10px] text-emerald-700 mt-0.5">420 units reused</p>
              </div>
            </div>

            <div className="p-3 bg-slate-50 rounded-xl border border-slate-100 space-y-2 text-xs">
              <div className="flex justify-between text-slate-700">
                <span>Avg. Inter-PHC Courier ETA:</span>
                <span className="font-bold">24 mins</span>
              </div>
              <div className="flex justify-between text-slate-700">
                <span>Cold-Chain Compliance Rate:</span>
                <span className="font-bold text-emerald-700">99.4% (2-8°C verified)</span>
              </div>
              <div className="flex justify-between text-slate-700">
                <span>Active Rural PHC Partners:</span>
                <span className="font-bold">6 Health Centres</span>
              </div>
            </div>

            <button
              onClick={() => {
                const reportContent = "MediFlow AI - Kallakurichi PHC Quarterly Medicine Report\nTotal Stockouts Prevented: 94.2%\nWaste Averted: Rs. 148,500\nCold Chain Compliance: 99.4%";
                const blob = new Blob([reportContent], { type: 'text/plain' });
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `mediflow-phc-report-${Date.now()}.txt`;
                a.click();
              }}
              className="w-full py-2.5 rounded-xl bg-teal-700 hover:bg-teal-800 text-white font-semibold text-xs flex items-center justify-center gap-1.5 transition-colors"
            >
              <Download className="w-4 h-4" />
              <span>Export PHC Audit Report (.txt)</span>
            </button>
          </div>
        </div>
      )}

      {/* Subpage 2: Sustainability */}
      {subPage === 'sustainability' && (
        <div className="space-y-4">
          <div className="bg-gradient-to-br from-emerald-800 to-teal-900 rounded-2xl p-4 text-white shadow-sm space-y-2">
            <div className="flex items-center gap-2">
              <Leaf className="w-5 h-5 text-emerald-300" />
              <h3 className="text-sm font-bold">Circular Medicine Redistribution</h3>
            </div>
            <p className="text-xs text-emerald-100/90 leading-relaxed">
              Expiring medicines in rural health centres are traditionally incinerated as medical waste. MediFlow AI's mesh redistributes near-expiry surplus to high-demand neighboring hospitals.
            </p>
          </div>

          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
            <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wide">Ecological Impact</h4>
            <div className="space-y-2 text-xs">
              <div className="p-3 bg-emerald-50/60 rounded-xl border border-emerald-100 flex items-center justify-between">
                <div>
                  <p className="font-bold text-slate-900">420 Medicine Packages Rescued</p>
                  <p className="text-[11px] text-slate-500">Diverted from toxic chemical destruction</p>
                </div>
                <span className="text-sm font-extrabold text-emerald-700">100%</span>
              </div>
              <div className="p-3 bg-teal-50/60 rounded-xl border border-teal-100 flex items-center justify-between">
                <div>
                  <p className="font-bold text-slate-900">142 kg CO₂ Transport Offset</p>
                  <p className="text-[11px] text-slate-500">Local 14km 2-wheeler vs 120km state warehouse round-trip</p>
                </div>
                <span className="text-sm font-extrabold text-teal-800">-78% CO₂</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Subpage 3: Notifications */}
      {subPage === 'notifications' && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Recent Alerts ({notifications.length})
            </h3>
            <button
              onClick={onClearAllNotifications}
              className="text-xs font-semibold text-teal-700 hover:text-teal-900"
            >
              Mark all as read
            </button>
          </div>

          <div className="space-y-2.5">
            {notifications.map((notif) => (
              <div
                key={notif.id}
                onClick={() => onMarkNotificationRead(notif.id)}
                className={`p-3 rounded-2xl border transition-all cursor-pointer ${
                  notif.read
                    ? 'bg-white border-slate-100 text-slate-700'
                    : 'bg-teal-50/60 border-teal-200 text-teal-950 font-medium'
                }`}
              >
                <div className="flex items-start justify-between gap-2">
                  <h4 className="text-xs font-bold text-slate-900">{notif.title}</h4>
                  <span className="text-[10px] text-slate-400 whitespace-nowrap">{notif.timestamp}</span>
                </div>
                <p className="text-[11px] text-slate-600 mt-1 leading-relaxed">{notif.message}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Subpage 4: Tamil Voice Assistant */}
      {subPage === 'tamil_voice' && (
        <div className="space-y-4">
          {/* Voice Assistant Visualizer Card */}
          <div className="bg-gradient-to-b from-teal-900 to-slate-900 rounded-2xl p-5 text-white shadow-md text-center space-y-4">
            <div className="w-16 h-16 rounded-full bg-teal-500/20 border-2 border-emerald-400/50 mx-auto flex items-center justify-center relative">
              {isListening && (
                <div className="w-20 h-20 rounded-full bg-emerald-400/30 animate-ping absolute" />
              )}
              <Mic className={`w-8 h-8 ${isListening ? 'text-emerald-300 animate-pulse' : 'text-teal-200'}`} />
            </div>

            <div>
              <h3 className="text-base font-bold text-white">
                மருத்துவக் குரல் உதவியாளர்
              </h3>
              <p className="text-xs text-teal-200/90 mt-0.5">
                MediFlow Tamil Voice Assistant · Hands-Free Rural Operation
              </p>
            </div>

            {/* Audio Wave / Speaking Indicator */}
            {isSpeaking && (
              <div className="flex items-center justify-center gap-1.5 py-1 text-emerald-300 text-xs font-semibold">
                <Volume2 className="w-4 h-4 animate-bounce" />
                <span>பதில் கூறுகிறது... (Speaking audio response)</span>
              </div>
            )}

            {/* Mic trigger button */}
            <button
              onClick={handleSimulateCustomMic}
              disabled={isListening}
              className="px-6 py-2.5 rounded-full bg-emerald-500 hover:bg-emerald-400 active:bg-emerald-600 text-slate-950 font-bold text-xs shadow-md transition-all flex items-center gap-2 mx-auto"
            >
              <Mic className="w-4 h-4" />
              <span>{isListening ? 'கேட்கிறது... (Listening)' : 'குரல் மூலம் பேசு (Tap to Speak)'}</span>
            </button>
          </div>

          {/* Current Query & Response Box */}
          {voiceResponse && (
            <div className="bg-white rounded-2xl p-4 border border-teal-200 shadow-sm space-y-3 animate-fade-in">
              <div>
                <p className="text-[10px] text-slate-400 uppercase font-bold">நீங்கள் கேட்ட கேள்வி (Your Query):</p>
                <p className="text-xs font-bold text-teal-900 mt-0.5">"{voiceQuery}"</p>
              </div>

              <div className="p-3 bg-teal-50/80 rounded-xl border border-teal-100 text-xs space-y-1.5">
                <div className="flex items-center justify-between">
                  <span className="font-bold text-teal-950 flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                    <span>AI பதில் (Response):</span>
                  </span>
                  <button
                    onClick={() => handleSpeak(voiceResponse.tamil)}
                    className="p-1 rounded-md text-teal-800 hover:bg-teal-200/50"
                    title="Repeat Audio"
                  >
                    <Volume2 className="w-4 h-4" />
                  </button>
                </div>
                <p className="text-slate-800 font-medium leading-relaxed font-sans">{voiceResponse.tamil}</p>
                <p className="text-[11px] text-slate-500 italic pt-1 border-t border-teal-200/40">
                  {voiceResponse.english}
                </p>
              </div>
            </div>
          )}

          {/* Preset Tamil Voice Prompts */}
          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
            <h4 className="text-xs font-bold text-slate-800 uppercase tracking-wide">
              மாதிரி குரல் கட்டளைகள் (Quick Tamil Voice Prompts)
            </h4>
            <div className="space-y-2">
              {TAMIL_VOICE_PRESETS.map((preset, idx) => (
                <button
                  key={idx}
                  onClick={() => handleSelectPresetQuery(preset)}
                  className="w-full p-2.5 rounded-xl bg-slate-50 hover:bg-teal-50 border border-slate-200/80 hover:border-teal-300 text-left transition-all group"
                >
                  <p className="text-xs font-bold text-slate-900 group-hover:text-teal-900 font-sans">
                    🗣️ {preset.queryTamil}
                  </p>
                  <p className="text-[10px] text-slate-500 mt-0.5 italic">
                    "{preset.queryEnglish}"
                  </p>
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Subpage 5: Settings */}
      {subPage === 'settings' && (
        <div className="space-y-4">
          {/* PHC Credentials */}
          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
            <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wide">PHC Center Identity</h3>
            <div className="space-y-2 text-xs">
              <div className="flex justify-between py-1.5 border-b border-slate-100">
                <span className="text-slate-500">Center Name:</span>
                <span className="font-bold text-slate-900">Kallakurichi Taluk PHC</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100">
                <span className="text-slate-500">PHC Code:</span>
                <span className="font-mono font-bold text-slate-900">TN-KAL-PHC-04</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100">
                <span className="text-slate-500">Medical Officer:</span>
                <span className="font-bold text-slate-900">Dr. Aruna V., M.B.B.S.</span>
              </div>
              <div className="flex justify-between py-1.5">
                <span className="text-slate-500">Registered Pharmacist:</span>
                <span className="font-bold text-slate-900">R. Selvam, D.Pharm</span>
              </div>
            </div>
          </div>

          {/* Offline Sync Configuration */}
          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-xs font-bold text-slate-900">Rural Offline Persistence</h3>
                <p className="text-[11px] text-slate-500">Operate seamlessly when rural cell towers drop</p>
              </div>
              <input
                type="checkbox"
                checked={offlineEnabled}
                onChange={(e) => setOfflineEnabled(e.target.checked)}
                className="w-4 h-4 text-teal-600 rounded-sm focus:ring-teal-500"
              />
            </div>
            <div className="p-2.5 bg-slate-50 rounded-xl text-xs flex items-center justify-between">
              <span className="text-slate-600">Pending offline ledger syncs:</span>
              <span className="font-bold text-emerald-700">{offlineQueueCount} pending</span>
            </div>
          </div>

          {/* Language Selection */}
          <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-xs space-y-3">
            <h3 className="text-xs font-bold text-slate-800 uppercase tracking-wide">Language / மொழி</h3>
            <div className="grid grid-cols-2 gap-2 text-xs">
              <button
                onClick={() => {
                  if (language !== 'en') onToggleLanguage();
                }}
                className={`p-2.5 rounded-xl border text-center font-bold ${
                  language === 'en'
                    ? 'bg-teal-700 text-white border-teal-700'
                    : 'bg-slate-50 text-slate-700 border-slate-200'
                }`}
              >
                English
              </button>
              <button
                onClick={() => {
                  if (language !== 'ta') onToggleLanguage();
                }}
                className={`p-2.5 rounded-xl border text-center font-bold ${
                  language === 'ta'
                    ? 'bg-teal-700 text-white border-teal-700'
                    : 'bg-slate-50 text-slate-700 border-slate-200'
                }`}
              >
                தமிழ் (Tamil)
              </button>
            </div>
          </div>

          {/* Build Info */}
          <div className="text-center text-[11px] text-slate-400 space-y-0.5 pt-2">
            <p>MediFlow AI for Android · Build 2.4.0 (Compose M3)</p>
            <p>Department of Public Health & Preventive Medicine, Tamil Nadu</p>
          </div>
        </div>
      )}
    </div>
  );
};
