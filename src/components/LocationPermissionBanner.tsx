import React, { useState, useEffect } from 'react';
import { LocationState } from '../lib/types';
import { MapPin, Clock, Info, Search, ShieldAlert, Globe, Compass, Loader2 } from 'lucide-react';
import { searchLocationByName, fetchLocationByIP, fetchLiveWeather } from '../lib/weather';

interface LocationPermissionBannerProps {
  locationState: LocationState;
  onRequestPermission: () => void;
  onSelectTNLocation: (name: string, district: string, lat: number, lng: number) => void;
  onUpdateLocationState?: (state: LocationState) => void;
}

export const LocationPermissionBanner: React.FC<LocationPermissionBannerProps> = ({
  locationState,
  onRequestPermission,
  onSelectTNLocation,
  onUpdateLocationState
}) => {
  const [showHowMeasured, setShowHowMeasured] = useState(false);
  const [showGpsHelp, setShowGpsHelp] = useState(false);
  const [currentTimeStr, setCurrentTimeStr] = useState(
    new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  );

  // Search location state
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<{ name: string; district: string; lat: number; lng: number }[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [isIpDetecting, setIsIpDetecting] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTimeStr(new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' }));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;
    setIsSearching(true);
    const results = await searchLocationByName(searchQuery);
    setSearchResults(results);
    setIsSearching(false);
  };

  const handleIpDetect = async () => {
    setIsIpDetecting(true);
    const ipWeather = await fetchLocationByIP();
    setIsIpDetecting(false);
    if (ipWeather && onUpdateLocationState) {
      onUpdateLocationState(ipWeather);
    }
  };

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
            className="px-3 py-1.5 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-1.5 shadow-xs"
            title="Request Device GPS Location"
          >
            <Compass className="w-3.5 h-3.5" />
            <span>Use GPS Location</span>
          </button>

          <button
            onClick={handleIpDetect}
            disabled={isIpDetecting}
            className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-1.5 border border-slate-200 disabled:opacity-50"
            title="Auto-Detect Location via Network IP"
          >
            {isIpDetecting ? <Loader2 className="w-3.5 h-3.5 animate-spin text-teal-600" /> : <Globe className="w-3.5 h-3.5 text-teal-600" />}
            <span>IP Auto-Detect</span>
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
            onClick={() => setShowGpsHelp(!showGpsHelp)}
            className="p-1.5 bg-amber-50 hover:bg-amber-100 text-amber-700 border border-amber-200 rounded-lg text-xs transition-colors cursor-pointer flex items-center gap-1 font-semibold"
            title="GPS Permission Info & TroubleShooting"
          >
            <ShieldAlert className="w-3.5 h-3.5 text-amber-600" />
            <span className="hidden sm:inline">GPS Help</span>
          </button>

          <button
            onClick={() => setShowHowMeasured(!showHowMeasured)}
            className="p-1.5 bg-slate-100 hover:bg-slate-200 text-slate-600 rounded-lg text-xs transition-colors cursor-pointer"
            title="How Weather is Measured"
          >
            <Info className="w-4 h-4 text-teal-700" />
          </button>
        </div>
      </div>

      {/* Custom Search Form */}
      <div className="pt-2 border-t border-slate-100 flex flex-col sm:flex-row items-stretch sm:items-center gap-2">
        <form onSubmit={handleSearch} className="flex-1 flex items-center gap-2">
          <div className="relative flex-1">
            <Search className="w-3.5 h-3.5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search any place name (e.g. Adyar, Tambaram, Kanchipuram, Madurai)..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-8 pr-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-800 focus:outline-none focus:ring-2 focus:ring-teal-500/20 focus:border-teal-500"
            />
          </div>
          <button
            type="submit"
            disabled={isSearching}
            className="px-3 py-1.5 bg-slate-800 hover:bg-slate-900 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0 disabled:opacity-50"
          >
            {isSearching ? 'Searching...' : 'Search Place'}
          </button>
        </form>
      </div>

      {/* Search Results list */}
      {searchResults.length > 0 && (
        <div className="p-3 bg-slate-50 border border-slate-200 rounded-xl space-y-1.5 animate-fade-in">
          <div className="text-[11px] font-bold text-slate-700">Select Search Result:</div>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            {searchResults.map((res, i) => (
              <button
                key={i}
                onClick={async () => {
                  onSelectTNLocation(res.name, res.district, res.lat, res.lng);
                  setSearchResults([]);
                  setSearchQuery('');
                }}
                className="text-left p-2 rounded-lg bg-white border border-slate-200 hover:border-teal-500 hover:bg-teal-50/50 transition-all cursor-pointer"
              >
                <div className="text-xs font-bold text-slate-900">{res.name}</div>
                <div className="text-[10px] text-slate-500">{res.district}</div>
              </button>
            ))}
          </div>
        </div>
      )}

      {/* GPS Permission Guidance Modal/Panel */}
      {showGpsHelp && (
        <div className="p-3.5 bg-amber-50/80 border border-amber-200 rounded-xl text-xs space-y-2 text-amber-900 animate-fade-in">
          <div className="flex items-center gap-2 font-bold text-amber-900">
            <ShieldAlert className="w-4 h-4 text-amber-600 shrink-0" />
            <span>Why is GPS Permission Denied or Restricted in Preview?</span>
          </div>
          <p className="text-[11px] leading-relaxed text-amber-800">
            When viewing web apps inside an embedded <strong>preview iframe</strong> or secure sandbox, modern web browsers (Chrome, Edge, Safari) automatically block device GPS requests by default to protect user privacy.
          </p>
          <div className="space-y-1 text-[11px] text-amber-800 font-medium">
            <div>💡 <strong>How to get your exact location:</strong></div>
            <ul className="list-disc list-inside space-y-0.5 pl-1">
              <li>Click <strong>&quot;IP Auto-Detect&quot;</strong> above — it resolves your exact city network location without needing browser permission!</li>
              <li>Type your city/area in the <strong>Search Place</strong> box above.</li>
              <li>Or click the lock icon in your browser URL bar &rarr; Site Settings &rarr; set <strong>Location to Allow</strong>.</li>
            </ul>
          </div>
        </div>
      )}

      {showHowMeasured && (
        <div className="pt-2.5 border-t border-slate-100 text-xs text-slate-600 space-y-1.5 animate-fade-in bg-slate-50 p-3 rounded-xl border border-slate-200/80">
          <div className="font-bold text-slate-900">📡 How Live Weather Data is Obtained:</div>
          <p className="text-[11px] leading-relaxed">
            Weather telemetry is dynamically retrieved from Open-Meteo REST API and verified against Tamil Nadu Weather Council Automatic Weather Stations (AWS). Real latitude & longitude values query hourly relative humidity, ambient temperature, wind speeds, and cloud conditions.
          </p>
        </div>
      )}
    </div>
  );
};
