import React from 'react';

interface SidebarProps {
  activePage: string;
  onNavigate: (page: string) => void;
  openCount?: number;
}

export function Sidebar({ activePage, onNavigate, openCount = 0 }: SidebarProps) {
  const menuSections = [
    {
      title: 'PRIMARY PORTAL',
      items: [
        { id: 'dashboard', label: 'Dashboard Overview', icon: '📊' },
        { id: 'directory', label: 'Residential Directory', icon: '👥' },
        { id: 'matrix', label: '400 Flats Inspector', icon: '🏢' },
        { id: 'my-profile', label: 'User & Security Profile', icon: '👤' },
      ],
    },
    {
      title: 'OPERATIONS & FACILITY',
      items: [
        { id: 'complaints', label: 'Complaints & Helpdesk', icon: '🛠️', badge: openCount > 0 ? `${openCount} Open` : null },
        { id: 'technician', label: 'Technician Dispatch', icon: '🔧' },
        { id: 'dues', label: 'Maintenance & Gateway', icon: '💳' },
        { id: 'notices', label: 'Community Announcements', icon: '📢' },
        { id: 'visitors', label: 'Visitor Passes & Gate', icon: '🛡️' },
      ],
    },
    {
      title: 'IOT & SUSTAINABILITY',
      items: [
        { id: 'water', label: 'Water Resources Tracker', icon: '🚰' },
        { id: 'environment', label: 'Air Quality & Solar Turn', icon: '☀️' },
        { id: 'flood', label: 'Storm Water & Flood System', icon: '🌧️' },
        { id: 'lpg', label: 'Smart LPG Cylinder Monitor', icon: '🔥' },
        { id: 'anomalies', label: 'Resource Anomaly Audit', icon: '⚡' },
      ],
    },
    {
      title: 'DATA & CLOUD',
      items: [
        { id: 'reports', label: 'Facility Analytics', icon: '📈' },
        { id: 'supabase', label: 'Supabase Cloud Store', icon: '⚡' },
      ],
    },
  ];

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 min-h-[calc(100vh-4rem)] p-4 flex flex-col justify-between border-r border-slate-800 text-xs select-none">
      <div className="space-y-6">
        {menuSections.map((section, idx) => (
          <div key={idx}>
            <div className="text-[10px] font-bold text-slate-500 tracking-wider uppercase mb-2 px-3">
              {section.title}
            </div>
            <div className="space-y-1">
              {section.items.map((item) => {
                const isActive = activePage === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => onNavigate(item.id)}
                    className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl font-semibold transition-all cursor-pointer ${
                      isActive
                        ? 'bg-gradient-to-r from-teal-600 to-emerald-600 text-white shadow-md'
                        : 'hover:bg-slate-800 text-slate-300 hover:text-white'
                    }`}
                  >
                    <div className="flex items-center gap-2.5">
                      <span className="text-base">{item.icon}</span>
                      <span>{item.label}</span>
                    </div>

                    {item.badge && (
                      <span className="px-2 py-0.5 bg-rose-500 text-white font-bold rounded-full text-[10px]">
                        {item.badge}
                      </span>
                    )}
                  </button>
                );
              })}
            </div>
          </div>
        ))}
      </div>

      <div className="pt-4 border-t border-slate-800 text-center text-slate-500 text-[11px]">
        <p className="font-semibold text-slate-400">EcoApartment OS v3.2</p>
        <p className="text-[10px] mt-0.5">SRM Campus Complex · Tamil Nadu</p>
      </div>
    </aside>
  );
}

export default Sidebar;
