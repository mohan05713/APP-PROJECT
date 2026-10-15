import React, { useState, useEffect } from 'react';
import { UserProfile } from '../lib/types';
import { Shield, User, LogOut, Clock, Key } from 'lucide-react';

interface NavbarProps {
  currentUser: UserProfile;
  onProfileSwitch: (username: string) => void;
  activePage: string;
  onNavigate: (page: string) => void;
  onLogout: () => void;
  supabaseStatus?: {
    isConnected: boolean;
    recordCount: number;
    isLoading: boolean;
    errorMessage: string | null;
  };
}

export const Navbar: React.FC<NavbarProps> = ({
  currentUser,
  onProfileSwitch,
  activePage,
  onNavigate,
  onLogout,
  supabaseStatus
}) => {
  const [timeString, setTimeString] = useState('');

  useEffect(() => {
    const updateTime = () => {
      setTimeString(new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' }));
    };
    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, []);

  const isAdmin = currentUser.role === 'AUTHORITY';

  return (
    <header className="sticky top-0 z-40 bg-white border-b border-slate-200 px-6 py-3.5 flex items-center justify-between gap-8 shadow-xs">
      {/* Zone 1: Brand Wordmark */}
      <div className="flex items-center gap-3 shrink-0">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-emerald-600 to-teal-700 flex items-center justify-center text-white font-extrabold text-lg shadow-sm">
          🌿
        </div>
        <button 
          onClick={() => onNavigate('dashboard')}
          className="text-lg font-extrabold tracking-tight text-slate-900 whitespace-nowrap shrink-0 hover:text-teal-700 transition-colors cursor-pointer text-left"
        >
          EcoApartment OS
        </button>
      </div>

      {/* Zone 2: Clean Navigation Links */}
      <nav className="hidden lg:flex items-center gap-6 text-sm font-semibold text-slate-600">
        <button
          onClick={() => onNavigate('dashboard')}
          className={`hover:text-teal-700 transition-colors whitespace-nowrap shrink-0 cursor-pointer ${
            activePage === 'dashboard' ? 'text-teal-700 font-bold' : ''
          }`}
        >
          Dashboard
        </button>
        <button
          onClick={() => onNavigate('directory')}
          className={`hover:text-teal-700 transition-colors whitespace-nowrap shrink-0 cursor-pointer ${
            activePage === 'directory' ? 'text-teal-700 font-bold' : ''
          }`}
        >
          Directory (400)
        </button>
        <button
          onClick={() => onNavigate('complaints')}
          className={`hover:text-teal-700 transition-colors whitespace-nowrap shrink-0 cursor-pointer ${
            activePage === 'complaints' ? 'text-teal-700 font-bold' : ''
          }`}
        >
          Helpdesk
        </button>
        <button
          onClick={() => onNavigate('dues')}
          className={`hover:text-teal-700 transition-colors whitespace-nowrap shrink-0 cursor-pointer ${
            activePage === 'dues' ? 'text-teal-700 font-bold' : ''
          }`}
        >
          Billing & Dues
        </button>
        <button
          onClick={() => onNavigate('supabase')}
          className={`hover:text-teal-700 transition-colors whitespace-nowrap shrink-0 cursor-pointer flex items-center gap-1.5 ${
            activePage === 'supabase' ? 'text-teal-700 font-bold' : ''
          }`}
        >
          <span>⚡ Supabase DB</span>
        </button>
        <button
          onClick={() => onNavigate('myprofile')}
          className={`hover:text-teal-700 transition-colors whitespace-nowrap shrink-0 cursor-pointer ${
            activePage === 'myprofile' ? 'text-teal-700 font-bold' : ''
          }`}
        >
          My Profile
        </button>
      </nav>

      {/* Zone 3: Supabase Status Indicator, Live Clock, Portal Badge, Profile Switcher & Logout */}
      <div className="flex items-center gap-2.5 shrink-0">
        {/* Supabase Database Live Status Badge */}
        {supabaseStatus && (
          <button
            onClick={() => onNavigate('supabase')}
            className={`px-2.5 py-1 rounded-full text-xs font-bold flex items-center gap-1.5 border transition-all cursor-pointer ${
              supabaseStatus.isLoading
                ? 'bg-amber-50 text-amber-800 border-amber-300 animate-pulse'
                : supabaseStatus.isConnected
                ? 'bg-emerald-50 text-emerald-900 border-emerald-300 hover:bg-emerald-100'
                : 'bg-rose-50 text-rose-900 border-rose-300 hover:bg-rose-100'
            }`}
            title="Click to manage Supabase database settings and test connection"
          >
            <span className={`w-2 h-2 rounded-full ${
              supabaseStatus.isLoading
                ? 'bg-amber-500 animate-ping'
                : supabaseStatus.isConnected
                ? 'bg-emerald-500'
                : 'bg-rose-500'
            }`} />
            <span className="hidden sm:inline">
              {supabaseStatus.isLoading
                ? 'Connecting DB...'
                : supabaseStatus.isConnected
                ? `Supabase Live (${supabaseStatus.recordCount})`
                : 'Supabase Offline (Cached)'}
            </span>
          </button>
        )}

        {/* Digital Clock */}
        <div className="hidden xl:flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 rounded-lg text-xs font-mono font-bold text-slate-700">
          <Clock className="w-3.5 h-3.5 text-slate-500" />
          <span>{timeString}</span>
        </div>

        {/* Portal Distinction Badge */}
        <div
          className={`px-3 py-1 rounded-full text-xs font-extrabold tracking-tight hidden md:inline-flex items-center gap-1.5 border ${
            isAdmin ? 'bg-teal-950 text-teal-300 border-teal-700' : 'bg-emerald-50 text-emerald-900 border-emerald-300'
          }`}
        >
          {isAdmin ? <Shield className="w-3.5 h-3.5" /> : <User className="w-3.5 h-3.5" />}
          <span>{isAdmin ? 'ADMIN PORTAL' : 'RESIDENT PORTAL'}</span>
        </div>

        {/* User Active Pill */}
        <button
          onClick={() => onNavigate('myprofile')}
          className="flex items-center gap-2.5 bg-teal-50 hover:bg-teal-100 border border-teal-200/80 px-3 py-1.5 rounded-full cursor-pointer transition-colors"
          title="Click to Edit Profile"
        >
          <div className="w-7 h-7 rounded-full bg-teal-700 text-white flex items-center justify-center font-bold text-xs">
            {currentUser.name.charAt(0)}
          </div>
          <div className="hidden xl:block text-left leading-tight">
            <div className="text-xs font-bold text-teal-950 truncate max-w-[120px]">
              {currentUser.name}
            </div>
            <div className="text-[10px] text-teal-700 font-semibold truncate">
              {currentUser.role} · {currentUser.flat}
            </div>
          </div>
        </button>

        {/* EXPLICIT LOGOUT SYMBOL */}
        <button
          onClick={onLogout}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-rose-50 hover:bg-rose-100 border border-rose-200 text-rose-700 text-xs font-bold transition-colors cursor-pointer"
          title="Logout of Session"
        >
          <LogOut className="w-4 h-4 text-rose-600" />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
};

