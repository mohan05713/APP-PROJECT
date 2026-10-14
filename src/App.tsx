import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { Sidebar } from './components/Sidebar';
import { QuickNav } from './components/QuickNav';
import { Toast } from './components/Toast';
import { Modal } from './components/Modal';
import { localDb } from './lib/supabase';
import { fetchLiveWeather } from './lib/weather';
import { 
  Resident, Complaint, MaintenanceDue, CommunityNotice, Visitor, 
  IoTTelemetry, GasCylinder, Anomaly, UserProfile, BlockName, LocationState 
} from './lib/types';

import { DashboardPage } from './pages/DashboardPage';
import { DirectoryPage } from './pages/DirectoryPage';
import { MatrixPage } from './pages/MatrixPage';
import { ComplaintsPage } from './pages/ComplaintsPage';
import { TechnicianPage } from './pages/TechnicianPage';
import { DuesPage } from './pages/DuesPage';
import { NoticesPage } from './pages/NoticesPage';
import { VisitorsPage } from './pages/VisitorsPage';
import { WaterPage } from './pages/WaterPage';
import { EnvironmentPage } from './pages/EnvironmentPage';
import { FloodPage } from './pages/FloodPage';
import { LpgPage } from './pages/LpgPage';
import { AnomaliesPage } from './pages/AnomaliesPage';
import { ReportsPage } from './pages/ReportsPage';
import { SupabasePage } from './pages/SupabasePage';
import { LoginPage } from './pages/LoginPage';
import { MyProfilePage } from './pages/MyProfilePage';

