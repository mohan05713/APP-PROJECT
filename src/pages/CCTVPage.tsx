import React, { useMemo, useState } from 'react';
import { Camera, CarFront, ShieldCheck, Lock, AlertTriangle, VideoOff, Search } from 'lucide-react';
import { Resident, UserProfile } from '../lib/types';

interface CCTVPageProps {
  residents: Resident[];
  currentUser: UserProfile;
}

type CameraKind = 'HOME' | 'PARKING';

interface CameraCard {
  id: string;
  flat: string;
  resident: string;
  kind: CameraKind;
  label: string;
}

export const CCTVPage: React.FC<CCTVPageProps> = ({ residents, currentUser }) => {
  const [search, setSearch] = useState('');
  const isAdmin = currentUser.role === 'AUTHORITY';

  const cameras = useMemo<CameraCard[]>(() => {
    const visibleResidents = isAdmin
      ? residents
      : residents.filter((resident) => resident.flat_number === currentUser.flat);

    return visibleResidents.flatMap((resident) => [
      {
        id: `${resident.flat_number}-home`,
        flat: resident.flat_number,
        resident: resident.resident_name,
        kind: 'HOME' as const,
        label: `${resident.flat_number} · Private house camera`,
      },
      {
        id: `${resident.flat_number}-parking`,
        flat: resident.flat_number,
        resident: resident.resident_name,
        kind: 'PARKING' as const,
        label: `${resident.flat_number} · Assigned parking view`,
      },
    ]);
  }, [residents, currentUser.assignedFlats, currentUser.flat, isAdmin]);

  const filteredCameras = cameras.filter((camera) =>
    `${camera.flat} ${camera.resident} ${camera.label}`.toLowerCase().includes(search.toLowerCase().trim())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.2em] text-teal-700">Security & privacy</p>
          <h1 className="mt-1 text-2xl font-black tracking-tight text-slate-900">CCTV Surveillance</h1>
          <p className="mt-1 text-sm text-slate-500">
            {isAdmin
              ? 'Facility overview · apartment house-camera and assigned parking-camera directory'
              : `Resident access preview · ${currentUser.flat}`}
          </p>
        </div>
        <div className="inline-flex items-center gap-2 self-start rounded-full border border-slate-200 bg-white px-3 py-2 text-xs font-bold text-slate-600">
          <ShieldCheck className="h-4 w-4 text-teal-700" />
          {isAdmin ? 'Admin overview' : 'Resident privacy view'}
        </div>
      </div>

      <div className="rounded-2xl border border-amber-300 bg-amber-50 p-4">
        <div className="flex items-start gap-3">
          <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0 text-amber-700" />
          <div>
            <p className="text-sm font-extrabold text-amber-950">Demo preview — live camera feeds are not connected</p>
            <p className="mt-1 text-xs leading-5 text-amber-900">
              This page demonstrates the intended camera list only. The current app uses demo profile switching, not verified sign-in.
              Do not connect real house-camera streams until Supabase Auth and server-side camera access policies are configured and tested.
            </p>
          </div>
        </div>
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <div className="rounded-2xl border border-slate-200 bg-white p-4">
          <p className="text-xs font-semibold text-slate-500">Visible camera entries</p>
          <p className="mt-1 text-2xl font-black tabular-nums text-slate-900">{filteredCameras.length}</p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-4">
          <p className="text-xs font-semibold text-slate-500">House cameras</p>
          <p className="mt-1 text-2xl font-black tabular-nums text-slate-900">{filteredCameras.filter((camera) => camera.kind === 'HOME').length}</p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-white p-4">
          <p className="text-xs font-semibold text-slate-500">Parking views</p>
          <p className="mt-1 text-2xl font-black tabular-nums text-slate-900">{filteredCameras.filter((camera) => camera.kind === 'PARKING').length}</p>
        </div>
      </div>

      {isAdmin && (
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search by flat number or resident"
            className="w-full rounded-xl border border-slate-200 bg-white py-3 pl-10 pr-4 text-sm outline-none transition focus:border-teal-600 focus:ring-4 focus:ring-teal-100"
          />
        </div>
      )}

      {!isAdmin && (
        <div className="rounded-2xl border border-teal-200 bg-teal-50 p-4">
          <div className="flex items-start gap-3">
            <Lock className="mt-0.5 h-5 w-5 shrink-0 text-teal-800" />
            <div>
              <p className="text-sm font-extrabold text-teal-950">Your house camera is private</p>
              <p className="mt-1 text-xs leading-5 text-teal-900">
                The intended resident view is limited to your own house camera and the parking view assigned to {currentUser.flat}.
                Other residents' house-camera entries are not included in this page's resident view.
              </p>
            </div>
          </div>
        </div>
      )}

      {filteredCameras.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center">
          <Camera className="mx-auto h-8 w-8 text-slate-400" />
          <p className="mt-3 text-sm font-bold text-slate-800">No camera entries found</p>
          <p className="mt-1 text-xs text-slate-500">Check the flat assignment or search term.</p>
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {filteredCameras.map((camera) => (
            <article key={camera.id} className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
              <div className="flex aspect-video flex-col items-center justify-center gap-2 bg-slate-950 text-slate-300">
                {camera.kind === 'HOME' ? <Camera className="h-8 w-8 text-teal-300" /> : <CarFront className="h-8 w-8 text-teal-300" />}
                <VideoOff className="h-4 w-4 text-slate-500" />
                <span className="text-[11px] font-semibold tracking-wide">STREAM NOT CONFIGURED</span>
              </div>
              <div className="space-y-2 p-4">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="text-sm font-extrabold text-slate-900">{camera.label}</p>
                    <p className="mt-1 text-xs text-slate-500">{camera.resident}</p>
                  </div>
                  <span className={`shrink-0 rounded-full px-2 py-1 text-[10px] font-bold ${camera.kind === 'HOME' ? 'bg-teal-50 text-teal-800' : 'bg-sky-50 text-sky-800'}`}>
                    {camera.kind === 'HOME' ? 'PRIVATE' : 'PARKING'}
                  </span>
                </div>
                <p className="text-[11px] text-slate-500">Camera ID: {camera.id}</p>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
};

export default CCTVPage;
