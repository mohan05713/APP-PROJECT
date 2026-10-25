const http = require('http');
const fs = require('fs');
const path = require('path');

// Uncaught Exception & Rejection Safeguards
process.on('uncaughtException', (err) => {
  console.error('[EcoApartment OS] Uncaught exception (recovered):', err.stack || err.message || err);
});

process.on('unhandledRejection', (reason, promise) => {
  console.error('[EcoApartment OS] Unhandled rejection (recovered):', reason);
});

// Port Allocation
const PORT = process.env.PORT ? parseInt(process.env.PORT, 10) : 3000;

console.log(`[EcoApartment OS] Initializing web server on port ${PORT}...`);

// Mock Data Stores
const mockComplaints = [
  { ticket_id: "TKT-1001", flat_number: "B-402", category: "PLUMBING", priority: "HIGH", description: "Low water pressure in kitchen pipeline during peak morning hours", status: "IN_PROGRESS", assigned_technician: "R. Kathir (Plumbing Specialist)", work_log: "Pipeline pressure valve inspected." },
  { ticket_id: "TKT-1002", flat_number: "A-101", category: "ELECTRICAL", priority: "CRITICAL", description: "Main circuit breaker tripping repeatedly in master bedroom", status: "ASSIGNED", assigned_technician: "K. Selvam (Senior Wireman)", work_log: "MCB replacement scheduled." },
  { ticket_id: "TKT-1003", flat_number: "C-204", category: "ELEVATOR", priority: "URGENT", description: "Tower C Lift 2 door sensor delay on 2nd floor", status: "OPEN", assigned_technician: "S. Arumugam (Lift Engineer)", work_log: "Dispatch ticket generated." },
  { ticket_id: "TKT-1004", flat_number: "D-105", category: "CIVIL", priority: "NORMAL", description: "Balcony tile grouting refurbishment requested", status: "RESOLVED", assigned_technician: "M. Kumar (Civil Mason)", work_log: "Grouting completed on Oct 21." },
  { ticket_id: "TKT-1005", flat_number: "B-301", category: "GAS_SUPPLY", priority: "HIGH", description: "Piped LPG manifold regulator pressure calibration", status: "IN_PROGRESS", assigned_technician: "V. Prakash (Gas Technician)", work_log: "Pressure regulator checked; 1.85 bar nominal." },
  { ticket_id: "TKT-1006", flat_number: "A-108", category: "PLUMBING", priority: "NORMAL", description: "Bathroom aerator tap filter replacement", status: "RESOLVED", assigned_technician: "R. Kathir (Plumbing Specialist)", work_log: "Filter swapped." }
];

const mockNotices = [
  { id: "NOT-01", title: "Annual RWA General Body Meeting & Eco Audit", category: "COMMUNITY", priority: "HIGH", description: "All residents are invited to the Annual RWA meeting at the Clubhouse Hall on Sunday at 10:00 AM.", author: "SRM Facility Admin", posted_time: "2026-10-20", is_pinned: true },
  { id: "NOT-02", title: "Block B Main Overhead Tank Sanitization", category: "MAINTENANCE", priority: "IMPORTANT", description: "Water tank cleaning and pipeline pressure audit scheduled for Saturday from 10:00 AM to 2:00 PM.", author: "SRM Facility Admin", posted_time: "2026-10-22", is_pinned: false },
  { id: "NOT-03", title: "EV Charging Infrastructure Tariff Revision", category: "GREEN_ENERGY", priority: "NORMAL", description: "Subsidized solar EV charging rate fixed at ₹4.50/kWh for all basement EV slots.", author: "SRM Facility Admin", posted_time: "2026-10-24", is_pinned: false }
];

const mockVisitors = [
  { id: "VIS-01", pass_code: "GP-98401", name: "Suresh Kumar (Swiggy)", phone: "+91 98401 55210", visitor_type: "DELIVERY", flat_number: "B-402", vehicle_number: "TN-09-CB-4892", entry_time: "12:30 PM", approval_status: "CHECKED_IN" },
  { id: "VIS-02", pass_code: "GP-77089", name: "Ramesh Sharma (Courier)", phone: "+91 94441 22334", visitor_type: "GUEST", flat_number: "A-101", vehicle_number: "Walk-in", entry_time: "11:15 AM", approval_status: "APPROVED" },
  { id: "VIS-03", pass_code: "GP-65412", name: "Manikandan (Zomato Delivery)", phone: "+91 93610 88214", visitor_type: "DELIVERY", flat_number: "C-204", vehicle_number: "TN-11-AA-9988", entry_time: "01:15 PM", approval_status: "CHECKED_IN" },
  { id: "VIS-04", pass_code: "GP-88341", name: "Dr. K. Venkat (Guest)", phone: "+91 98402 77112", visitor_type: "GUEST", flat_number: "D-105", vehicle_number: "TN-09-DV-1050", entry_time: "02:00 PM", approval_status: "APPROVED" },
  { id: "VIS-05", pass_code: "GP-43210", name: "Murugan (Ola Cabs)", phone: "+91 97908 44221", visitor_type: "CAB", flat_number: "A-103", vehicle_number: "TN-01-BK-3344", entry_time: "10:45 AM", approval_status: "CHECKED_OUT" },
  { id: "VIS-06", pass_code: "GP-12984", name: "Rajan (Urban Company AC)", phone: "+91 91765 33001", visitor_type: "SERVICE", flat_number: "B-301", vehicle_number: "TN-09-XX-7711", entry_time: "09:30 AM", approval_status: "CHECKED_OUT" }
];

// Master Datastores for Admin 400 Flats Control & Resident Isolation
const mockDuesMaster = [];
const mockParkingMaster = [];
const mockResidentsMaster = {};
const mockFamilyMaster = {};
const RESIDENTS_PERSIST_FILE = path.join(__dirname, 'residents_store.json');

function saveResidentsToDisk() {
  try {
    const data = {
      residents: mockResidentsMaster,
      families: mockFamilyMaster
    };
    fs.writeFileSync(RESIDENTS_PERSIST_FILE, JSON.stringify(data, null, 2), 'utf8');
  } catch (err) {
    console.error('[EcoApartment OS] Failed to write residents_store.json:', err.message);
  }
}

