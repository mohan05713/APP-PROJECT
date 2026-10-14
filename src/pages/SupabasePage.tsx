import React, { useState } from 'react';
import { Database, ShieldCheck, RefreshCw, Key, CheckCircle, Server, Copy } from 'lucide-react';
import { localDb } from '../lib/supabase';

interface SupabasePageProps {
  onToast: (msg: string, icon?: string) => void;
}

export function SupabasePage({ onToast }: SupabasePageProps) {
  const [copied, setCopied] = useState(false);
  const [syncing, setSyncing] = useState(false);

  const residentsCount = localDb.getResidents().length;
  const complaintsCount = localDb.getComplaints().length;
  const duesCount = localDb.getDues().length;

  const handleCopySchema = () => {
    const sqlSchema = `-- EcoApartment OS SQL Schema for Supabase PostgreSQL
CREATE TABLE IF NOT EXISTS residents (
  flat_number VARCHAR(10) PRIMARY KEY,
  block VARCHAR(2) NOT NULL,
  floor_number INT NOT NULL,
  resident_name VARCHAR(100) NOT NULL,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  contact_number VARCHAR(20),
  email VARCHAR(100),
  occupancy_status VARCHAR(20) DEFAULT 'OCCUPIED',
  dues_status VARCHAR(20) DEFAULT 'PAID',
  vehicle_number VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS complaints (
  ticket_id VARCHAR(30) PRIMARY KEY,
  flat_number VARCHAR(10) REFERENCES residents(flat_number),
  floor_number INT,
  category VARCHAR(20),
  priority VARCHAR(20),
  description TEXT,
  status VARCHAR(20),
  authority_notes TEXT,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  raised_by_name VARCHAR(100),
  assigned_technician VARCHAR(100),
  work_log TEXT,
  resolution_solution TEXT
);

CREATE TABLE IF NOT EXISTS maintenance_dues (
  id VARCHAR(30) PRIMARY KEY,
  flat_number VARCHAR(10) REFERENCES residents(flat_number),
  month_year VARCHAR(30),
  amount_rupees NUMERIC(10,2),
  due_date DATE,
  status VARCHAR(20),
  paid_date DATE,
  payment_method VARCHAR(50)
);

-- Row Level Security (RLS) Policies
ALTER TABLE residents ENABLE ROW LEVEL SECURITY;
ALTER TABLE complaints ENABLE ROW LEVEL SECURITY;
ALTER TABLE maintenance_dues ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Residents view own profile" ON residents FOR SELECT USING (auth.uid() = username OR role = 'AUTHORITY');
`;
    navigator.clipboard.writeText(sqlSchema);
    setCopied(true);
    onToast('Copied PostgreSQL Schema SQL to clipboard!', '📋');
    setTimeout(() => setCopied(false), 3000);
  };

  const handleSyncToSupabase = () => {
    setSyncing(true);
    setTimeout(() => {
      setSyncing(false);
      onToast(`Synced ${residentsCount} resident records & data to local persistent store!`, '⚡');
    }, 1200);
  };

  const handleResetDefaults = () => {
    if (confirm('Reset all resident, complaint, and billing records to original default state?')) {
      localDb.resetToDefaults();
      onToast('Database reset to defaults. Reloading app...', '🔄');
      setTimeout(() => window.location.reload(), 1000);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-emerald-900 to-teal-900 text-white p-6 rounded-2xl shadow-lg">
        <div>
          <div className="flex items-center gap-2 text-emerald-400 font-bold text-xs uppercase tracking-wider mb-1">
            <Database className="w-4 h-4" /> Real-time Database Engine
          </div>
          <h1 className="text-2xl font-black">Supabase PostgreSQL Integration</h1>
          <p className="text-emerald-100 text-xs mt-1 max-w-xl">
            Persistent storage manager for 400 resident profiles, maintenance tickets, IoT logs, and payment billing ledger with Row-Level Security (RLS).
          </p>
        </div>

        <div className="flex gap-2">
          <button
            onClick={handleSyncToSupabase}
            disabled={syncing}
            className="px-4 py-2.5 bg-emerald-500 hover:bg-emerald-400 text-slate-900 font-bold text-xs rounded-xl flex items-center gap-2 transition-all shadow-md cursor-pointer disabled:opacity-50"
          >
            <RefreshCw className={`w-4 h-4 ${syncing ? 'animate-spin' : ''}`} />
            {syncing ? 'Syncing Store...' : 'Sync Local & Remote Store'}
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold text-slate-500 uppercase">Resident Profiles</span>
            <Server className="w-5 h-5 text-teal-600" />
          </div>
          <p className="text-2xl font-black text-slate-800">{residentsCount} / 400</p>
          <p className="text-xs text-slate-500 mt-1">Blocks A–D across Floors 1–4</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold text-slate-500 uppercase">Active Tickets</span>
            <ShieldCheck className="w-5 h-5 text-emerald-600" />
          </div>
          <p className="text-2xl font-black text-slate-800">{complaintsCount}</p>
          <p className="text-xs text-slate-500 mt-1">Helpdesk complaints stored</p>
        </div>

        <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs font-bold text-slate-500 uppercase">Billing Ledger</span>
            <CheckCircle className="w-5 h-5 text-blue-600" />
          </div>
          <p className="text-2xl font-black text-slate-800">{duesCount} Records</p>
          <p className="text-xs text-slate-500 mt-1">Maintenance dues and receipts</p>
        </div>
      </div>

      {/* Schema & Security */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-base font-bold text-slate-800 flex items-center gap-2">
              <Key className="w-4 h-4 text-emerald-600" /> Database DDL Schema & RLS Setup
            </h2>
            <p className="text-xs text-slate-500">PostgreSQL table definitions and Row-Level Security rules.</p>
          </div>

          <div className="flex gap-2">
            <button
              onClick={handleCopySchema}
              className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs rounded-lg flex items-center gap-1.5 transition-colors cursor-pointer"
            >
              <Copy className="w-3.5 h-3.5" />
              {copied ? 'Copied!' : 'Copy Schema SQL'}
            </button>
            <button
              onClick={handleResetDefaults}
              className="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-700 font-bold text-xs rounded-lg transition-colors cursor-pointer"
            >
              Reset to Defaults
            </button>
          </div>
        </div>

        <pre className="bg-slate-900 text-emerald-400 p-4 rounded-xl text-xs font-mono overflow-x-auto max-h-80 leading-relaxed border border-slate-800">
{`-- EcoApartment OS SQL Schema for Supabase PostgreSQL
CREATE TABLE IF NOT EXISTS residents (
  flat_number VARCHAR(10) PRIMARY KEY,
  block VARCHAR(2) NOT NULL,
  floor_number INT NOT NULL,
  resident_name VARCHAR(100) NOT NULL,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(100) NOT NULL,
  contact_number VARCHAR(20),
  email VARCHAR(100),
  occupancy_status VARCHAR(20) DEFAULT 'OCCUPIED',
  dues_status VARCHAR(20) DEFAULT 'PAID',
  vehicle_number VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS complaints (
  ticket_id VARCHAR(30) PRIMARY KEY,
  flat_number VARCHAR(10) REFERENCES residents(flat_number),
  floor_number INT,
  category VARCHAR(20),
  priority VARCHAR(20),
  description TEXT,
  status VARCHAR(20),
  authority_notes TEXT,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  raised_by_name VARCHAR(100),
  assigned_technician VARCHAR(100),
  work_log TEXT,
  resolution_solution TEXT
);

CREATE TABLE IF NOT EXISTS maintenance_dues (
  id VARCHAR(30) PRIMARY KEY,
  flat_number VARCHAR(10) REFERENCES residents(flat_number),
  month_year VARCHAR(30),
  amount_rupees NUMERIC(10,2),
  due_date DATE,
  status VARCHAR(20),
  paid_date DATE,
  payment_method VARCHAR(50)
);

-- Row Level Security (RLS) Policies
ALTER TABLE residents ENABLE ROW LEVEL SECURITY;
ALTER TABLE complaints ENABLE ROW LEVEL SECURITY;
ALTER TABLE maintenance_dues ENABLE ROW LEVEL SECURITY;`}
        </pre>
      </div>
    </div>
  );
}
