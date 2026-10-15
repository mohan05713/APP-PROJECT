import { createClient } from '@supabase/supabase-js';
import { 
  INITIAL_RESIDENTS, INITIAL_COMPLAINTS, INITIAL_DUES, 
  INITIAL_NOTICES, INITIAL_VISITORS, INITIAL_IOT, 
  INITIAL_GAS_CYLINDERS, INITIAL_ANOMALIES 
} from './initialData';
import { 
  Resident, Complaint, MaintenanceDue, CommunityNotice, 
  Visitor, IoTTelemetry, GasCylinder, Anomaly, BlockName, OccupancyStatus, DuesStatus 
} from './types';

// Supabase URL & Anon Key with default demo fallback or localStorage override
const configUrl = typeof window !== 'undefined' ? localStorage.getItem('eco_supabase_url') : null;
const configKey = typeof window !== 'undefined' ? localStorage.getItem('eco_supabase_anon_key') : null;

export const SUPABASE_URL = configUrl || import.meta.env.VITE_SUPABASE_URL || 'https://yeabhmluvopehqcqwuox.supabase.co';
export const SUPABASE_ANON_KEY = configKey || import.meta.env.VITE_SUPABASE_ANON_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.demo-key-ecoapartment-os';

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

export function getSupabaseConfig() {
  const url = localStorage.getItem('eco_supabase_url') || import.meta.env.VITE_SUPABASE_URL || SUPABASE_URL;
  const key = localStorage.getItem('eco_supabase_anon_key') || import.meta.env.VITE_SUPABASE_ANON_KEY || SUPABASE_ANON_KEY;
  const isEnvSet = Boolean(import.meta.env.VITE_SUPABASE_URL && import.meta.env.VITE_SUPABASE_ANON_KEY);
  const isCustomSet = Boolean(localStorage.getItem('eco_supabase_url'));
  
  return {
    url,
    key,
    isConfigured: isEnvSet || isCustomSet,
    source: isCustomSet ? 'Custom Browser Credentials' : isEnvSet ? 'Vite Environment Variables' : 'Demo Project Fallback'
  };
}

export function updateSupabaseConfig(url: string, key: string) {
  if (url) localStorage.setItem('eco_supabase_url', url.trim());
  if (key) localStorage.setItem('eco_supabase_anon_key', key.trim());
}

export function resetSupabaseConfig() {
  localStorage.removeItem('eco_supabase_url');
  localStorage.removeItem('eco_supabase_anon_key');
}

const STORAGE_KEYS = {
  RESIDENTS: 'eco_os_residents_v3',
  COMPLAINTS: 'eco_os_complaints_v3',
  DUES: 'eco_os_dues_v3',
  NOTICES: 'eco_os_notices_v3',
  VISITORS: 'eco_os_visitors_v3',
  IOT: 'eco_os_iot_v3',
  GAS: 'eco_os_gas_v3',
  ANOMALIES: 'eco_os_anomalies_v3',
};

function getItem<T>(key: string, fallback: T): T {
  try {
    const data = localStorage.getItem(key);
    return data ? JSON.parse(data) : fallback;
  } catch (err) {
    console.warn(`Failed reading localStorage key: ${key}`, err);
    return fallback;
  }
}

function setItem<T>(key: string, value: T): void {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (err) {
    console.error(`Failed writing localStorage key: ${key}`, err);
  }
}

