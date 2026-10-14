import React, { useState } from 'react';
import { UserProfile, Resident } from '../lib/types';
import { User, Lock, Mail, Phone, Key, Save, CheckCircle2, Shield } from 'lucide-react';

interface MyProfilePageProps {
  currentUser: UserProfile;
  residents: Resident[];
  onUpdateProfile: (updatedProfile: Partial<UserProfile>) => void;
  onUpdateResident: (updatedResident: Resident) => void;
  onToast: (msg: string, icon?: string) => void;
}

export const MyProfilePage: React.FC<MyProfilePageProps> = ({
  currentUser,
  residents,
  onUpdateProfile,
  onUpdateResident,
  onToast,
}) => {
  const residentRecord = residents.find((r) => r.flat_number === currentUser.flat || r.username === currentUser.username);

  const [username, setUsername] = useState(currentUser.username);
  const [password, setPassword] = useState(currentUser.password || residentRecord?.password || 'demo123');
  const [name, setName] = useState(currentUser.name);
  const [email, setEmail] = useState(currentUser.email || residentRecord?.email || '');
  const [phone, setPhone] = useState(currentUser.phone || residentRecord?.contact_number || '');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // 1. Update current user state
    onUpdateProfile({
      username,
      password,
      name,
      email,
      phone
    });

    // 2. If user is resident, update matching resident record in 400-resident database
    if (residentRecord) {
      onUpdateResident({
        ...residentRecord,
        resident_name: name,
        username: username,
        password: password,
        contact_number: phone,
        email: email
      });
    }

    onToast('User profile, login credentials, email, and phone number updated successfully!', '✅');
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-2xs space-y-6">
        <div className="pb-4 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
              <User className="w-5 h-5 text-teal-700" />
              <span>Personal Account & Profile Settings</span>
            </h2>
            <p className="text-xs text-slate-500 font-medium mt-0.5">
              Edit your account username, login password, email address, and phone number
            </p>
          </div>

          <span className="text-xs font-bold px-3 py-1 bg-teal-50 text-teal-800 border border-teal-200 rounded-full">
            {currentUser.role} · {currentUser.flat}
          </span>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          <div>
            <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1.5">
              <User className="w-3.5 h-3.5 text-slate-400" />
              <span>Full Name</span>
            </label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:border-teal-600 focus:bg-white transition-all text-xs font-medium"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1.5">
              <Key className="w-3.5 h-3.5 text-slate-400" />
              <span>Account Username</span>
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:border-teal-600 focus:bg-white transition-all text-xs font-mono font-bold text-teal-900"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1.5">
              <Lock className="w-3.5 h-3.5 text-slate-400" />
              <span>User Password</span>
            </label>
            <input
              type="text"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:border-teal-600 focus:bg-white transition-all text-xs font-mono font-bold text-slate-900"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1.5">
              <Mail className="w-3.5 h-3.5 text-slate-400" />
              <span>Email Address</span>
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:border-teal-600 focus:bg-white transition-all text-xs font-medium"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1 flex items-center gap-1.5">
              <Phone className="w-3.5 h-3.5 text-slate-400" />
              <span>Contact Phone Number</span>
            </label>
            <input
              type="text"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl outline-none focus:border-teal-600 focus:bg-white transition-all text-xs font-mono font-medium"
              required
            />
          </div>

          <div className="pt-2">
            <button
              type="submit"
              className="w-full py-3 bg-teal-700 hover:bg-teal-800 text-white rounded-xl font-bold transition-colors cursor-pointer flex items-center justify-center gap-2 shadow-sm"
            >
              <Save className="w-4 h-4" />
              <span>Save & Update Profile Details</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
