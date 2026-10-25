import React, { useState } from 'react';
import { Complaint, UserProfile } from '../lib/types';
import { Wrench, Plus, CheckCircle, Clock, ShieldCheck, Check } from 'lucide-react';

interface ComplaintsPageProps {
  complaints: Complaint[];
  currentUser: UserProfile;
  onOpenRaiseTicket: () => void;
  onResolveTicket: (ticketId: string, solutionNote: string) => void;
}

export const ComplaintsPage: React.FC<ComplaintsPageProps> = ({
  complaints,
  currentUser,
  onOpenRaiseTicket,
  onResolveTicket,
}) => {
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [resolvingTicketId, setResolvingTicketId] = useState<string | null>(null);
  const [solutionInput, setSolutionInput] = useState('');

  const isAdmin = currentUser.role === 'AUTHORITY' || currentUser.role === 'TECHNICIAN';

  const filteredComplaints = complaints.filter((c) => {
    const matchCat = categoryFilter === 'ALL' || c.category === categoryFilter;
    const matchStatus = statusFilter === 'ALL' || c.status === statusFilter;
    
    // If resident, show all tickets or own flat tickets
    return matchCat && matchStatus;
  });

  const handleResolveSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!resolvingTicketId) return;
    onResolveTicket(resolvingTicketId, solutionInput || 'Issue inspected and verified resolved by maintenance team.');
    setResolvingTicketId(null);
    setSolutionInput('');
  };

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-slate-100">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <Wrench className="w-5 h-5 text-amber-600" />
            <span>🛠️ Helpdesk Tickets & Maintenance Work Desk</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Complaints logged in Resident Desk appear in Admin Portal. Authority resolutions reflect back to residents.
          </p>
        </div>

        <button
          onClick={onOpenRaiseTicket}
          className="inline-flex items-center gap-2 px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>Raise Ticket</span>
        </button>
      </div>

      {/* FILTER BAR */}
      <div className="flex flex-wrap items-center gap-3">
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs font-semibold text-slate-700 outline-none cursor-pointer"
        >
          <option value="ALL">All Categories</option>
          <option value="PLUMBING">Plumbing</option>
          <option value="ELECTRICAL">Electrical</option>
          <option value="GAS">Gas</option>
          <option value="ELEVATOR">Elevator</option>
          <option value="SECURITY">Security</option>
        </select>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs font-semibold text-slate-700 outline-none cursor-pointer"
        >
          <option value="ALL">All Status</option>
          <option value="ASSIGNED">Assigned</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESOLVED">Resolved</option>
        </select>
      </div>

      {/* COMPLAINTS TABLE */}
      <div className="overflow-x-auto border border-slate-200 rounded-lg">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200">
            <tr>
              <th className="p-3">Ticket ID</th>
              <th className="p-3">Flat</th>
              <th className="p-3">Category</th>
              <th className="p-3">Priority</th>
              <th className="p-3">Issue Description</th>
              <th className="p-3">Authority Solution & Work Notes</th>
              <th className="p-3">Status</th>
              <th className="p-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {filteredComplaints.length === 0 ? (
              <tr>
                <td colSpan={8} className="text-center p-8 text-slate-400 font-medium">
                  No tickets found matching the filter selection.
                </td>
              </tr>
            ) : (
              filteredComplaints.map((c) => {
                const priorityClass =
                  c.priority === 'CRITICAL'
                    ? 'bg-rose-50 text-rose-700 border-rose-200'
                    : c.priority === 'URGENT' || c.priority === 'HIGH'
                    ? 'bg-amber-50 text-amber-700 border-amber-200'
                    : 'bg-sky-50 text-sky-700 border-sky-200';

                const statusClass =
                  c.status === 'RESOLVED'
                    ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                    : c.status === 'IN_PROGRESS'
                    ? 'bg-amber-50 text-amber-800 border-amber-200'
                    : 'bg-slate-100 text-slate-700 border-slate-300';

                return (
                  <tr key={c.ticket_id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="p-3 font-bold font-mono text-teal-800">{c.ticket_id}</td>
                    <td className="p-3 font-bold text-slate-900">{c.flat_number}</td>
                    <td className="p-3 text-slate-700 font-semibold">{c.category}</td>
                    <td className="p-3">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${priorityClass}`}>
                        {c.priority}
                      </span>
                    </td>
                    <td className="p-3 text-slate-600 max-w-xs">{c.description}</td>
                    <td className="p-3 text-slate-800 max-w-xs">
                      {c.resolution_solution || c.work_log || c.authority_notes || 'Pending technician inspection.'}
                    </td>
                    <td className="p-3">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${statusClass}`}>
                        {c.status}
                      </span>
                    </td>
                    <td className="p-3 text-right">
                      {c.status !== 'RESOLVED' ? (
                        isAdmin ? (
                          <button
                            onClick={() => {
                              setResolvingTicketId(c.ticket_id);
                              setSolutionInput('Replaced pressure regulator valve. Verified 2.4 bar stable line pressure.');
                            }}
                            className="px-3 py-1 bg-teal-700 hover:bg-teal-800 text-white rounded font-bold transition-colors cursor-pointer"
                          >
                            Resolve & Submit Solution
                          </button>
                        ) : (
                          <span className="text-amber-700 text-[11px] font-bold">In Processing</span>
                        )
                      ) : (
                        <span className="text-emerald-700 font-bold text-[11px]">✓ Solution Verified</span>
                      )}
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {/* RESOLUTION SOLUTION MODAL */}
      {resolvingTicketId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full border border-slate-200 shadow-2xl space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900">Provide Resolution Solution for {resolvingTicketId}</h3>

            <form onSubmit={handleResolveSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Solution / Action Taken Notes</label>
                <textarea
                  value={solutionInput}
                  onChange={(e) => setSolutionInput(e.target.value)}
                  rows={4}
                  className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl outline-none"
                  placeholder="Describe technical work done to resolve issue..."
                  required
                />
              </div>

              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setResolvingTicketId(null)}
                  className="flex-1 py-2 bg-slate-100 text-slate-700 font-bold rounded-lg hover:bg-slate-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 bg-teal-700 text-white font-bold rounded-lg hover:bg-teal-800"
                >
                  Confirm & Resolve
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
