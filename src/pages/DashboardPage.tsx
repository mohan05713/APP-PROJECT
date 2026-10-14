import React, { useState } from 'react';
import { Resident, Complaint, IoTTelemetry, GasCylinder, Anomaly, LocationState } from '../lib/types';
import { LocationPermissionBanner } from '../components/LocationPermissionBanner';
import { 
  Building2, Droplets, Sun, Flame, Plus, Wrench, ShieldCheck, Download, 
  Power, RefreshCw, Play, AlertTriangle, CloudRain, Wind, Activity, ChevronDown, ChevronUp, Info, Clock, Thermometer
} from 'lucide-react';

interface DashboardPageProps {
  residents: Resident[];
  complaints: Complaint[];
  iot: IoTTelemetry;
  gas: GasCylinder[];
  anomalies: Anomaly[];
  locationState: LocationState;
  onRequestLocationPermission: () => void;
  onSelectTNLocation: (name: string, district: string, lat: number, lng: number) => void;
  onNavigate: (page: string) => void;
  onOpenAddResident: () => void;
  onOpenRaiseTicket: () => void;
  onOpenGatePass: () => void;
  onTogglePump: () => void;
  onToggleDrainagePump: () => void;
  onSwitchLpgManifold: () => void;
  onRunAnomalyScan: () => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({
  residents,
  complaints,
  iot,
  gas,
  anomalies,
  locationState,
  onRequestLocationPermission,
  onSelectTNLocation,
  onNavigate,
  onOpenAddResident,
  onOpenRaiseTicket,
  onOpenGatePass,
  onTogglePump,
  onToggleDrainagePump,
  onSwitchLpgManifold,
  onRunAnomalyScan,
}) => {
  const [showMeasurementDetails, setShowMeasurementDetails] = useState(false);

  const occupiedCount = residents.filter(r => r.occupancy_status === 'OCCUPIED').length;
  const occupancyPct = ((occupiedCount / 400) * 100).toFixed(1);

  return (
    <div className="space-y-6">
      {/* 1. LOCATION & REAL WEATHER BANNER */}
      <LocationPermissionBanner
        locationState={locationState}
        onRequestPermission={onRequestLocationPermission}
        onSelectTNLocation={onSelectTNLocation}
      />

      {/* 2. FRONT DASHBOARD LIVE CONTROL TOGGLES BAR */}
      <div className="bg-gradient-to-r from-slate-900 to-slate-800 text-white rounded-2xl p-6 shadow-md border border-slate-700 space-y-4">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-700 pb-3.5">
          <div>
            <h2 className="text-base font-extrabold text-white tracking-tight flex items-center gap-2">
              <Activity className="w-5 h-5 text-teal-400" />
              <span>⚡ Live Facility IoT Control Toggles & Monitors</span>
            </h2>
            <p className="text-xs text-slate-300 font-medium mt-0.5">
              Instant front-dashboard toggles for water pumps, stormwater drainage, LPG manifolds, and anomaly detection
            </p>
          </div>

          <span className="text-[11px] font-bold text-emerald-400 bg-emerald-950/80 border border-emerald-500/40 px-3 py-1 rounded-full">
            Real-Time Simulated Telemetry Active
          </span>
        </div>

        {/* 4 LIVE TOGGLE CONTROL CARDS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5 pt-1">
          {/* Toggle 1: Water Resources Tracker */}
          <div className="bg-slate-800/90 border border-slate-700 rounded-xl p-3.5 flex flex-col justify-between space-y-2.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Droplets className="w-4 h-4 text-sky-400" />
                <span className="text-xs font-bold text-slate-200">Water Pump</span>
              </div>
              <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                iot.is_pump_running ? 'bg-emerald-950 text-emerald-300 border-emerald-500/50' : 'bg-slate-700 text-slate-300 border-slate-600'
              }`}>
                {iot.is_pump_running ? 'RUNNING' : 'OFF'}
              </span>
            </div>
            <div className="text-[11px] text-slate-400 font-mono">
              OHT: {iot.water_tank_level_pct}% · Flow: {iot.inflow_rate_lpm} L/m
            </div>
            <button
              onClick={onTogglePump}
              className={`w-full py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-1.5 ${
                iot.is_pump_running ? 'bg-rose-600 hover:bg-rose-700 text-white' : 'bg-teal-600 hover:bg-teal-700 text-white'
              }`}
            >
              <Power className="w-3.5 h-3.5" />
              <span>{iot.is_pump_running ? 'Stop Pump' : 'Start Pump'}</span>
            </button>
          </div>

          {/* Toggle 2: Stormwater Drainage Pump */}
          <div className="bg-slate-800/90 border border-slate-700 rounded-xl p-3.5 flex flex-col justify-between space-y-2.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <CloudRain className="w-4 h-4 text-indigo-400" />
                <span className="text-xs font-bold text-slate-200">Stormwater Pump</span>
              </div>
              <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                iot.drainage_pump_active ? 'bg-emerald-950 text-emerald-300 border-emerald-500/50' : 'bg-slate-700 text-slate-300 border-slate-600'
              }`}>
                {iot.drainage_pump_active ? 'ACTIVE' : 'STANDBY'}
              </span>
            </div>
            <div className="text-[11px] text-slate-400 font-mono">
              Sump Pit: 12.4% · Rain: {iot.rain_fall_rate_mm} mm/h
            </div>
            <button
              onClick={onToggleDrainagePump}
              className={`w-full py-1.5 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-1.5 ${
                iot.drainage_pump_active ? 'bg-rose-600 hover:bg-rose-700 text-white' : 'bg-teal-600 hover:bg-teal-700 text-white'
              }`}
            >
              <Power className="w-3.5 h-3.5" />
              <span>{iot.drainage_pump_active ? 'Deactivate' : 'Activate'}</span>
            </button>
          </div>

