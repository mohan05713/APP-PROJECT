import React, { useState, useMemo } from 'react';
import { Resident, UserProfile } from '../lib/types';
import { Search, Plus, Trash2, Edit3, Eye, EyeOff, Shield, Key, Lock, Phone, Mail } from 'lucide-react';

interface DirectoryPageProps {
  residents: Resident[];
  currentUser: UserProfile;
  onAddResident: () => void;
  onEditResident: (flatNumber: string) => void;
  onVacateResident: (flatNumber: string) => void;
}

export const DirectoryPage: React.FC<DirectoryPageProps> = ({
  residents,
  currentUser,
  onAddResident,
  onEditResident,
  onVacateResident,
}) => {
  const [search, setSearch] = useState('');
  const [blockFilter, setBlockFilter] = useState<string>('ALL');
  const [floorFilter, setFloorFilter] = useState<string>('ALL');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [currentPage, setCurrentPage] = useState(1);
  const [showPasswords, setShowPasswords] = useState(false);
  const pageSize = 25;

  const isAdmin = currentUser.role === 'AUTHORITY';

  // Filter residents
  const filteredResidents = useMemo(() => {
    return residents.filter((r) => {
      const matchSearch =
        r.resident_name.toLowerCase().includes(search.toLowerCase()) ||
        r.flat_number.toLowerCase().includes(search.toLowerCase()) ||
        r.username.toLowerCase().includes(search.toLowerCase()) ||
        r.contact_number.includes(search) ||
        r.email.toLowerCase().includes(search.toLowerCase());

      const matchBlock = blockFilter === 'ALL' || r.block === blockFilter;
      const matchFloor = floorFilter === 'ALL' || String(r.floor_number) === floorFilter;
      const matchStatus = statusFilter === 'ALL' || r.occupancy_status === statusFilter;

      return matchSearch && matchBlock && matchFloor && matchStatus;
    });
  }, [residents, search, blockFilter, floorFilter, statusFilter]);

  // Pagination
  const totalPages = Math.ceil(filteredResidents.length / pageSize) || 1;
  const paginatedResidents = useMemo(() => {
    const start = (currentPage - 1) * pageSize;
    return filteredResidents.slice(start, start + pageSize);
  }, [filteredResidents, currentPage]);

  const handleReset = () => {
    setSearch('');
    setBlockFilter('ALL');
    setFloorFilter('ALL');
    setStatusFilter('ALL');
    setCurrentPage(1);
  };

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-5">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <span>👥 Resident Directory (400 Tenant Profiles)</span>
            {!isAdmin && (
              <span className="text-[10px] font-bold text-amber-800 bg-amber-50 border border-amber-200 px-2 py-0.5 rounded-full">
                View-Only Mode (Admin Edit Locked)
              </span>
            )}
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Tap any row or resident (e.g. Mohan Palaniappan Flat B-402) to inspect/edit username, password, email, and phone
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowPasswords(!showPasswords)}
            className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-1.5"
          >
            {showPasswords ? <EyeOff className="w-3.5 h-3.5" /> : <Eye className="w-3.5 h-3.5" />}
            <span>{showPasswords ? 'Hide Passwords' : 'Show Passwords'}</span>
          </button>

          {isAdmin && (
            <button
              onClick={onAddResident}
              className="inline-flex items-center gap-2 px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
            >
              <Plus className="w-4 h-4" />
              <span>Add Resident (Admin)</span>
            </button>
          )}
        </div>
      </div>

      {/* FILTER BAR */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Search */}
        <div className="relative flex-1 min-w-[240px]">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setCurrentPage(1);
            }}
            placeholder="Search by name, flat (e.g. B-402), username, phone, or email..."
            className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-xs outline-none focus:border-teal-600 focus:bg-white transition-all"
          />
        </div>

        {/* Block Filter */}
        <select
          value={blockFilter}
          onChange={(e) => {
            setBlockFilter(e.target.value);
            setCurrentPage(1);
          }}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs font-semibold text-slate-700 outline-none cursor-pointer"
        >
          <option value="ALL">All Blocks</option>
          <option value="A">Block A</option>
          <option value="B">Block B</option>
          <option value="C">Block C</option>
          <option value="D">Block D</option>
        </select>

        {/* Floor Filter */}
        <select
          value={floorFilter}
          onChange={(e) => {
            setFloorFilter(e.target.value);
            setCurrentPage(1);
          }}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs font-semibold text-slate-700 outline-none cursor-pointer"
        >
          <option value="ALL">All Floors</option>
          <option value="1">Floor 1</option>
          <option value="2">Floor 2</option>
          <option value="3">Floor 3</option>
          <option value="4">Floor 4</option>
        </select>

        {/* Status Filter */}
        <select
          value={statusFilter}
          onChange={(e) => {
            setStatusFilter(e.target.value);
            setCurrentPage(1);
          }}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs font-semibold text-slate-700 outline-none cursor-pointer"
        >
          <option value="ALL">All Status</option>
          <option value="OCCUPIED">Occupied</option>
          <option value="VACANT">Vacant</option>
          <option value="MAINTENANCE">Maintenance</option>
        </select>

        <button
          onClick={handleReset}
          className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition-colors cursor-pointer"
        >
          Reset
        </button>
      </div>

      {/* DIRECTORY TABLE */}
      <div className="overflow-x-auto border border-slate-200 rounded-lg">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200">
            <tr>
              <th className="p-3">Flat</th>
              <th className="p-3">Resident Name</th>
              <th className="p-3">Username</th>
              <th className="p-3">Password</th>
              <th className="p-3">Contact</th>
              <th className="p-3">Email</th>
              <th className="p-3">Occupancy</th>
              <th className="p-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {paginatedResidents.length === 0 ? (
              <tr>
                <td colSpan={8} className="text-center p-8 text-slate-400 font-medium">
                  No resident records match current search criteria.
                </td>
              </tr>
            ) : (
              paginatedResidents.map((r) => {
                const occBadge =
                  r.occupancy_status === 'OCCUPIED'
                    ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                    : r.occupancy_status === 'VACANT'
                    ? 'bg-slate-100 text-slate-700 border-slate-300'
                    : 'bg-amber-50 text-amber-800 border-amber-200';

                return (
                  <tr 
                    key={r.flat_number} 
                    onClick={() => onEditResident(r.flat_number)}
                    className="hover:bg-teal-50/40 transition-colors cursor-pointer"
                    title="Tap to Inspect and Edit Full Details"
                  >
                    <td className="p-3 font-bold font-mono text-teal-800">{r.flat_number}</td>
                    <td className="p-3 font-bold text-slate-900">{r.resident_name}</td>
                    <td className="p-3 font-mono font-bold text-teal-900">{r.username}</td>
                    <td className="p-3 font-mono text-slate-700">
                      {showPasswords ? (
                        <span className="bg-amber-50 text-amber-900 px-1.5 py-0.5 rounded font-bold border border-amber-200">{r.password}</span>
                      ) : (
                        '••••••••'
                      )}
                    </td>
                    <td className="p-3 font-mono text-slate-600">{r.contact_number}</td>
                    <td className="p-3 text-slate-500">{r.email}</td>
                    <td className="p-3">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${occBadge}`}>
                        {r.occupancy_status}
                      </span>
                    </td>
                    <td className="p-3 text-right space-x-1.5" onClick={(e) => e.stopPropagation()}>
                      <button
                        onClick={() => onEditResident(r.flat_number)}
                        className="px-2.5 py-1 bg-white border border-slate-200 hover:bg-slate-100 text-slate-700 rounded font-bold transition-colors cursor-pointer"
                      >
                        Inspect / Edit
                      </button>
                      {isAdmin && (
                        <button
                          onClick={() => onVacateResident(r.flat_number)}
                          className="px-2.5 py-1 bg-white border border-rose-200 hover:bg-rose-50 text-rose-700 rounded font-bold transition-colors cursor-pointer"
                        >
                          Vacate
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* PAGINATION FOOTER */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-500 pt-2">
        <div>
          Showing {Math.min((currentPage - 1) * pageSize + 1, filteredResidents.length)} to{' '}
          {Math.min(currentPage * pageSize, filteredResidents.length)} of {filteredResidents.length} flats (Blocks A–D)
        </div>

        <div className="flex items-center gap-2 font-bold">
          <button
            onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
            disabled={currentPage <= 1}
            className="px-3 py-1 bg-slate-100 hover:bg-slate-200 disabled:opacity-40 rounded text-slate-700 transition-colors cursor-pointer"
          >
            Previous
          </button>
          <span className="text-slate-800">
            Page {currentPage} of {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
            disabled={currentPage >= totalPages}
            className="px-3 py-1 bg-slate-100 hover:bg-slate-200 disabled:opacity-40 rounded text-slate-700 transition-colors cursor-pointer"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
};