export function App() {
  const [activePage, setActivePage] = useState<string>('dashboard');
  const [toast, setToast] = useState<{ message: string | null; icon?: string }>({ message: null });

  // State loaded from persistent store
  const [residents, setResidents] = useState<Resident[]>(() => localDb.getResidents());
  const [complaints, setComplaints] = useState<Complaint[]>(() => localDb.getComplaints());
  const [dues, setDues] = useState<MaintenanceDue[]>(() => localDb.getDues());
  const [notices, setNotices] = useState<CommunityNotice[]>(() => localDb.getNotices());
  const [visitors, setVisitors] = useState<Visitor[]>(() => localDb.getVisitors());
  const [iot, setIoT] = useState<IoTTelemetry>(() => localDb.getIoT());
  const [gas, setGas] = useState<GasCylinder[]>(() => localDb.getGas());
  const [anomalies, setAnomalies] = useState<Anomaly[]>(() => localDb.getAnomalies());

  // Location & Real Weather State
  const [locationState, setLocationState] = useState<LocationState>({
    permissionGranted: true,
    lat: 12.8231,
    lng: 80.0442,
    locationName: 'SRM Kattankulathur',
    district: 'Chengalpattu, TN',
    temperature: 30.2,
    weatherCondition: 'Partly Cloudy · Coastal Breeze',
    windSpeedKm: 14.2,
    humidityPct: 68,
    lastUpdatedTime: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })
  });

  // Active User Profile
  const [currentUser, setCurrentUser] = useState<UserProfile>({
    username: 'flat_402',
    password: 'mohan123',
    name: 'Mohan Palaniappan',
    role: 'RESIDENT',
    flat: 'B-402',
    phone: '+91 6380244185',
    email: 'mohan.p@srmgreen.in'
  });

  // Fetch real weather on load
  useEffect(() => {
    fetchLiveWeather(12.8231, 80.0442, 'SRM Kattankulathur', 'Chengalpattu, TN').then((res) => setLocationState(res));
  }, []);

  const handleRequestLocationPermission = () => {
    if ('geolocation' in navigator) {
      showToast('Requesting GPS location permission...', '📍');
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          const weather = await fetchLiveWeather(lat, lng, 'My Detected GPS Location', 'Tamil Nadu Region');
          setLocationState(weather);
          showToast(`GPS location synced! ${weather.locationName} (${weather.temperature}°C)`, '✅');
        },
        async (error) => {
          showToast('GPS permission denied. Using SRM Kattankulathur weather feed.', 'ℹ️');
        }
      );
    } else {
      showToast('Geolocation service not supported by browser. Using SRM Kattankulathur weather.', 'ℹ️');
    }
  };

  const handleSelectTNLocation = async (name: string, district: string, lat: number, lng: number) => {
    showToast(`Loading live weather for ${name}...`, '🌦️');
    const weather = await fetchLiveWeather(lat, lng, name, district);
    setLocationState(weather);
    showToast(`Weather synced for ${name}! Temp: ${weather.temperature}°C`, '✅');
  };

  // Modal States
  const [isResidentModalOpen, setIsResidentModalOpen] = useState(false);
  const [editingFlatNum, setEditingFlatNum] = useState<string | null>(null);
  const [resForm, setResForm] = useState({
    block: 'B' as BlockName,
    floor: 4,
    flatNum: '',
    name: '',
    username: '',
    password: '',
    phone: '',
    email: '',
    occupancy: 'OCCUPIED' as Resident['occupancy_status'],
    vehicle: ''
  });

  const [isTicketModalOpen, setIsTicketModalOpen] = useState(false);
  const [ticketForm, setTicketForm] = useState({
    flatNum: 'B-402',
    floor: 4,
    category: 'PLUMBING' as Complaint['category'],
    priority: 'HIGH' as Complaint['priority'],
    description: 'Low water pressure in kitchen pipeline during peak morning transition.'
  });

  const [isGatePassModalOpen, setIsGatePassModalOpen] = useState(false);
  const [gatePassForm, setGatePassForm] = useState({
    flatNum: 'B-402',
    name: 'P. Vignesh',
    phone: '+91 9791098765',
    purpose: 'Amazon Logistics Courier Delivery'
  });

  const [isNoticeModalOpen, setIsNoticeModalOpen] = useState(false);
  const [noticeForm, setNoticeForm] = useState({
    title: '',
    description: '',
    category: 'COMMUNITY' as CommunityNotice['category'],
    priority: 'NORMAL' as CommunityNotice['priority']
  });

  const showToast = (message: string, icon = '🌿') => {
    setToast({ message, icon });
    setTimeout(() => setToast({ message: null }), 3500);
  };

  // Logout Handler
  const handleLogout = () => {
    showToast('Signed out of session.', '🚪');
    setActivePage('login');
  };

  // Switch Profiles
  const handleProfileSwitch = (username: string) => {
    const isAdmin = username === 'admin';
    if (isAdmin) {
      setCurrentUser({
        username: 'admin',
        password: 'admin123',
        name: 'SRM Facility Admin',
        role: 'AUTHORITY',
        flat: 'Admin Office',
        phone: '+91 4427451000',
        email: 'admin@srmgreen.in'
      });
      showToast('Switched to Admin Office (Full Authority Permissions)', '🛡️');
      return;
    }

    // Find resident record in 400 flats database
    const found = residents.find((r) => r.username.toLowerCase() === username.toLowerCase() || r.flat_number.toLowerCase() === username.toLowerCase());
    if (found) {
      setCurrentUser({
        username: found.username,
        password: found.password,
        name: found.resident_name,
        role: 'RESIDENT',
        flat: found.flat_number,
        phone: found.contact_number,
        email: found.email
      });
      showToast(`Switched profile to ${found.resident_name} (${found.flat_number})`, '👤');
    } else {
      setCurrentUser({
        username: 'flat_402',
        password: 'mohan123',
        name: 'Mohan Palaniappan',
        role: 'RESIDENT',
        flat: 'B-402',
        phone: '+91 6380244185',
        email: 'mohan.p@srmgreen.in'
      });
      showToast('Switched to Mohan Palaniappan (Flat B-402)', '🏠');
    }
  };

  // Update Profile
  const handleUpdateProfile = (updatedProfile: Partial<UserProfile>) => {
    const updated = { ...currentUser, ...updatedProfile };
    setCurrentUser(updated);

    // Sync to 400 residents dataset
    if (currentUser.role === 'RESIDENT') {
      const updatedResidents = residents.map((r) =>
        r.flat_number === currentUser.flat || r.username === currentUser.username
          ? {
              ...r,
              resident_name: updated.name,
              username: updated.username,
              password: updated.password || r.password,
              contact_number: updated.phone,
              email: updated.email || r.email
            }
          : r
      );
      setResidents(updatedResidents);
      localDb.saveResidents(updatedResidents);
    }
  };

  const handleUpdateResident = (updatedRes: Resident) => {
    const updatedResidents = residents.map((r) => (r.flat_number === updatedRes.flat_number ? updatedRes : r));
    setResidents(updatedResidents);
    localDb.saveResidents(updatedResidents);
  };

  // RESIDENT CRUD HANDLERS
  const handleOpenAddResident = () => {
    setEditingFlatNum(null);
    setResForm({
      block: 'A',
      floor: 1,
      flatNum: '',
      name: '',
      username: '',
      password: '',
      phone: '',
      email: '',
      occupancy: 'OCCUPIED',
      vehicle: ''
    });
    setIsResidentModalOpen(true);
  };

  const handleOpenEditResident = (flatNumber: string) => {
    const found = residents.find((r) => r.flat_number === flatNumber);
    if (found) {
      setEditingFlatNum(flatNumber);
      setResForm({
        block: found.block,
        floor: found.floor_number,
        flatNum: found.flat_number,
        name: found.resident_name,
        username: found.username,
        password: found.password,
        phone: found.contact_number,
        email: found.email,
        occupancy: found.occupancy_status,
        vehicle: found.vehicle_number
      });
      setIsResidentModalOpen(true);
    }
  };

  const handleSaveResident = (e: React.FormEvent) => {
    e.preventDefault();
    if (!resForm.flatNum || !resForm.name) return;

    let updated: Resident[];
    if (editingFlatNum) {
      updated = residents.map((r) =>
        r.flat_number === editingFlatNum
          ? {
              ...r,
              block: resForm.block,
              floor_number: Number(resForm.floor),
              resident_name: resForm.name,
              username: resForm.username || r.username,
              password: resForm.password || r.password,
              contact_number: resForm.phone,
              email: resForm.email,
              occupancy_status: resForm.occupancy,
              vehicle_number: resForm.vehicle
            }
          : r
      );
      showToast(`Updated resident profile for Flat ${editingFlatNum}!`, '✅');
    } else {
      const newRes: Resident = {
        flat_number: resForm.flatNum,
        block: resForm.block,
        floor_number: Number(resForm.floor),
        resident_name: resForm.name,
        username: resForm.username || `flat_${resForm.flatNum.toLowerCase().replace('-', '')}`,
        password: resForm.password || 'pass123',
        contact_number: resForm.phone,
        email: resForm.email,
        occupancy_status: resForm.occupancy,
        dues_status: 'PAID',
        vehicle_number: resForm.vehicle
      };
      updated = [newRes, ...residents];
      showToast(`Added new resident profile for Flat ${resForm.flatNum}!`, '✅');
    }

    setResidents(updated);
    localDb.saveResidents(updated);
    setIsResidentModalOpen(false);
  };

  const handleVacateResident = (flatNumber: string) => {
    if (confirm(`Vacate Flat ${flatNumber}? This will set occupancy status to VACANT.`)) {
      const updated = residents.map((r) =>
        r.flat_number === flatNumber
          ? { ...r, occupancy_status: 'VACANT' as const, resident_name: 'Unoccupied Unit', contact_number: '—' }
          : r
      );
      setResidents(updated);
      localDb.saveResidents(updated);
      showToast(`Flat ${flatNumber} is now marked as VACANT.`, '📦');
    }
  };

  // HELPDESK TICKET HANDLERS
  const handleSaveTicket = (e: React.FormEvent) => {
    e.preventDefault();
    const tid = `TKT-${Math.floor(100 + Math.random() * 900)}-${ticketForm.category.slice(0, 3)}`;
    const newTicket: Complaint = {
      ticket_id: tid,
      flat_number: ticketForm.flatNum,
      floor_number: Number(ticketForm.floor),
      category: ticketForm.category,
      priority: ticketForm.priority,
      description: ticketForm.description,
      status: 'ASSIGNED',
      authority_notes: 'Dispatched to on-site maintenance bay.',
      timestamp: new Date().toISOString().slice(0, 16).replace('T', ' '),
      raised_by_name: currentUser.name,
      assigned_technician: `Team ${ticketForm.category}`
    };

    const updated = [newTicket, ...complaints];
    setComplaints(updated);
    localDb.saveComplaints(updated);
    setIsTicketModalOpen(false);
    showToast(`Logged Helpdesk Ticket ${tid}!`, '🛠️');
  };

  const handleResolveTicket = (ticketId: string, solutionNote: string) => {
    const updated = complaints.map((c) =>
      c.ticket_id === ticketId
        ? {
            ...c,
            status: 'RESOLVED' as const,
            resolution_solution: solutionNote,
            work_log: solutionNote
          }
        : c
    );
    setComplaints(updated);
    localDb.saveComplaints(updated);
    showToast(`Ticket ${ticketId} resolved with solution note!`, '✅');
  };

  // GATE PASS HANDLERS
  const handleSaveGatePass = (e: React.FormEvent) => {
    e.preventDefault();
    const passCode = `GP-${gatePassForm.flatNum.replace('-', '')}-${Math.floor(1000 + Math.random() * 9000)}`;
    const newVis: Visitor = {
      id: `VIS-${Math.floor(900 + Math.random() * 99)}`,
      flat_number: gatePassForm.flatNum,
      visitor_name: gatePassForm.name,
      visitor_phone: gatePassForm.phone,
      purpose: gatePassForm.purpose,
      check_in_time: new Date().toISOString().slice(0, 16).replace('T', ' '),
      status: 'APPROVED',
      pass_code: passCode
    };

    const updated = [newVis, ...visitors];
    setVisitors(updated);
    localDb.saveVisitors(updated);
    setIsGatePassModalOpen(false);
    showToast(`Gate Pass Generated! Code: ${passCode}`, '🛡️');
  };

  const handleUpdateVisitorStatus = (id: string, status: Visitor['status']) => {
    const updated = visitors.map((v) => (v.id === id ? { ...v, status } : v));
    setVisitors(updated);
    localDb.saveVisitors(updated);
    showToast(`Visitor checked out!`, '📦');
  };

  // DUES PAYMENT
  const handlePayDue = (dueId: string, method: string) => {
    const updated = dues.map((d) =>
      d.id === dueId
        ? {
            ...d,
            status: 'PAID' as const,
            paid_date: new Date().toISOString().slice(0, 10),
            payment_method: method
          }
        : d
    );
    setDues(updated);
    localDb.saveDues(updated);
    showToast(`Payment successful via ${method}!`, '💳');
  };

  const handleAddDue = (flatNum: string, monthYear: string, amount: number) => {
    const newBill: MaintenanceDue = {
      id: `DUE-${Math.floor(1000 + Math.random() * 9000)}`,
      flat_number: flatNum,
      month_year: monthYear,
      amount_rupees: amount,
      due_date: new Date(Date.now() + 15 * 86400000).toISOString().slice(0, 10),
      status: 'PENDING'
    };
    const updated = [newBill, ...dues];
    setDues(updated);
    localDb.saveDues(updated);
    showToast(`Issued Maintenance Bill to Flat ${flatNum}!`, '💳');
  };

  // NOTICE HANDLERS
  const handleSaveNotice = (e: React.FormEvent) => {
    e.preventDefault();
    const newNot: CommunityNotice = {
      id: `NOT-2026-${Math.floor(10 + Math.random() * 90)}`,
      title: noticeForm.title,
      description: noticeForm.description,
      category: noticeForm.category,
      priority: noticeForm.priority,
      posted_time: new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }),
      posted_by: currentUser.name
    };

    const updated = [newNot, ...notices];
    setNotices(updated);
    localDb.saveNotices(updated);
    setIsNoticeModalOpen(false);
    showToast('Posted community notice to residents!', '📢');
  };

  // IOT CONTROLS
  const handleTogglePump = () => {
    const updated = { ...iot, is_pump_running: !iot.is_pump_running };
    setIoT(updated);
    localDb.saveIoT(updated);
    showToast(updated.is_pump_running ? 'Submersible water pump activated!' : 'Submersible water pump stopped.', '💧');
  };

  const handleToggleDrainagePump = () => {
    const updated = { ...iot, drainage_pump_active: !iot.drainage_pump_active };
    setIoT(updated);
    localDb.saveIoT(updated);
    showToast(updated.drainage_pump_active ? 'Basement drainage pump activated!' : 'Drainage pump stopped.', '🌧️');
  };

  const handleSwitchManifold = () => {
    const updated = gas.map((c) => {
      if (c.cylinder_number === 1) return { ...c, status: 'STANDBY' as const };
      if (c.cylinder_number === 2) return { ...c, status: 'ACTIVE' as const };
      return c;
    });
    setGas(updated);
    localDb.saveGas(updated);
    showToast('Switched LPG manifold supply line to Cylinder 2 reserve!', '🔥');
  };

  const handleBookRefill = (unitId: string) => {
    const refId = `REF-${Math.floor(1000 + Math.random() * 9000)}`;
    showToast(`LPG refill booked! Order Ref: ${refId}`, '📦');
  };

  const handleRunAnomalyScan = () => {
    showToast('Executing WaterMeter.detect_anomaly() algorithmic scan...', '⚙️');
    const spikeFlow = Number((52 + Math.random() * 15).toFixed(1));
    const newAnom: Anomaly = {
      id: `ANM-${Math.floor(300 + Math.random() * 99)}`,
      sensor_id: `WM-${Math.floor(100 + Math.random() * 899)}`,
      zone_id: 'Block-C Domestic Riser',
      flow_rate: spikeFlow,
      threshold: 45.0,
      status: 'CRITICAL',
      timestamp: new Date().toISOString().slice(0, 16).replace('T', ' '),
      notes: `Flow spike ${spikeFlow} L/min exceeded 45.0 L/min safety threshold.`
    };
    const updated = [newAnom, ...anomalies];
    setAnomalies(updated);
    localDb.saveAnomalies(updated);
  };

  const openTicketsCount = complaints.filter((c) => c.status !== 'RESOLVED').length;

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
      <Navbar
        currentUser={currentUser}
        onProfileSwitch={handleProfileSwitch}
        activePage={activePage}
        onNavigate={setActivePage}
        onLogout={handleLogout}
      />

      <QuickNav activePage={activePage} onNavigate={setActivePage} />

      <div className="flex-1 flex">
        <div className="hidden lg:block">
          <Sidebar
            activePage={activePage}
            onNavigate={setActivePage}
            openCount={openTicketsCount}
          />
        </div>

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto w-full">
          {activePage === 'dashboard' && (
            <DashboardPage
              residents={residents}
              complaints={complaints}
              iot={iot}
              gas={gas}
              anomalies={anomalies}
              locationState={locationState}
              onRequestLocationPermission={handleRequestLocationPermission}
              onSelectTNLocation={handleSelectTNLocation}
              onNavigate={setActivePage}
              onOpenAddResident={handleOpenAddResident}
              onOpenRaiseTicket={() => setIsTicketModalOpen(true)}
              onOpenGatePass={() => setIsGatePassModalOpen(true)}
              onTogglePump={handleTogglePump}
              onToggleDrainagePump={handleToggleDrainagePump}
              onSwitchLpgManifold={handleSwitchManifold}
              onRunAnomalyScan={handleRunAnomalyScan}
            />
          )}

          {activePage === 'directory' && (
            <DirectoryPage
              residents={residents}
              currentUser={currentUser}
              onAddResident={handleOpenAddResident}
              onEditResident={handleOpenEditResident}
              onVacateResident={handleVacateResident}
            />
          )}

          {activePage === 'matrix' && (
            <MatrixPage
              residents={residents}
              onEditResident={handleOpenEditResident}
            />
          )}

          {activePage === 'complaints' && (
            <ComplaintsPage
              complaints={complaints}
              currentUser={currentUser}
              onOpenRaiseTicket={() => setIsTicketModalOpen(true)}
              onResolveTicket={handleResolveTicket}
            />
          )}

          {activePage === 'technician' && (
            <TechnicianPage complaints={complaints} />
          )}

          {activePage === 'dues' && (
            <DuesPage
              dues={dues}
              currentUser={currentUser}
              onPayDue={handlePayDue}
              onAddDue={handleAddDue}
            />
          )}

          {activePage === 'notices' && (
            <NoticesPage
              notices={notices}
              currentUser={currentUser}
              onOpenPostNotice={() => setIsNoticeModalOpen(true)}
            />
          )}

          {activePage === 'visitors' && (
            <VisitorsPage
              visitors={visitors}
              onOpenGatePass={() => setIsGatePassModalOpen(true)}
              onUpdateVisitorStatus={handleUpdateVisitorStatus}
            />
          )}

          {activePage === 'water' && (
            <WaterPage iot={iot} onTogglePump={handleTogglePump} />
          )}

          {activePage === 'environment' && (
            <EnvironmentPage iot={iot} />
          )}

          {activePage === 'flood' && (
            <FloodPage iot={iot} onToggleDrainagePump={handleToggleDrainagePump} />
          )}

          {activePage === 'lpg' && (
            <LpgPage
              cylinders={gas}
              onSwitchManifold={handleSwitchManifold}
              onBookRefill={handleBookRefill}
            />
          )}

          {activePage === 'anomalies' && (
            <AnomaliesPage anomalies={anomalies} onRunScan={handleRunAnomalyScan} />
          )}

          {activePage === 'reports' && (
            <ReportsPage
              residents={residents}
              dues={dues}
              iot={iot}
              onToast={showToast}
            />
          )}

          {activePage === 'supabase' && (
            <SupabasePage onToast={showToast} />
          )}

          {activePage === 'myprofile' && (
            <MyProfilePage
              currentUser={currentUser}
              residents={residents}
              onUpdateProfile={handleUpdateProfile}
              onUpdateResident={handleUpdateResident}
              onToast={showToast}
            />
          )}

          {activePage === 'login' && (
            <LoginPage
              currentUser={currentUser}
              residents={residents}
              onProfileSwitch={handleProfileSwitch}
              onToast={showToast}
              onNavigate={setActivePage}
            />
          )}
        </main>
      </div>

      {/* MODALS */}
      {/* 1. INSPECTOR / RESIDENT EDITOR MODAL FOR ALL 400 RESIDENTS */}
      <Modal
        isOpen={isResidentModalOpen}
        onClose={() => setIsResidentModalOpen(false)}
        title={editingFlatNum ? `✏️ Inspect & Edit Resident Profile (${editingFlatNum})` : '➕ Add Resident Profile'}
      >
        <form onSubmit={handleSaveResident} className="space-y-4 text-xs">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Block</label>
              <select
                value={resForm.block}
                onChange={(e) => setResForm({ ...resForm, block: e.target.value as BlockName })}
                disabled={currentUser.role !== 'AUTHORITY'}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none disabled:opacity-60"
              >
                <option value="A">Block A</option>
                <option value="B">Block B</option>
                <option value="C">Block C</option>
                <option value="D">Block D</option>
              </select>
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Floor (1–4)</label>
              <input
                type="number"
                min={1}
                max={4}
                value={resForm.floor}
                onChange={(e) => setResForm({ ...resForm, floor: Number(e.target.value) })}
                disabled={currentUser.role !== 'AUTHORITY'}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none disabled:opacity-60"
                required
              />
            </div>
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Flat Number (e.g., B-402)</label>
            <input
              type="text"
              value={resForm.flatNum}
              onChange={(e) => setResForm({ ...resForm, flatNum: e.target.value })}
              disabled={currentUser.role !== 'AUTHORITY' && Boolean(editingFlatNum)}
              placeholder="e.g. B-402"
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none disabled:opacity-60"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Resident Full Name</label>
            <input
              type="text"
              value={resForm.name}
              onChange={(e) => setResForm({ ...resForm, name: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none font-bold"
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Username</label>
              <input
                type="text"
                value={resForm.username}
                onChange={(e) => setResForm({ ...resForm, username: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono font-bold text-teal-800 outline-none"
                required
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Password</label>
              <input
                type="text"
                value={resForm.password}
                onChange={(e) => setResForm({ ...resForm, password: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg font-mono font-bold text-slate-900 outline-none"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Phone Number</label>
              <input
                type="text"
                value={resForm.phone}
                onChange={(e) => setResForm({ ...resForm, phone: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none font-mono"
                required
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Email Address</label>
              <input
                type="email"
                value={resForm.email}
                onChange={(e) => setResForm({ ...resForm, email: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Occupancy Status</label>
              <select
                value={resForm.occupancy}
                onChange={(e) => setResForm({ ...resForm, occupancy: e.target.value as Resident['occupancy_status'] })}
                disabled={currentUser.role !== 'AUTHORITY'}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none disabled:opacity-60"
              >
                <option value="OCCUPIED">OCCUPIED</option>
                <option value="VACANT">VACANT</option>
                <option value="MAINTENANCE">MAINTENANCE</option>
              </select>
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Vehicle Registration</label>
              <input
                type="text"
                value={resForm.vehicle}
                onChange={(e) => setResForm({ ...resForm, vehicle: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none font-mono"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2.5 bg-teal-700 hover:bg-teal-800 text-white font-bold rounded-lg transition-colors cursor-pointer"
          >
            Save Profile & Sync to Supabase Store
          </button>
        </form>
      </Modal>

      {/* 2. RAISE HELPDESK TICKET MODAL */}
      <Modal
        isOpen={isTicketModalOpen}
        onClose={() => setIsTicketModalOpen(false)}
        title="🛠️ Raise Helpdesk Ticket"
      >
        <form onSubmit={handleSaveTicket} className="space-y-4 text-xs">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Flat Number</label>
              <input
                type="text"
                value={ticketForm.flatNum}
                onChange={(e) => setTicketForm({ ...ticketForm, flatNum: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
                required
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Category</label>
              <select
                value={ticketForm.category}
                onChange={(e) => setTicketForm({ ...ticketForm, category: e.target.value as Complaint['category'] })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
              >
                <option value="PLUMBING">PLUMBING</option>
                <option value="ELECTRICAL">ELECTRICAL</option>
                <option value="GAS">GAS</option>
                <option value="ELEVATOR">ELEVATOR</option>
                <option value="SECURITY">SECURITY</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Issue Description</label>
            <textarea
              value={ticketForm.description}
              onChange={(e) => setTicketForm({ ...ticketForm, description: e.target.value })}
              rows={3}
              className="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg outline-none"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-2.5 bg-teal-700 hover:bg-teal-800 text-white font-bold rounded-lg transition-colors cursor-pointer"
          >
            Submit Helpdesk Ticket
          </button>
        </form>
      </Modal>

      {/* 3. GATE PASS MODAL */}
      <Modal
        isOpen={isGatePassModalOpen}
        onClose={() => setIsGatePassModalOpen(false)}
        title="🛡️ Generate Pre-Approved Gate Pass"
      >
        <form onSubmit={handleSaveGatePass} className="space-y-4 text-xs">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block font-bold text-slate-700 mb-1">Host Flat</label>
              <input
                type="text"
                value={gatePassForm.flatNum}
                onChange={(e) => setGatePassForm({ ...gatePassForm, flatNum: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
                required
              />
            </div>

            <div>
              <label className="block font-bold text-slate-700 mb-1">Visitor Name</label>
              <input
                type="text"
                value={gatePassForm.name}
                onChange={(e) => setGatePassForm({ ...gatePassForm, name: e.target.value })}
                className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
                required
              />
            </div>
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Visitor Phone</label>
            <input
              type="text"
              value={gatePassForm.phone}
              onChange={(e) => setGatePassForm({ ...gatePassForm, phone: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Visit Purpose</label>
            <input
              type="text"
              value={gatePassForm.purpose}
              onChange={(e) => setGatePassForm({ ...gatePassForm, purpose: e.target.value })}
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-2.5 bg-teal-700 hover:bg-teal-800 text-white font-bold rounded-lg transition-colors cursor-pointer"
          >
            Generate Gate Pass Code
          </button>
        </form>
      </Modal>

      {/* 4. POST NOTICE MODAL */}
      <Modal
        isOpen={isNoticeModalOpen}
        onClose={() => setIsNoticeModalOpen(false)}
        title="📢 Post Community Announcement"
      >
        <form onSubmit={handleSaveNotice} className="space-y-4 text-xs">
          <div>
            <label className="block font-bold text-slate-700 mb-1">Notice Title</label>
            <input
              type="text"
              value={noticeForm.title}
              onChange={(e) => setNoticeForm({ ...noticeForm, title: e.target.value })}
              placeholder="e.g., Annual Resident General Body Meeting"
              className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg outline-none"
              required
            />
          </div>

          <div>
            <label className="block font-bold text-slate-700 mb-1">Notice Content</label>
            <textarea
              value={noticeForm.description}
              onChange={(e) => setNoticeForm({ ...noticeForm, description: e.target.value })}
              rows={4}
              className="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg outline-none"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full py-2.5 bg-teal-700 hover:bg-teal-800 text-white font-bold rounded-lg transition-colors cursor-pointer"
          >
            Broadcast Notice
          </button>
        </form>
      </Modal>

      <Toast message={toast.message} icon={toast.icon} />
    </div>
  );
}