          {/* Toggle 3: Smart LPG Manifold Switch */}
          <div className="bg-slate-800/90 border border-slate-700 rounded-xl p-3.5 flex flex-col justify-between space-y-2.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Flame className="w-4 h-4 text-orange-400" />
                <span className="text-xs font-bold text-slate-200">Smart LPG Bank</span>
              </div>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-orange-950 text-orange-300 border border-orange-500/50">
                ACTIVE CYL 1
              </span>
            </div>
            <div className="text-[11px] text-slate-400 font-mono">
              Level: 6.8kg (48%) · Cyl 2 Standby
            </div>
            <button
              onClick={onSwitchLpgManifold}
              className="w-full py-1.5 bg-slate-700 hover:bg-slate-600 text-white rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-1.5"
            >
              <RefreshCw className="w-3.5 h-3.5" />
              <span>Switch Manifold</span>
            </button>
          </div>

          {/* Toggle 4: Resource Anomaly Leak Detector */}
          <div className="bg-slate-800/90 border border-slate-700 rounded-xl p-3.5 flex flex-col justify-between space-y-2.5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-rose-400" />
                <span className="text-xs font-bold text-slate-200">Resource Monitor</span>
              </div>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-700 text-slate-300 border border-slate-600">
                OOP SENSOR
              </span>
            </div>
            <div className="text-[11px] text-slate-400 font-mono">
              Threshold: 45.0 L/min · Scan Ready
            </div>
            <button
              onClick={onRunAnomalyScan}
              className="w-full py-1.5 bg-teal-600 hover:bg-teal-700 text-white rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-1.5"
            >
              <Play className="w-3.5 h-3.5 fill-current" />
              <span>Run Leak Scan</span>
            </button>
          </div>
        </div>
      </div>

