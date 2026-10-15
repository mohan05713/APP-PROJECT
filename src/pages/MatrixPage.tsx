import React, { useState } from 'react';
import { Resident, BlockName, UserProfile } from '../lib/types';

interface MatrixPageProps {
  residents: Resident[];
  currentUser?: UserProfile;
  onEditResident: (flatNumber: string) => void;
}

export function MatrixPage({ residents, currentUser, onEditResident }: MatrixPageProps) {
  const [selectedBlock, setSelectedBlock] = useState<string>('ALL');
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [occupancyFilter, setOccupancyFilter] = useState<string>('ALL');

  const isAdmin = currentUser?.role === 'AUTHORITY';
  const blocks: BlockName[] = ['A', 'B', 'C', 'D'];

  const filteredResidents = residents.filter((r) => {
    const matchesBlock = selectedBlock === 'ALL' || r.block === selectedBlock;
    const matchesOccupancy = occupancyFilter === 'ALL' || r.occupancy_status === occupancyFilter;
    const searchLower = searchTerm.toLowerCase();
    const matchesSearch =
      r.flat_number.toLowerCase().includes(searchLower) ||
      r.resident_name.toLowerCase().includes(searchLower) ||
      r.username.toLowerCase().includes(searchLower) ||
      r.contact_number.includes(searchLower) ||
      r.email.toLowerCase().includes(searchLower);

    return matchesBlock && matchesOccupancy && matchesSearch;
  });

  const totalOccupied = residents.filter((r) => r.occupancy_status === 'OCCUPIED').length;
  const totalVacant = residents.filter((r) => r.occupancy_status === 'VACANT').length;
  const totalMaintenance = residents.filter((r) => r.occupancy_status === 'UNDER_MAINTENANCE').length;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-teal-800 to-emerald-900 rounded-2xl p-6 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-white/10 backdrop-blur rounded-full text-xs font-semibold text-teal-200 mb-2">
            <span>🏢 400 Residential Units Matrix</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">400-Flat Resident Inspector</h1>
          <p className="text-teal-100 text-sm mt-1 max-w-xl">
            {isAdmin
              ? 'Tap any unit card below to view, inspect, or modify username, password, email, and phone for all 400 residents in the complex database.'
              : 'Tap any unit card below to view contact details, block, floor, and occupancy status for all 400 units in the residential complex.'}
          </p>
        </div>

        <div className="grid grid-cols-3 gap-3 bg-white/10 backdrop-blur p-3 rounded-xl border border-white/10 text-center text-xs">
          <div>
            <div className="text-xl font-black text-emerald-300">{totalOccupied}</div>
            <div className="text-teal-200 font-medium">Occupied</div>
          </div>
          <div>
            <div className="text-xl font-black text-amber-300">{totalVacant}</div>
            <div className="text-teal-200 font-medium">Vacant</div>
          </div>
          <div>
            <div className="text-xl font-black text-rose-300">{totalMaintenance}</div>
            <div className="text-teal-200 font-medium">Maintenance</div>
          </div>
        </div>
      </div>

      {/* Controls & Search */}
      <div className="bg-white rounded-xl p-4 border border-slate-200 shadow-sm flex flex-col lg:flex-row items-center justify-between gap-4 text-xs">
        {/* Block Selector Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto w-full lg:w-auto pb-2 lg:pb-0">
          <button
            onClick={() => setSelectedBlock('ALL')}
            className={`px-3 py-2 rounded-lg font-bold transition-all cursor-pointer whitespace-nowrap ${
              selectedBlock === 'ALL'
                ? 'bg-teal-700 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            All Blocks (400)
          </button>
          {blocks.map((blk) => {
            const count = residents.filter((r) => r.block === blk).length;
            return (
              <button
                key={blk}
                onClick={() => setSelectedBlock(blk)}
                className={`px-3 py-2 rounded-lg font-bold transition-all cursor-pointer whitespace-nowrap ${
                  selectedBlock === blk
                    ? 'bg-teal-700 text-white shadow-sm'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                Block {blk} ({count})
              </button>
            );
          })}
        </div>

        {/* Filters */}
        <div className="flex flex-wrap items-center gap-3 w-full lg:w-auto justify-end">
          <select
            value={occupancyFilter}
            onChange={(e) => setOccupancyFilter(e.target.value)}
            className="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-700 font-semibold outline-none"
          >
            <option value="ALL">All Occupancy</option>
            <option value="OCCUPIED">Occupied Only</option>
            <option value="VACANT">Vacant Only</option>
            <option value="UNDER_MAINTENANCE">Maintenance</option>
          </select>

          <div className="relative flex-1 sm:w-64">
            <input
              type="text"
              placeholder="Search resident, flat, phone..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-9 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 placeholder-slate-400 outline-none focus:ring-2 focus:ring-teal-500"
            />
            <span className="absolute left-3 top-2.5 text-slate-400">🔍</span>
          </div>
        </div>
      </div>

      {/* Grid of Flats */}
      <div className="bg-white rounded-xl p-5 border border-slate-200 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-bold text-slate-800 text-sm">
            Displaying {filteredResidents.length} Units {selectedBlock !== 'ALL' && `in Block ${selectedBlock}`}
          </h2>
          <span className="text-xs text-slate-500">Tap card to edit resident profile & login details</span>
        </div>

        {filteredResidents.length === 0 ? (
          <div className="text-center py-12 bg-slate-50 rounded-xl border border-dashed border-slate-200">
            <p className="text-slate-500 font-medium text-sm">No units match the selected filter criteria.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-8 gap-3">
            {filteredResidents.map((res) => {
              const isOccupied = res.occupancy_status === 'OCCUPIED';
              const isMaintenance = res.occupancy_status === 'UNDER_MAINTENANCE';

              return (
                <button
                  key={res.flat_number}
                  onClick={() => onEditResident(res.flat_number)}
                  className={`group relative p-3 rounded-xl border text-left transition-all hover:scale-105 hover:shadow-md cursor-pointer flex flex-col justify-between ${
                    isOccupied
                      ? 'bg-emerald-50/60 border-emerald-200 hover:border-emerald-400'
                      : isMaintenance
                      ? 'bg-amber-50/60 border-amber-200 hover:border-amber-400'
                      : 'bg-slate-50 border-slate-200 hover:border-slate-400'
                  }`}
                >
                  <div>
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-black text-slate-900 text-xs tracking-wide">
                        {res.flat_number}
                      </span>
                      <span
                        className={`w-2 h-2 rounded-full ${
                          isOccupied ? 'bg-emerald-500' : isMaintenance ? 'bg-amber-500' : 'bg-slate-300'
                        }`}
                      />
                    </div>
                    <p className="font-semibold text-slate-800 text-[11px] truncate group-hover:text-teal-800">
                      {res.resident_name || 'Vacant Unit'}
                    </p>
                    <p className="text-[10px] text-slate-500 truncate mt-0.5">
                      @{res.username || 'unassigned'}
                    </p>
                  </div>

                  <div className="mt-2 pt-2 border-t border-slate-200/60 flex items-center justify-between text-[10px] text-slate-500">
                    <span className="truncate">{res.contact_number || 'No contact'}</span>
                    <span className="text-teal-600 font-bold opacity-0 group-hover:opacity-100 transition-opacity">
                      Edit ✏️
                    </span>
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}

export default MatrixPage;
