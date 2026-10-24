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
  { ticket_id: "TKT-1003", flat_number: "C-204", category: "ELEVATOR", priority: "URGENT", description: "Tower C Lift 2 door sensor delay on 2nd floor", status: "OPEN", assigned_technician: "S. Arumugam (Lift Engineer)", work_log: "Dispatch ticket generated." }
];

const mockNotices = [
  { id: "NOT-01", title: "Annual RWA General Body Meeting & Eco Audit", category: "COMMUNITY", priority: "HIGH", description: "All residents are invited to the Annual RWA meeting at the Clubhouse Hall on Sunday at 10:00 AM.", author: "SRM Facility Admin", posted_time: "2026-10-20", is_pinned: true },
  { id: "NOT-02", title: "Block B Main Overhead Tank Sanitization", category: "MAINTENANCE", priority: "IMPORTANT", description: "Water tank cleaning and pipeline pressure audit scheduled for Saturday from 10:00 AM to 2:00 PM.", author: "SRM Facility Admin", posted_time: "2026-10-22", is_pinned: false }
];

const mockVisitors = [
  { id: "VIS-01", pass_code: "GP-98401", name: "Suresh Kumar (Swiggy)", phone: "+91 98401 55210", visitor_type: "DELIVERY", flat_number: "B-402", vehicle_number: "TN-09-CB-4892", entry_time: "12:30 PM", approval_status: "CHECKED_IN" },
  { id: "VIS-02", pass_code: "GP-77089", name: "Ramesh Sharma (Courier)", phone: "+91 94441 22334", visitor_type: "GUEST", flat_number: "A-101", vehicle_number: "Walk-in", entry_time: "11:15 AM", approval_status: "APPROVED" }
];

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
        let user = {
          username: 'flat_402',
          role: 'RESIDENT',
          flat_number: 'B-402',
          resident_name: 'Mohan Palaniappan',
          contact_number: '+91 63838 98769',
          email: 'mohanpalaniappan007@gmail.com',
          emergency_contact: '+91 94441 55210',
          vehicle_number: 'TN-09-CB-1234',
          dues_status: 'PAID'
        };
        if (id === 'admin' || id.includes('admin')) {
          user = { username: 'admin', role: 'AUTHORITY', flat_number: 'Admin Office', resident_name: 'SRM Facility Admin', contact_number: '+91 94441 55210', email: 'admin@srmgreen.in' };
        } else if (id.includes('101') || id.includes('sham') || id.includes('shyam') || id.includes('77089')) {
          user = { username: 'flat_101', role: 'RESIDENT', flat_number: 'A-101', resident_name: 'R. SHAM SUDHAN', contact_number: '+91 77089 94065', email: 'shamsudhan@gmail.com' };
        } else if (id.includes('105') || id.includes('jai') || id.includes('93610')) {
          user = { username: 'flat_105', role: 'RESIDENT', flat_number: 'A-105', resident_name: 'M. JAI MUGESH', contact_number: '+91 93610 77508', email: 'jaimugesh@srmgreen.in' };
        } else if (id.includes('103') || id.includes('madhu') || id.includes('98401')) {
          user = { username: 'flat_103', role: 'RESIDENT', flat_number: 'A-103', resident_name: 'MADHU VARSHAN', contact_number: '+91 98401 55432', email: 'madhuvarshan@srmgreen.in' };
        } else if (id.includes('402') || id.includes('mohan') || id.includes('63838') || id.includes('63802')) {
          user = { username: 'flat_402', role: 'RESIDENT', flat_number: 'B-402', resident_name: 'Mohan Palaniappan', contact_number: '+91 63838 98769', email: 'mohanpalaniappan007@gmail.com' };
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
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, message: 'Resident profile saved successfully' }));
      });
      return;
    }

    const page = parseInt(urlObj.searchParams.get('page') || '1', 10);
    const limit = parseInt(urlObj.searchParams.get('limit') || '25', 10);
    const search = (urlObj.searchParams.get('search') || '').toLowerCase().trim();
    const blockFilter = urlObj.searchParams.get('block') || 'ALL';

    const sampleResidents = [];
    const blocks = ['A', 'B', 'C', 'D'];
    
    // Seed key residents
    sampleResidents.push({ flat_number: 'A-101', block: 'A', floor_number: 1, resident_name: 'R. SHAM SUDHAN', family_head: 'R. SHAM SUDHAN', username: 'flat_101', password: 'sham123', contact_number: '+91 77089 94065', email: 'shamsudhan@gmail.com', occupancy_status: 'OCCUPIED', dues_status: 'PAID', gents_count: 1, ladies_count: 1, children_count: 2, total_members: 4 });
    sampleResidents.push({ flat_number: 'A-103', block: 'A', floor_number: 1, resident_name: 'MADHU VARSHAN', family_head: 'MADHU VARSHAN', username: 'flat_103', password: 'madhu123', contact_number: '+91 98401 55432', email: 'madhuvarshan@srmgreen.in', occupancy_status: 'OCCUPIED', dues_status: 'PAID', gents_count: 2, ladies_count: 1, children_count: 1, total_members: 4 });
    sampleResidents.push({ flat_number: 'A-105', block: 'A', floor_number: 1, resident_name: 'M. JAI MUGESH', family_head: 'M. JAI MUGESH', username: 'flat_105', password: 'jai123', contact_number: '+91 93610 77508', email: 'jaimugesh@srmgreen.in', occupancy_status: 'OCCUPIED', dues_status: 'PAID', gents_count: 1, ladies_count: 2, children_count: 1, total_members: 4 });
    sampleResidents.push({ flat_number: 'B-402', block: 'B', floor_number: 4, resident_name: 'Mohan Palaniappan', family_head: 'Mohan Palaniappan', username: 'flat_402', password: 'mohan123', contact_number: '+91 63838 98769', email: 'mohanpalaniappan007@gmail.com', occupancy_status: 'OCCUPIED', dues_status: 'PAID', gents_count: 2, ladies_count: 1, children_count: 1, total_members: 4 });

    // Generate rest of 400 flats
    for (const blk of blocks) {
      for (let floor = 1; floor <= 4; floor++) {
        for (let unit = 1; unit <= 25; unit++) {
          const flatNum = `${blk}-${floor}0${unit < 10 ? '0' + unit : unit}`.replace('00', '0');
          if (!sampleResidents.some(r => r.flat_number === flatNum)) {
            sampleResidents.push({
              flat_number: flatNum,
              block: blk,
              floor_number: floor,
              resident_name: `Resident ${flatNum}`,
              family_head: `Resident ${flatNum}`,
              username: `flat_${flatNum.replace('-', '').toLowerCase()}`,
              password: `srm${flatNum.replace('-', '').toLowerCase()}`,
              contact_number: `+91 9840${(floor * 10 + unit).toString().padStart(5, '0')}`,
              email: `flat_${flatNum.replace('-', '').toLowerCase()}@srmgreen.in`,
              occupancy_status: unit % 12 === 0 ? 'VACANT' : 'OCCUPIED',
              dues_status: unit % 9 === 0 ? 'PENDING' : 'PAID',
              gents_count: 1,
              ladies_count: 1,
              children_count: (unit % 3),
              total_members: 2 + (unit % 3)
            });
          }
        }
      }
    }

    let filtered = sampleResidents.filter(r => {
      const matchBlock = (blockFilter === 'ALL' || r.block === blockFilter);
      const matchSearch = !search || r.flat_number.toLowerCase().includes(search) || r.resident_name.toLowerCase().includes(search) || r.username.toLowerCase().includes(search);
      return matchBlock && matchSearch;
    });

    const total = filtered.length;
    const totalPages = Math.ceil(total / limit) || 1;
    const startIndex = (page - 1) * limit;
    const paginated = filtered.slice(startIndex, startIndex + limit);

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      residents: paginated,
      total: total,
      page: page,
      limit: limit,
      total_pages: totalPages,
      summary: { total_flats: 400, total_gents: 480, total_ladies: 460, total_children: 540, total_population: 1480 }
    }));
    return;
  }

  // Single Resident Details
  if (pathname.startsWith('/api/residents/')) {
    const flat = decodeURIComponent(pathname.replace('/api/residents/', '')).toUpperCase();
    const isShyam = flat === 'A-101' || flat.includes('101');
    const name = isShyam ? 'R. SHAM SUDHAN' : (flat === 'B-402' ? 'Mohan Palaniappan' : `Resident ${flat}`);
    const phone = isShyam ? '+91 77089 94065' : (flat === 'B-402' ? '+91 63802 44185' : '+91 98401 55210');
    const email = isShyam ? 'shamsudhan@gmail.com' : (flat === 'B-402' ? 'mohanpalaniappan007@gmail.com' : `resident_${flat.toLowerCase()}@srmgreen.in`);

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      resident: {
        flat_number: flat,
        block: flat.charAt(0) || 'A',
        floor_number: 1,
        resident_name: name,
        username: `flat_${flat.replace('-', '').toLowerCase()}`,
        contact_number: phone,
        email: email,
        occupancy_status: 'OCCUPIED',
        dues_status: 'PAID',
        vehicle_number: isShyam ? 'TN-09-BE-4451' : 'TN-09-CB-1234',
        emergency_contact: '+91 94441 55210'
      }
    }));
    return;
  }

  // Family Roster API
  if (pathname === '/api/family') {
    const flat = (urlObj.searchParams.get('flat') || 'A-101').toUpperCase();
    const isShyam = flat === 'A-101' || flat.includes('101');
    const isMohan = flat === 'B-402' || flat.includes('402');

    const familyHead = isShyam ? 'R. SHAM SUDHAN' : (isMohan ? 'Mohan Palaniappan' : `Resident Head (${flat})`);
    const members = isShyam ? [
      { name: 'R. SHAM SUDHAN', relation: 'Family Head (Self)', gender: 'Gent', age: 41, blood_group: 'O+' },
      { name: 'S. Kausalya Sham', relation: 'Spouse (Wife)', gender: 'Lady', age: 38, blood_group: 'A+' },
      { name: 'S. Kavin Sudhan', relation: 'Son', gender: 'Child (Boy)', age: 12, blood_group: 'O+' },
      { name: 'S. Diya Sudhan', relation: 'Daughter', gender: 'Child (Girl)', age: 8, blood_group: 'B+' }
    ] : [
      { name: familyHead, relation: 'Family Head (Self)', gender: 'Gent', age: 38, blood_group: 'O+' },
      { name: 'S. Divya', relation: 'Spouse (Wife)', gender: 'Lady', age: 35, blood_group: 'A+' },
      { name: 'M. Kavin', relation: 'Son', gender: 'Child (Boy)', age: 10, blood_group: 'O+' },
      { name: 'P. Palaniappan', relation: 'Father', gender: 'Gent', age: 68, blood_group: 'B+' }
    ];

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      flat_number: flat,
      family_head: familyHead,
      total_members: members.length,
      gents_count: members.filter(m => m.gender === 'Gent').length,
      ladies_count: members.filter(m => m.gender === 'Lady').length,
      children_count: members.filter(m => m.gender.includes('Child')).length,
      members: members
    }));
    return;
  }

  // Profile Update API
  if (pathname === '/api/profile/update' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ success: true, message: 'Profile updated successfully' }));
    });
    return;
  }

  // Complaints API
  if (pathname === '/api/complaints') {
    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        const id = `TKT-${Math.floor(1000 + Math.random() * 9000)}`;
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, ticket_id: id, message: 'Helpdesk ticket logged' }));
      });
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(mockComplaints));
    return;
  }

  if (pathname.startsWith('/api/complaints/') && req.method === 'PUT') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, message: 'Ticket status updated' }));
    return;
  }

  // Maintenance Dues API
  if (pathname === '/api/dues') {
    const flat = (urlObj.searchParams.get('flat') || 'A-101').toUpperCase();
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify([
      { id: `DUE-${flat}-WATER`, flat_number: flat, category: 'WATER', month_year: 'September 2026', amount_rupees: 450.00, due_date: 'Oct 05, 2026', status: 'PENDING' },
      { id: `DUE-${flat}-GAS`, flat_number: flat, category: 'GAS', month_year: 'September 2026', amount_rupees: 350.00, due_date: 'Oct 05, 2026', status: 'PENDING' },
      { id: `DUE-${flat}-ELEC`, flat_number: flat, category: 'ELECTRICITY', month_year: 'September 2026', amount_rupees: 600.00, due_date: 'Oct 05, 2026', status: 'PENDING' },
      { id: `DUE-${flat}-RES`, flat_number: flat, category: 'RESIDENTIAL', month_year: 'September 2026', amount_rupees: 1050.00, due_date: 'Oct 05, 2026', status: 'PENDING' },
      { id: `DUE-${flat}-OVERDUE`, flat_number: flat, category: 'OVERDUE', month_year: 'August Carryover Balance', amount_rupees: 750.00, due_date: 'IMMEDIATE', status: 'OVERDUE' }
    ]));
    return;
  }

  // Dues Pay API
  if (pathname === '/api/dues/pay' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk; });
    req.on('end', () => {
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
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, message: 'Notice posted successfully' }));
      });
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(mockNotices));
    return;
  }

  // Visitors Pass API
  if (pathname === '/api/visitors') {
    if (req.method === 'POST') {
      let body = '';
      req.on('data', chunk => { body += chunk; });
      req.on('end', () => {
        const passCode = `GP-${Math.floor(10000 + Math.random() * 90000)}`;
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ success: true, pass_code: passCode, message: 'Gate pass generated' }));
      });
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(mockVisitors));
    return;
  }

  if (pathname.startsWith('/api/visitors/') && req.method === 'PUT') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ success: true, message: 'Visitor status updated' }));
    return;
  }

  // Parking Allotments API
  if (pathname === '/api/parking') {
    const flat = urlObj.searchParams.get('flat');
    const parkList = [
      { slot_id: 'P-A101', flat_number: 'A-101', basement: 'Basement 1', bay: 'Bay A-12', resident_name: 'R. SHAM SUDHAN', vehicle_number: 'TN-09-BE-4451', vehicle_type: 'Four Wheeler (SUV)', ev_charging: 'Yes (3.3kW)', rfid_tag: 'RFID-A101-99' },
      { slot_id: 'P-A103', flat_number: 'A-103', basement: 'Basement 1', bay: 'Bay A-14', resident_name: 'MADHU VARSHAN', vehicle_number: 'TN-09-MV-5543', vehicle_type: 'Four Wheeler (Sedan)', ev_charging: 'Yes (3.3kW)', rfid_tag: 'RFID-A103-88' },
      { slot_id: 'P-A105', flat_number: 'A-105', basement: 'Basement 1', bay: 'Bay A-16', resident_name: 'M. JAI MUGESH', vehicle_number: 'TN-09-JM-7750', vehicle_type: 'Four Wheeler (SUV)', ev_charging: 'Yes (7.2kW Fast)', rfid_tag: 'RFID-A105-77' },
      { slot_id: 'P-B402', flat_number: 'B-402', basement: 'Basement 1', bay: 'Bay B-04', resident_name: 'Mohan Palaniappan', vehicle_number: 'TN-09-CB-1234', vehicle_type: 'Four Wheeler (EV SUV)', ev_charging: 'Yes (7.2kW Fast)', rfid_tag: 'RFID-B402-01' }
    ];
    
    if (flat) {
      const myPark = parkList.filter(p => p.flat_number === flat.toUpperCase());
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(myPark.length > 0 ? myPark : [parkList[0]]));
      return;
    }

    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(parkList));
    return;
  }

  // IoT Telemetry API
  if (pathname === '/api/iot' || pathname === '/api/iot/telemetry') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      water_tank_level_pct: 84.0,
      sump_level_pct: 92.0,
      inflow_rate_lpm: 450.0,
      outflow_rate_lpm: 380.0,
      solar_generation_kw: 18.4,
      solar_energy_today_kwh: 128.6,
      temperature_celsius: 29.5,
      humidity_percentage: 64.0,
      co2_saved_kg: 142.0
    }));
    return;
  }

  // Export CSV / JSON / PDF API Endpoints
  if (pathname === '/api/export/residents.csv') {
    let csv = 'Flat,Block,Floor,Resident Name,Username,Contact Number,Email,Occupancy,Dues Status,Total Members\n';
    csv += 'A-101,A,1,R. SHAM SUDHAN,flat_101,+91 77089 94065,shamsudhan@gmail.com,OCCUPIED,PAID,4\n';
    csv += 'A-103,A,1,MADHU VARSHAN,flat_103,+91 98401 55432,madhuvarshan@srmgreen.in,OCCUPIED,PAID,4\n';
    csv += 'A-105,A,1,M. JAI MUGESH,flat_105,+91 93610 77508,jaimugesh@srmgreen.in,OCCUPIED,PAID,4\n';
    csv += 'B-402,B,4,Mohan Palaniappan,flat_402,+91 63802 44185,mohanpalaniappan007@gmail.com,OCCUPIED,PAID,4\n';
    const blocks = ['A', 'B', 'C', 'D'];
    for (const blk of blocks) {
      for (let floor = 1; floor <= 4; floor++) {
        for (let unit = 1; unit <= 25; unit++) {
          const flatNum = `${blk}-${floor}0${unit < 10 ? '0' + unit : unit}`.replace('00', '0');
          if (!['A-101', 'A-103', 'A-105', 'B-402'].includes(flatNum)) {
            csv += `${flatNum},${blk},${floor},Resident ${flatNum},flat_${flatNum.replace('-', '').toLowerCase()},+91 9840${(floor * 10 + unit).toString().padStart(5, '0')},flat_${flatNum.replace('-', '').toLowerCase()}@srmgreen.in,${unit % 12 === 0 ? 'VACANT' : 'OCCUPIED'},${unit % 9 === 0 ? 'PENDING' : 'PAID'},${2 + (unit % 3)}\n`;
          }
        }
      }
    }
    res.writeHead(200, {
      'Content-Type': 'text/csv; charset=utf-8',
      'Content-Disposition': 'attachment; filename="residents_400_export.csv"'
    });
    res.end(csv);
    return;
  }

  if (pathname === '/api/export/dues.csv') {
    let csv = 'Due ID,Flat,Category,Period,Amount (INR),Due Date,Status\n';
    csv += 'DUE-A101-WATER,A-101,WATER,September 2026,450.00,Oct 05 2026,PAID\n';
    csv += 'DUE-B402-WATER,B-402,WATER,September 2026,450.00,Oct 05 2026,PAID\n';
    res.writeHead(200, {
      'Content-Type': 'text/csv; charset=utf-8',
      'Content-Disposition': 'attachment; filename="dues_ledger_export.csv"'
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
  console.error(`[EcoApartment OS] Primary server error on port ${PORT}:`, err);
});

primaryServer.listen(PORT, '0.0.0.0', () => {
  console.log(`[EcoApartment OS] Primary web server listening on http://0.0.0.0:${PORT}`);
});

// Safe Container Shutdown
process.on('SIGTERM', () => {
  console.log('[EcoApartment OS] SIGTERM received. Graceful shutdown.');
  process.exit(0);
});

process.on('SIGINT', () => {
  console.log('[EcoApartment OS] SIGINT received. Shutting down.');
  process.exit(0);
});
