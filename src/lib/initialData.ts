import { Resident, Complaint, MaintenanceDue, CommunityNotice, Visitor, IoTTelemetry, GasCylinder, Anomaly, Technician, BlockName } from './types';

// Tamil / Indian Resident Name Generators
const FIRST_NAMES = [
  'Arun', 'Karthik', 'Suresh', 'Vijay', 'Ramesh', 'Priya', 'Deepa', 'Lakshmi', 'Anitha', 'Kavitha',
  'Saravanan', 'Ganesh', 'Prakash', 'Bala', 'Senthil', 'Divya', 'Sangeetha', 'Nandhini', 'Revathi', 'Meena',
  'Rajesh', 'Manoj', 'Dinesh', 'Venkatesh', 'Sundar', 'Aparna', 'Geetha', 'Radha', 'Sujatha', 'Uma',
  'Murali', 'Ashok', 'Sankar', 'Anand', 'Kumar', 'Vidya', 'Pooja', 'Nisha', 'Bhavani', 'Shanthi'
];

const LAST_NAMES = [
  'Kumar', 'Rajan', 'Subramanian', 'Natarajan', 'Viswanathan', 'Srinivasan', 'Krishnan', 'Murugan',
  'Palani', 'Raman', 'Swaminathan', 'Sundaram', 'Raghavan', 'Pandian', 'Ganesan', 'Iyengar',
  'Reddy', 'Rao', 'Nair', 'Pillai', 'Chettiar', 'Gounder', 'Naidu', 'Menon'
];

function generate400Residents(): Resident[] {
  const residents: Resident[] = [];
  const blocks: BlockName[] = ['A', 'B', 'C', 'D'];

  blocks.forEach((block) => {
    for (let floor = 1; floor <= 4; floor++) {
      for (let unit = 1; unit <= 25; unit++) {
        const unitFormatted = unit < 10 ? `0${unit}` : `${unit}`;
        const flatNum = `${block}-${floor}${unitFormatted}`;
        const uname = `flat_${block.toLowerCase()}${floor}${unitFormatted}`;

        // Preserve specific existing residents
        if (flatNum === 'B-402') {
          residents.push({
            flat_number: 'B-402',
            block: 'B',
            floor_number: 4,
            resident_name: 'Mohan Palaniappan',
            username: 'flat_402',
            password: 'mohan123',
            contact_number: '+91 6380244185',
            email: 'mohan.p@srmgreen.in',
            occupancy_status: 'OCCUPIED',
            dues_status: 'PAID',
            vehicle_number: 'TN-07-BP-4020'
          });
          continue;
        }

        if (flatNum === 'A-101') {
          residents.push({
            flat_number: 'A-101',
            block: 'A',
            floor_number: 1,
            resident_name: 'R. Sham Sudhan',
            username: 'flat_101',
            password: 'sham123',
            contact_number: '+91 9840123456',
            email: 'sham.sudhan@srmgreen.in',
            occupancy_status: 'OCCUPIED',
            dues_status: 'PAID',
            vehicle_number: 'TN-09-AX-1010'
          });
          continue;
        }

        // Random deterministically generated resident profile
        const seed = (block.charCodeAt(0) * 100) + (floor * 25) + unit;
        const fn = FIRST_NAMES[seed % FIRST_NAMES.length];
        const ln = LAST_NAMES[(seed * 3) % LAST_NAMES.length];
        const name = `${fn} ${ln}`;
        
        // 95% occupied, 4% vacant, 1% refurbish
        const occSeed = seed % 100;
        const occStatus = occSeed < 94 ? 'OCCUPIED' : (occSeed < 98 ? 'VACANT' : 'MAINTENANCE');
        
        // Dues
        const dueSeed = seed % 10;
        const dueStatus = dueSeed < 8 ? 'PAID' : (dueSeed < 9 ? 'PENDING' : 'OVERDUE');

        const phoneNum = `+91 9${String(100000000 + (seed * 1234567) % 899999999).slice(0, 9)}`;

        residents.push({
          flat_number: flatNum,
          block: block,
          floor_number: floor,
          resident_name: occStatus === 'VACANT' ? 'Unoccupied Unit' : name,
          username: uname,
          password: `pass${floor}${unitFormatted}`,
          contact_number: occStatus === 'VACANT' ? '—' : phoneNum,
          email: occStatus === 'VACANT' ? '—' : `${fn.toLowerCase()}.${ln.toLowerCase()}@srmgreen.in`,
          occupancy_status: occStatus,
          dues_status: occStatus === 'VACANT' ? 'PAID' : dueStatus,
          vehicle_number: occStatus === 'OCCUPIED' ? `TN-${(10 + seed % 80)}-${block}${floor}-${unitFormatted}` : '—'
        });
      }
    }
  });

  return residents;
}

export const INITIAL_RESIDENTS: Resident[] = generate400Residents();

