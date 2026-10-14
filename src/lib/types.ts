export type Role = 'RESIDENT' | 'AUTHORITY' | 'TECHNICIAN';

export type BlockName = 'A' | 'B' | 'C' | 'D';

export type OccupancyStatus = 'OCCUPIED' | 'VACANT' | 'MAINTENANCE';

export type DuesStatus = 'PAID' | 'PENDING' | 'OVERDUE';

export interface Resident {
  flat_number: string; // e.g. "B-402"
  block: BlockName;
  floor_number: number; // 1..4
  resident_name: string;
  username: string;
  password: string;
  contact_number: string;
  email: string;
  occupancy_status: OccupancyStatus;
  dues_status: DuesStatus;
  vehicle_number: string;
}

export type ComplaintCategory = 'PLUMBING' | 'ELECTRICAL' | 'GAS' | 'ELEVATOR' | 'SECURITY';

export type PriorityLevel = 'CRITICAL' | 'URGENT' | 'HIGH' | 'STANDARD' | 'NORMAL';

export type TicketStatus = 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED';

export interface Complaint {
  ticket_id: string;
  flat_number: string;
  floor_number: number;
  category: ComplaintCategory;
  priority: PriorityLevel;
  description: string;
  status: TicketStatus;
  authority_notes: string;
  timestamp: string;
  raised_by_name: string;
  assigned_technician: string;
  work_log?: string;
  resolution_solution?: string;
}

export interface MaintenanceDue {
  id: string;
  flat_number: string;
  month_year: string;
  amount_rupees: number;
  due_date: string;
  status: DuesStatus;
  paid_date?: string;
  payment_method?: string;
}

export interface CommunityNotice {
  id: string;
  title: string;
  description: string;
  category: 'MAINTENANCE' | 'SECURITY' | 'COMMUNITY' | 'ALERT' | 'GENERAL';
  priority: 'NORMAL' | 'URGENT' | 'CRITICAL';
  posted_time: string;
  posted_by: string;
}

export interface Visitor {
  id: string;
  flat_number: string;
  visitor_name: string;
  visitor_phone: string;
  purpose: string;
  check_in_time: string;
  check_out_time?: string;
  status: 'PENDING' | 'APPROVED' | 'CHECKED_IN' | 'COMPLETED';
  pass_code: string;
}

export interface IoTTelemetry {
  water_tank_level_pct: number;
  sump_level_pct: number;
  inflow_rate_lpm: number;
  outflow_rate_lpm: number;
  solar_generation_kw: number;
  temperature_celsius: number;
  humidity_percentage: number;
  air_quality_index: number;
  is_pump_running: boolean;
  rain_fall_rate_mm: number;
  drainage_pump_active: boolean;
}

export interface GasCylinder {
  unit_id: string;
  cylinder_number: number;
  status: 'ACTIVE' | 'STANDBY' | 'EMPTY';
  current_level_kg: number;
  total_capacity_kg: number;
  pressure_bar: number;
  last_refill_date: string;
}

export interface Anomaly {
  id: string;
  sensor_id: string;
  zone_id: string;
  flow_rate: number;
  threshold: number;
  status: 'CRITICAL' | 'INVESTIGATING' | 'RESOLVED';
  timestamp: string;
  notes: string;
}

export interface UserProfile {
  username: string;
  password?: string;
  name: string;
  role: Role;
  flat: string;
  phone: string;
  email?: string;
}

export interface LocationState {
  permissionGranted: boolean;
  lat: number;
  lng: number;
  locationName: string;
  district: string;
  temperature: number;
  weatherCondition: string;
  windSpeedKm: number;
  humidityPct: number;
  lastUpdatedTime: string;
}