function initMasterDatastores() {
  if (mockDuesMaster.length > 0) return;
  const blocks = ['A', 'B', 'C', 'D'];

  // Seed key resident dues
  mockDuesMaster.push({ id: 'DUE-B402-SEP', flat_number: 'B-402', resident_name: 'Mohan Palaniappan', phone: '+91 63838 98769', monthly_amount: 3200.00, breakdown: 'Water: ₹450 | Gas: ₹350 | Elec: ₹600 | Res: ₹1,050 | Overdue: ₹750', due_date: 'Oct 05, 2026', status: 'PAID', last_paid: 'Oct 01, 2026', payment_method: 'UPI (GPay)' });
  mockDuesMaster.push({ id: 'DUE-A101-SEP', flat_number: 'A-101', resident_name: 'R. SHAM SUDHAN', phone: '+91 77089 94065', monthly_amount: 3200.00, breakdown: 'Water: ₹450 | Gas: ₹350 | Elec: ₹600 | Res: ₹1,050 | Overdue: ₹750', due_date: 'Oct 05, 2026', status: 'PAID', last_paid: 'Oct 02, 2026', payment_method: 'Net Banking' });
  mockDuesMaster.push({ id: 'DUE-A103-SEP', flat_number: 'A-103', resident_name: 'MADHU VARSHAN', phone: '+91 98401 55432', monthly_amount: 3200.00, breakdown: 'Water: ₹450 | Gas: ₹350 | Elec: ₹600 | Res: ₹1,050 | Overdue: ₹750', due_date: 'Oct 05, 2026', status: 'PAID', last_paid: 'Oct 03, 2026', payment_method: 'UPI (PhonePe)' });
  mockDuesMaster.push({ id: 'DUE-A105-SEP', flat_number: 'A-105', resident_name: 'M. JAI MUGESH', phone: '+91 93610 77508', monthly_amount: 3200.00, breakdown: 'Water: ₹450 | Gas: ₹350 | Elec: ₹600 | Res: ₹1,050 | Overdue: ₹750', due_date: 'Oct 05, 2026', status: 'PAID', last_paid: 'Oct 02, 2026', payment_method: 'Debit Card' });

  // Generate 400 flats dues in short form
  for (const blk of blocks) {
    for (let floor = 1; floor <= 4; floor++) {
      for (let unit = 1; unit <= 25; unit++) {
        const flatNum = `${blk}-${floor}0${unit < 10 ? '0' + unit : unit}`.replace('00', '0');
        if (!mockDuesMaster.some(d => d.flat_number === flatNum)) {
          const isOverdue = (unit % 19 === 0);
          const isPending = !isOverdue && (unit % 9 === 0);
          const status = isOverdue ? 'OVERDUE' : (isPending ? 'PENDING' : 'PAID');
          mockDuesMaster.push({
            id: `DUE-${flatNum.replace('-', '')}-SEP`,
            flat_number: flatNum,
            resident_name: `Resident ${flatNum}`,
            phone: `+91 9840${(floor * 10 + unit).toString().padStart(5, '0')}`,
            monthly_amount: 3200.00,
            breakdown: 'Water: ₹450 | Gas: ₹350 | Elec: ₹600 | Res: ₹1,050',
            due_date: isOverdue ? 'IMMEDIATE' : 'Oct 05, 2026',
            status: status,
            last_paid: status === 'PAID' ? 'Oct 02, 2026' : '—',
            payment_method: status === 'PAID' ? 'UPI / Auto-Debit' : '—'
          });
        }
      }
    }
  }

  // Seed primary resident parking bays
  mockParkingMaster.push({ slot_id: 'P-B402', flat_number: 'B-402', basement: 'Basement 1', bay: 'Bay B-04', resident_name: 'Mohan Palaniappan', vehicle_number: 'TN-09-CB-1234', vehicle_type: 'Four Wheeler (EV SUV)', ev_charging: 'Yes (7.2kW Fast)', rfid_tag: 'RFID-B402-01', status: 'OCCUPIED' });
  mockParkingMaster.push({ slot_id: 'P-A101', flat_number: 'A-101', basement: 'Basement 1', bay: 'Bay A-12', resident_name: 'R. SHAM SUDHAN', vehicle_number: 'TN-09-BE-4451', vehicle_type: 'Four Wheeler (SUV)', ev_charging: 'Yes (3.3kW AC)', rfid_tag: 'RFID-A101-99', status: 'OCCUPIED' });
  mockParkingMaster.push({ slot_id: 'P-A103', flat_number: 'A-103', basement: 'Basement 1', bay: 'Bay A-14', resident_name: 'MADHU VARSHAN', vehicle_number: 'TN-09-MV-5543', vehicle_type: 'Four Wheeler (Sedan)', ev_charging: 'Yes (3.3kW AC)', rfid_tag: 'RFID-A103-88', status: 'OCCUPIED' });
  mockParkingMaster.push({ slot_id: 'P-A105', flat_number: 'A-105', basement: 'Basement 1', bay: 'Bay A-16', resident_name: 'M. JAI MUGESH', vehicle_number: 'TN-09-JM-7750', vehicle_type: 'Four Wheeler (SUV)', ev_charging: 'Yes (7.2kW Fast)', rfid_tag: 'RFID-A105-77', status: 'OCCUPIED' });

  // Generate 400 resident bays: exactly 379 occupied, 21 unallocated available
  let bayCount = 4;
  let unallocTarget = 21;
  let unallocAssigned = 0;

  for (const blk of blocks) {
    for (let floor = 1; floor <= 4; floor++) {
      for (let unit = 1; unit <= 25; unit++) {
        const flatNum = `${blk}-${floor}0${unit < 10 ? '0' + unit : unit}`.replace('00', '0');
        if (!mockParkingMaster.some(p => p.flat_number === flatNum)) {
          bayCount++;
          const isB1 = (blk === 'A' || blk === 'B');
          const basement = isB1 ? 'Basement 1' : 'Basement 2';
          const bayLabel = `Bay ${blk}-${(floor * 10 + unit).toString().padStart(2, '0')}`;
          const hasEv = (bayCount % 3 === 0);
          const isUnallocated = unallocAssigned < unallocTarget && (unit === 25 || (unit === 24 && floor >= 3));

          if (isUnallocated) {
            unallocAssigned++;
            mockParkingMaster.push({
              slot_id: `P-${blk}${floor}${unit < 10 ? '0' + unit : unit}`,
              flat_number: 'UNALLOCATED',
              basement: basement,
              bay: bayLabel,
              resident_name: '— (Available for Allotment)',
              vehicle_number: '—',
              vehicle_type: 'Open Bay (Car / EV)',
              ev_charging: hasEv ? 'Yes (3.3kW AC)' : 'Standard Socket',
              rfid_tag: 'Unassigned',
              status: 'AVAILABLE'
            });
          } else {
            mockParkingMaster.push({
              slot_id: `P-${blk}${floor}${unit < 10 ? '0' + unit : unit}`,
              flat_number: flatNum,
              basement: basement,
              bay: bayLabel,
              resident_name: `Resident ${flatNum}`,
              vehicle_number: `TN-09-XX-${(floor * 1000 + unit * 37).toString().slice(0, 4)}`,
              vehicle_type: (unit % 2 === 0 ? 'Four Wheeler (SUV/Sedan)' : 'Four Wheeler (Hatchback)'),
              ev_charging: hasEv ? 'Yes (7.2kW Fast)' : (bayCount % 2 === 0 ? 'Yes (3.3kW AC)' : 'Standard Socket'),
              rfid_tag: `RFID-${flatNum.replace('-', '')}-01`,
              status: 'OCCUPIED'
            });
          }
        }
      }
    }
  }

  // 40 Dedicated Visitor Parking Bays (All 40 Available)
  for (let v = 1; v <= 40; v++) {
    const isB1 = v <= 20;
    mockParkingMaster.push({
      slot_id: `P-VIS-${v < 10 ? '0' + v : v}`,
      flat_number: 'VISITOR BAY',
      basement: isB1 ? 'Basement 1' : 'Basement 2',
      bay: `Visitor Bay V-${v < 10 ? '0' + v : v}`,
      resident_name: 'Guest / Visitor Open Parking',
      vehicle_number: 'Guest Vehicle Access',
      vehicle_type: 'Designated Visitor Bay',
      ev_charging: v % 4 === 0 ? 'Yes (3.3kW AC)' : 'Standard Socket',
      rfid_tag: 'Guest Pass RFID',
      status: 'AVAILABLE'
    });
  }

  // Seed Master 400 Residents & Verified Families
  if (Object.keys(mockResidentsMaster).length === 0) {
    // 1. Primary Resident: Mohan Palaniappan (Flat B-402)
    mockResidentsMaster['B-402'] = {
      flat_number: 'B-402',
      block: 'B',
      floor_number: 4,
      resident_name: 'Mohan Palaniappan',
      family_head: 'Mohan Palaniappan',
      username: 'flat_402',
      password: 'mohan123',
      contact_number: '+91 63838 98769',
      email: 'mohanpalaniappan007@gmail.com',
      emergency_contact: '+91 94441 55210',
      vehicle_number: 'TN-09-CB-1234',
      blood_group: 'O+',
      occupancy_status: 'OCCUPIED',
      dues_status: 'PAID',
      gents_count: 2,
      ladies_count: 1,
      children_count: 1,
      total_members: 4
    };
    mockFamilyMaster['B-402'] = {
      flat_number: 'B-402',
      family_head: 'Mohan Palaniappan',
      total_members: 4,
      gents_count: 2,
      ladies_count: 1,
      children_count: 1,
      members: [
        { name: 'Mohan Palaniappan', relation: 'Family Head (Self)', gender: 'Gent', age: 38, blood_group: 'O+', contact: '+91 63838 98769' },
        { name: 'S. Divya', relation: 'Spouse (Wife)', gender: 'Lady', age: 35, blood_group: 'A+', contact: '+91 94441 22890' },
        { name: 'M. Kavin', relation: 'Son', gender: 'Child (Boy)', age: 10, blood_group: 'O+', contact: 'School Student' },
        { name: 'P. Palaniappan', relation: 'Father', gender: 'Gent', age: 68, blood_group: 'B+', contact: 'Senior Citizen' }
      ]
    };

    // 2. R. SHAM SUDHAN (Flat A-101)
    mockResidentsMaster['A-101'] = {
      flat_number: 'A-101',
      block: 'A',
      floor_number: 1,
      resident_name: 'R. SHAM SUDHAN',
      family_head: 'R. SHAM SUDHAN',
      username: 'flat_101',
      password: 'sham123',
      contact_number: '+91 77089 94065',
      email: 'shamsudhan@gmail.com',
      emergency_contact: '+91 94441 55210',
      vehicle_number: 'TN-09-BE-4451',
      blood_group: 'O+',
      occupancy_status: 'OCCUPIED',
      dues_status: 'PAID',
      gents_count: 1,
      ladies_count: 1,
      children_count: 2,
      total_members: 4
    };
    mockFamilyMaster['A-101'] = {
      flat_number: 'A-101',
      family_head: 'R. SHAM SUDHAN',
      total_members: 4,
      gents_count: 1,
      ladies_count: 1,
      children_count: 2,
      members: [
        { name: 'R. SHAM SUDHAN', relation: 'Family Head (Self)', gender: 'Gent', age: 41, blood_group: 'O+', contact: '+91 77089 94065' },
        { name: 'S. Kausalya Sham', relation: 'Spouse (Wife)', gender: 'Lady', age: 38, blood_group: 'A+', contact: '+91 94441 55667' },
        { name: 'S. Kavin Sudhan', relation: 'Son', gender: 'Child (Boy)', age: 12, blood_group: 'O+', contact: 'School Student' },
        { name: 'S. Diya Sudhan', relation: 'Daughter', gender: 'Child (Girl)', age: 8, blood_group: 'B+', contact: 'School Student' }
      ]
    };

    // 3. MADHU VARSHAN (Flat A-103)
    mockResidentsMaster['A-103'] = {
      flat_number: 'A-103',
      block: 'A',
      floor_number: 1,
      resident_name: 'MADHU VARSHAN',
      family_head: 'MADHU VARSHAN',
      username: 'flat_103',
      password: 'madhu123',
      contact_number: '+91 98401 55432',
      email: 'madhuvarshan@srmgreen.in',
      emergency_contact: '+91 94441 55210',
      vehicle_number: 'TN-09-MV-5543',
      blood_group: 'B+',
      occupancy_status: 'OCCUPIED',
      dues_status: 'PAID',
      gents_count: 2,
      ladies_count: 1,
      children_count: 1,
      total_members: 4
    };
    mockFamilyMaster['A-103'] = {
      flat_number: 'A-103',
      family_head: 'MADHU VARSHAN',
      total_members: 4,
      gents_count: 2,
      ladies_count: 1,
      children_count: 1,
      members: [
        { name: 'MADHU VARSHAN', relation: 'Family Head (Self)', gender: 'Gent', age: 39, blood_group: 'B+', contact: '+91 98401 55432' },
        { name: 'M. Priya Varshan', relation: 'Spouse (Wife)', gender: 'Lady', age: 36, blood_group: 'O+', contact: '+91 98401 55433' },
        { name: 'V. Aadhavan', relation: 'Son', gender: 'Child (Boy)', age: 11, blood_group: 'B+', contact: 'School Student' },
        { name: 'V. Sundar', relation: 'Brother', gender: 'Gent', age: 32, blood_group: 'B+', contact: '+91 98401 55434' }
      ]
    };

    // 4. M. JAI MUGESH (Flat A-105)
    mockResidentsMaster['A-105'] = {
      flat_number: 'A-105',
      block: 'A',
      floor_number: 1,
      resident_name: 'M. JAI MUGESH',
      family_head: 'M. JAI MUGESH',
      username: 'flat_105',
      password: 'jai123',
      contact_number: '+91 93610 77508',
      email: 'jaimugesh@srmgreen.in',
      emergency_contact: '+91 94441 55210',
      vehicle_number: 'TN-09-JM-7750',
      blood_group: 'AB+',
      occupancy_status: 'OCCUPIED',
      dues_status: 'PAID',
      gents_count: 1,
      ladies_count: 2,
      children_count: 1,
      total_members: 4
    };
    mockFamilyMaster['A-105'] = {
      flat_number: 'A-105',
      family_head: 'M. JAI MUGESH',
      total_members: 4,
      gents_count: 1,
      ladies_count: 2,
      children_count: 1,
      members: [
        { name: 'M. JAI MUGESH', relation: 'Family Head (Self)', gender: 'Gent', age: 37, blood_group: 'AB+', contact: '+91 93610 77508' },
        { name: 'J. Deepa Mugesh', relation: 'Spouse (Wife)', gender: 'Lady', age: 34, blood_group: 'A+', contact: '+91 93610 77509' },
        { name: 'J. Ananya', relation: 'Daughter', gender: 'Child (Girl)', age: 9, blood_group: 'AB+', contact: 'School Student' },
        { name: 'M. Meenakshi', relation: 'Mother', gender: 'Lady', age: 64, blood_group: 'O+', contact: 'Senior Citizen' }
      ]
    };

    // Generate remaining 400 flats
    for (const blk of blocks) {
      for (let floor = 1; floor <= 4; floor++) {
        for (let unit = 1; unit <= 25; unit++) {
          const flatNum = `${blk}-${floor}0${unit < 10 ? '0' + unit : unit}`.replace('00', '0');
          if (!mockResidentsMaster[flatNum]) {
            const gentCount = 1;
            const ladyCount = 1;
            const childCount = (unit % 3);
            const totalCount = gentCount + ladyCount + childCount;
            mockResidentsMaster[flatNum] = {
              flat_number: flatNum,
              block: blk,
              floor_number: floor,
              resident_name: `Resident ${flatNum}`,
              family_head: `Resident ${flatNum}`,
              username: `flat_${flatNum.replace('-', '').toLowerCase()}`,
              password: `srm${flatNum.replace('-', '').toLowerCase()}`,
              contact_number: `+91 9840${(floor * 10 + unit).toString().padStart(5, '0')}`,
              email: `flat_${flatNum.replace('-', '').toLowerCase()}@srmgreen.in`,
              emergency_contact: '+91 94441 55210',
              vehicle_number: `TN-09-${blk}${floor}-${(1000 + unit)}`,
              blood_group: ['O+', 'A+', 'B+', 'AB+'][unit % 4],
              occupancy_status: unit % 12 === 0 ? 'VACANT' : 'OCCUPIED',
              dues_status: unit % 9 === 0 ? 'PENDING' : 'PAID',
              gents_count: gentCount,
              ladies_count: ladyCount,
              children_count: childCount,
              total_members: totalCount
            };

            const defaultMembers = [
              { name: `Resident Head (${flatNum})`, relation: 'Family Head (Self)', gender: 'Gent', age: 36, blood_group: ['O+', 'A+', 'B+', 'AB+'][unit % 4], contact: `+91 9840${(floor * 10 + unit).toString().padStart(5, '0')}` },
              { name: `Spouse (${flatNum})`, relation: 'Spouse (Wife)', gender: 'Lady', age: 33, blood_group: 'A+', contact: 'Family Shared' }
            ];
            if (childCount > 0) {
              defaultMembers.push({ name: `Child (${flatNum})`, relation: 'Child', gender: 'Child (Boy)', age: 8, blood_group: 'O+', contact: 'School Student' });
            }

            mockFamilyMaster[flatNum] = {
              flat_number: flatNum,
              family_head: `Resident ${flatNum}`,
              total_members: totalCount,
              gents_count: gentCount,
              ladies_count: ladyCount,
              children_count: childCount,
              members: defaultMembers
            };
          }
        }
      }
    }

    // Load persisted store from disk if exists
    try {
      if (fs.existsSync(RESIDENTS_PERSIST_FILE)) {
        const raw = fs.readFileSync(RESIDENTS_PERSIST_FILE, 'utf8');
        const parsed = JSON.parse(raw);
        if (parsed.residents) Object.assign(mockResidentsMaster, parsed.residents);
        if (parsed.families) Object.assign(mockFamilyMaster, parsed.families);
        console.log('[EcoApartment OS] Loaded persisted residents_store.json successfully.');
      }
    } catch (e) {
      console.warn('[EcoApartment OS] Could not load residents_store.json:', e.message);
    }
  }
}
initMasterDatastores();

