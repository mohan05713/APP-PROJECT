import React from 'react';
import { Anomaly } from '../lib/types';
import { AlertTriangle, Play, ShieldAlert, CheckCircle, Activity } from 'lucide-react';

interface AnomaliesPageProps {
  anomalies: Anomaly[];
  onRunScan: () => void;
}

export const AnomaliesPage: React.FC<AnomaliesPageProps> = ({ anomalies, onRunScan }) => {
  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <AlertTriangle className="w-5 h-5 text-rose-600" />
            <span>⚠️ Resource Anomaly & Water Leak Detection Engine</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Real-time automated detection of pipe bursts, uncharacteristic flow spikes, and sustained nocturnal leakage
          </p>
        </div>

        <button
          onClick={onRunScan}
          className="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-2 shrink-0 shadow-xs"
        >
          <Play className="w-3.5 h-3.5 fill-current" />
          <span>Execute Algorithmic Leak Scan</span>
        </button>
      </div>

      {/* ANOMALY LOG TABLE */}
      <div className="overflow-x-auto border border-slate-200 rounded-lg">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200">
            <tr>
              <th className="p-3">Log ID</th>
              <th className="p-3">Sensor Code</th>
              <th className="p-3">Zone / Location</th>
              <th className="p-3">Measured Flow</th>
              <th className="p-3">Threshold Limit</th>
              <th className="p-3">Severity Status</th>
              <th className="p-3">Timestamp</th>
              <th className="p-3">Technical Diagnostic</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {anomalies.map((a) => (
              <tr key={a.id} className="hover:bg-slate-50 transition-colors">
                <td className="p-3 font-bold font-mono text-rose-700">{a.id}</td>
                <td className="p-3 font-mono font-bold text-slate-800">{a.sensor_id}</td>
                <td className="p-3 font-semibold text-slate-900">{a.zone_id}</td>
                <td className="p-3 font-mono font-extrabold text-rose-800">{a.flow_rate} L/min</td>
                <td className="p-3 font-mono text-slate-500">{a.threshold} L/min</td>
                <td className="p-3">
                  <span className="text-[10px] font-bold px-2.5 py-0.5 rounded-full bg-rose-50 text-rose-800 border border-rose-200">
                    {a.status}
                  </span>
                </td>
                <td className="p-3 text-slate-500 font-mono text-[11px]">{a.timestamp}</td>
                <td className="p-3 text-slate-600">{a.notes}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
