import { Resident } from './types';

/**
 * Downloads a string content as a file with specified MIME type and filename.
 */
export function downloadFile(content: string, filename: string, mimeType: string) {
  const blob = new Blob([content], { type: mimeType });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

/**
 * Converts an array of resident objects into CSV string format.
 */
export function convertResidentsToCSV(residents: Resident[]): string {
  const headers = [
    'Flat Number',
    'Block',
    'Floor',
    'Resident Name',
    'Username',
    'Password',
    'Phone Number',
    'Email Address',
    'Occupancy Status',
    'Dues Status',
    'Vehicle Number'
  ];

  const rows = residents.map((r) => [
    `"${r.flat_number || ''}"`,
    `"${r.block || ''}"`,
    `"${r.floor_number ?? ''}"`,
    `"${(r.resident_name || '').replace(/"/g, '""')}"`,
    `"${(r.username || '').replace(/"/g, '""')}"`,
    `"${(r.password || '').replace(/"/g, '""')}"`,
    `"${(r.contact_number || '').replace(/"/g, '""')}"`,
    `"${(r.email || '').replace(/"/g, '""')}"`,
    `"${r.occupancy_status || ''}"`,
    `"${r.dues_status || ''}"`,
    `"${(r.vehicle_number || '').replace(/"/g, '""')}"`
  ]);

  return [headers.join(','), ...rows.map((row) => row.join(','))].join('\n');
}

/**
 * Download all resident records as an editable CSV file.
 */
export function downloadAllResidentsCSV(residents: Resident[], filename = '400_residential_people_database.csv') {
  const csvContent = convertResidentsToCSV(residents);
  downloadFile(csvContent, filename, 'text/csv;charset=utf-8;');
}

/**
 * Download all resident records as JSON file.
 */
export function downloadAllResidentsJSON(residents: Resident[], filename = '400_residential_people_database.json') {
  const jsonContent = JSON.stringify(residents, null, 2);
  downloadFile(jsonContent, filename, 'application/json');
}

/**
 * Download details for a single resident profile as editable CSV.
 */
export function downloadSingleResidentCSV(resident: Resident) {
  const csvContent = convertResidentsToCSV([resident]);
  const safeName = resident.resident_name.replace(/[^a-zA-Z0-9]/g, '_');
  const filename = `resident_profile_${resident.flat_number}_${safeName}.csv`;
  downloadFile(csvContent, filename, 'text/csv;charset=utf-8;');
}

/**
 * Download details for a single resident profile as formatted JSON file.
 */
export function downloadSingleResidentJSON(resident: Resident) {
  const jsonContent = JSON.stringify(resident, null, 2);
  const safeName = resident.resident_name.replace(/[^a-zA-Z0-9]/g, '_');
  const filename = `resident_profile_${resident.flat_number}_${safeName}.json`;
  downloadFile(jsonContent, filename, 'application/json');
}

/**
 * Download details for a single resident profile as editable text document.
 */
export function downloadSingleResidentText(resident: Resident) {
  const text = `=====================================================
ECO-OS RESIDENTIAL PROFILE REPORT
=====================================================
Flat Number      : ${resident.flat_number}
Block / Floor    : Block ${resident.block}, Floor ${resident.floor_number}
Resident Name    : ${resident.resident_name}
Username         : ${resident.username}
Password         : ${resident.password}
Phone Number     : ${resident.contact_number}
Email Address    : ${resident.email}
Occupancy Status : ${resident.occupancy_status}
Dues Status      : ${resident.dues_status}
Vehicle Number   : ${resident.vehicle_number || 'N/A'}
=====================================================
Generated on ${new Date().toLocaleString()}
`;
  const safeName = resident.resident_name.replace(/[^a-zA-Z0-9]/g, '_');
  const filename = `resident_profile_${resident.flat_number}_${safeName}.txt`;
  downloadFile(text, filename, 'text/plain;charset=utf-8;');
}