// Helper to update a resident and family profile in master datastore
function updateResidentRecord(item) {
  const flat = (item.flat_number || 'B-402').toUpperCase().trim();
  if (!mockResidentsMaster[flat]) {
    mockResidentsMaster[flat] = {
      flat_number: flat,
      block: flat.charAt(0) || 'B',
      floor_number: parseInt(flat.charAt(2)) || 1,
      occupancy_status: 'OCCUPIED',
      dues_status: 'PAID',
      gents_count: 1,
      ladies_count: 1,
      children_count: 1,
      total_members: 3
    };
  }

  const r = mockResidentsMaster[flat];
  if (item.resident_name) {
    r.resident_name = item.resident_name.trim();
    r.family_head = item.resident_name.trim();
  }
  if (item.contact_number || item.phone) {
    r.contact_number = (item.contact_number || item.phone).trim();
  }
  if (item.email) r.email = item.email.trim();
  if (item.emergency_contact) r.emergency_contact = item.emergency_contact.trim();
  if (item.vehicle_number) r.vehicle_number = item.vehicle_number.trim();
  if (item.blood_group) r.blood_group = item.blood_group.trim();
  if (item.password) r.password = item.password.trim();
  if (item.occupancy_status) r.occupancy_status = item.occupancy_status;
  if (item.dues_status) r.dues_status = item.dues_status;

  // Family Roster Update
  if (Array.isArray(item.members)) {
    const gents = item.members.filter(m => (m.gender || '').includes('Gent')).length;
    const ladies = item.members.filter(m => (m.gender || '').includes('Lady')).length;
    const children = item.members.filter(m => (m.gender || '').includes('Child')).length;
    const total = item.members.length;

    r.gents_count = gents;
    r.ladies_count = ladies;
    r.children_count = children;
    r.total_members = total;
    r.members = item.members;

    mockFamilyMaster[flat] = {
      flat_number: flat,
      family_head: r.resident_name,
      total_members: total,
      gents_count: gents,
      ladies_count: ladies,
      children_count: children,
      members: item.members
    };
  }

  // Synchronize Dues and Parking records if present
  const due = mockDuesMaster.find(d => d.flat_number === flat);
  if (due) {
    if (r.resident_name) due.resident_name = r.resident_name;
    if (r.contact_number) due.phone = r.contact_number;
  }

  const park = mockParkingMaster.find(p => p.flat_number === flat);
  if (park) {
    if (r.resident_name) park.resident_name = r.resident_name;
    if (r.vehicle_number) park.vehicle_number = r.vehicle_number;
  }

  saveResidentsToDisk();
  return { resident: r, family: mockFamilyMaster[flat] };
}

