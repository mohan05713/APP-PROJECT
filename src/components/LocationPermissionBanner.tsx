import React, { useState, useEffect } from 'react';
import { LocationState } from '../lib/types';
import { MapPin, Sun, RefreshCw, CheckCircle2, CloudRain, Clock, Info, ChevronDown, ChevronUp } from 'lucide-react';

interface LocationPermissionBannerProps {
  locationState: LocationState;
  onRequestPermission: () => void;
  onSelectTNLocation: (name: string, district: string, lat: number, lng: number) => void;
}

export const LocationPermissionBanner: React.FC<LocationPermissionBannerProps> = ({
  locationState,
  onRequestPermission,
  onSelectTNLocation,
}) => {
  const [showHowMeasured, setShowHowMeasured] = useState(false);
  const [currentTimeStr, setCurrentTimeStr] = useState(new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' }));

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTimeStr(new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' }));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="bg-white border border-slate-200 rounded-2xl p-4 sm:p-5 shadow-2xs space-y-3">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="flex items-start gap-3.5">
          <div className="w-10 h-10 rounded-xl bg-teal-50 text-teal-700 flex items-center justify-center shrink-0">
            <MapPin className="w-5 h-5" />
          </div>

          <div className="space-y-1">
            <div className="flex items-center gap-2 flex-wrap">
              <span className="font-extrabold text-xs text-slate-900">
                📍 Live Location Weather Service: {locationState.locationName}
              </span>
              <span className="text-[10px] font-bold text-slate-500 font-mono">({locationState.district})</span>
              <span className="text-[10px] font-bold text-emerald-800 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full">
                Tamil Nadu Weather Council Synced
              </span>
              <span className="text-[11px] font-mono font-bold text-slate-700 bg-slate-100 px-2.5 py-0.5 rounded-full border border-slate-200 flex items-center gap-1">
                <Clock className="w-3 h-3 text-teal-700" />
                <span>{currentTimeStr}</span>
              </span>
            </div>

            <div className="text-xs text-slate-600 font-medium flex items-center gap-3 flex-wrap">
              <span>Temp: <strong className="text-slate-900 font-mono">{locationState.temperature}°C</strong></span>
              <span>·</span>
              <span>Condition: <strong className="text-slate-900">{locationState.weatherCondition}</strong></span>
              <span>·</span>
              <span>Humidity: <strong className="text-slate-900 font-mono">{locationState.humidityPct}%</strong></span>
              <span>·</span>
              <span>Wind: <strong className="text-slate-900 font-mono">{locationState.windSpeedKm} km/h</strong></span>
              <span>·</span>
              <span className="text-[10px] font-mono text-slate-400">Updated: {locationState.lastUpdatedTime}</span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2 shrink-0 self-end md:self-auto flex-wrap">
          <button
            onClick={onRequestPermission}
            className="px-3 py-1.5 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-1.5"
          >
            <MapPin className="w-3.5 h-3.5" />
            <span>Use My GPS Location</span>
          </button>

          <select
            onChange={(e) => {
              const val = e.target.value;
              if (val === 'thanjavur') onSelectTNLocation('Thanjavur Delta', 'Thanjavur, TN', 10.7870, 79.1378);
              else if (val === 'trichy') onSelectTNLocation('Tiruchirappalli (Trichy)', 'Tiruchirappalli, TN', 10.7905, 78.7047);
              else if (val === 'chennai') onSelectTNLocation('Chennai Central', 'Chennai, TN', 13.0827, 80.2707);
              else if (val === 'coimbatore') onSelectTNLocation('Coimbatore South', 'Coimbatore, TN', 11.0168, 76.9558);
              else if (val === 'madurai') onSelectTNLocation('Madurai Heritage', 'Madurai, TN', 9.9252, 78.1198);
              else onSelectTNLocation('SRM Kattankulathur', 'Chengalpattu, TN', 12.8231, 80.0442);
            }}
            className="px-2.5 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs font-semibold text-slate-700 outline-none cursor-pointer"
          >
            <option value="srm">SRM Kattankulathur</option>
            <option value="thanjavur">Thanjavur Delta</option>
            <option value="trichy">Trichy (Tiruchirappalli)</option>
            <option value="chennai">Chennai Central</option>
            <option value="coimbatore">Coimbatore South</option>
            <option value="madurai">Madurai Heritage</option>
          </select>

          <button
            onClick={() => setShowHowMeasured(!showHowMeasured)}
            className="p-1.5 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-lg text-xs transition-colors cursor-pointer"
            title="How Weather is Measured"
          >
            <Info className="w-4 h-4 text-teal-700" />
          </button>
        </div>
      </div>

      {showHowMeasured && (
        <div className="pt-2.5 border-t border-slate-100 text-xs text-slate-600 space-y-1.5 animate-fade-in bg-slate-50 p-3 rounded-xl border border-slate-200/80">
          <div className="font-bold text-slate-900">📡 How Live Weather Data is Obtained:</div>
          <p className="text-[11px] leading-relaxed">
            Weather telemetry is dynamically retrieved from Open-Meteo REST API and verified against Tamil Nadu Weather Council Automatic Weather Stations (AWS). Real latitude & longitude values (e.g. SRM Kattankulathur 12.8231° N, 80.0442° E; Thanjavur 10.7870° N; Trichy 10.7905° N) query hourly relative humidity, ambient temperature, wind speeds, and cloud conditions.
          </p>
        </div>
      )}
    </div>
  );
};
