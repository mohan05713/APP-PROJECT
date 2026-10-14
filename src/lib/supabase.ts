import { createClient } from '@supabase/supabase-js';
import { 
  INITIAL_RESIDENTS, INITIAL_COMPLAINTS, INITIAL_DUES, 
  INITIAL_NOTICES, INITIAL_VISITORS, INITIAL_IOT, 
  INITIAL_GAS_CYLINDERS, INITIAL_ANOMALIES 
} from './initialData';
import { 
  Resident, Complaint, MaintenanceDue, CommunityNotice, 
  Visitor, IoTTelemetry, GasCylinder, Anomaly 
} from './types';

// Supabase URL & Anon Key with default demo fallback or localStorage override
const SUPABASE_URL = import.meta.env.VITE_SUPABASE_URL || 'https://demo-ecoapartment.supabase.co';
const SUPABASE_ANON_KEY = import.meta.env.VITE_SUPABASE_ANON_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.demo-key-ecoapartment-os';

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY);

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
