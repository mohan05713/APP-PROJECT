import React, { useState, useMemo } from 'react';
import { Resident, UserProfile } from '../lib/types';
import { Search, Plus, Eye, EyeOff, ShieldCheck, Lock, Download, FileSpreadsheet, FileText, FileCode } from 'lucide-react';
import { 
  downloadAllResidentsCSV, 
  downloadAllResidentsJSON, 
  downloadSingleResidentCSV, 
  downloadSingleResidentText,
  downloadSingleResidentJSON,
  convertResidentsToCSV,
  downloadFile
} from '../lib/exportUtils';

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
  
  const isAdmin = currentUser.role === 'AUTHORITY';
  // Default to showing passwords for Admin, hidden for regular residents
  const [showPasswords, setShowPasswords] = useState<boolean>(isAdmin);
  const pageSize = 25;

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

  const handleDownloadFilteredCSV = () => {
    const csvContent = convertResidentsToCSV(filteredResidents);
    downloadFile(csvContent, `residents_filtered_${filteredResidents.length}_people.csv`, 'text/csv;charset=utf-8;');
  };

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-5">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <span>👥 Resident Directory ({residents.length} Tenant Profiles)</span>
            {isAdmin ? (
              <span className="text-[10px] font-bold text-teal-800 bg-teal-50 border border-teal-200 px-2 py-0.5 rounded-full flex items-center gap-1">
                <ShieldCheck className="w-3 h-3 text-teal-600" /> Admin Mode (Full Access)
              </span>
            ) : (
              <span className="text-[10px] font-bold text-amber-800 bg-amber-50 border border-amber-200 px-2 py-0.5 rounded-full flex items-center gap-1">
                <Lock className="w-3 h-3 text-amber-600" /> Residential Portal (Password Protected)
              </span>
            )}
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            {isAdmin
              ? 'Admin Directory Mode: View, inspect, edit, or download usernames, passwords, emails, and phone numbers for all residents.'
              : 'Residential Directory: Browse resident contact info and download profile details. Passwords are strictly protected in Residential Mode.'}
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          {/* Show Passwords button ONLY for Admin Portal */}
          {isAdmin && (
            <button
              onClick={() => setShowPasswords(!showPasswords)}
              className="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-1.5"
              title="Toggle password visibility for admin management"
            >
              {showPasswords ? <EyeOff className="w-3.5 h-3.5 text-amber-600" /> : <Eye className="w-3.5 h-3.5 text-teal-600" />}
              <span>{showPasswords ? 'Hide Passwords' : 'Show Passwords'}</span>
            </button>
          )}

          {isAdmin && (
            <button
              onClick={onAddResident}
              className="inline-flex items-center gap-2 px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
            >
              <Plus className="w-4 h-4" />
              <span>Add Resident</span>
            </button>
          )}
        </div>
      </div>

      {/* QUICK DOWNLOAD ALL DATA BANNER */}
      <div className="bg-gradient-to-r from-teal-900 to-emerald-900 text-white rounded-xl p-4 shadow-sm flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2 font-black text-sm">
            <Download className="w-4 h-4 text-teal-300" />
            <span>Download Residential Database (400 People)</span>
            <span className="text-[10px] uppercase tracking-wider font-extrabold bg-teal-700/80 text-teal-100 px-2 py-0.5 rounded">
              Editable Format
            </span>
          </div>
          <p className="text-xs text-teal-100/90 leading-relaxed max-w-2xl">
            Export all 400 resident records including <span className="underline font-semibold">Usernames</span>, <span className="underline font-semibold">Passwords</span>, <span className="underline font-semibold">Phone Numbers</span>, <span className="underline font-semibold">Emails</span>, and <span className="underline font-semibold">Flat Numbers</span> as a fully editable CSV spreadsheet or structured JSON.
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-2 shrink-0">
          <button
            onClick={() => downloadAllResidentsCSV(residents)}
            className="px-3.5 py-2 bg-white hover:bg-teal-50 text-teal-950 font-extrabold rounded-lg text-xs transition-all shadow-xs cursor-pointer flex items-center gap-1.5 border border-white/20"
            title="Download full 400 residents list as editable CSV (Excel / Sheets compatible)"
          >
            <FileSpreadsheet className="w-4 h-4 text-emerald-700" />
            <span>Download All 400 (Editable CSV)</span>
          </button>

          <button
            onClick={() => downloadAllResidentsJSON(residents)}
            className="px-3.5 py-2 bg-teal-800/90 hover:bg-teal-700 text-white font-extrabold rounded-lg text-xs transition-all cursor-pointer flex items-center gap-1.5 border border-teal-600/50"
            title="Download full 400 residents as JSON file"
          >
            <FileCode className="w-4 h-4 text-teal-200" />
            <span>Download All (JSON)</span>
          </button>
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

        {/* Filtered download button if active filter */}
        {filteredResidents.length < residents.length && (
          <button
            onClick={handleDownloadFilteredCSV}
            className="px-3 py-2 bg-emerald-50 hover:bg-emerald-100 text-emerald-800 border border-emerald-200 rounded-lg text-xs font-bold transition-colors cursor-pointer flex items-center gap-1.5"
            title={`Download ${filteredResidents.length} filtered residents as CSV`}
          >
            <FileSpreadsheet className="w-3.5 h-3.5 text-emerald-700" />
            <span>Download Filtered ({filteredResidents.length})</span>
          </button>
        )}

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
              <th className="p-3 text-right">Actions / Download</th>
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
                    title={isAdmin ? "Tap to Inspect and Edit Full Profile" : "Tap to View Contact Details"}
                  >
                    <td className="p-3 font-bold font-mono text-teal-800">{r.flat_number}</td>
                    <td className="p-3 font-bold text-slate-900">{r.resident_name}</td>
                    <td className="p-3 font-mono font-bold text-teal-900">{r.username}</td>
                    <td className="p-3 font-mono text-slate-700">
                      {isAdmin ? (
                        showPasswords ? (
                          <span className="bg-amber-50 text-amber-900 px-1.5 py-0.5 rounded font-bold border border-amber-200 text-[11px]">
                            {r.password}
                          </span>
                        ) : (
                          <span className="text-slate-400 font-mono text-[11px] select-none">••••••••</span>
                        )
                      ) : (
                        <span className="text-slate-400 font-mono text-[11px] select-none italic" title="Protected in Residential Portal">
                          ••••••••
                        </span>
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
                      {/* Individual Download Buttons */}
                      <button
                        onClick={() => downloadSingleResidentCSV(r)}
                        className="px-2 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-800 border border-emerald-200 rounded font-bold transition-colors cursor-pointer text-[11px] inline-flex items-center gap-1"
                        title={`Download editable CSV for ${r.resident_name}`}
                      >
                        <FileSpreadsheet className="w-3 h-3 text-emerald-600" />
                        <span>CSV</span>
                      </button>

                      <button
                        onClick={() => downloadSingleResidentText(r)}
                        className="px-2 py-1 bg-slate-50 hover:bg-slate-100 text-slate-700 border border-slate-200 rounded font-bold transition-colors cursor-pointer text-[11px] inline-flex items-center gap-1"
                        title={`Download profile text file for ${r.resident_name}`}
                      >
                        <FileText className="w-3 h-3 text-slate-500" />
                        <span>TXT</span>
                      </button>

                      <button
                        onClick={() => onEditResident(r.flat_number)}
                        className="px-2.5 py-1 bg-white border border-slate-200 hover:bg-slate-100 text-slate-700 rounded font-bold transition-colors cursor-pointer text-[11px]"
                      >
                        {isAdmin ? 'Inspect / Edit' : 'View'}
                      </button>

                      {isAdmin && (
                        <button
                          onClick={() => onVacateResident(r.flat_number)}
                          className="px-2.5 py-1 bg-white border border-rose-200 hover:bg-rose-50 text-rose-700 rounded font-bold transition-colors cursor-pointer text-[11px]"
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
