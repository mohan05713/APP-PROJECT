import React from 'react';
import { Complaint } from '../lib/types';
import { Wrench, UserCheck, ShieldCheck, Clock } from 'lucide-react';

interface TechnicianPageProps {
  complaints: Complaint[];
}

export const TechnicianPage: React.FC<TechnicianPageProps> = ({ complaints }) => {
  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      <div className="pb-4 border-b border-slate-100">
        <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
          <UserCheck className="w-5 h-5 text-teal-700" />
          <span>👨‍🔧 On-Site Technician Duty Roster & Work Desk</span>
        </h2>
        <p className="text-xs text-slate-500 font-medium mt-0.5">
          Dispatched technical maintenance teams across Plumbing, Electrical, Gas, Elevator, and Security
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {[
          { name: 'K. Senthil', dept: 'Plumbing Bay Lead', phone: '+91 98401 23456', status: 'ON DUTY' },
          { name: 'M. Anand', dept: 'Electrical Engineering', phone: '+91 97902 34567', status: 'ON DUTY' },
          { name: 'R. Karthik', dept: 'Gas Manifold Inspector', phone: '+91 98803 45678', status: 'ON STANDBY' },
        ].map((tech, idx) => (
          <div key={idx} className="p-4 bg-slate-50 border border-slate-200 rounded-xl space-y-1.5">
            <div className="flex items-center justify-between">
              <span className="font-extrabold text-xs text-slate-900">{tech.name}</span>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-800 border border-emerald-200">{tech.status}</span>
            </div>
            <div className="text-xs text-slate-600 font-medium">{tech.dept}</div>
            <div className="text-xs text-slate-500 font-mono">{tech.phone}</div>
          </div>
        ))}
      </div>
    </div>
  );
};