export const INITIAL_COMPLAINTS: Complaint[] = [
  {
    ticket_id: 'TKT-104-PLU',
    flat_number: 'B-402',
    floor_number: 4,
    category: 'PLUMBING',
    priority: 'HIGH',
    description: 'Low water flow rate in kitchen faucet during morning overhead tank transition.',
    status: 'IN_PROGRESS',
    authority_notes: 'Technician Kathir assigned. Main line valve check scheduled.',
    timestamp: '2026-10-14 08:30',
    raised_by_name: 'Mohan Palaniappan',
    assigned_technician: 'R. Kathir (Plumbing Lead)',
    work_log: 'Inspected Riser B-4. Pressure regulator calibrated to 2.4 bar.'
  },
  {
    ticket_id: 'TKT-108-ELE',
    flat_number: 'A-101',
    floor_number: 1,
    category: 'ELECTRICAL',
    priority: 'URGENT',
    description: 'Main MCB tripped during solar inverter automated changeover.',
    status: 'ASSIGNED',
    authority_notes: 'Dispatched electrician. Phase balance to be verified.',
    timestamp: '2026-10-14 09:15',
    raised_by_name: 'R. Sham Sudhan',
    assigned_technician: 'V. Anbu (Lead Electrician)',
    work_log: 'Under diagnosis. Checking ATS panel switch contacts.'
  },
  {
    ticket_id: 'TKT-112-GAS',
    flat_number: 'C-215',
    floor_number: 2,
    category: 'GAS',
    priority: 'CRITICAL',
    description: 'Gas manifold pressure drop alert triggered on Flat C-215 branch line.',
    status: 'RESOLVED',
    authority_notes: 'Automated solenoid valve closed branch line. Joint re-sealed.',
    timestamp: '2026-10-13 16:45',
    raised_by_name: 'Ganesh Viswanathan',
    assigned_technician: 'S. Velu (Gas Specialist)',
    work_log: 'Pressure test holding stable at 1.8 bar. Leak cleared.'
  }
];

export const INITIAL_DUES: MaintenanceDue[] = [
  {
    id: 'DUE-2026-10-B402',
    flat_number: 'B-402',
    month_year: 'October 2026',
    amount_rupees: 3850.00,
    due_date: '2026-10-15',
    status: 'PAID',
    paid_date: '2026-10-02',
    payment_method: 'UPI (GPay)'
  },
  {
    id: 'DUE-2026-10-A101',
    flat_number: 'A-101',
    month_year: 'October 2026',
    amount_rupees: 3850.00,
    due_date: '2026-10-15',
    status: 'PAID',
    paid_date: '2026-10-04',
    payment_method: 'Net Banking (HDFC)'
  },
  {
    id: 'DUE-2026-10-C205',
    flat_number: 'C-205',
    month_year: 'October 2026',
    amount_rupees: 3850.00,
    due_date: '2026-10-15',
    status: 'PENDING'
  }
];

export const INITIAL_NOTICES: CommunityNotice[] = [
  {
    id: 'NOT-2026-08',
    title: 'Bi-Annual Overhead Water Tank Cleaning Schedule',
    description: 'Water supply will be regulated between 10:00 AM and 2:00 PM on Saturday for chemical scrubbing and UV sterilization of Tanks A & B.',
    category: 'MAINTENANCE',
    priority: 'URGENT',
    posted_time: 'Oct 12, 2026',
    posted_by: 'Facility Admin Office'
  },
  {
    id: 'NOT-2026-07',
    title: 'Diwali Festive Lightings & Fire Safety Directives',
    description: 'All residents are requested to ensure balcony diyas are kept away from window blinds. Dedicated fire safety wardens will patrol Blocks A–D.',
    category: 'COMMUNITY',
    priority: 'NORMAL',
    posted_time: 'Oct 10, 2026',
    posted_by: 'Resident Welfare Association'
  }
];

export const INITIAL_VISITORS: Visitor[] = [
  {
    id: 'VIS-901',
    flat_number: 'B-402',
    visitor_name: 'P. Vignesh',
    visitor_phone: '+91 9791098765',
    purpose: 'Amazon Logistics Courier Delivery',
    check_in_time: '2026-10-14 10:45',
    status: 'CHECKED_IN',
    pass_code: 'GP-402-8821'
  }
];

export const INITIAL_TECHNICIANS: Technician[] = [
  { id: 'TCH-01', name: 'R. Kathir', specialization: 'Plumbing & Hydraulic Systems', phone: '+91 9884012345', status: 'ON_CALL', active_tickets: 1 },
  { id: 'TCH-02', name: 'V. Anbu', specialization: 'Electrical & Solar Inverters', phone: '+91 9884054321', status: 'ON_CALL', active_tickets: 1 },
  { id: 'TCH-03', name: 'S. Velu', specialization: 'Smart Gas Manifolds', phone: '+91 9884099887', status: 'AVAILABLE', active_tickets: 0 }
];

export const INITIAL_IOT: IoTTelemetry = {
  water_tank_level_pct: 84,
  sump_level_pct: 92,
  inflow_rate_lpm: 128.5,
  outflow_rate_lpm: 94.2,
  solar_generation_kw: 18.4,
  temperature_celsius: 29.5,
  humidity_percentage: 64,
  air_quality_index: 42,
  is_pump_running: true,
  rain_fall_rate_mm: 0.0,
  drainage_pump_active: false
};

export const INITIAL_GAS_CYLINDERS: GasCylinder[] = [
  { unit_id: 'LPG-UNIT-1', cylinder_number: 1, status: 'ACTIVE', current_level_kg: 6.8, total_capacity_kg: 14.2, pressure_bar: 2.1, last_refill_date: '2026-09-28' },
  { unit_id: 'LPG-UNIT-2', cylinder_number: 2, status: 'STANDBY', current_level_kg: 14.2, total_capacity_kg: 14.2, pressure_bar: 2.4, last_refill_date: '2026-10-05' },
  { unit_id: 'LPG-UNIT-3', cylinder_number: 3, status: 'EMPTY', current_level_kg: 0.0, total_capacity_kg: 14.2, pressure_bar: 0.0, last_refill_date: '2026-08-14' }
];

export const INITIAL_ANOMALIES: Anomaly[] = [
  {
    id: 'ANM-301',
    sensor_id: 'WM-842',
    zone_id: 'Block-B Kitchen Riser 4',
    flow_rate: 62.4,
    threshold: 45.0,
    status: 'CRITICAL',
    timestamp: '2026-10-14 06:15',
    notes: 'Flow spike 62.4 L/min exceeded 45.0 L/min safety threshold during non-peak hours.'
  }
];