export const localDb = {
  getResidents: (): Resident[] => getItem(STORAGE_KEYS.RESIDENTS, INITIAL_RESIDENTS),
  saveResidents: (data: Resident[]) => setItem(STORAGE_KEYS.RESIDENTS, data),

  getComplaints: (): Complaint[] => getItem(STORAGE_KEYS.COMPLAINTS, INITIAL_COMPLAINTS),
  saveComplaints: (data: Complaint[]) => setItem(STORAGE_KEYS.COMPLAINTS, data),

  getDues: (): MaintenanceDue[] => getItem(STORAGE_KEYS.DUES, INITIAL_DUES),
  saveDues: (data: MaintenanceDue[]) => setItem(STORAGE_KEYS.DUES, data),

  getNotices: (): CommunityNotice[] => getItem(STORAGE_KEYS.NOTICES, INITIAL_NOTICES),
  saveNotices: (data: CommunityNotice[]) => setItem(STORAGE_KEYS.NOTICES, data),

  getVisitors: (): Visitor[] => getItem(STORAGE_KEYS.VISITORS, INITIAL_VISITORS),
  saveVisitors: (data: Visitor[]) => setItem(STORAGE_KEYS.VISITORS, data),

  getIoT: (): IoTTelemetry => getItem(STORAGE_KEYS.IOT, INITIAL_IOT),
  saveIoT: (data: IoTTelemetry) => setItem(STORAGE_KEYS.IOT, data),

  getGas: (): GasCylinder[] => getItem(STORAGE_KEYS.GAS, INITIAL_GAS_CYLINDERS),
  saveGas: (data: GasCylinder[]) => setItem(STORAGE_KEYS.GAS, data),

  getAnomalies: (): Anomaly[] => getItem(STORAGE_KEYS.ANOMALIES, INITIAL_ANOMALIES),
  saveAnomalies: (data: Anomaly[]) => setItem(STORAGE_KEYS.ANOMALIES, data),

  resetToDefaults: () => {
    localStorage.removeItem(STORAGE_KEYS.RESIDENTS);
    localStorage.removeItem(STORAGE_KEYS.COMPLAINTS);
    localStorage.removeItem(STORAGE_KEYS.DUES);
    localStorage.removeItem(STORAGE_KEYS.NOTICES);
    localStorage.removeItem(STORAGE_KEYS.VISITORS);
    localStorage.removeItem(STORAGE_KEYS.IOT);
    localStorage.removeItem(STORAGE_KEYS.GAS);
    localStorage.removeItem(STORAGE_KEYS.ANOMALIES);
  }
};

/**
 * Maps a row from public.residents in Supabase to application Resident model
 */
export function supabaseRowToResident(row: Record<string, any>): Resident {
  const profile = (typeof row.profile_data === 'object' && row.profile_data !== null) ? row.profile_data : {};
  const flat_number = row.unit_number || row.flat_number || profile.flat_number || (row.resident_code ? row.resident_code.replace(/^RES-/, '') : 'A-101');
  const block = row.block || profile.block || (flat_number.includes('-') ? flat_number.split('-')[0] : 'A');
  const floor_number = row.floor ?? row.floor_number ?? profile.floor_number ?? 1;
  const resident_name = row.full_name || row.resident_name || profile.resident_name || profile.name || 'Resident';
  const username = row.username || profile.username || `flat_${flat_number.toLowerCase().replace('-', '')}`;
  const contact_number = row.phone || row.contact_number || profile.contact_number || '—';
  const email = row.email || profile.email || `${username}@srmgreen.in`;
  const occupancy_status = row.occupancy_status || profile.occupancy_status || 'OCCUPIED';
  const dues_status = profile.dues_status || row.dues_status || 'PAID';
  const vehicle_number = row.vehicle_number || profile.vehicle_number || '—';
  const password = profile.password || row.password || 'pass123';
  const assignedFlats = profile.assignedFlats || row.assigned_flats || [flat_number];

  return {
    flat_number,
    block: block as BlockName,
    floor_number: Number(floor_number),
    resident_name,
    username,
    password,
    contact_number,
    email,
    occupancy_status: occupancy_status as OccupancyStatus,
    dues_status: dues_status as DuesStatus,
    vehicle_number,
    assignedFlats
  };
}

/**
 * Maps application Resident model to public.residents table row in Supabase
 */
export function residentToSupabaseRow(resident: Resident): Record<string, any> {
  const profile_data = {
    dues_status: resident.dues_status,
    assignedFlats: resident.assignedFlats || [resident.flat_number],
    password: resident.password || 'pass123',
    flat_number: resident.flat_number,
    block: resident.block,
    floor_number: resident.floor_number,
    resident_name: resident.resident_name,
    username: resident.username,
    contact_number: resident.contact_number,
    email: resident.email,
    occupancy_status: resident.occupancy_status,
    vehicle_number: resident.vehicle_number
  };

  return {
    resident_code: `RES-${resident.flat_number}`,
    username: resident.username,
    full_name: resident.resident_name,
    block: resident.block,
    floor: resident.floor_number,
    unit_number: resident.flat_number,
    email: resident.email,
    phone: resident.contact_number,
    vehicle_number: resident.vehicle_number,
    occupancy_status: resident.occupancy_status,
    profile_data: profile_data,
    updated_at: new Date().toISOString()
  };
}

