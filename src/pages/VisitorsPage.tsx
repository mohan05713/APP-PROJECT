import React from 'react';
import { Visitor } from '../lib/types';
import { ShieldCheck, Plus, CheckCircle, Clock } from 'lucide-react';

interface VisitorsPageProps {
  visitors: Visitor[];
  onOpenGatePass: () => void;
  onUpdateVisitorStatus: (id: string, status: Visitor['status']) => void;
}

export const VisitorsPage: React.FC<VisitorsPageProps> = ({ visitors, onOpenGatePass, onUpdateVisitorStatus }) => {
  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-teal-700" />
            <span>🛡️ Security Gate Pass & Visitor Entry Log</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Pre-approved visitor gate passes and real-time security gate verification
          </p>
        </div>

        <button
          onClick={onOpenGatePass}
          className="px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-2 shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>Generate Gate Pass</span>
        </button>
      </div>

      {/* TABLE */}
      <div className="overflow-x-auto border border-slate-200 rounded-lg">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200">
            <tr>
              <th className="p-3">Gate Pass Code</th>
              <th className="p-3">Host Flat</th>
              <th className="p-3">Visitor Name</th>
              <th className="p-3">Contact</th>
              <th className="p-3">Purpose</th>
              <th className="p-3">Check-In Time</th>
              <th className="p-3">Status</th>
              <th className="p-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {visitors.map((v) => (
              <tr key={v.id} className="hover:bg-slate-50 transition-colors">
                <td className="p-3 font-mono font-bold text-teal-800">{v.pass_code}</td>
                <td className="p-3 font-bold text-slate-900">{v.flat_number}</td>
                <td className="p-3 font-semibold text-slate-900">{v.visitor_name}</td>
                <td className="p-3 font-mono text-slate-600">{v.visitor_phone}</td>
                <td className="p-3 text-slate-600">{v.purpose}</td>
                <td className="p-3 font-mono text-[11px] text-slate-500">{v.check_in_time}</td>
                <td className="p-3">
                  <span
                    className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                      v.status === 'APPROVED' || v.status === 'INSIDE'
                        ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                        : 'bg-slate-100 text-slate-600 border-slate-200'
                    }`}
                  >
                    {v.status}
                  </span>
                </td>
                <td className="p-3 text-right">
                  {v.status !== 'CHECKED_OUT' && (
                    <button
                      onClick={() => onUpdateVisitorStatus(v.id, 'CHECKED_OUT')}
                      className="px-2.5 py-1 bg-white border border-slate-200 hover:bg-slate-100 text-slate-700 rounded font-bold transition-colors cursor-pointer"
                    >
                      Check-Out
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
