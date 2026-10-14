import React, { useState } from 'react';
import { IoTTelemetry } from '../lib/types';
import { Droplets, Power, Activity, ShieldCheck, AlertCircle, RefreshCw, ChevronDown, ChevronUp, Info, CheckCircle2 } from 'lucide-react';

interface WaterPageProps {
  iot: IoTTelemetry;
  onTogglePump: () => void;
}

export const WaterPage: React.FC<WaterPageProps> = ({ iot, onTogglePump }) => {
  const [showHowMeasured, setShowHowMeasured] = useState(true);

  return (
    <div className="space-y-6">
      {/* HEADER BAR */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <Droplets className="w-5 h-5 text-sky-600" />
            <span>💧 Water Resources Tracker & Overhead Tank Control</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Real-time ultrasonic flow metering across Blocks A–D with automated pump relays
          </p>
        </div>

        <button
          onClick={onTogglePump}
          className={`px-4 py-2.5 rounded-xl text-xs font-bold transition-all shadow-xs cursor-pointer flex items-center gap-2 shrink-0 ${
            iot.is_pump_running
              ? 'bg-rose-600 hover:bg-rose-700 text-white'
              : 'bg-teal-700 hover:bg-teal-800 text-white'
          }`}
        >
          <Power className="w-4 h-4" />
          <span>{iot.is_pump_running ? 'Stop Submersible Pump' : 'Start Submersible Pump'}</span>
        </button>
      </div>

      {/* METRIC CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Overhead Tank Level</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.water_tank_level_pct}%</div>
          <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden mt-3">
            <div className="bg-sky-500 h-full rounded-full transition-all duration-500" style={{ width: `${iot.water_tank_level_pct}%` }} />
          </div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">84,000 / 100,000 Litres Capacity</div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Inflow Supply Rate</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.inflow_rate_lpm} <span className="text-sm font-normal text-slate-500">L/min</span></div>
          <div className="text-[11px] font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full inline-block mt-2">
            Supply Line Pressure: 3.2 Bar
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Outflow Consumption</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.outflow_rate_lpm} <span className="text-sm font-normal text-slate-500">L/min</span></div>
          <div className="text-[11px] font-bold text-sky-700 bg-sky-50 border border-sky-200 px-2 py-0.5 rounded-full inline-block mt-2">
            400 Flats Active
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Submersible Pump Relay</div>
          <div className="text-3xl font-extrabold font-mono mt-1">
            <span className={iot.is_pump_running ? 'text-emerald-600' : 'text-slate-400'}>
              {iot.is_pump_running ? 'RUNNING' : 'STANDBY'}
            </span>
          </div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">Auto-Cutoff Trigger: 95% Tank Level</div>
        </div>
      </div>

      {/* BLOCK-WISE WATER CONSUMPTION & SENSOR BREAKDOWN */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
        <h3 className="text-sm font-extrabold text-slate-900 flex items-center gap-2">
          <Activity className="w-4 h-4 text-teal-700" />
          <span>Block-wise Domestic Water Supply Riser Sensors</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {[
            { block: 'Block A (Flats 101–425)', sensor: 'WM-101', rate: '28.4 L/min', status: 'NORMAL', pct: '88%' },
            { block: 'Block B (Flats 101–425)', sensor: 'WM-202', rate: '31.2 L/min', status: 'NORMAL', pct: '92%' },
            { block: 'Block C (Flats 101–425)', sensor: 'WM-303', rate: '18.9 L/min', status: 'OPTIMAL', pct: '65%' },
            { block: 'Block D (Flats 101–425)', sensor: 'WM-404', rate: '22.0 L/min', status: 'NORMAL', pct: '74%' },
          ].map((b, idx) => (
            <div key={idx} className="p-4 bg-slate-50 border border-slate-200 rounded-xl space-y-2">
              <div className="flex items-center justify-between">
                <span className="font-extrabold text-xs text-slate-900">{b.block}</span>
                <span className="text-[10px] font-mono font-bold text-teal-800 bg-teal-50 px-2 py-0.5 rounded border border-teal-200">{b.sensor}</span>
              </div>
              <div className="text-lg font-extrabold text-slate-900 font-mono">{b.rate}</div>
              <div className="flex items-center justify-between text-[11px] text-slate-500">
                <span>Usage Quota: {b.pct}</span>
                <span className="font-bold text-emerald-700">{b.status}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* HOW WATER DETAILS ARE MEASURED (DETAILED EXPLANATION) */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
        <button
          onClick={() => setShowHowMeasured(!showHowMeasured)}
          className="w-full flex items-center justify-between text-xs font-bold text-slate-900 cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <Info className="w-4 h-4 text-teal-700" />
            <span>📡 How Water Details & Telemetry Are Tracked in EcoApartment OS</span>
          </div>
          {showHowMeasured ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
        </button>

        {showHowMeasured && (
          <div className="pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-3 leading-relaxed animate-fade-in">
            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>1. Ultrasonic Transit-Time Flow Sensors (WM-100 to WM-400)</span>
              </div>
              <p className="text-slate-600 pl-6">
                Mounted directly onto the primary 4-inch supply risers in Blocks A–D. Ultrasonic sound waves traverse the liquid column diagonally; flow velocity is calculated from the time difference between upstream and downstream pulses with ±0.5% precision.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>2. Hydrostatic Overhead Tank Pressure Transducers</span>
              </div>
              <p className="text-slate-600 pl-6">
                Positioned at the floor of the 100,000L rooftop reservoir. Measures water column head pressure (0 to 10 meters H₂O), converting raw barometric differential into exact percentage volume levels.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>3. Automated Cutoff Relays & Supabase Event Sync</span>
              </div>
              <p className="text-slate-600 pl-6">
                When water level drops below 25%, automated logic sends a pulse signal to start the 15 HP submersible pump. Once tank capacity hits 95%, auto-cutoff triggers to prevent spill anomalies and saves telemetry records to Supabase.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