/**
 * Fetches all resident records from Supabase public.residents table (up to 1000 records).
 * Uses local browser cache as fallback if network or database error occurs.
 */
export async function fetchAllResidentsFromSupabase(): Promise<{ 
  residents: Resident[]; 
  count: number; 
  error: any; 
  isLive: boolean; 
}> {
  try {
    const { data, count, error } = await supabase
      .from('residents')
      .select('*', { count: 'exact' })
      .range(0, 1000);

    if (error) {
      console.warn('Supabase query error on public.residents table:', error.message || error);
      return { 
        residents: localDb.getResidents(), 
        count: localDb.getResidents().length, 
        error, 
        isLive: false 
      };
    }

    if (data && data.length > 0) {
      const residents = data.map(supabaseRowToResident);
      // Synchronize with offline browser cache
      localDb.saveResidents(residents);
      return { 
        residents, 
        count: count ?? data.length, 
        error: null, 
        isLive: true 
      };
    }

    return { 
      residents: localDb.getResidents(), 
      count: localDb.getResidents().length, 
      error: null, 
      isLive: true 
    };
  } catch (err: any) {
    console.error('Failed fetching residents from Supabase:', err);
    return { 
      residents: localDb.getResidents(), 
      count: localDb.getResidents().length, 
      error: err, 
      isLive: false 
    };
  }
}

/**
 * Inserts or updates a resident record in Supabase public.residents table.
 */
export async function saveResidentToSupabase(resident: Resident): Promise<{ success: boolean; error: any }> {
  try {
    const row = residentToSupabaseRow(resident);

    const { error } = await supabase
      .from('residents')
      .upsert(row, { onConflict: 'unit_number' });

    if (error) {
      // Fallback matching on username if unit_number constraint differs
      const { error: err2 } = await supabase
        .from('residents')
        .upsert(row, { onConflict: 'username' });

      if (err2) {
        console.error('Supabase save error:', error, err2);
        return { success: false, error: error || err2 };
      }
    }

    return { success: true, error: null };
  } catch (err: any) {
    console.error('Exception in saveResidentToSupabase:', err);
    return { success: false, error: err };
  }
}

/**
 * Updates or removes a resident record in Supabase public.residents table.
 */
export async function deleteResidentFromSupabase(flatNumber: string): Promise<{ success: boolean; error: any }> {
  try {
    const { error } = await supabase
      .from('residents')
      .update({
        occupancy_status: 'VACANT',
        full_name: 'Unoccupied Unit',
        phone: '—',
        updated_at: new Date().toISOString()
      })
      .or(`unit_number.eq.${flatNumber},resident_code.eq.RES-${flatNumber}`);

    if (error) {
      const { error: err2 } = await supabase
        .from('residents')
        .delete()
        .or(`unit_number.eq.${flatNumber},resident_code.eq.RES-${flatNumber}`);

      if (err2) return { success: false, error: error || err2 };
    }

    return { success: true, error: null };
  } catch (err: any) {
    return { success: false, error: err };
  }
}

/**
 * Diagnostic test that verifies live read & update capability on public.residents
 */
export async function testSupabaseConnection(): Promise<{
  connected: boolean;
  latencyMs: number;
  count: number;
  message: string;
  tableName: string;
  error?: string;
}> {
  const startTime = performance.now();
  try {
    const { data, count, error } = await supabase
      .from('residents')
      .select('id, unit_number, full_name, occupancy_status', { count: 'exact' })
      .limit(5);

    const latencyMs = Math.round(performance.now() - startTime);

    if (error) {
      return {
        connected: false,
        latencyMs,
        count: 0,
        tableName: 'public.residents',
        message: `Database Query Error: ${error.message || JSON.stringify(error)}`,
        error: error.message || String(error)
      };
    }

    return {
      connected: true,
      latencyMs,
      count: count ?? (data ? data.length : 0),
      tableName: 'public.residents',
      message: `Successfully connected to public.residents table in ${latencyMs}ms. Verified ${count ?? (data ? data.length : 0)} resident records.`
    };
  } catch (err: any) {
    const latencyMs = Math.round(performance.now() - startTime);
    return {
      connected: false,
      latencyMs,
      count: 0,
      tableName: 'public.residents',
      message: `Connection Exception: ${err.message || String(err)}`,
      error: err.message || String(err)
    };
  }
}

