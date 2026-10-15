import React from 'react';

interface ToastProps {
  message: string | null;
  icon?: string;
}

export function Toast({ message, icon = 'ℹ️' }: ToastProps) {
  if (!message) return null;

  return (
    <div className="fixed bottom-6 right-6 z-50 flex items-center gap-3 px-4 py-3 bg-slate-900 text-white rounded-xl shadow-2xl border border-slate-700 animate-slideUp text-xs font-semibold">
      <span className="text-base">{icon}</span>
      <span>{message}</span>
    </div>
  );
}

export default Toast;
