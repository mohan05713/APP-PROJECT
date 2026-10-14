import React, { useState } from 'react';
import { MaintenanceDue, UserProfile } from '../lib/types';
import { PaymentGatewayModal } from '../components/PaymentGatewayModal';
import { CreditCard, Printer, Plus, ShieldCheck, CheckCircle2, AlertCircle } from 'lucide-react';

interface DuesPageProps {
  dues: MaintenanceDue[];
  currentUser: UserProfile;
  onPayDue: (dueId: string, method: string) => void;
  onAddDue: (flatNum: string, monthYear: string, amount: number) => void;
}

export const DuesPage: React.FC<DuesPageProps> = ({ dues, currentUser, onPayDue, onAddDue }) => {
  const [selectedDueForPayment, setSelectedDueForPayment] = useState<MaintenanceDue | null>(null);
  const [selectedReceipt, setSelectedReceipt] = useState<MaintenanceDue | null>(null);
  const [isAddDueOpen, setIsAddDueOpen] = useState(false);
  const [newDueFlat, setNewDueFlat] = useState('B-402');
  const [newDueMonth, setNewDueMonth] = useState('November 2026');
  const [newDueAmount, setNewDueAmount] = useState(3850);

  const isAdmin = currentUser.role === 'AUTHORITY';

  // Find user's pending bill or first pending bill
  const myPendingDue = dues.find(
    (d) => (d.flat_number === currentUser.flat || currentUser.role === 'AUTHORITY') && d.status !== 'PAID'
  ) || dues.find((d) => d.status !== 'PAID');

  const handleAddDueSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onAddDue(newDueFlat, newDueMonth, newDueAmount);
    setIsAddDueOpen(false);
  };

  return (
    <div className="bg-white border border-slate-200 rounded-xl p-6 shadow-2xs space-y-6">
      {/* HEADER */}
      <div className="pb-4 border-b border-slate-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
            <CreditCard className="w-5 h-5 text-teal-700" />
            <span>💳 Maintenance Dues & Utility Billing Portal</span>
          </h2>
          <p className="text-xs text-slate-500 font-medium mt-0.5">
            Flat monthly tariff (₹3,850.00 / month including water, solar, security AMC)
          </p>
        </div>

        {isAdmin && (
          <button
            onClick={() => setIsAddDueOpen(true)}
            className="inline-flex items-center gap-2 px-4 py-2 bg-teal-700 hover:bg-teal-800 text-white rounded-lg text-xs font-bold transition-colors cursor-pointer shrink-0"
          >
            <Plus className="w-4 h-4" />
            <span>Issue Billing Cycle (Admin Only)</span>
          </button>
        )}
      </div>

      {/* FEATURED PENDING BILL PAY CARD */}
      {myPendingDue ? (
        <div className="bg-gradient-to-r from-teal-900 via-teal-800 to-slate-900 text-white rounded-2xl p-6 shadow-md flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border border-teal-700/50">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <span className="text-[10px] font-bold text-amber-300 bg-amber-950/80 border border-amber-500/40 px-2.5 py-0.5 rounded-full uppercase tracking-wider">
                Pending Maintenance Due
              </span>
              <span className="text-xs font-mono font-bold text-teal-200">Flat Unit: {myPendingDue.flat_number}</span>
            </div>
            <h3 className="text-lg font-extrabold text-white">{myPendingDue.month_year} Maintenance Tariff</h3>
            <p className="text-xs text-slate-300">
              Due Date: <span className="font-mono font-bold text-amber-300">{myPendingDue.due_date}</span> · Includes Water, Solar, Elevator & Security AMC
            </p>
          </div>

          <div className="flex flex-col sm:items-end gap-2 w-full sm:w-auto">
            <div className="text-2xl font-extrabold font-mono text-white">₹{myPendingDue.amount_rupees.toFixed(2)}</div>
            <button
              onClick={() => setSelectedDueForPayment(myPendingDue)}
              className="px-6 py-3 bg-emerald-500 hover:bg-emerald-600 text-slate-950 font-extrabold rounded-xl text-xs transition-all shadow-lg cursor-pointer flex items-center justify-center gap-2 w-full sm:w-auto"
            >
              <CreditCard className="w-4 h-4" />
              <span>Pay Now via Gateway (GPay / Card / UPI)</span>
            </button>
          </div>
        </div>
      ) : (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded-xl text-xs font-bold text-emerald-900 flex items-center gap-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-600" />
          <span>All maintenance dues are fully settled for your unit! Zero outstanding balance.</span>
        </div>
      )}

      {/* DUES TABLE */}
      <div className="overflow-x-auto border border-slate-200 rounded-lg">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50 text-slate-500 font-bold uppercase text-[10px] tracking-wider border-b border-slate-200">
            <tr>
              <th className="p-3">Bill ID</th>
              <th className="p-3">Flat</th>
              <th className="p-3">Billing Cycle</th>
              <th className="p-3">Amount</th>
              <th className="p-3">Due Date</th>
              <th className="p-3">Status</th>
              <th className="p-3">Payment Method</th>
              <th className="p-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {dues.map((d) => {
              const statusClass =
                d.status === 'PAID'
                  ? 'bg-emerald-50 text-emerald-800 border-emerald-200'
                  : d.status === 'PENDING'
                  ? 'bg-amber-50 text-amber-800 border-amber-200'
                  : 'bg-rose-50 text-rose-800 border-rose-200';

              return (
                <tr key={d.id} className="hover:bg-slate-50/80 transition-colors">
                  <td className="p-3 font-bold font-mono text-teal-800">{d.id}</td>
                  <td className="p-3 font-bold text-slate-900">{d.flat_number}</td>
                  <td className="p-3 text-slate-700">{d.month_year}</td>
                  <td className="p-3 font-mono font-extrabold text-slate-900">₹{d.amount_rupees.toFixed(2)}</td>
                  <td className="p-3 text-slate-600 font-mono">{d.due_date}</td>
                  <td className="p-3">
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${statusClass}`}>
                      {d.status}
                    </span>
                  </td>
                  <td className="p-3 text-slate-500 font-medium">{d.payment_method || '—'}</td>
                  <td className="p-3 text-right">
                    {d.status !== 'PAID' ? (
                      <button
                        onClick={() => setSelectedDueForPayment(d)}
                        className="px-3.5 py-1.5 bg-teal-700 hover:bg-teal-800 text-white rounded-lg font-bold transition-colors cursor-pointer"
                      >
                        Pay Now (Gateway)
                      </button>
                    ) : (
                      <button
                        onClick={() => setSelectedReceipt(d)}
                        className="px-2.5 py-1 bg-white border border-slate-200 hover:bg-slate-100 text-slate-700 rounded-lg font-bold transition-colors cursor-pointer inline-flex items-center gap-1"
                      >
                        <Printer className="w-3 h-3" />
                        <span>Receipt</span>
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* INTERACTIVE PAYMENT GATEWAY SIMULATOR MODAL */}
      {selectedDueForPayment && (
        <PaymentGatewayModal
          isOpen={Boolean(selectedDueForPayment)}
          onClose={() => setSelectedDueForPayment(null)}
          dueId={selectedDueForPayment.id}
          amountRupees={selectedDueForPayment.amount_rupees}
          flatNumber={selectedDueForPayment.flat_number}
          monthYear={selectedDueForPayment.month_year}
          onPaymentSuccess={(method) => onPayDue(selectedDueForPayment.id, method)}
        />
      )}

      {/* PRINTABLE RECEIPT MODAL */}
      {selectedReceipt && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs animate-fade-in">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full border border-slate-200 shadow-2xl space-y-4">
            <div className="border-b border-slate-200 pb-3 text-center space-y-1">
              <div className="text-lg font-extrabold text-slate-900">SRM GREEN RESIDENCY</div>
              <div className="text-xs text-slate-500 font-semibold">OFFICIAL MAINTENANCE PAYMENT RECEIPT</div>
            </div>

            <div className="space-y-2 text-xs font-mono">
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Bill ID:</span>
                <span className="font-bold text-slate-900">{selectedReceipt.id}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Flat Unit:</span>
                <span className="font-bold text-slate-900">{selectedReceipt.flat_number}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Billing Cycle:</span>
                <span className="font-bold text-slate-900">{selectedReceipt.month_year}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Amount Paid:</span>
                <span className="font-extrabold text-teal-800">₹{selectedReceipt.amount_rupees.toFixed(2)}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Payment Mode:</span>
                <span className="font-bold text-slate-900">{selectedReceipt.payment_method}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Paid Date:</span>
                <span className="font-bold text-emerald-800">{selectedReceipt.paid_date}</span>
              </div>
              <div className="flex justify-between border-b border-slate-100 pb-1">
                <span className="text-slate-500">Status:</span>
                <span className="font-bold text-emerald-800">FULLY SETTLED ✓</span>
              </div>
            </div>

            <button
              onClick={() => setSelectedReceipt(null)}
              className="w-full py-2 bg-slate-900 text-white rounded-lg text-xs font-bold hover:bg-slate-800 cursor-pointer"
            >
              Close Receipt
            </button>
          </div>
        </div>
      )}

      {/* ADMIN ADD BILL MODAL */}
      {isAddDueOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white rounded-2xl p-6 max-w-md w-full border border-slate-200 shadow-2xl space-y-4">
            <h3 className="text-sm font-extrabold text-slate-900">Issue Maintenance Bill (Admin Office)</h3>
            <form onSubmit={handleAddDueSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-slate-700 mb-1">Target Flat Number</label>
                <input type="text" value={newDueFlat} onChange={(e) => setNewDueFlat(e.target.value)} className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-lg outline-none font-mono" required />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Billing Month & Year</label>
                <input type="text" value={newDueMonth} onChange={(e) => setNewDueMonth(e.target.value)} className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-lg outline-none" required />
              </div>
              <div>
                <label className="block font-bold text-slate-700 mb-1">Tariff Amount (₹)</label>
                <input type="number" value={newDueAmount} onChange={(e) => setNewDueAmount(Number(e.target.value))} className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-lg outline-none font-mono" required />
              </div>
              <div className="flex gap-2">
                <button type="button" onClick={() => setIsAddDueOpen(false)} className="flex-1 py-2 bg-slate-100 text-slate-700 font-bold rounded-lg">Cancel</button>
                <button type="submit" className="flex-1 py-2 bg-teal-700 text-white font-bold rounded-lg">Issue Bill</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