      {/* 3. EXPANDABLE TELEMETRY MEASUREMENT EXPLANATION */}
      <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-2xs space-y-3">
        <button
          onClick={() => setShowMeasurementDetails(!showMeasurementDetails)}
          className="w-full flex items-center justify-between text-xs font-bold text-slate-900 cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <Info className="w-4 h-4 text-teal-700" />
            <span>How Water, Flood & Stormwater Flow Telemetry are Measured (Technical Details)</span>
          </div>
          {showMeasurementDetails ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
        </button>

        {showMeasurementDetails && (
          <div className="pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-3 animate-fade-in leading-relaxed">
            <div className="p-3 bg-slate-50 rounded-lg border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 text-xs">💧 Water Resource Tracking Methodology:</div>
              <p>
                Inflow and consumption rates are measured using ultrasonic transit-time flow meters (<code className="font-mono text-teal-800 font-bold">WM-100</code> to <code className="font-mono text-teal-800 font-bold">WM-400</code>) installed on Blocks A–D main vertical supply risers. Ultrasonic pulses measure fluid velocity with ±0.5% accuracy.
              </p>
            </div>

            <div className="p-3 bg-slate-50 rounded-lg border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 text-xs">🌧️ Flood & Stormwater Pit Measurement:</div>
              <p>
                Basement drainage pit levels are monitored via submerged hydrostatic pressure transducers and non-contact ultrasonic depth sensors (<code className="font-mono text-indigo-800 font-bold">ST-PIT-01</code>). When the water level exceeds 40%, automated relays trigger high-capacity sump pumps to prevent basement inundation.
              </p>
            </div>
          </div>
        )}
      </div>

      {/* STATS GRID */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Stat 1: Occupancy */}
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 flex items-start gap-3.5 shadow-2xs hover:shadow-xs transition-shadow">
          <div className="w-11 h-11 rounded-lg bg-teal-50 text-teal-700 flex items-center justify-center shrink-0">
            <Building2 className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs font-semibold text-slate-500">Total Residency</div>
            <div className="text-2xl font-extrabold text-slate-900 tracking-tight font-mono">400 Flats</div>
            <div className="text-xs font-medium text-slate-500 mt-0.5">
              {occupiedCount} Occupied ({occupancyPct}%)
            </div>
          </div>
        </div>

        {/* Stat 2: Overhead Water Tank */}
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 flex items-start gap-3.5 shadow-2xs hover:shadow-xs transition-shadow">
          <div className="w-11 h-11 rounded-lg bg-sky-50 text-sky-700 flex items-center justify-center shrink-0">
            <Droplets className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs font-semibold text-slate-500">Overhead Water Tank</div>
            <div className="text-2xl font-extrabold text-slate-900 tracking-tight font-mono">
              {iot.water_tank_level_pct}%
            </div>
            <div className="text-xs font-medium text-slate-500 mt-0.5">
              84,000 / 100,000 Litres
            </div>
          </div>
        </div>

        {/* Stat 3: Rooftop Solar */}
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 flex items-start gap-3.5 shadow-2xs hover:shadow-xs transition-shadow">
          <div className="w-11 h-11 rounded-lg bg-amber-50 text-amber-700 flex items-center justify-center shrink-0">
            <Sun className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs font-semibold text-slate-500">Rooftop Solar Array</div>
            <div className="text-2xl font-extrabold text-slate-900 tracking-tight font-mono">
              {iot.solar_generation_kw} kW
            </div>
            <div className="text-xs font-medium text-slate-500 mt-0.5">
              128.6 kWh Today · 142kg CO2 Saved
            </div>
          </div>
        </div>

        {/* Stat 4: Smart Gas Manifold */}
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 flex items-start gap-3.5 shadow-2xs hover:shadow-xs transition-shadow">
          <div className="w-11 h-11 rounded-lg bg-orange-50 text-orange-700 flex items-center justify-center shrink-0">
            <Flame className="w-6 h-6" />
          </div>
          <div>
            <div className="text-xs font-semibold text-slate-500">Smart Gas Manifold</div>
            <div className="text-2xl font-extrabold text-slate-900 tracking-tight font-mono">Cylinder 1</div>
            <div className="text-xs font-medium text-slate-500 mt-0.5">
              6.8 kg (48%) Active · Cyl 2 Standby
            </div>
          </div>
        </div>
      </div>

      {/* DASHBOARD COLUMNS */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* WATER FLOW GRAPH */}
        <div className="bg-white border border-slate-200 rounded-xl p-5 shadow-2xs">
          <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-100">
            <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <Droplets className="w-4 h-4 text-sky-600" />
              <span>24-Hour Water Flow vs Quota</span>
            </h3>
            <span className="text-[11px] font-semibold text-slate-500">Smart Metering</span>
          </div>

          <div className="h-44 flex items-end gap-2 pt-6 pb-2">
            {[
              { time: '00-04h', pct: 'h-[35%]', label: '42,000L' },
              { time: '04-08h', pct: 'h-[90%]', label: '145,000L Peak' },
              { time: '08-12h', pct: 'h-[65%]', label: '98,000L' },
              { time: '12-16h', pct: 'h-[50%]', label: '76,000L' },
              { time: '16-20h', pct: 'h-[85%]', label: '132,000L Peak' },
              { time: '20-24h', pct: 'h-[40%]', label: '55,000L' },
            ].map((bar, idx) => (
              <div key={idx} className="flex-1 flex flex-col items-center gap-1.5 h-full justify-end">
                <div 
                  className={`w-full ${bar.pct} bg-teal-600 rounded-md hover:bg-teal-500 transition-all cursor-pointer relative group`}
                  title={bar.label}
                />
                <span className="text-[10px] font-semibold text-slate-400">{bar.time}</span>
              </div>
            ))}
          </div>

          <div className="mt-4 pt-3 border-t border-slate-100 flex flex-wrap justify-between text-xs text-slate-600">
            <span>Total Today: <strong className="font-mono text-slate-900">142,500 L</strong></span>
            <span>Community Quota: <strong className="font-mono text-slate-900">160,000 L</strong></span>
            <span className="text-emerald-700 font-bold">Within Limit (89.1%)</span>
          </div>
        </div>

        {/* ACTIVE HELPDESK TICKETS */}
        <div className="bg-white border border-slate-200 rounded-xl p-5 shadow-2xs">
          <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-100">
            <h3 className="text-sm font-bold text-slate-900 flex items-center gap-2">
              <Wrench className="w-4 h-4 text-amber-600" />
              <span>Active Facility Tickets</span>
            </h3>
            <button
              onClick={() => onNavigate('complaints')}
              className="text-xs font-bold text-teal-700 hover:text-teal-800 transition-colors cursor-pointer"
            >
              View All
            </button>
          </div>

          <div className="space-y-3">
            {complaints.slice(0, 4).map((c) => {
              const badgeBg = c.priority === 'CRITICAL' ? 'bg-rose-50 text-rose-700 border-rose-200' : 'bg-amber-50 text-amber-700 border-amber-200';
              return (
                <div
                  key={c.ticket_id}
                  className="p-3 border border-slate-200 rounded-lg flex items-center justify-between gap-3 bg-slate-50/50"
                >
                  <div className="space-y-0.5">
                    <div className="text-xs font-bold text-slate-900 flex items-center gap-2">
                      <span className="font-mono text-teal-800">{c.ticket_id}</span>
                      <span>·</span>
                      <span>{c.flat_number}</span>
                      <span className="text-[10px] font-semibold text-slate-500">({c.category})</span>
                    </div>
                    <div className="text-xs text-slate-600 line-clamp-1">{c.description}</div>
                  </div>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border shrink-0 ${badgeBg}`}>
                    {c.status}
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};