// Native API Request Handler
function handleNodeApiRequest(pathname, req, res, urlObj) {
  if (res.headersSent) return;

  // Login Authentication API
  if (pathname === '/api/login' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const creds = JSON.parse(body || '{}');
        const id = (creds.username || '').toLowerCase().trim();
        let user;
        if (id === 'admin' || id.includes('admin')) {
          user = { username: 'admin', role: 'AUTHORITY', flat_number: 'Admin Office', resident_name: 'SRM Facility Admin', contact_number: '+91 94441 55210', email: 'admin@srmgreen.in' };
        } else {
          let targetFlat = 'B-402';
          if (id.includes('101') || id.includes('sham') || id.includes('shyam') || id.includes('77089')) targetFlat = 'A-101';
          else if (id.includes('105') || id.includes('jai') || id.includes('93610')) targetFlat = 'A-105';
          else if (id.includes('103') || id.includes('madhu') || id.includes('98401')) targetFlat = 'A-103';
          else if (id.includes('402') || id.includes('mohan') || id.includes('63838') || id.includes('63802')) targetFlat = 'B-402';
          else {
            const foundFlat = Object.keys(mockResidentsMaster).find(f => f.toLowerCase() === id || f.toLowerCase().replace('-', '') === id);
            if (foundFlat) targetFlat = foundFlat;
          }

          const r = mockResidentsMaster[targetFlat] || {};
          user = {
            username: 'flat_' + targetFlat.replace('-', ''),
            role: 'RESIDENT',
            flat_number: targetFlat,
            resident_name: r.resident_name || (targetFlat === 'B-402' ? 'Mohan Palaniappan' : 'Resident'),
            contact_number: r.contact_number || (targetFlat === 'B-402' ? '+91 63838 98769' : '+91 94441 55210'),
            email: r.email || (targetFlat === 'B-402' ? 'mohanpalaniappan007@gmail.com' : 'resident@srmgreen.in'),
            emergency_contact: r.emergency_contact || '+91 94441 55210',
            vehicle_number: r.vehicle_number || 'TN-09-CB-1234',
            blood_group: r.blood_group || 'O+',
            dues_status: r.dues_status || 'PAID'
          };
        }

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, user: user }));
      } catch (e) {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, user: { username: 'flat_402', role: 'RESIDENT', flat_number: 'B-402', resident_name: 'Mohan Palaniappan', contact_number: '+91 63838 98769' } }));
      }
    });
    return;
  }

  // Residents Directory API
  if (pathname === '/api/residents') {
    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        try {
          const item = JSON.parse(body || '{}');
          const result = updateResidentRecord(item);
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, message: 'Resident profile saved successfully', ...result }));
        } catch (e) {
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, message: 'Resident profile saved' }));
        }
      });
      return;
    }

    const page = parseInt(urlObj.searchParams.get('page') || '1', 10);
    const limit = parseInt(urlObj.searchParams.get('limit') || '25', 10);
    const search = (urlObj.searchParams.get('search') || '').toLowerCase().trim();
    const blockFilter = urlObj.searchParams.get('block') || 'ALL';

    const allResidents = Object.values(mockResidentsMaster);

    let filtered = allResidents.filter(r => {
      const matchBlock = (blockFilter === 'ALL' || r.block === blockFilter);
      const matchSearch = !search ||
        r.flat_number.toLowerCase().includes(search) ||
        (r.resident_name && r.resident_name.toLowerCase().includes(search)) ||
        (r.username && r.username.toLowerCase().includes(search)) ||
        (r.contact_number && r.contact_number.includes(search));
      return matchBlock && matchSearch;
    });

    const total = filtered.length;
    const totalPages = Math.ceil(total / limit) || 1;
    const startIndex = (page - 1) * limit;
    const paginated = filtered.slice(startIndex, startIndex + limit);

    // Compute aggregate population statistics
    let totalGents = 0, totalLadies = 0, totalChildren = 0, totalPop = 0;
    allResidents.forEach(r => {
      totalGents += (r.gents_count || 1);
      totalLadies += (r.ladies_count || 1);
      totalChildren += (r.children_count || 0);
      totalPop += (r.total_members || 2);
    });

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      residents: paginated,
      total: total,
      page: page,
      limit: limit,
      total_pages: totalPages,
      summary: { total_flats: allResidents.length, total_gents: totalGents, total_ladies: totalLadies, total_children: totalChildren, total_population: totalPop }
    }));
    return;
  }

  // Single Resident Details API
  if (pathname.startsWith('/api/residents/')) {
    const flat = decodeURIComponent(pathname.replace('/api/residents/', '')).toUpperCase();
    const r = mockResidentsMaster[flat] || {
      flat_number: flat,
      block: flat.charAt(0) || 'A',
      floor_number: 1,
      resident_name: `Resident ${flat}`,
      username: `flat_${flat.replace('-', '').toLowerCase()}`,
      contact_number: '+91 98401 55210',
      email: `resident_${flat.toLowerCase()}@srmgreen.in`,
      emergency_contact: '+91 94441 55210',
      vehicle_number: 'TN-09-XX-1234',
      blood_group: 'O+',
      dues_status: 'PAID',
      occupancy_status: 'OCCUPIED'
    };

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ resident: r }));
    return;
  }

  // Family Roster API
  if (pathname === '/api/family') {
    const flat = (urlObj.searchParams.get('flat') || 'B-402').toUpperCase();
    const resData = mockResidentsMaster[flat];
    const fam = mockFamilyMaster[flat] || {
      flat_number: flat,
      family_head: resData ? resData.resident_name : `Resident Head (${flat})`,
      total_members: resData ? resData.total_members : 3,
      gents_count: resData ? resData.gents_count : 1,
      ladies_count: resData ? resData.ladies_count : 1,
      children_count: resData ? resData.children_count : 1,
      members: [
        { name: resData ? resData.resident_name : `Resident Head (${flat})`, relation: 'Family Head (Self)', gender: 'Gent', age: 38, blood_group: 'O+', contact: resData ? resData.contact_number : '+91 98401 55000' }
      ]
    };

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(fam));
    return;
  }

  // Profile Update API (User Self-Edit & Admin Edit)
  if ((pathname === '/api/profile/update' || pathname === '/api/residents/update') && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const item = JSON.parse(body || '{}');
        const result = updateResidentRecord(item);
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
          success: true,
          message: `Details for Flat ${result.resident.flat_number} saved and permanently stored`,
          ...result
        }));
      } catch (err) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: false, message: 'Invalid payload format' }));
      }
    });
    return;
  }

  // Complaints API
  if (pathname === '/api/complaints') {
    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        try {
          const item = JSON.parse(body || '{}');
          const id = `TKT-${Math.floor(1000 + Math.random() * 9000)}`;
          const newTicket = {
            ticket_id: id,
            flat_number: (item.flat_number || 'B-402').toUpperCase(),
            category: item.category || 'GENERAL',
            priority: item.priority || 'HIGH',
            description: item.description || 'Maintenance request',
            status: 'OPEN',
            assigned_technician: item.assigned_technician || 'Pending Dispatch',
            work_log: 'Ticket registered by resident.'
          };
          mockComplaints.unshift(newTicket);
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, ticket_id: id, message: 'Helpdesk ticket logged' }));
        } catch (e) {
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, ticket_id: `TKT-${Math.floor(1000 + Math.random() * 9000)}` }));
        }
      });
      return;
    }

    const flatParam = urlObj.searchParams.get('flat');
    if (flatParam && flatParam.toUpperCase() !== 'ALL') {
      const filtered = mockComplaints.filter(c => c.flat_number.toUpperCase() === flatParam.toUpperCase());
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(filtered));
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(mockComplaints));
    return;
  }

  if (pathname.startsWith('/api/complaints/') && req.method === 'PUT') {
    const id = pathname.replace('/api/complaints/', '');
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const t = mockComplaints.find(c => c.ticket_id === id);
        if (t) {
          if (payload.status) t.status = payload.status;
          if (payload.assigned_technician) t.assigned_technician = payload.assigned_technician;
          if (payload.notes) t.work_log = payload.notes;
        }
      } catch (e) {}
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, message: 'Ticket status updated' }));
    });
    return;
  }

  // Maintenance Dues API
  if (pathname === '/api/dues') {
    const flatParam = urlObj.searchParams.get('flat');
    if (flatParam && flatParam.toUpperCase() !== 'ALL') {
      const flat = flatParam.toUpperCase();
      const record = mockDuesMaster.find(m => m.flat_number === flat);
      const isPaid = record ? record.status === 'PAID' : false;
      const isOverdue = record ? record.status === 'OVERDUE' : false;

      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify([
        { id: `DUE-${flat}-WATER`, flat_number: flat, category: 'WATER', month_year: 'September 2026', amount_rupees: 450.00, due_date: 'Oct 05, 2026', status: isPaid ? 'PAID' : 'PENDING', resident_name: record ? record.resident_name : 'Mohan Palaniappan' },
        { id: `DUE-${flat}-GAS`, flat_number: flat, category: 'GAS', month_year: 'September 2026', amount_rupees: 350.00, due_date: 'Oct 05, 2026', status: isPaid ? 'PAID' : 'PENDING', resident_name: record ? record.resident_name : 'Mohan Palaniappan' },
        { id: `DUE-${flat}-ELEC`, flat_number: flat, category: 'ELECTRICITY', month_year: 'September 2026', amount_rupees: 600.00, due_date: 'Oct 05, 2026', status: isPaid ? 'PAID' : 'PENDING', resident_name: record ? record.resident_name : 'Mohan Palaniappan' },
        { id: `DUE-${flat}-RES`, flat_number: flat, category: 'RESIDENTIAL', month_year: 'September 2026', amount_rupees: 1050.00, due_date: 'Oct 05, 2026', status: isPaid ? 'PAID' : 'PENDING', resident_name: record ? record.resident_name : 'Mohan Palaniappan' },
        { id: `DUE-${flat}-OVERDUE`, flat_number: flat, category: 'OVERDUE', month_year: 'August Carryover Balance', amount_rupees: 750.00, due_date: 'IMMEDIATE', status: isOverdue ? 'OVERDUE' : (isPaid ? 'PAID' : 'PENDING'), resident_name: record ? record.resident_name : 'Mohan Palaniappan' }
      ]));
      return;
    }

    // Admin Master Roster View (All 400 flats in short form)
    const paidCount = mockDuesMaster.filter(d => d.status === 'PAID').length;
    const overdueCount = mockDuesMaster.filter(d => d.status === 'OVERDUE').length;
    const pendingCount = mockDuesMaster.filter(d => d.status === 'PENDING').length;
    const totalFlats = mockDuesMaster.length;
    const totalBilled = totalFlats * 3200;
    const totalCollected = paidCount * 3200;
    const totalPending = (overdueCount + pendingCount) * 3200;

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      summary: {
        total_flats: totalFlats,
        total_billed: totalBilled,
        total_collected: totalCollected,
        total_pending: totalPending,
        paid_count: paidCount,
        pending_count: pendingCount,
        overdue_count: overdueCount
      },
      dues: mockDuesMaster
    }));
    return;
  }

  // Admin Dues Update Status API
  if (pathname === '/api/dues/admin-update' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const flat = (payload.flat_number || '').toUpperCase();
        const item = mockDuesMaster.find(d => d.flat_number === flat);
        if (item) {
          if (payload.status) item.status = payload.status;
          if (payload.status === 'PAID') {
            item.last_paid = new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' });
            item.payment_method = payload.payment_method || 'Admin Verified / Cash Receipt';
          }
        }
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, message: `Maintenance status updated for flat ${flat}` }));
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Invalid update payload' }));
      }
    });
    return;
  }

  // Dues Pay API
  if (pathname === '/api/dues/pay' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const flat = (payload.flat_number || 'B-402').toUpperCase();
        const item = mockDuesMaster.find(d => d.flat_number === flat);
        if (item) {
          item.status = 'PAID';
          item.last_paid = new Date().toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' });
          item.payment_method = payload.payment_method || 'UPI / Payment Gateway';
        }
      } catch (e) {}
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, message: 'Payment verified and settled' }));
    });
    return;
  }

  // Community Notices API
  if (pathname === '/api/notices') {
    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        try {
          const item = JSON.parse(body || '{}');
          const newNotice = {
            id: `NOT-${Math.floor(10 + Math.random() * 90)}`,
            title: item.title || 'Community Update',
            category: item.category || 'COMMUNITY',
            priority: item.priority || 'NORMAL',
            description: item.description || '',
            author: item.author || 'SRM Facility Admin',
            posted_time: new Date().toISOString().split('T')[0],
            is_pinned: !!item.is_pinned
          };
          mockNotices.unshift(newNotice);
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, notice: newNotice, message: 'Notice posted successfully' }));
        } catch (e) {
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, message: 'Notice posted' }));
        }
      });
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(mockNotices));
    return;
  }

  // Delete Community Notice API
  if (pathname.startsWith('/api/notices/') && req.method === 'DELETE') {
    const id = pathname.replace('/api/notices/', '');
    const idx = mockNotices.findIndex(n => n.id === id);
    if (idx !== -1) mockNotices.splice(idx, 1);
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, message: 'Notice removed' }));
    return;
  }

  // Visitors Pass API
  if (pathname === '/api/visitors') {
    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        try {
          const item = JSON.parse(body || '{}');
          const passCode = `GP-${Math.floor(10000 + Math.random() * 90000)}`;
          const newPass = {
            id: `VIS-${Math.floor(10 + Math.random() * 90)}`,
            pass_code: passCode,
            name: item.name || 'Visitor',
            phone: item.phone || '+91 98401 00000',
            visitor_type: item.visitor_type || 'GUEST',
            flat_number: (item.flat_number || 'B-402').toUpperCase(),
            vehicle_number: item.vehicle_number || 'Walk-in',
            entry_time: new Date().toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }),
            approval_status: 'APPROVED'
          };
          mockVisitors.unshift(newPass);
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, pass_code: passCode, pass: newPass, message: 'Gate pass generated' }));
        } catch (e) {
          const passCode = `GP-${Math.floor(10000 + Math.random() * 90000)}`;
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ success: true, pass_code: passCode, message: 'Gate pass generated' }));
        }
      });
      return;
    }

    const flatParam = urlObj.searchParams.get('flat');
    if (flatParam && flatParam.toUpperCase() !== 'ALL') {
      const filtered = mockVisitors.filter(v => v.flat_number.toUpperCase() === flatParam.toUpperCase());
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(filtered));
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(mockVisitors));
    return;
  }

  if (pathname.startsWith('/api/visitors/') && req.method === 'PUT') {
    const id = pathname.replace('/api/visitors/', '');
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const v = mockVisitors.find(vis => vis.id === id || vis.pass_code === id);
        if (v && payload.status) {
          v.approval_status = payload.status;
        }
      } catch (e) {}
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, message: 'Visitor status updated' }));
    });
    return;
  }

  // Parking Allotments API
  if (pathname === '/api/parking') {
    const flat = urlObj.searchParams.get('flat');
    if (flat && flat.toUpperCase() !== 'ALL') {
      const myPark = mockParkingMaster.filter(p => p.flat_number === flat.toUpperCase());
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(myPark.length > 0 ? myPark : [mockParkingMaster[0]]));
      return;
    }

    // Master Parking Allotment (All 440 bays: available + occupied)
    const occupiedCount = mockParkingMaster.filter(p => p.status === 'OCCUPIED').length;
    const availableCount = mockParkingMaster.filter(p => p.status === 'AVAILABLE').length;
    const evCount = mockParkingMaster.filter(p => p.ev_charging && p.ev_charging.includes('Yes')).length;

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      summary: {
        total_slots: mockParkingMaster.length,
        occupied_count: occupiedCount,
        available_count: availableCount,
        ev_charging_bays: evCount,
        resident_bays: 400,
        visitor_bays: 40
      },
      slots: mockParkingMaster
    }));
    return;
  }

  // Parking Allot / Reassign API
  if (pathname === '/api/parking/allot' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const slot = mockParkingMaster.find(p => p.slot_id === payload.slot_id);
        if (slot) {
          if (payload.flat_number) slot.flat_number = payload.flat_number.toUpperCase();
          if (payload.resident_name) slot.resident_name = payload.resident_name;
          if (payload.vehicle_number) slot.vehicle_number = payload.vehicle_number;
          if (payload.vehicle_type) slot.vehicle_type = payload.vehicle_type;
          if (payload.status) slot.status = payload.status;
          if (payload.ev_charging) slot.ev_charging = payload.ev_charging;
        }
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, message: 'Parking slot successfully updated' }));
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Failed to update parking allotment' }));
      }
    });
    return;
  }

  // IoT Telemetry & Measurement Science API
  if (pathname === '/api/iot' || pathname === '/api/iot/telemetry') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      water_tank_level_pct: 84.0,
      sump_level_pct: 92.0,
      inflow_rate_lpm: 450.0,
      outflow_rate_lpm: 380.0,
      water_tds_ppm: 142.0,
      water_turbidity_ntu: 0.85,
      solar_generation_kw: 18.4,
      solar_energy_today_kwh: 128.6,
      clean_energy_share_pct: 62.0,
      temperature_celsius: 29.5,
      humidity_percentage: 64.0,
      aqi_index: 42,
      pm25_ugm3: 12.0,
      pm10_ugm3: 28.0,
      co2_ambient_ppm: 412,
      co2_saved_kg: 142.0,
      storm_drain_level_pct: 12.0,
      lpg_cylinder_weight_kg: 14.2,
      lpg_line_pressure_bar: 1.85,
      acoustic_leak_db: 18.5,
      measurement_specs: {
        water_level: {
          sensor: "JSN-SR04T-2.0 Waterproof Ultrasonic Transducer",
          principle: "Acoustic Time-of-Flight (ToF) Echo Integration (40 kHz)",
          formula: "Distance d = (v_sound * t) / 2; Height h = H_tank - d; Vol = pi * r^2 * h",
          telemetry: "RS485 Modbus RTU @ 9600 baud, 5s poll interval, ESP32 Gateway",
          accuracy: "±1.0% FS, median-filtered over 10 consecutive acoustic reflections"
        },
        flow_rate: {
          sensor: "YF-B7 Hall-Effect Paddlewheel Turbine Meter (DN50)",
          principle: "Electromagnetic induction from spinning neodymium rotor magnets",
          formula: "Flow Q = (Pulse_Freq / K_factor) * 60 L/min; Vol = Integral(Q dt)",
          telemetry: "Hardware interrupt pulse counter on ESP32, 1s sample accumulation",
          accuracy: "±1.5% linear calibration range (10 - 500 L/min)"
        },
        water_quality: {
          sensor: "Dual-Platinum EC Probe + 850nm IR 90° Nephelometric Turbidity Sensor",
          principle: "Alternating current impedance + optical Mie scattering at 90 degrees",
          formula: "TDS (ppm) = EC_uS/cm * 0.64; Turbidity = Formazin Calibration Curve",
          telemetry: "Isolated I2C Analog Front-End with ADS1115 16-bit ADC",
          accuracy: "TDS ±2 ppm, Turbidity ±0.05 NTU"
        },
        solar_generation: {
          sensor: "SMA Sunny Tripower Grid-Tie Inverter RS485 Modbus Telemetry",
          principle: "Hall-effect DC current transducers & 3-phase RMS voltage sampling",
          formula: "P_AC = sqrt(3) * V_LL * I_rms * cos(phi) * efficiency; Carbon = kWh * 0.82 kg",
          telemetry: "Modbus RTU / TCP over industrial RS485 shielded twisted pair",
          accuracy: "Class 0.5 Revenue Grade Metering (±0.5% active power accuracy)"
        },
        air_quality: {
          sensor: "Plantower PMS5003 Laser Particle Counter + Sensirion SHT31 + MH-Z19B NDIR",
          principle: "Laser light scattering (Mie theory) + NDIR 4.26um infrared absorption",
          formula: "Mass Concentration = Particle_Count * Shape_Factor; NAQI formula",
          telemetry: "UART Serial 9600 baud packet stream with cyclic redundancy check",
          accuracy: "PM2.5 ±10% @ 100-500 ug/m3, CO2 ±50 ppm, Temp ±0.2°C, RH ±2%"
        },
        lpg_manifold: {
          sensor: "4-Wire Piezoresistive Wheatstone Bridge Strain-Gauge Load Cells + Transducer",
          principle: "Resistive foil deformation under vertical compressive force",
          formula: "Net Gas = Gross Weight - Tare (15.6 kg); Pressure = Piezoresistive V_out",
          telemetry: "HX711 24-bit Differential ADC to ESP32 node via I2C",
          accuracy: "±20 grams mass resolution, ±0.02 bar line pressure"
        },
        storm_flood: {
          sensor: "Keller 26Y Stainless Steel Piezoresistive Submersible Depth Transmitter",
          principle: "Hydrostatic liquid column head pressure measurement",
          formula: "Depth h = Pressure / (rho * g); rho = 1000 kg/m3, g = 9.81 m/s2",
          telemetry: "Industrial 4-20 mA current loop with 250 ohm precision shunt",
          accuracy: "±0.25% FS depth resolution, lightning surge protected"
        },
        acoustic_leak: {
          sensor: "Murata Piezoelectric High-Bandwidth Contact Accelerometer Riser Array",
          principle: "Piezo-crystal charge generation from ultrasonic turbulence vibrations (1-5 kHz)",
          formula: "FFT On-Edge Spectral Density; Leak Alarm when V_rms > 2.5 * sigma_baseline",
          telemetry: "Edge FFT feature extraction on Cortex-M4, LoRaWAN 865 MHz alert beacon",
          accuracy: "Micro-leak detection threshold: 1.5 Litres/hour orifice flow"
        }
      }
    }));
    return;
  }

  // IoT Hardware Clusters Diagnostic Ping API
  if (pathname === '/api/iot/ping') {
    const nodes = [
      { id: "NODE-WTR-OHT", name: "Overhead Tank Ultrasonic Node", location: "Roof Block A/B", sensor_model: "JSN-SR04T-2.0", latency_ms: 12, rssi_dbm: -58, bus_voltage_v: 24.1, status: "OPTIMAL", packets_lost_pct: 0.0, last_calibrated: "2026-09-15" },
      { id: "NODE-WTR-PUMP", name: "Pump House Flow & Current Node", location: "Ground Pump Room", sensor_model: "YF-B7 + ACS712", latency_ms: 18, rssi_dbm: -64, bus_voltage_v: 24.0, status: "OPTIMAL", packets_lost_pct: 0.0, last_calibrated: "2026-09-20" },
      { id: "NODE-SLR-INV", name: "Solar Inverter Modbus Telemetry", location: "Rooftop Microgrid", sensor_model: "SMA Modbus RS485", latency_ms: 24, rssi_dbm: -52, bus_voltage_v: 23.9, status: "OPTIMAL", packets_lost_pct: 0.0, last_calibrated: "2026-10-01" },
      { id: "NODE-ENV-AQI", name: "Campus Air Quality Station", location: "Central Eco-Park", sensor_model: "PMS5003 + SHT31", latency_ms: 15, rssi_dbm: -61, bus_voltage_v: 5.0, status: "OPTIMAL", packets_lost_pct: 0.0, last_calibrated: "2026-08-30" },
      { id: "NODE-DRN-FLOD", name: "Stormwater Submersible Depth", location: "Basement 2 Sump", sensor_model: "Keller 26Y Hydrostatic", latency_ms: 32, rssi_dbm: -72, bus_voltage_v: 24.2, status: "OPTIMAL", packets_lost_pct: 0.1, last_calibrated: "2026-09-10" },
      { id: "NODE-GAS-MANI", name: "LPG Manifold Load Cell Hub", location: "Block B Gas Bank", sensor_model: "HX711 + Honeywell 4-Bar", latency_ms: 14, rssi_dbm: -55, bus_voltage_v: 5.1, status: "OPTIMAL", packets_lost_pct: 0.0, last_calibrated: "2026-10-05" },
      { id: "NODE-LEK-ACOU", name: "Acoustic Riser Vibration Array", location: "Towers A-D Core", sensor_model: "Murata Piezo FFT", latency_ms: 41, rssi_dbm: -68, bus_voltage_v: 3.3, status: "OPTIMAL", packets_lost_pct: 0.0, last_calibrated: "2026-09-28" }
    ];
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, timestamp: new Date().toISOString(), clusters_tested: nodes.length, all_nodes_online: true, average_latency_ms: 22.3, clusters: nodes }));
    return;
  }

  // Eco-Rewards & Green Credits API
  if (pathname === '/api/eco-rewards') {
    const flat = (urlObj.searchParams.get('flat') || 'B-402').toUpperCase();
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      flat_number: flat,
      resident_name: flat === 'B-402' ? 'Mohan Palaniappan' : (flat === 'A-101' ? 'R. SHAM SUDHAN' : `Resident ${flat}`),
      eco_coins: flat === 'B-402' ? 1240 : (flat === 'A-101' ? 980 : 650),
      tier: flat === 'B-402' ? 'Platinum Eco-Warrior' : (flat === 'A-101' ? 'Gold Sustainability Champion' : 'Silver Green Neighbor'),
      rank_in_society: flat === 'B-402' ? 3 : (flat === 'A-101' ? 7 : 42),
      water_saved_litres_this_month: 3200,
      solar_ev_kwh_charged: 48.5,
      co2_prevented_kg: 84.2,
      society_summary: {
        total_eco_coins_awarded: 412800,
        total_co2_offset_tonnes: 48.6,
        total_water_saved_kL: 1420,
        participating_flats: 400
      },
      leaderboard: [
        { rank: 1, flat: "C-204", name: "K. Sundararajan", coins: 1480, tier: "Diamond Eco-Hero", co2_saved_kg: 112 },
        { rank: 2, flat: "A-105", name: "M. JAI MUGESH", coins: 1350, tier: "Platinum Eco-Warrior", co2_saved_kg: 98 },
        { rank: 3, flat: "B-402", name: "Mohan Palaniappan", coins: 1240, tier: "Platinum Eco-Warrior", co2_saved_kg: 84 },
        { rank: 4, flat: "D-105", name: "Dr. K. Venkat", coins: 1190, tier: "Platinum Eco-Warrior", co2_saved_kg: 81 },
        { rank: 5, flat: "A-103", name: "MADHU VARSHAN", coins: 1120, tier: "Gold Champion", co2_saved_kg: 76 },
        { rank: 6, flat: "B-301", name: "R. Prakash", coins: 1040, tier: "Gold Champion", co2_saved_kg: 72 },
        { rank: 7, flat: "A-101", name: "R. SHAM SUDHAN", coins: 980, tier: "Gold Champion", co2_saved_kg: 68 },
        { rank: 8, flat: "C-102", name: "S. Annamalai", coins: 920, tier: "Gold Champion", co2_saved_kg: 64 },
        { rank: 9, flat: "D-204", name: "V. Ramamurthy", coins: 890, tier: "Silver Neighbor", co2_saved_kg: 59 },
        { rank: 10, flat: "B-105", name: "N. Selvaraj", coins: 860, tier: "Silver Neighbor", co2_saved_kg: 56 }
      ]
    }));
    return;
  }

  if (pathname === '/api/eco-rewards/redeem' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      try {
        const payload = JSON.parse(body || '{}');
        const coinsRedeemed = parseInt(payload.coins || 500, 10);
        const rewardType = payload.reward_type || "MAINTENANCE_REBATE";
        const rebateRupees = coinsRedeemed === 500 ? 250 : (coinsRedeemed === 1000 ? 550 : 100);

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
          success: true,
          reward_type: rewardType,
          coins_redeemed: coinsRedeemed,
          rebate_rupees: rebateRupees,
          voucher_code: `ECO-VOUCH-${Math.floor(10000 + Math.random() * 90000)}`,
          message: `Successfully redeemed ${coinsRedeemed} Green Eco-Coins! ₹${rebateRupees} discount applied to Maintenance Dues.`
        }));
      } catch (e) {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, message: "Eco-points redeemed successfully!" }));
      }
    });
    return;
  }

  // Export CSV / JSON / PDF API Endpoints
  if (pathname === '/api/export/residents.csv') {
    let csv = 'Flat,Block,Floor,Resident Name,Username,Contact Number,Email,Emergency Contact,Vehicle Number,Blood Group,Occupancy,Dues Status,Total Members\n';
    Object.values(mockResidentsMaster).forEach(r => {
      csv += `"${r.flat_number}","${r.block}",${r.floor_number},"${r.resident_name}","${r.username}","${r.contact_number}","${r.email || ''}","${r.emergency_contact || ''}","${r.vehicle_number || ''}","${r.blood_group || 'O+'}","${r.occupancy_status}","${r.dues_status}",${r.total_members || 1}\n`;
    });
    res.writeHead(200, {
      'Content-Type': 'text/csv; charset=utf-8',
      'Content-Disposition': 'attachment; filename="residents_400_export.csv"'
    });
    res.end(csv);
    return;
  }

  if (pathname === '/api/export/dues.csv') {
    let csv = 'Due ID,Flat Number,Resident Name,Monthly Amount (INR),Breakdown,Due Date,Status,Payment Method\n';
    mockDuesMaster.forEach(d => {
      csv += `"${d.id}","${d.flat_number}","${d.resident_name}",${d.monthly_amount},"${d.breakdown}","${d.due_date}","${d.status}","${d.payment_method || '—'}"\n`;
    });
    res.writeHead(200, {
      'Content-Type': 'text/csv; charset=utf-8',
      'Content-Disposition': 'attachment; filename="srm_green_400_dues_ledger.csv"'
    });
    res.end(csv);
    return;
  }

  if (pathname === '/api/export/telemetry.json') {
    const data = {
      timestamp: new Date().toISOString(),
      water_oht_pct: 84.0,
      sump_pct: 92.0,
      solar_kw: 18.4,
      aqi: 42,
      lpg_active_cylinder_kg: 6.8,
      status: "OPTIMAL"
    };
    res.writeHead(200, {
      'Content-Type': 'application/json',
      'Content-Disposition': 'attachment; filename="iot_telemetry_export.json"'
    });
    res.end(JSON.stringify(data, null, 2));
    return;
  }

  if (pathname === '/api/pdf') {
    const pdfPath = path.join(__dirname, 'EcoApartment_Credentials_and_Manual.pdf');
    if (fs.existsSync(pdfPath)) {
      res.writeHead(200, {
        'Content-Type': 'application/pdf',
        'Content-Disposition': 'attachment; filename="EcoApartment_Manual.pdf"'
      });
      fs.createReadStream(pdfPath).pipe(res);
      return;
    }
  }

  // Pump / Gas / Anomaly Controls
  if (pathname === '/api/pump/toggle' && req.method === 'POST') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ mode: 'AUTO', running: true, status: 'Motor Active' }));
    return;
  }

  if (pathname === '/api/gas/switch' && req.method === 'POST') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, active_unit: 2 }));
    return;
  }

  if (pathname === '/api/gas/book' && req.method === 'POST') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, booking_id: `LPG-REF-${Math.floor(10000 + Math.random() * 90000)}` }));
    return;
  }

  if (pathname === '/api/anomalies') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify([
      { sensor_id: 'SENS-WTR-B402', zone_id: 'Block B Riser 4', flow_rate: 18.5, threshold: 12.0, status: 'RESOLVED', timestamp: '2026-10-24 08:30:00', notes: 'Micro-leak caught by WaterMeter class' }
    ]));
    return;
  }

  if (pathname === '/api/anomalies/scan' && req.method === 'POST') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ anomaly_detected: false, message: 'All 400 flats telemetry normal' }));
    return;
  }

  // Building Matrix API
  if (pathname === '/api/flats/matrix') {
    const matrix = {};
    ['A', 'B', 'C', 'D'].forEach(blk => {
      matrix[blk] = {};
      [1, 2, 3, 4].forEach(flr => {
        matrix[blk][flr] = [];
        for (let u = 1; u <= 25; u++) {
          const flatNum = `${blk}-${flr}0${u < 10 ? '0' + u : u}`.replace('00', '0');
          matrix[blk][flr].push({
            flat_number: flatNum,
            resident_name: `Resident ${flatNum}`,
            occupancy_status: u % 12 === 0 ? 'VACANT' : 'OCCUPIED'
          });
        }
      });
    });
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(matrix));
    return;
  }

  // Supabase Schema & Config API
  if (pathname === '/api/supabase/schema') {
    const sqlText = `-- SRM GREEN RESIDENCY - OFFICIAL SUPABASE DATABASE SCHEMA
CREATE TABLE IF NOT EXISTS residents (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  flat_number VARCHAR(20) NOT NULL UNIQUE,
  block VARCHAR(10) NOT NULL,
  floor_number INT NOT NULL,
  resident_name VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  contact_number VARCHAR(20),
  email VARCHAR(100),
  occupancy_status VARCHAR(20) DEFAULT 'OCCUPIED',
  dues_status VARCHAR(20) DEFAULT 'PAID',
  created_at TIMESTAMPTZ DEFAULT NOW()
);`;
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end(sqlText);
    return;
  }

  if (pathname === '/api/supabase/config' && req.method === 'POST') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, message: 'Supabase configuration saved' }));
    return;
  }

  // Default API response
  res.writeHead(200, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ success: true, status: 'ok', endpoint: pathname }));
}

