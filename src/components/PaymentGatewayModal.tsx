import React, { useState } from 'react';
import { CreditCard, QrCode, Smartphone, Building, CheckCircle2, ShieldCheck, ArrowRight, Loader2, X } from 'lucide-react';

interface PaymentGatewayModalProps {
  isOpen: boolean;
  onClose: () => void;
  dueId: string;
  amountRupees: number;
  flatNumber: string;
  monthYear: string;
  onPaymentSuccess: (method: string) => void;
}

export const PaymentGatewayModal: React.FC<PaymentGatewayModalProps> = ({
  isOpen,
  onClose,
  dueId,
  amountRupees,
  flatNumber,
  monthYear,
  onPaymentSuccess,
}) => {
  const [method, setMethod] = useState<'UPI' | 'CARD' | 'NETBANKING'>('UPI');
  const [upiId, setUpiId] = useState('mohan.palani@okaxis');
  const [upiPin, setUpiPin] = useState('');
  const [cardNumber, setCardNumber] = useState('4532 8912 3456 7890');
  const [isProcessing, setIsProcessing] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  if (!isOpen) return null;

  const handlePay = (e: React.FormEvent) => {
    e.preventDefault();
    setIsProcessing(true);

    setTimeout(() => {
      setIsProcessing(false);
      setIsSuccess(true);
      setTimeout(() => {
        setIsSuccess(false);
        onPaymentSuccess(method === 'UPI' ? 'UPI (GPay / PhonePe)' : method === 'CARD' ? 'Visa / Mastercard Debit' : 'HDFC NetBanking');
        onClose();
      }, 1500);
    }, 1800);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/70 backdrop-blur-xs animate-fade-in">
      <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 space-y-5">
        {/* HEADER */}
        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-teal-700 text-white flex items-center justify-center font-bold text-sm">
              ₹
            </div>
            <div>
              <h3 className="text-sm font-extrabold text-slate-900">SRM Green Residency Payment Gateway</h3>
              <p className="text-[11px] text-slate-500 font-medium">Secured 256-Bit SSL Checkout Simulator</p>
            </div>
          </div>
          <button onClick={onClose} className="p-1 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 cursor-pointer">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* ORDER SUMMARY */}
        <div className="bg-slate-50 p-3.5 rounded-xl border border-slate-200/80 flex items-center justify-between text-xs">
          <div>
            <div className="font-bold text-slate-900">Maintenance Bill ({monthYear})</div>
            <div className="text-[11px] text-slate-500 font-mono">Flat {flatNumber} · Bill ID: {dueId}</div>
          </div>
          <div className="text-right">
            <div className="text-base font-extrabold text-teal-900 font-mono">₹{amountRupees.toFixed(2)}</div>
            <div className="text-[10px] text-emerald-700 font-bold">Zero Gateway Fee</div>
          </div>
        </div>

        {/* SUCCESS OVERLAY */}
        {isSuccess ? (
          <div className="py-8 text-center space-y-3 animate-fade-in">
            <CheckCircle2 className="w-14 h-14 text-emerald-600 mx-auto animate-bounce" />
            <div className="text-base font-extrabold text-slate-900">Payment Successful!</div>
            <p className="text-xs text-slate-500 font-medium">
              Transaction ref ID <span className="font-mono font-bold text-slate-800">TXN-{Math.floor(100000 + Math.random() * 900000)}</span> verified. Issuing official receipt...
            </p>
          </div>
        ) : isProcessing ? (
          <div className="py-10 text-center space-y-3">
            <Loader2 className="w-10 h-10 text-teal-700 animate-spin mx-auto" />
            <div className="text-xs font-bold text-slate-800">Processing Payment with Banking Server...</div>
            <p className="text-[11px] text-slate-400">Please do not refresh or close window</p>
          </div>
        ) : (
          <form onSubmit={handlePay} className="space-y-4 text-xs">
            {/* PAYMENT METHOD SELECTOR */}
            <div className="grid grid-cols-3 gap-2">
              <button
                type="button"
                onClick={() => setMethod('UPI')}
                className={`p-2.5 rounded-xl border text-center font-bold cursor-pointer transition-all flex flex-col items-center gap-1 ${
                  method === 'UPI' ? 'bg-teal-50 border-teal-600 text-teal-950 ring-1 ring-teal-600/30' : 'bg-slate-50 border-slate-200 text-slate-600'
                }`}
              >
                <Smartphone className="w-4 h-4 text-teal-700" />
                <span>UPI (GPay/PhonePe)</span>
              </button>

              <button
                type="button"
                onClick={() => setMethod('CARD')}
                className={`p-2.5 rounded-xl border text-center font-bold cursor-pointer transition-all flex flex-col items-center gap-1 ${
                  method === 'CARD' ? 'bg-teal-50 border-teal-600 text-teal-950 ring-1 ring-teal-600/30' : 'bg-slate-50 border-slate-200 text-slate-600'
                }`}
              >
                <CreditCard className="w-4 h-4 text-teal-700" />
                <span>Debit / Credit</span>
              </button>

              <button
                type="button"
                onClick={() => setMethod('NETBANKING')}
                className={`p-2.5 rounded-xl border text-center font-bold cursor-pointer transition-all flex flex-col items-center gap-1 ${
                  method === 'NETBANKING' ? 'bg-teal-50 border-teal-600 text-teal-950 ring-1 ring-teal-600/30' : 'bg-slate-50 border-slate-200 text-slate-600'
                }`}
              >
                <Building className="w-4 h-4 text-teal-700" />
                <span>NetBanking</span>
              </button>
            </div>

            {/* UPI DETAILS */}
            {method === 'UPI' && (
              <div className="space-y-3 pt-1">
                <div>
                  <label className="block font-bold text-slate-700 mb-1">VPA / UPI ID</label>
                  <input
                    type="text"
                    value={upiId}
                    onChange={(e) => setUpiId(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono text-xs outline-none"
                    required
                  />
                </div>

                <div>
                  <label className="block font-bold text-slate-700 mb-1">4-Digit or 6-Digit UPI PIN (Simulator)</label>
                  <input
                    type="password"
                    maxLength={6}
                    value={upiPin}
                    onChange={(e) => setUpiPin(e.target.value)}
                    placeholder="••••••"
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono text-xs outline-none"
                    required
                  />
                </div>
              </div>
            )}

            {/* CARD DETAILS */}
            {method === 'CARD' && (
              <div className="space-y-3 pt-1">
                <div>
                  <label className="block font-bold text-slate-700 mb-1">Card Number</label>
                  <input
                    type="text"
                    value={cardNumber}
                    onChange={(e) => setCardNumber(e.target.value)}
                    className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono text-xs outline-none"
                    required
                  />
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="block font-bold text-slate-700 mb-1">Expiry (MM/YY)</label>
                    <input type="text" defaultValue="08/28" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono text-xs outline-none" required />
                  </div>
                  <div>
                    <label className="block font-bold text-slate-700 mb-1">CVV</label>
                    <input type="password" maxLength={3} defaultValue="882" className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono text-xs outline-none" required />
                  </div>
                </div>
              </div>
            )}

            {/* NETBANKING DETAILS */}
            {method === 'NETBANKING' && (
              <div className="pt-1">
                <label className="block font-bold text-slate-700 mb-1">Select Bank</label>
                <select className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-semibold text-xs outline-none">
                  <option>HDFC Bank NetBanking</option>
                  <option>State Bank of India (SBI)</option>
                  <option>ICICI Bank Retail</option>
                  <option>Axis Bank NetBanking</option>
                  <option>Indian Overseas Bank (IOB)</option>
                </select>
              </div>
            )}

            <button
              type="submit"
              className="w-full py-3 bg-teal-700 hover:bg-teal-800 text-white rounded-xl font-bold transition-colors cursor-pointer flex items-center justify-center gap-2 shadow-xs"
            >
              <span>Pay ₹{amountRupees.toFixed(2)} Now</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </form>
        )}
      </div>
    </div>
  );
};
