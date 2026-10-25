import React, { useState } from 'react';
import { IoTTelemetry } from '../lib/types';
import { Sun, Wind, CloudRain, Thermometer, ShieldCheck, Info, ChevronDown, ChevronUp, MapPin, RefreshCw, CheckCircle2 } from 'lucide-react';

interface EnvironmentPageProps {
  iot: IoTTelemetry;
}

export const EnvironmentPage: React.FC<EnvironmentPageProps> = ({ iot }) => {
  const [showHowMeasured, setShowHowMeasured] = useState(true);

  return (
    <div className="space-y-6">
      {/* HEADER */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <Sun className="w-5 h-5 text-amber-500" />
            <span>☀️ Air, Solar Turn & Environmental Monitoring</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Rooftop solar generation, air quality index (AQI), ambient humidity, and Tamil Nadu climate telemetry
          </p>
        </div>

        <span className="text-[11px] font-bold text-emerald-800 bg-emerald-50 border border-emerald-200 px-3 py-1 rounded-full">
          Tamil Nadu Weather Council Live Synced
        </span>
      </div>

      {/* TOP METRICS GRID */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Solar Generation Rate</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.solar_generation_kw} <span className="text-sm font-normal text-slate-500">kW</span></div>
          <div className="text-[11px] font-bold text-amber-800 bg-amber-50 border border-amber-200 px-2 py-0.5 rounded-full inline-block mt-2">
            128.6 kWh Cumulative Today
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Ambient Temperature</div>
          <div className="text-3xl font-extrabold text-slate-900 font-mono mt-1">{iot.temperature_c}°C</div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">SRM Campus Meteorological Station</div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Air Quality Index (AQI)</div>
          <div className="text-3xl font-extrabold text-emerald-700 font-mono mt-1">42 <span className="text-sm font-normal text-slate-500">(Good)</span></div>
          <div className="text-[11px] font-bold text-emerald-800 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full inline-block mt-2">
            PM2.5: 12 µg/m³
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-xl p-4.5 shadow-2xs">
          <div className="text-xs font-semibold text-slate-500">Carbon Offset Today</div>
          <div className="text-3xl font-extrabold text-teal-800 font-mono mt-1">142.8 <span className="text-sm font-normal text-slate-500">kg CO₂</span></div>
          <div className="text-[11px] text-slate-500 font-medium mt-2">Equivalent to Planting 7 Trees</div>
        </div>
      </div>

      {/* DETAILED ROOFTOP SOLAR ARRAY BREAKDOWN */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
        <h3 className="text-sm font-extrabold text-slate-900 flex items-center gap-2">
          <Sun className="w-4 h-4 text-amber-600" />
          <span>40 kWp Rooftop Solar Inverter Array (Blocks A–D)</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          {[
            { name: 'Block A Array (10 kWp)', gen: '11.8 kW', eff: '98.2%', status: 'GRID ACTIVE' },
            { name: 'Block B Array (10 kWp)', gen: '12.4 kW', eff: '98.8%', status: 'GRID ACTIVE' },
            { name: 'Block C Array (10 kWp)', gen: '11.2 kW', eff: '97.4%', status: 'GRID ACTIVE' },
            { name: 'Block D Array (10 kWp)', gen: '12.8 kW', eff: '99.1%', status: 'GRID ACTIVE' },
          ].map((s, idx) => (
            <div key={idx} className="p-4 bg-slate-50 border border-slate-200 rounded-xl space-y-2">
              <div className="font-extrabold text-xs text-slate-900">{s.name}</div>
              <div className="text-lg font-extrabold text-amber-700 font-mono">{s.gen}</div>
              <div className="flex items-center justify-between text-[11px] text-slate-500">
                <span>Inverter Eff: {s.eff}</span>
                <span className="font-bold text-emerald-700">{s.status}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* HOW ENVIRONMENTAL & WEATHER DETAILS ARE MEASURED */}
      <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-4">
        <button
          onClick={() => setShowHowMeasured(!showHowMeasured)}
          className="w-full flex items-center justify-between text-xs font-bold text-slate-900 cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <Info className="w-4 h-4 text-teal-700" />
            <span>📡 How Air, Solar & Weather Data are Retrieved and Calculated</span>
          </div>
          {showHowMeasured ? <ChevronUp className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
        </button>

        {showHowMeasured && (
          <div className="pt-3 border-t border-slate-100 text-xs text-slate-600 space-y-3 leading-relaxed animate-fade-in">
            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>1. Open-Meteo REST API & Tamil Nadu Weather Council Integration</span>
              </div>
              <p className="text-slate-600 pl-6">
                Live ambient temperature, relative humidity, wind speed, and precipitation forecasts are fetched from Open-Meteo geocoded weather endpoints using GPS latitude & longitude coordinates.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>2. Monocrystalline Solar MPPT Inverters</span>
              </div>
              <p className="text-slate-600 pl-6">
                Rooftop panel array output is measured by string inverters equipped with Maximum Power Point Tracking (MPPT). Data is transmitted over RS485 Modbus protocol to the central facility management gateway.
              </p>
            </div>

            <div className="p-3.5 bg-slate-50 rounded-xl border border-slate-200/80 space-y-1">
              <div className="font-bold text-slate-900 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-teal-700" />
                <span>3. Optical PM2.5 / PM10 Dust Sensors</span>
              </div>
              <p className="text-slate-600 pl-6">
                Laser particulate sensors installed in the central courtyard measure scattered light particles to calculate mass concentration in micrograms per cubic meter (µg/m³), outputting standard AQI values.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
