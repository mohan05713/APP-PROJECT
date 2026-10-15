import React from 'react';

interface QuickNavProps {
  activePage: string;
  onNavigate: (page: string) => void;
}

export function QuickNav({ activePage, onNavigate }: QuickNavProps) {
  const quickLinks = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊' },
    { id: 'directory', label: 'Directory', icon: '👥' },
    { id: 'matrix', label: '400 Flats Grid', icon: '🏢' },
    { id: 'complaints', label: 'Complaints', icon: '🛠️' },
    { id: 'dues', label: 'Maintenance Dues', icon: '💳' },
    { id: 'notices', label: 'Notice Board', icon: '📢' },
    { id: 'visitors', label: 'Visitors & Gate', icon: '🛡️' },
    { id: 'water', label: 'Water Monitor', icon: '🚰' },
    { id: 'environment', label: 'Air & Solar', icon: '☀️' },
    { id: 'flood', label: 'Storm Water', icon: '🌧️' },
    { id: 'lpg', label: 'Smart LPG', icon: '🔥' },
    { id: 'anomalies', label: 'Resource Logs', icon: '⚡' },
    { id: 'supabase', label: 'Supabase Sync', icon: '⚡' },
  ];

  return (
    <div className="bg-white border-b border-slate-200 px-4 py-2 flex items-center gap-2 overflow-x-auto scrollbar-none text-xs">
      {quickLinks.map((link) => {
        const isActive = activePage === link.id;
        return (
          <button
            key={link.id}
            onClick={() => onNavigate(link.id)}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full font-semibold transition-all whitespace-nowrap cursor-pointer ${
              isActive
                ? 'bg-teal-700 text-white shadow-sm'
                : 'bg-slate-100 hover:bg-slate-200 text-slate-700'
            }`}
          >
            <span>{link.icon}</span>
            <span>{link.label}</span>
          </button>
        );
      })}
    </div>
  );
}

export default QuickNav;
