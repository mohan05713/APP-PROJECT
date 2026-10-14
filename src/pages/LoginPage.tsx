import React, { useState } from 'react';
import { UserProfile, Role, Resident } from '../lib/types';
import { Key, User, Shield, Wrench, Lock, CheckCircle2 } from 'lucide-react';

interface LoginPageProps {
  currentUser: UserProfile;
  residents: Resident[];
  onProfileSwitch: (username: string) => void;
  onToast: (msg: string, icon?: string) => void;
  onNavigate: (page: string) => void;
}

export const LoginPage: React.FC<LoginPageProps> = ({
  currentUser,
  residents,
  onProfileSwitch,
  onToast,
  onNavigate,
}) => {
  const [activeTab, setActiveTab] = useState<'RESIDENT' | 'ADMIN'>('RESIDENT');
  const [usernameInput, setUsernameInput] = useState('flat_402');
  const [passwordInput, setPasswordInput] = useState('mohan123');

  const handleManualLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (activeTab === 'ADMIN') {
      onProfileSwitch('admin');
      onToast('Logged in as SRM Facility Admin Office with full edit permissions!', '🛡️');
    } else {
      // Find matching resident
      const match = residents.find(
        (r) => r.username.toLowerCase() === usernameInput.toLowerCase() || r.flat_number.toLowerCase() === usernameInput.toLowerCase()
      );
      if (match) {
        onProfileSwitch(match.username);
        onToast(`Logged in as ${match.resident_name} (${match.flat_number})!`, '🏠');
      } else {
        onProfileSwitch('flat_402');
        onToast(`Signed in as Resident: ${usernameInput}`, '🔑');
      }
    }
    onNavigate('dashboard');
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      {/* TAB SELECTOR */}
      <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-2xs space-y-6">
        <div className="text-center space-y-1">
          <h2 className="text-lg font-extrabold text-slate-900 tracking-tight">
            EcoApartment OS — Secure Portal Login
          </h2>
          <p className="text-xs text-slate-500 font-medium">
            Separate Login Portals for Admin Office Authority & 400 Resident Tenants
          </p>
        </div>

        <div className="flex bg-slate-100 p-1 rounded-xl">
          <button
            onClick={() => {
              setActiveTab('RESIDENT');
              setUsernameInput('flat_402');
              setPasswordInput('mohan123');
            }}
            className={`flex-1 py-2.5 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
              activeTab === 'RESIDENT' ? 'bg-white text-teal-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <User className="w-4 h-4" />
            <span>Resident Tenant Login</span>
          </button>

          <button
            onClick={() => {
              setActiveTab('ADMIN');
              setUsernameInput('admin');
              setPasswordInput('admin123');
            }}
            className={`flex-1 py-2.5 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
              activeTab === 'ADMIN' ? 'bg-white text-teal-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Shield className="w-4 h-4 text-teal-700" />
            <span>Admin Office Portal</span>
          </button>
        </div>

        {/* LOGIN FORM */}
        <form onSubmit={handleManualLogin} className="space-y-4 text-xs">
          <div>
            <label className="block font-bold text-slate-700 mb-1">
              {activeTab === 'ADMIN' ? 'Admin Username' : 'Resident Username or Flat Number (e.g. flat_402 or B-402)'}
            </label>
            <input
              type="text"
              value={usernameInput}
              onChange={(e) => setUsernameInput(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl font-mono text-xs outline-none focus:border-teal-600 focus:bg-white"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Password</label>
            <input
              type="password"
              value={passwordInput}
              onChange={(e) => setPasswordInput(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl font-mono text-xs outline-none focus:border-teal-600 focus:bg-white"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 bg-teal-700 hover:bg-teal-800 text-white font-bold rounded-xl transition-colors cursor-pointer text-xs"
          >
            Sign In to {activeTab === 'ADMIN' ? 'Admin Office Portal' : 'Resident Tenant Portal'}
          </button>
        </form>
      </div>

      {/* DEMO PROFILES QUICK LAUNCH */}
      <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-2xs space-y-3">
        <h3 className="text-xs font-extrabold text-slate-900 uppercase tracking-wider">
          ⚡ One-Click Profile Launch Shortcuts
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5 text-xs">
          <button
            onClick={() => {
              onProfileSwitch('admin');
              onToast('Signed in as Admin Office (Full Permissions)', '🛡️');
              onNavigate('dashboard');
            }}
            className="p-3 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-colors cursor-pointer"
          >
            <div className="font-extrabold text-slate-900">Admin Office (Authority)</div>
            <div className="text-[11px] text-slate-500 font-mono">Username: admin · Password: admin123</div>
          </button>

          <button
            onClick={() => {
              onProfileSwitch('flat_402');
              onToast('Signed in as Resident: Mohan Palaniappan (B-402)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-3 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-colors cursor-pointer"
          >
            <div className="font-extrabold text-slate-900">Mohan Palaniappan (Flat B-402)</div>
            <div className="text-[11px] text-slate-500 font-mono">Username: flat_402 · Password: mohan123</div>
          </button>

          <button
            onClick={() => {
              onProfileSwitch('flat_101');
              onToast('Signed in as Resident: R. Sham Sudhan (A-101)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-3 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-colors cursor-pointer"
          >
            <div className="font-extrabold text-slate-900">R. Sham Sudhan (Flat A-101)</div>
            <div className="text-[11px] text-slate-500 font-mono">Username: flat_101 · Password: sham123</div>
          </button>

          <button
            onClick={() => {
              onProfileSwitch('tech_plumber');
              onToast('Signed in as Technician: R. Kathir', '👷');
              onNavigate('technician');
            }}
            className="p-3 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-300 rounded-xl text-left transition-colors cursor-pointer"
          >
            <div className="font-extrabold text-slate-900">R. Kathir (Plumbing Technician)</div>
            <div className="text-[11px] text-slate-500 font-mono">Username: tech_plumber · Password: tech123</div>
          </button>
        </div>
      </div>
    </div>
  );
};
