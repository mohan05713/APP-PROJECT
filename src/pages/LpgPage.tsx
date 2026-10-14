import React, { useState } from 'react';
import { GasCylinder } from '../lib/types';
import { Flame, RefreshCw, ShoppingCart, ShieldAlert, Info, ChevronDown, ChevronUp, CheckCircle2 } from 'lucide-react';

interface LpgPageProps {
  cylinders: GasCylinder[];
  onSwitchManifold: () => void;
  onBookRefill: (unitId: string) => void;
}

export const LpgPage: React.FC<LpgPageProps> = ({ cylinders, onSwitchManifold, onBookRefill }) => {
  const [showHowMeasured, setShowHowMeasured] = useState(true);

  return (
    <div className="space-y-6">
      {/* HEADER */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <Flame className="w-5 h-5 text-orange-600" />
            <span>🔥 Smart 3-Cylinder LPG Bank & Centralized Gas Manifold</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Automatic load-cell strain weight monitoring with seamless auto-changeover valves
          </p>
        </div>

        <button
          onClick={onSwitchManifold}
          className="px-4 py-2.5 bg-orange-600 hover:bg-orange-700 text-white rounded-xl text-xs font-bold transition-colors cursor-pointer flex items-center gap-2 shrink-0 shadow-xs"
        >
          <RefreshCw className="w-4 h-4" />
          <span>Switch Active Manifold to Reserve</span>
        </button>
      </div>

      {/* 3 CYLINDER CARDS GRID */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {cylinders.map((c) => {
          const statusBg =
            c.status === 'ACTIVE'
              ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
              : c.status === 'STANDBY'
              ? 'bg-sky-50 text-sky-800 border-sky-200'
              : 'bg-rose-50 text-rose-800 border-rose-200';

          return (
            <div key={c.cylinder_number} className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
              <div className="flex items-center justify-between">
                <span className="font-mono text-xs font-bold text-slate-500">CYLINDER #{c.cylinder_number}</span>
                <span className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full border ${statusBg}`}>
                  {c.status}
                </span>
              </div>

              <div>
                <div className="text-3xl font-extrabold text-slate-900 font-mono">{c.current_weight_kg.toFixed(1)} <span className="text-sm font-normal text-slate-500">/ {c.capacity_kg} kg</span></div>
                <div className="w-full bg-slate-100 h-3 rounded-full overflow-hidden mt-3">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${
                      c.remaining_pct < 20 ? 'bg-rose-500' : c.remaining_pct < 50 ? 'bg-amber-500' : 'bg-emerald-500'
                    }`}
                    style={{ width: `${c.remaining_pct}%` }}
                  />
                </div>
                <div className="flex justify-between text-xs text-slate-500 font-mono mt-1.5">
                  <span>Capacity: {c.remaining_pct.toFixed(0)}%</span>
                  <span>Tare: 14.2 kg</span>
                </div>
              </div>

              <div className="text-xs text-slate-600 font-mono space-y-1 bg-slate-50 p-3 rounded-lg border border-slate-100">
                <div>Manifold Pressure: 2.1 Bar</div>
                <div>Sensor ID: LPG-SCALE-0{c.cylinder_number}</div>
              </div>

              <button
                onClick={() => onBookRefill(`CYL-${c.cylinder_number}`)}
                className="w-full py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center justify-center gap-1.5"
              >
                <ShoppingCart className="w-3.5 h-3.5" />
                <span>Book LPG Commercial Refill</span>
              </button>
            </div>
          );
        })}
      </div>

      {/* HOW LPG DETAILS ARE MEASURED */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
        <button
          onClick={() => setShowHowMeasured(!showHowMeasured)}
          className="w-full flex items-center justify-between text-xs font-bold text-slate-900 cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <Info className="w-4 h-4 text-teal-700" />
            <span>📡 How Smart LPG Bank Metrics Are Measured in Real-Time</span>
          </div>
          {showHowMeasured ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
        </button>

        {showHowMeasured && (
          <div className="pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-3 leading-relaxed animate-fade-in">
            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>1. Load-Cell Weight Strain Gauges (LPG-SCALE-01 to 03)</span>
              </div>
              <p className="text-slate-600 pl-6">
                Each 47.5kg commercial LPG cylinder rests on a heavy-duty industrial load cell scale. The strain gauge converts mechanical weight changes into calibrated electrical signals.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>2. Automated Solenoid Auto-Changeover Valve</span>
              </div>
              <p className="text-slate-600 pl-6">
                When active cylinder weight drops below 3.0kg, the microcontroller activates solenoid valves to seamlessly switch supply line pressure to Cylinder 2 without interrupting kitchen gas supply.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