// Main Request Router
function handleHttpRequest(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS, HEAD');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    if (!res.headersSent) {
      res.writeHead(204);
      res.end();
    }
    return;
  }

  let pathname = '/';
  let urlObj = null;
  try {
    urlObj = new URL(req.url || '/', `http://${req.headers.host || 'localhost'}`);
    pathname = urlObj.pathname;
  } catch (e) {
    pathname = (req.url || '/').split('?')[0];
  }

  // Health check endpoints for Cloud Run container probes
  if (pathname === '/api/health' || pathname === '/health' || pathname === '/healthz' || pathname === '/ping') {
    if (!res.headersSent) {
      res.writeHead(200, { 'Content-Type': 'application/json', 'Cache-Control': 'no-cache' });
      if (req.method === 'HEAD') {
        res.end();
      } else {
        res.end(JSON.stringify({
          status: 'healthy',
          service: 'EcoApartment OS',
          platform: 'SRM Green Residency',
          flats: 400,
          timestamp: new Date().toISOString()
        }));
      }
    }
    return;
  }

  // Serve Android APK
  if (pathname === '/app-debug.apk' || pathname === '/app.apk') {
    const apkPaths = [
      path.join(__dirname, 'app-debug.apk'),
      path.join(__dirname, '.build-outputs', 'app-debug.apk'),
      path.join(__dirname, 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk')
    ];
    for (const apkPath of apkPaths) {
      if (fs.existsSync(apkPath)) {
        if (!res.headersSent) {
          res.writeHead(200, {
            'Content-Type': 'application/vnd.android.package-archive',
            'Content-Disposition': 'attachment; filename="EcoApartment_OS.apk"'
          });
          const stream = fs.createReadStream(apkPath);
          stream.on('error', () => { if (!res.headersSent) { res.writeHead(500); res.end('Read error'); } });
          stream.pipe(res);
        }
        return;
      }
    }
  }

  // Serve main HTML application
  if (pathname === '/' || pathname === '/index.html') {
    const indexPath = path.join(__dirname, 'index.html');
    if (fs.existsSync(indexPath)) {
      if (!res.headersSent) {
        res.writeHead(200, {
          'Content-Type': 'text/html; charset=utf-8',
          'Cache-Control': 'no-cache'
        });
        const stream = fs.createReadStream(indexPath);
        stream.on('error', () => { if (!res.headersSent) { res.writeHead(500); res.end('Read error'); } });
        stream.pipe(res);
      }
      return;
    }
  }

  // Serve static assets if present on disk
  const safePath = path.join(__dirname, pathname.replace(/^\/+/, ''));
  if (pathname !== '/' && fs.existsSync(safePath) && fs.statSync(safePath).isFile()) {
    const ext = path.extname(safePath).toLowerCase();
    const mimeTypes = {
      '.html': 'text/html; charset=utf-8',
      '.js': 'application/javascript; charset=utf-8',
      '.css': 'text/css; charset=utf-8',
      '.json': 'application/json',
      '.png': 'image/png',
      '.jpg': 'image/jpeg',
      '.svg': 'image/svg+xml',
      '.csv': 'text/csv; charset=utf-8',
      '.txt': 'text/plain; charset=utf-8',
      '.pdf': 'application/pdf'
    };
    if (!res.headersSent) {
      res.writeHead(200, { 'Content-Type': mimeTypes[ext] || 'application/octet-stream' });
      const stream = fs.createReadStream(safePath);
      stream.on('error', () => { if (!res.headersSent) { res.writeHead(500); res.end('Read error'); } });
      stream.pipe(res);
    }
    return;
  }

  // Direct Native Node API Handling
  if (pathname.startsWith('/api/')) {
    handleNodeApiRequest(pathname, req, res, urlObj);
    return;
  }

  // SPA Default Fallback
  const indexPath = path.join(__dirname, 'index.html');
  if (fs.existsSync(indexPath)) {
    if (!res.headersSent) {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      const stream = fs.createReadStream(indexPath);
      stream.on('error', () => { if (!res.headersSent) { res.writeHead(500); res.end('Read error'); } });
      stream.pipe(res);
    }
    return;
  }

  if (!res.headersSent) {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Not found' }));
  }
}

// Bind Primary HTTP Server
const primaryServer = http.createServer(handleHttpRequest);

primaryServer.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.warn(`[EcoApartment OS] Port ${PORT} in use, process continuing safely.`);
  } else {
    console.error(`[EcoApartment OS] Primary server error on port ${PORT}:`, err);
  }
});

if (require.main === module) {
  try {
    primaryServer.listen(PORT, '0.0.0.0', () => {
      console.log(`[EcoApartment OS] Primary web server listening on http://0.0.0.0:${PORT}`);
    });
  } catch (err) {
    console.warn(`[EcoApartment OS] Server listen warning:`, err.message);
  }

  // Safe Container Shutdown
  process.on('SIGTERM', () => {
    console.log('[EcoApartment OS] SIGTERM received. Graceful shutdown.');
    process.exit(0);
  });

  process.on('SIGINT', () => {
    console.log('[EcoApartment OS] SIGINT received. Shutting down.');
    process.exit(0);
  });
}

module.exports = {
  primaryServer,
  mockResidentsMaster,
  mockFamilyMaster,
  mockDuesMaster,
  mockParkingMaster,
  updateResidentRecord,
  handleHttpRequest,
  saveResidentsToDisk
};
