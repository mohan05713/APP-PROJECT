import React from 'react';
import { CommunityNotice, UserProfile } from '../lib/types';
import { Megaphone, Plus, Calendar, User } from 'lucide-react';

interface NoticesPageProps {
  notices: CommunityNotice[];
  currentUser: UserProfile;
  onOpenPostNotice: () => void;
}

export const NoticesPage: React.FC<NoticesPageProps> = ({ notices, currentUser, onOpenPostNotice }) => {
  const isAdmin = currentUser.role === 'AUTHORITY';

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <Megaphone className="w-5 h-5 text-teal-700" />
            <span>📢 Community Notice Board & Welfare Advisories</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Official announcements broadcasted by SRM Green Residency Facility Admin Office
          </p>
        </div>

        {/* POST NOTICE BUTTON - STRICTLY VISIBLE ONLY TO ADMIN AUTHORITY */}
        {isAdmin ? (
          <button
            onClick={onOpenPostNotice}
            className="inline-flex items-center gap-2 px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
          >
            <Plus className="w-4 h-4" />
            <span>Post Notice (Admin Only)</span>
          </button>
        ) : (
          <span className="text-[11px] font-bold text-slate-500 bg-slate-100 px-3 py-1 rounded-full border border-slate-200">
            Read-Only Resident Notice Feed
          </span>
        )}
      </div>

      {/* NOTICES LIST */}
      <div className="space-y-4">
        {notices.map((n) => {
          const priorityBg =
            n.priority === 'CRITICAL'
              ? 'bg-rose-50 text-rose-800 border-rose-200'
              : n.priority === 'URGENT'
              ? 'bg-amber-50 text-amber-800 border-amber-200'
              : 'bg-sky-50 text-sky-800 border-sky-200';

          return (
            <div key={n.id} className="p-5 border border-slate-200 rounded-xl bg-slate-50/50 space-y-2 hover:border-slate-300 transition-colors">
              <div className="flex items-center justify-between gap-3">
                <div className="flex items-center gap-2">
                  <span className="font-mono text-xs font-bold text-teal-800">{n.id}</span>
                  <span className="text-xs font-bold text-slate-700">· {n.category}</span>
                </div>
                <span className={`text-[10px] font-bold px-2.5 py-0.5 rounded-full border ${priorityBg}`}>
                  {n.priority}
                </span>
              </div>

              <h3 className="text-sm font-extrabold text-slate-900">{n.title}</h3>
              <p className="text-xs text-slate-600 leading-relaxed">{n.description}</p>

              <div className="pt-2 flex items-center gap-4 text-[11px] text-slate-400 font-medium border-t border-slate-200/60">
                <span className="flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5 text-slate-400" />
                  <span>{n.posted_time}</span>
                </span>
                <span className="flex items-center gap-1">
                  <User className="w-3.5 h-3.5 text-slate-400" />
                  <span>{n.posted_by}</span>
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
