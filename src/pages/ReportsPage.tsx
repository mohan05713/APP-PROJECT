import React from 'react';
import { Resident, MaintenanceDue, IoTTelemetry } from '../lib/types';
import { FileText, Download, BarChart2, CheckCircle2, ShieldCheck } from 'lucide-react';

interface ReportsPageProps {
  residents: Resident[];
  dues: MaintenanceDue[];
  iot: IoTTelemetry;
  onToast: (msg: string, icon?: string) => void;
}

export const ReportsPage: React.FC<ReportsPageProps> = ({ residents, dues, iot, onToast }) => {
  const paidDuesCount = dues.filter((d) => d.status === 'PAID').length;
  const totalDuesCollected = dues.reduce((acc, curr) => (curr.status === 'PAID' ? acc + curr.amount_rupees : acc), 0);

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <FileText className="w-5 h-5 text-teal-700" />
            <span>📊 Sustainability Audit & Financial Analytics Reports</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Comprehensive facility audit summaries, water balance ratios, and tariff collection ledgers
          </p>
        </div>

        <button
          onClick={() => onToast('Exported official audit PDF summary!', '📄')}
          className="px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-2 shrink-0"
        >
          <Download className="w-4 h-4" />
          <span>Export Monthly Audit PDF</span>
        </button>
      </div>

      {/* STATS OVERVIEW */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 bg-slate-50 border border-slate-200 rounded-xl space-y-1">
          <div className="text-xs font-semibold text-slate-500">Total Maintenance Dues Collected</div>
          <div className="text-2xl font-extrabold text-slate-900 font-mono">₹{totalDuesCollected.toLocaleString('en-IN')}.00</div>
          <div className="text-[11px] text-emerald-700 font-bold">{paidDuesCount} Bills Fully Settled</div>
        </div>

        <div className="p-4 bg-slate-50 border border-slate-200 rounded-xl space-y-1">
          <div className="text-xs font-semibold text-slate-500">Solar Carbon Offset Value</div>
          <div className="text-2xl font-extrabold text-teal-800 font-mono">142.8 kg CO₂</div>
          <div className="text-[11px] text-slate-500 font-medium">SRM Green Residency Facility Rating</div>
        </div>

        <div className="p-4 bg-slate-50 border border-slate-200 rounded-xl space-y-1">
          <div className="text-xs font-semibold text-slate-500">Facility Occupancy Rate</div>
          <div className="text-2xl font-extrabold text-slate-900 font-mono">100.0%</div>
          <div className="text-[11px] text-slate-500 font-medium">400 / 400 Tenant Units Active</div>
        </div>
      </div>
    </div>
  );
};
