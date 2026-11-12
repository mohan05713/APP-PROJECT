import React, { useState } from 'react';
import { UserProfile, Role, Resident } from '../lib/types';
import { User, Shield, UserPlus, Key, Database, Sparkles, CheckCircle2, Eye, EyeOff, Search, Building2, ExternalLink, Filter } from 'lucide-react';

interface LoginPageProps {
  currentUser: UserProfile;
  residents: Resident[];
  onProfileSwitch: (username: string) => void;
  onRegisterNewUser?: (newUser: { name: string; username: string; password: string; phone: string; email: string }) => void;
  onToast: (msg: string, icon?: string) => void;
  onNavigate: (page: string) => void;
}

export const LoginPage: React.FC<LoginPageProps> = ({
  currentUser,
  residents,
  onProfileSwitch,
  onRegisterNewUser,
  onToast,
  onNavigate,
}) => {
  const [activeTab, setActiveTab] = useState<'RESIDENT' | 'ADMIN' | 'NEW_USER'>('RESIDENT');
  
  // Standard login fields
  const [usernameInput, setUsernameInput] = useState('flat_402');
  const [passwordInput, setPasswordInput] = useState('mohan123');
  const [showPassword, setShowPassword] = useState(false);

  // New user registration fields
  const [newName, setNewName] = useState('New Resident');
  const [newUsername, setNewUsername] = useState('new_user');
  const [newPassword, setNewPassword] = useState('newuser123');
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [newPhone, setNewPhone] = useState('+91 9876543210');
  const [newEmail, setNewEmail] = useState('newuser@srmgreen.in');

  // 400 Residents Database Directory Search State
  const [dbSearch, setDbSearch] = useState('');
  const [selectedBlock, setSelectedBlock] = useState<string>('ALL');
  const [dbPage, setDbPage] = useState(1);
  const itemsPerPage = 12;

  const handleManualLogin = (e: React.FormEvent) => {
    e.preventDefault();
    if (activeTab === 'ADMIN') {
      onProfileSwitch('admin');
      onToast('Logged in as SRM Facility Admin Office with full edit permissions!', '🛡️');
      onNavigate('dashboard');
    } else if (activeTab === 'NEW_USER') {
      if (onRegisterNewUser) {
        onRegisterNewUser({
          name: newName,
          username: newUsername,
          password: newPassword,
          phone: newPhone,
          email: newEmail,
        });
      } else {
        onProfileSwitch('new_user');
        onToast(`Registered & Logged in as ${newName}! Access granted to Flats 400, 401, 402, 403. Saved to Supabase.`, '⚡');
      }
      onNavigate('dashboard');
    } else {
      // Find matching resident
      const match = residents.find(
        (r) =>
          r.username.toLowerCase() === usernameInput.trim().toLowerCase() ||
          r.flat_number.toLowerCase() === usernameInput.trim().toLowerCase() ||
          r.flat_number.replace('-', '').toLowerCase() === usernameInput.trim().toLowerCase()
      );
      if (match) {
        onProfileSwitch(match.username);
        onToast(`Logged in as ${match.resident_name} (${match.flat_number})!`, '🏠');
      } else {
        onProfileSwitch(usernameInput.trim());
        onToast(`Signed in as Resident: ${usernameInput}`, '🔑');
      }
      onNavigate('dashboard');
    }
  };

  // Filter 400 residents database
  const filteredResidents = residents.filter((r) => {
    const matchesBlock = selectedBlock === 'ALL' || r.block === selectedBlock;
    const searchLower = dbSearch.toLowerCase().trim();
    const matchesSearch =
      !searchLower ||
      r.flat_number.toLowerCase().includes(searchLower) ||
      r.resident_name.toLowerCase().includes(searchLower) ||
      r.username.toLowerCase().includes(searchLower) ||
      r.password.toLowerCase().includes(searchLower);
    return matchesBlock && matchesSearch;
  });

  const totalPages = Math.ceil(filteredResidents.length / itemsPerPage);
  const currentResidentsSlice = filteredResidents.slice((dbPage - 1) * itemsPerPage, dbPage * itemsPerPage);

  return (
    <div className="relative max-w-7xl mx-auto space-y-7 rounded-[2rem] bg-[#071b18] p-4 sm:p-6 lg:p-8 shadow-2xl shadow-emerald-950/20 overflow-hidden">
      <div className="pointer-events-none absolute -top-32 -right-20 h-96 w-96 rounded-full bg-emerald-500/10 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-32 -left-20 h-96 w-96 rounded-full bg-teal-400/10 blur-3xl" />
      <div className="relative grid lg:grid-cols-[0.9fr_1.1fr] gap-6 lg:gap-8 items-stretch">
        <section className="relative min-h-[360px] lg:min-h-full overflow-hidden rounded-[1.75rem] border border-emerald-300/15 bg-gradient-to-br from-[#123d32] via-[#0b2b25] to-[#071b18] p-7 sm:p-9 text-white flex flex-col justify-between">
          <div className="absolute -right-12 top-20 h-64 w-64 rounded-full border border-emerald-200/10" />
          <div className="absolute -right-2 top-32 h-44 w-44 rounded-full border border-emerald-200/10" />
          <div className="relative">
            <div className="inline-flex items-center gap-3 rounded-2xl border border-white/10 bg-white/5 px-4 py-3 backdrop-blur">
              <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br from-emerald-300 to-teal-500 text-emerald-950 shadow-lg shadow-emerald-950/30">
                <Building2 className="h-6 w-6" />
              </div>
              <div>
                <p className="text-sm font-black tracking-wide">EcoApartment OS</p>
                <p className="text-[10px] font-semibold uppercase tracking-[0.2em] text-emerald-100/70">Smart Residency Platform</p>
              </div>
            </div>
            <p className="mt-12 text-xs font-bold uppercase tracking-[0.28em] text-emerald-200">Welcome to better living</p>
            <h1 className="mt-4 max-w-lg text-4xl sm:text-5xl font-black leading-[1.08] tracking-tight">Your community.<br/><span className="text-emerald-300">Connected.</span></h1>
            <p className="mt-5 max-w-md text-sm leading-7 text-emerald-50/75">A smarter way to access your residency services, stay connected with your community, and manage everyday living.</p>
          </div>
          <div className="relative mt-12 grid grid-cols-3 gap-2">
            <div className="rounded-2xl border border-white/10 bg-white/[0.06] p-3 backdrop-blur">
              <p className="text-2xl font-black text-white">400</p><p className="mt-1 text-[10px] font-semibold text-emerald-100/65">Residential units</p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/[0.06] p-3 backdrop-blur">
              <p className="text-2xl font-black text-white">04</p><p className="mt-1 text-[10px] font-semibold text-emerald-100/65">Connected blocks</p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/[0.06] p-3 backdrop-blur">
              <p className="text-2xl font-black text-white"><Shield className="h-6 w-6"/></p><p className="mt-1 text-[10px] font-semibold text-emerald-100/65">Secure access</p>
            </div>
          </div>
          <p className="relative mt-6 text-[10px] font-medium tracking-wide text-emerald-100/45">SRM Green Residency · Blocks A–D</p>
        </section>
      {/* TAB SELECTOR & FORM */}
      <div className="rounded-[1.75rem] border border-slate-200/80 bg-white p-6 sm:p-8 lg:p-9 shadow-2xl shadow-black/20 space-y-6">
        <div className="text-left space-y-2">
          <h2 className="text-2xl sm:text-3xl font-black text-slate-950 tracking-tight">
            EcoApartment OS — Secure Portal Login & Registration
          </h2>
          <p className="text-sm text-slate-500 font-medium">
            Multi-Role Authentication Portal connected to Persistent Supabase Data Engine
          </p>
        </div>

        {/* TABS */}
        <div className="flex flex-col sm:flex-row bg-slate-100 p-1.5 rounded-2xl gap-1.5 border border-slate-200">
          <button
            onClick={() => {
              setActiveTab('RESIDENT');
              setUsernameInput('flat_402');
              setPasswordInput('mohan123');
            }}
            className={`flex-1 py-2.5 px-3 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
              activeTab === 'RESIDENT' ? 'bg-white text-teal-900 shadow-md ring-1 ring-teal-100' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <User className="w-4 h-4 text-teal-700" />
            <span>Resident Tenant Login</span>
          </button>

          <button
            onClick={() => {
              setActiveTab('NEW_USER');
            }}
            className={`flex-1 py-2.5 px-3 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
              activeTab === 'NEW_USER' ? 'bg-emerald-600 text-white shadow-md' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <UserPlus className="w-4 h-4" />
            <span>Sign in as New User</span>
          </button>

          <button
            onClick={() => {
              setActiveTab('ADMIN');
              setUsernameInput('admin');
              setPasswordInput('admin123');
            }}
            className={`flex-1 py-2.5 px-3 rounded-lg text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
              activeTab === 'ADMIN' ? 'bg-white text-teal-900 shadow-xs' : 'text-slate-600 hover:text-slate-900'
            }`}
          >
            <Shield className="w-4 h-4 text-teal-700" />
            <span>Admin Office Portal</span>
          </button>
        </div>

        {/* FORM FOR RESIDENT OR ADMIN */}
        {activeTab !== 'NEW_USER' && (
          <form onSubmit={handleManualLogin} className="space-y-4 text-xs">
            <div>
              <label className="block font-bold text-slate-700 mb-1">
                {activeTab === 'ADMIN' ? 'Admin Username (ID)' : 'Resident Username / Flat Number (e.g. flat_402, B-402, jaimukesh_m)'}
              </label>
              <input
                type="text"
                value={usernameInput}
                onChange={(e) => setUsernameInput(e.target.value)}
                className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl font-mono text-sm outline-none transition-all focus:border-teal-600 focus:bg-white focus:ring-4 focus:ring-teal-100"
                required
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={passwordInput}
                  onChange={(e) => setPasswordInput(e.target.value)}
                  className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl font-mono text-sm outline-none transition-all focus:border-teal-600 focus:bg-white focus:ring-4 focus:ring-teal-100 pr-11"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 transition-colors cursor-pointer p-1"
                  title={showPassword ? 'Hide password' : 'See password'}
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-3.5 bg-gradient-to-r from-teal-700 to-emerald-600 hover:from-teal-800 hover:to-emerald-700 text-white font-bold rounded-xl transition-all shadow-lg shadow-teal-900/20 cursor-pointer text-sm flex items-center justify-center gap-2"
            >
              <Key className="w-4 h-4" />
              Sign In to {activeTab === 'ADMIN' ? 'Admin Office Portal' : 'Resident Portal'}
            </button>
          </form>
        )}

        {/* FORM FOR SIGN IN AS NEW USER */}
        {activeTab === 'NEW_USER' && (
          <form onSubmit={handleManualLogin} className="space-y-4 text-xs">
            <div className="bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-200 p-4 rounded-xl space-y-2">
              <div className="flex items-center gap-2 text-emerald-800 font-extrabold text-xs">
                <Sparkles className="w-4 h-4 text-emerald-600" />
                <span>New Resident Registration & Multi-Flat Allocation</span>
              </div>
              <p className="text-slate-600 text-[11px]">
                Registering as a new user automatically assigns access to <span className="font-bold text-emerald-900">Flats 400, 401, 402, and 403</span> in Block A. User details and persistent records will be saved to Supabase store.
              </p>
              <div className="flex flex-wrap gap-1.5 pt-1">
                <span className="px-2 py-0.5 bg-emerald-600 text-white font-mono font-bold rounded text-[10px]">Flat A-400</span>
                <span className="px-2 py-0.5 bg-emerald-600 text-white font-mono font-bold rounded text-[10px]">Flat A-401</span>
                <span className="px-2 py-0.5 bg-emerald-600 text-white font-mono font-bold rounded text-[10px]">Flat A-402</span>
                <span className="px-2 py-0.5 bg-emerald-600 text-white font-mono font-bold rounded text-[10px]">Flat A-403</span>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Resident Full Name</label>
                <input
                  type="text"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  placeholder="e.g. New Resident Name"
                  className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs outline-none focus:border-emerald-600 focus:bg-white"
                  required
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">User ID / Username</label>
                <input
                  type="text"
                  value={newUsername}
                  onChange={(e) => setNewUsername(e.target.value)}
                  placeholder="e.g. new_user"
                  className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl font-mono text-xs outline-none focus:border-emerald-600 focus:bg-white"
                  required
                />
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Password</label>
                <div className="relative">
                  <input
                    type={showNewPassword ? 'text' : 'password'}
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl font-mono text-xs outline-none focus:border-emerald-600 focus:bg-white pr-10"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowNewPassword(!showNewPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700 transition-colors cursor-pointer p-1"
                    title={showNewPassword ? 'Hide password' : 'See password'}
                  >
                    {showNewPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block font-bold text-slate-700 mb-1">Contact Phone</label>
                <input
                  type="text"
                  value={newPhone}
                  onChange={(e) => setNewPhone(e.target.value)}
                  className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl font-mono text-xs outline-none focus:border-emerald-600 focus:bg-white"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Email Address</label>
              <input
                type="email"
                value={newEmail}
                onChange={(e) => setNewEmail(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl font-mono text-xs outline-none focus:border-emerald-600 focus:bg-white"
                required
              />
            </div>

            <button
              type="submit"
              className="w-full py-3.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white font-bold rounded-xl transition-all shadow-lg shadow-emerald-900/20 cursor-pointer text-sm flex items-center justify-center gap-2"
            >
              <Database className="w-4 h-4" />
              <span>Register & Sign In to Portal (Flats 400, 401, 402, 403) → Save to Supabase</span>
            </button>
          </form>
        )}
      </div>

      </div>

      {/* ONE-CLICK PROFILE LAUNCH SHORTCUTS */}
      <div className="relative rounded-[1.75rem] border border-white/10 bg-white p-6 sm:p-8 shadow-xl shadow-black/20 space-y-4">
        <div className="flex items-center justify-between">
          <h3 className="text-xs font-black text-slate-900 uppercase tracking-wider flex items-center gap-2">
            <span>⚡</span> One-Click Profile Launch Shortcuts
          </h3>
          <span className="text-[11px] text-slate-500 font-medium">Click any card to instantly log in</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3 text-xs">
          {/* 1. Sham Sudhan */}
          <button
            onClick={() => {
              onProfileSwitch('flat_101');
              onToast('Signed in as Resident: R. Sham Sudhan (Flat A-101)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-4 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-400 rounded-2xl text-left transition-all hover:shadow-md cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-slate-900 group-hover:text-teal-900">R. Sham Sudhan</span>
              <span className="px-1.5 py-0.5 bg-teal-100 text-teal-800 font-bold text-[10px] rounded">Flat A-101</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">flat_101</span></div>
            <div className="text-[11px] text-slate-600 font-mono">Password: <span className="font-bold text-slate-900">sham123</span></div>
          </button>

          {/* 2. Mohan P */}
          <button
            onClick={() => {
              onProfileSwitch('flat_402');
              onToast('Signed in as Resident: Mohan Palaniappan (Flat B-402)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-3.5 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-400 rounded-xl text-left transition-all cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-slate-900 group-hover:text-teal-900">Mohan P</span>
              <span className="px-1.5 py-0.5 bg-teal-100 text-teal-800 font-bold text-[10px] rounded">Flat B-402</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">flat_402</span></div>
            <div className="text-[11px] text-slate-600 font-mono">Password: <span className="font-bold text-slate-900">mohan123</span></div>
          </button>

          {/* 3. Jai Mukesh M */}
          <button
            onClick={() => {
              onProfileSwitch('jaimukesh_m');
              onToast('Signed in as Resident: Jai Mukesh M (Flat C-401)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-3.5 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-400 rounded-xl text-left transition-all cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-slate-900 group-hover:text-teal-900">Jai Mukesh M</span>
              <span className="px-1.5 py-0.5 bg-teal-100 text-teal-800 font-bold text-[10px] rounded">Flat C-401</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">jaimukesh_m</span></div>
            <div className="text-[11px] text-slate-600 font-mono">Password: <span className="font-bold text-slate-900">jai123</span></div>
          </button>

          {/* 4. Diicson AS */}
          <button
            onClick={() => {
              onProfileSwitch('diicson_as');
              onToast('Signed in as Resident: Diicson AS (Flat D-403)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-3.5 bg-slate-50 hover:bg-teal-50 border border-slate-200 hover:border-teal-400 rounded-xl text-left transition-all cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-slate-900 group-hover:text-teal-900">Diicson AS</span>
              <span className="px-1.5 py-0.5 bg-teal-100 text-teal-800 font-bold text-[10px] rounded">Flat D-403</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">diicson_as</span></div>
            <div className="text-[11px] text-slate-600 font-mono">Password: <span className="font-bold text-slate-900">diicson123</span></div>
          </button>

          {/* 5. Jai Mukesh Profile (Executive) */}
          <button
            onClick={() => {
              onProfileSwitch('jaimukesh_profile');
              onToast('Signed in as Jai Mukesh (Executive Profile - Flat A-400)', '👑');
              onNavigate('dashboard');
            }}
            className="p-4 bg-amber-50 hover:bg-amber-100 border border-amber-200 hover:border-amber-400 rounded-2xl text-left transition-all hover:shadow-md cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-amber-900">Jai Mukesh Profile</span>
              <span className="px-1.5 py-0.5 bg-amber-200 text-amber-900 font-bold text-[10px] rounded">Flat A-400</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">jaimukesh_profile</span></div>
            <div className="text-[11px] text-slate-600 font-mono">Password: <span className="font-bold text-slate-900">jai400123</span></div>
          </button>

          {/* 6. Abinaya T S */}
          <button
            onClick={() => {
              onProfileSwitch('flat_a319');
              onToast('Signed in as Resident: Abinaya T S (Flat A-319)', '🏠');
              onNavigate('dashboard');
            }}
            className="p-4 bg-sky-50 hover:bg-sky-100 border border-sky-200 hover:border-sky-400 rounded-2xl text-left transition-all hover:shadow-md cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-slate-900 group-hover:text-sky-900">Abinaya T S</span>
              <span className="px-1.5 py-0.5 bg-sky-100 text-sky-800 font-bold text-[10px] rounded">Flat A-319</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">flat_a319</span></div>
            <div className="text-[11px] text-slate-600">Phone: +91 88257 42523</div>
            <div className="text-[11px] text-slate-600">Blood Group: A+</div>
            <div className="text-[11px] text-slate-600">Email: abinaya231207@gmail.com</div>
          </button>

          {/* 7. New User Profile (Flats 400, 401, 402, 403) */}
          <button
            onClick={() => {
              if (onRegisterNewUser) {
                onRegisterNewUser({
                  name: 'New Resident User',
                  username: 'new_user',
                  password: 'newuser123',
                  phone: '+91 9876543210',
                  email: 'newuser@srmgreen.in'
                });
              } else {
                onProfileSwitch('new_user');
                onToast('Signed in as New User! Access to Flats 400, 401, 402, 403', '⚡');
              }
              onNavigate('dashboard');
            }}
            className="p-4 bg-emerald-50 hover:bg-emerald-100 border border-emerald-300 hover:border-emerald-500 rounded-2xl text-left transition-all hover:shadow-md cursor-pointer group"
          >
            <div className="flex justify-between items-center mb-1">
              <span className="font-black text-emerald-950">New User (Flats 400-403)</span>
              <span className="px-1.5 py-0.5 bg-emerald-600 text-white font-bold text-[10px] rounded">Flats 400-403</span>
            </div>
            <div className="text-[11px] text-slate-600 font-mono">User ID: <span className="font-bold text-slate-900">new_user</span></div>
            <div className="text-[11px] text-slate-600 font-mono">Password: <span className="font-bold text-slate-900">newuser123</span></div>
          </button>
        </div>
      </div>

      {/* 400 RESIDENTIAL PEOPLE USER ID & PASSWORD DATABASE DIRECTORY */}
      <div className="relative bg-white border border-slate-200 rounded-[1.75rem] p-6 sm:p-8 shadow-xl space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-100">
          <div>
            <h3 className="text-sm font-extrabold text-slate-900 flex items-center gap-2">
              <Building2 className="w-4 h-4 text-teal-700" />
              <span>400 Residential People Database Directory & Credentials</span>
            </h3>
            <p className="text-[11px] text-slate-500">
              Every single flat (A-101 to D-425) in the database has a provisioned User ID and Password. Click "Sign In" on any row to instantly log in as that resident.
            </p>
          </div>
          <div className="text-xs font-mono font-bold bg-teal-50 border border-teal-200 text-teal-900 px-3 py-1 rounded-lg shrink-0 self-start sm:self-auto">
            {filteredResidents.length} / 400 Accounts
          </div>
        </div>

        {/* SEARCH & FILTERS */}
        <div className="flex flex-col sm:flex-row items-center gap-3 text-xs">
          <div className="relative flex-1 w-full">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={dbSearch}
              onChange={(e) => {
                setDbSearch(e.target.value);
                setDbPage(1);
              }}
              placeholder="Search by Flat No (e.g. B-402, A-101), Resident Name, or User ID..."
              className="w-full pl-9 pr-3.5 py-2 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:border-teal-600 focus:bg-white text-xs font-medium"
            />
          </div>

          <div className="flex items-center gap-2 w-full sm:w-auto">
            <Filter className="w-3.5 h-3.5 text-slate-500" />
            <span className="font-bold text-slate-700 whitespace-nowrap">Block:</span>
            <select
              value={selectedBlock}
              onChange={(e) => {
                setSelectedBlock(e.target.value);
                setDbPage(1);
              }}
              className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl outline-none font-bold text-slate-700 cursor-pointer text-xs"
            >
              <option value="ALL">All Blocks (A, B, C, D)</option>
              <option value="A">Block A (100 Units)</option>
              <option value="B">Block B (100 Units)</option>
              <option value="C">Block C (100 Units)</option>
              <option value="D">Block D (100 Units)</option>
            </select>
          </div>
        </div>

        {/* RESIDENTS CREDENTIALS TABLE */}
        <div className="overflow-x-auto border border-slate-200 rounded-xl">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-600 font-bold uppercase text-[10px]">
              <tr>
                <th className="py-2.5 px-3">Flat No</th>
                <th className="py-2.5 px-3">Resident Name</th>
                <th className="py-2.5 px-3">User ID (Username)</th>
                <th className="py-2.5 px-3">Password</th>
                <th className="py-2.5 px-3">Status</th>
                <th className="py-2.5 px-3 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 font-medium">
              {currentResidentsSlice.length === 0 ? (
                <tr>
                  <td colSpan={6} className="py-6 text-center text-slate-400 italic">
                    No resident records found matching "{dbSearch}".
                  </td>
                </tr>
              ) : (
                currentResidentsSlice.map((res) => (
                  <tr key={res.flat_number} className="hover:bg-teal-50/50 transition-colors">
                    <td className="py-2.5 px-3 font-mono font-bold text-slate-900">{res.flat_number}</td>
                    <td className="py-2.5 px-3 font-bold text-slate-800">{res.resident_name}</td>
                    <td className="py-2.5 px-3 font-mono text-teal-800 font-bold">{res.username}</td>
                    <td className="py-2.5 px-3 font-mono text-slate-700 bg-slate-50 rounded border border-slate-200 px-2 py-0.5 text-[11px] inline-block my-1">
                      {res.password}
                    </td>
                    <td className="py-2.5 px-3">
                      <span
                        className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                          res.occupancy_status === 'OCCUPIED'
                            ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                            : 'bg-amber-50 text-amber-800 border border-amber-200'
                        }`}
                      >
                        {res.occupancy_status}
                      </span>
                    </td>
                    <td className="py-2.5 px-3 text-right">
                      <button
                        onClick={() => {
                          onProfileSwitch(res.username);
                          onToast(`Logged in as ${res.resident_name} (${res.flat_number})!`, '🔑');
                          onNavigate('dashboard');
                        }}
                        className="px-2.5 py-1 bg-teal-700 hover:bg-teal-800 text-white font-bold rounded-lg text-[11px] transition-colors cursor-pointer inline-flex items-center gap-1 shadow-2xs"
                      >
                        <span>Sign In</span>
                        <ExternalLink className="w-3 h-3" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* PAGINATION */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between text-xs pt-2">
            <span className="text-slate-500">
              Page <strong className="text-slate-900">{dbPage}</strong> of <strong>{totalPages}</strong> ({filteredResidents.length} total)
            </span>
            <div className="flex items-center gap-1.5">
              <button
                disabled={dbPage === 1}
                onClick={() => setDbPage((p) => Math.max(1, p - 1))}
                className="px-3 py-1 bg-slate-100 hover:bg-slate-200 disabled:opacity-40 text-slate-700 font-bold rounded-lg transition-colors cursor-pointer disabled:cursor-not-allowed"
              >
                Previous
              </button>
              <button
                disabled={dbPage === totalPages}
                onClick={() => setDbPage((p) => Math.min(totalPages, p + 1))}
                className="px-3 py-1 bg-slate-100 hover:bg-slate-200 disabled:opacity-40 text-slate-700 font-bold rounded-lg transition-colors cursor-pointer disabled:cursor-not-allowed"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
