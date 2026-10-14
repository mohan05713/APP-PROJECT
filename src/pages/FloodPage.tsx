import React, { useState } from 'react';
import { IoTTelemetry } from '../lib/types';
import { CloudRain, Power, ShieldAlert, Activity, Info, ChevronDown, ChevronUp, CheckCircle2 } from 'lucide-react';

interface FloodPageProps {
  iot: IoTTelemetry;
  onToggleDrainagePump: () => void;
}

export const FloodPage: React.FC<FloodPageProps> = ({ iot, onToggleDrainagePump }) => {
  const [showHowMeasured, setShowHowMeasured] = useState(true);

  return (
    <div className="space-y-6">
      {/* HEADER */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <CloudRain className="w-5 h-5 text-indigo-600" />
            <span>🌧️ Flood, Storm Water & Basement Sump Telemetry</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Real-time basement inundation protection with automated heavy-duty drainage sump pumps
          </p>
        </div>

        <button
          onClick={onToggleDrainagePump}
          className={`px-4 py-2.5 rounded-xl text-xs font-bold transition-all shadow-xs cursor-pointer flex items-center gap-2 shrink-0 ${
            iot.drainage_pump_active
              ? 'bg-rose-600 hover:bg-rose-700 text-white'
              : 'bg-indigo-700 hover:bg-indigo-800 text-white'
          }`}
        >
          <Power className="w-4 h-4" />
          <span>{iot.drainage_pump_active ? 'Deactivate Sump Pump' : 'Activate Heavy-Duty Sump Pump'}</span>
        </button>
      </div>

      {/* METRIC CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Basement Sump Pit Level</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.drainage_sump_level_pct}%</div>
          <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden mt-3">
            <div className="bg-indigo-600 h-full rounded-full transition-all duration-500" style={{ width: `${iot.drainage_sump_level_pct}%` }} />
          </div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">Safety Ceiling: 45% Max</div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Rainfall Intensity</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.rain_fall_rate_mm} <span className="text-sm font-normal text-slate-500">mm/h</span></div>
          <div className="text-[11px] font-bold text-emerald-800 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full inline-block mt-2">
            Normal Drainage Capacity
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Submerged Pump Status</div>
          <div className="text-3xl font-extrabold font-mono mt-1">
            <span className={iot.drainage_pump_active ? 'text-emerald-600' : 'text-slate-500'}>
              {iot.drainage_pump_active ? 'ACTIVE' : 'STANDBY'}
            </span>
          </div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">10 HP Submersible De-watering Pump</div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Stormwater Storage Tank</div>
          <div className="text-3xl font-extrabold text-teal-800 font-mono mt-1">32,500 <span className="text-sm font-normal text-slate-500">L</span></div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">Harvested for Garden & Car Wash</div>
        </div>
      </div>

      {/* HOW FLOOD & STORMWATER TELEMETRY ARE MEASURED */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
        <button
          onClick={() => setShowHowMeasured(!showHowMeasured)}
          className="w-full flex items-center justify-between text-xs font-bold text-slate-900 cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <Info className="w-4 h-4 text-teal-700" />
            <span>📡 How Flood & Stormwater Level Details are Measured</span>
          </div>
          {showHowMeasured ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
        </button>

        {showHowMeasured && (
          <div className="pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-3 leading-relaxed animate-fade-in">
            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>1. Submerged Pressure Sensors (ST-PIT-01 & ST-PIT-02)</span>
              </div>
              <p className="text-slate-600 pl-6">
                Directly submersed at the bottom of the basement catch pit. Measures piezometric liquid height to detect even minor water level accumulation during monsoons.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>2. Tipping-Bucket Rain Gauges</span>
              </div>
              <p className="text-slate-600 pl-6">
                Rooftop tipping bucket rain collector counts mechanical bucket tips (0.2mm per tip) to measure precise millimeter per hour (mm/h) precipitation intensity.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>3. Automated High-Capacity Sump Relays</span>
              </div>
              <p className="text-slate-600 pl-6">
                When water level reaches 40% threshold or rain exceeds 15 mm/h, the system automatically activates twin 10 HP de-watering pumps to flush stormwater directly into the municipal drain.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
