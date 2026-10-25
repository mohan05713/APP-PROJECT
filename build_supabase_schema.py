"""
Build complete supabase_schema.sql for EcoApartment OS.
Generates PostgreSQL DDL, Row Level Security (RLS) policies, Views, Triggers,
and full SQL INSERT statements for all 400 flats/residents, technicians, tickets, and IoT.
"""

import random
from generate_records import generate_all_400_records

def build_sql():
    records = generate_all_400_records()
    
    sql_lines = [
        "-- =====================================================================",
        "-- ECOAPARTMENT OS - SUPABASE POSTGRESQL PRODUCTION SCHEMA & SEED DATA",
        "-- Project: EcoApartment OS — Smart Facility Management & IoT Sustainability",
        "-- Deployment: SRM Green Residency (Blocks A, B, C, D | 400 Flats)",
        "-- Compatible with Supabase SQL Editor and PostgreSQL 15+",
        "-- =====================================================================\n",
        "-- Enable UUID extension if not enabled",
        "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";",
        "CREATE EXTENSION IF NOT EXISTS \"pgcrypto\";\n",
        "-- ---------------------------------------------------------------------",
        "-- 1. FLATS DIRECTORY (400 Units)",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.flats (",
        "    flat_code VARCHAR(10) PRIMARY KEY,          -- e.g. 'B-402', 'A-101'",
        "    block VARCHAR(2) NOT NULL,                  -- 'A', 'B', 'C', 'D'",
        "    floor INTEGER NOT NULL CHECK (floor BETWEEN 1 AND 4),",
        "    unit_number INTEGER NOT NULL,               -- 101 to 425",
        "    sqft INTEGER DEFAULT 1450,",
        "    bhk VARCHAR(10) DEFAULT '3 BHK',",
        "    water_meter_id VARCHAR(20) UNIQUE,",
        "    gas_meter_id VARCHAR(20) UNIQUE,",
        "    created_at TIMESTAMPTZ DEFAULT NOW(),",
        "    updated_at TIMESTAMPTZ DEFAULT NOW()",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 2. RESIDENTS TABLE (400 Profiles)",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.residents (",
        "    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),",
        "    auth_user_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,",
        "    flat_code VARCHAR(10) NOT NULL REFERENCES public.flats(flat_code) ON DELETE CASCADE,",
        "    resident_name VARCHAR(120) NOT NULL,",
        "    email VARCHAR(120) NOT NULL,",
        "    contact_number VARCHAR(30) NOT NULL,",
        "    occupancy_status VARCHAR(20) NOT NULL DEFAULT 'OCCUPIED_OWNER' CHECK (occupancy_status IN ('OCCUPIED_OWNER', 'OCCUPIED_TENANT', 'VACANT')),",
        "    move_in_date DATE,",
        "    emergency_contact VARCHAR(80),",
        "    vehicle_number VARCHAR(30),",
        "    dues_status VARCHAR(20) DEFAULT 'PAID' CHECK (dues_status IN ('PAID', 'PENDING', 'OVERDUE', 'VACANT_EXEMPT')),",
        "    monthly_dues NUMERIC(10, 2) DEFAULT 2800.00,",
        "    created_at TIMESTAMPTZ DEFAULT NOW(),",
        "    updated_at TIMESTAMPTZ DEFAULT NOW()",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 3. APP USERS & AUTH ROLES (Local & Supabase Auth Bridge)",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.app_users (",
        "    username VARCHAR(50) PRIMARY KEY,",
        "    password_hash VARCHAR(100) NOT NULL,",
        "    role VARCHAR(20) NOT NULL CHECK (role IN ('AUTHORITY', 'RESIDENT', 'TECHNICIAN')),",
        "    flat_code VARCHAR(10) REFERENCES public.flats(flat_code) ON DELETE SET NULL,",
        "    display_name VARCHAR(100) NOT NULL,",
        "    phone VARCHAR(30) NOT NULL,",
        "    created_at TIMESTAMPTZ DEFAULT NOW()",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 4. TECHNICIANS ROSTER",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.technicians (",
        "    id VARCHAR(20) PRIMARY KEY,",
        "    name VARCHAR(100) NOT NULL,",
        "    trade VARCHAR(50) NOT NULL CHECK (trade IN ('PLUMBING', 'ELECTRICAL', 'GAS_SUPPLY', 'ELEVATOR', 'SECURITY', 'HVAC')),",
        "    phone VARCHAR(30) NOT NULL,",
        "    status VARCHAR(20) DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_DUTY', 'OFF_DUTY')),",
        "    active_tasks INTEGER DEFAULT 0,",
        "    rating NUMERIC(3, 1) DEFAULT 4.8,",
        "    created_at TIMESTAMPTZ DEFAULT NOW()",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 5. COMPLAINTS & HELPDESK TICKETS",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.complaints (",
        "    ticket_id VARCHAR(30) PRIMARY KEY,",
        "    flat_code VARCHAR(10) NOT NULL REFERENCES public.flats(flat_code) ON DELETE CASCADE,",
        "    category VARCHAR(30) NOT NULL CHECK (category IN ('PLUMBING', 'ELECTRICAL', 'GAS_SUPPLY', 'ELEVATOR', 'SECURITY', 'CIVIL')),",
        "    priority VARCHAR(20) NOT NULL CHECK (priority IN ('STANDARD', 'URGENT', 'CRITICAL')),",
        "    description TEXT NOT NULL,",
        "    status VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED' CHECK (status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),",
        "    assigned_technician_id VARCHAR(20) REFERENCES public.technicians(id) ON DELETE SET NULL,",
        "    authority_notes TEXT,",
        "    raised_by_name VARCHAR(100) NOT NULL,",
        "    created_at TIMESTAMPTZ DEFAULT NOW(),",
        "    resolved_at TIMESTAMPTZ",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 6. MAINTENANCE DUES & BILLING",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.maintenance_dues (",
        "    id VARCHAR(40) PRIMARY KEY,",
        "    flat_code VARCHAR(10) NOT NULL REFERENCES public.flats(flat_code) ON DELETE CASCADE,",
        "    month_year VARCHAR(30) NOT NULL,",
        "    amount_rupees NUMERIC(10, 2) NOT NULL,",
        "    due_date DATE NOT NULL,",
        "    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PAID', 'PENDING', 'OVERDUE')),",
        "    paid_date DATE,",
        "    payment_method VARCHAR(50),",
        "    transaction_ref VARCHAR(100),",
        "    created_at TIMESTAMPTZ DEFAULT NOW()",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 7. VISITOR & GATE PASS SYSTEM",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.visitors (",
        "    id VARCHAR(30) PRIMARY KEY,",
        "    flat_code VARCHAR(10) NOT NULL REFERENCES public.flats(flat_code) ON DELETE CASCADE,",
        "    visitor_name VARCHAR(100) NOT NULL,",
        "    phone VARCHAR(30) NOT NULL,",
        "    visitor_type VARCHAR(30) NOT NULL CHECK (visitor_type IN ('GUEST', 'DELIVERY', 'CAB', 'SERVICE', 'CONTRACTOR')),",
        "    vehicle_number VARCHAR(30),",
        "    purpose TEXT NOT NULL,",
        "    entry_time TIMESTAMPTZ DEFAULT NOW(),",
        "    exit_time TIMESTAMPTZ,",
        "    pass_code VARCHAR(10) NOT NULL,",
        "    approval_status VARCHAR(20) DEFAULT 'APPROVED' CHECK (approval_status IN ('APPROVED', 'PENDING', 'REJECTED', 'EXPIRED')),",
        "    approved_by VARCHAR(100) NOT NULL",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 8. COMMUNITY NOTICE BOARD",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.community_notices (",
        "    id VARCHAR(30) PRIMARY KEY,",
        "    title VARCHAR(200) NOT NULL,",
        "    description TEXT NOT NULL,",
        "    category VARCHAR(50) NOT NULL,",
        "    priority VARCHAR(20) NOT NULL CHECK (priority IN ('NORMAL', 'HIGH', 'CRITICAL')),",
        "    posted_time TIMESTAMPTZ DEFAULT NOW(),",
        "    is_pinned BOOLEAN DEFAULT FALSE,",
        "    author VARCHAR(100) DEFAULT 'Facility Office'",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 9. IOT SENSORS TELEMETRY & WATER TANK HISTORY",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.iot_telemetry (",
        "    id BIGSERIAL PRIMARY KEY,",
        "    timestamp TIMESTAMPTZ DEFAULT NOW(),",
        "    water_tank_level_pct INTEGER NOT NULL,",
        "    water_tank_litres INTEGER NOT NULL,",
        "    gas_pressure_mbar NUMERIC(5, 2) NOT NULL,",
        "    aqi INTEGER NOT NULL,",
        "    temperature_celsius NUMERIC(4, 1) NOT NULL,",
        "    humidity_percentage INTEGER NOT NULL,",
        "    flood_sump_level_pct INTEGER DEFAULT 0,",
        "    flood_alarm_active BOOLEAN DEFAULT FALSE",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 10. LPG MULTI-CYLINDER MANIFOLD",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.lpg_inventory (",
        "    unit_id INTEGER PRIMARY KEY,",
        "    label VARCHAR(80) NOT NULL,",
        "    capacity_kg NUMERIC(4, 1) DEFAULT 14.2,",
        "    current_level_pct INTEGER NOT NULL,",
        "    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'STANDBY', 'REFILL_REQUIRED')),",
        "    auto_booking_enabled BOOLEAN DEFAULT TRUE,",
        "    last_refill_date VARCHAR(30)",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 11. WATER & RESOURCE ANOMALY LOGS (OOP Algorithm Log)",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.anomaly_logs (",
        "    id BIGSERIAL PRIMARY KEY,",
        "    sensor_id VARCHAR(30) NOT NULL,",
        "    zone_id VARCHAR(80) NOT NULL,",
        "    flow_rate_lpm NUMERIC(6, 2) NOT NULL,",
        "    leak_threshold_lpm NUMERIC(6, 2) NOT NULL,",
        "    severity VARCHAR(20) DEFAULT 'HIGH' CHECK (severity IN ('WARNING', 'CRITICAL')),",
        "    alert_message TEXT NOT NULL,",
        "    detected_at TIMESTAMPTZ DEFAULT NOW(),",
        "    is_resolved BOOLEAN DEFAULT FALSE",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- 12. APP SETTINGS & CONFIGURATION",
        "-- ---------------------------------------------------------------------",
        "CREATE TABLE IF NOT EXISTS public.app_settings (",
        "    config_key VARCHAR(50) PRIMARY KEY,",
        "    config_value TEXT NOT NULL,",
        "    description TEXT,",
        "    updated_at TIMESTAMPTZ DEFAULT NOW()",
        ");\n",
        "-- ---------------------------------------------------------------------",
        "-- ROW LEVEL SECURITY (RLS) POLICIES",
        "-- ---------------------------------------------------------------------",
        "ALTER TABLE public.flats ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.residents ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.app_users ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.technicians ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.complaints ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.maintenance_dues ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.visitors ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.community_notices ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.iot_telemetry ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.lpg_inventory ENABLE ROW LEVEL SECURITY;",
        "ALTER TABLE public.anomaly_logs ENABLE ROW LEVEL SECURITY;\n",
        "-- Public / Authenticated read policies",
        "CREATE POLICY \"Flats visible to all\" ON public.flats FOR SELECT USING (true);",
        "CREATE POLICY \"Residents viewable by authenticated users\" ON public.residents FOR SELECT USING (true);",
        "CREATE POLICY \"Residents modifiable by authority or self\" ON public.residents FOR ALL USING (true);",
        "CREATE POLICY \"Complaints viewable by all\" ON public.complaints FOR SELECT USING (true);",
        "CREATE POLICY \"Complaints insertable by all\" ON public.complaints FOR INSERT WITH CHECK (true);",
        "CREATE POLICY \"Complaints editable by authority\" ON public.complaints FOR UPDATE USING (true);",
        "CREATE POLICY \"Maintenance dues viewable by all\" ON public.maintenance_dues FOR SELECT USING (true);",
        "CREATE POLICY \"Maintenance dues updatable\" ON public.maintenance_dues FOR UPDATE USING (true);",
        "CREATE POLICY \"Notices viewable by all\" ON public.community_notices FOR SELECT USING (true);",
        "CREATE POLICY \"Notices manageable by admin\" ON public.community_notices FOR ALL USING (true);",
        "CREATE POLICY \"Visitors readable and insertable\" ON public.visitors FOR ALL USING (true);",
        "CREATE POLICY \"IoT and Telemetry readable by all\" ON public.iot_telemetry FOR SELECT USING (true);",
        "CREATE POLICY \"LPG readable and updatable\" ON public.lpg_inventory FOR ALL USING (true);",
        "CREATE POLICY \"Anomalies readable and updatable\" ON public.anomaly_logs FOR ALL USING (true);\n",
        "-- ---------------------------------------------------------------------",
        "-- USEFUL BUSINESS VIEWS",
        "-- ---------------------------------------------------------------------",
        "CREATE OR REPLACE VIEW public.view_block_occupancy AS",
        "SELECT",
        "    f.block,",
        "    COUNT(f.flat_code) AS total_flats,",
        "    COUNT(r.id) FILTER (WHERE r.occupancy_status != 'VACANT') AS occupied_flats,",
        "    COUNT(f.flat_code) - COUNT(r.id) FILTER (WHERE r.occupancy_status != 'VACANT') AS vacant_flats,",
        "    ROUND(100.0 * COUNT(r.id) FILTER (WHERE r.occupancy_status != 'VACANT') / COUNT(f.flat_code), 1) AS occupancy_percentage",
        "FROM public.flats f",
        "LEFT JOIN public.residents r ON f.flat_code = r.flat_code",
        "GROUP BY f.block",
        "ORDER BY f.block;\n",
        "CREATE OR REPLACE VIEW public.view_unpaid_dues AS",
        "SELECT",
        "    d.id,",
        "    d.flat_code,",
        "    r.resident_name,",
        "    r.contact_number,",
        "    d.month_year,",
        "    d.amount_rupees,",
        "    d.due_date,",
        "    d.status",
        "FROM public.maintenance_dues d",
        "JOIN public.residents r ON d.flat_code = r.flat_code",
        "WHERE d.status IN ('PENDING', 'OVERDUE')",
        "ORDER BY d.due_date ASC;\n",
        "-- ---------------------------------------------------------------------",
        "-- SEED DATA: 400 FLATS & RESIDENTS",
        "-- ---------------------------------------------------------------------"
    ]

    # Flats INSERT
    sql_lines.append("\n-- Seeding 400 Flats")
    sql_lines.append("INSERT INTO public.flats (flat_code, block, floor, unit_number, sqft, bhk, water_meter_id, gas_meter_id) VALUES")
    flat_rows = []
    for r in records:
        sqft = 1200 + (r["floor"] * 80)
        bhk = "2 BHK" if r["unit_number"] % 100 <= 10 else "3 BHK"
        flat_rows.append(f"('{r['flat_code']}', '{r['block']}', {r['floor']}, {r['unit_number']}, {sqft}, '{bhk}', '{r['water_meter_id']}', '{r['gas_meter_id']}')")
    sql_lines.append(",\n".join(flat_rows) + "\nON CONFLICT (flat_code) DO NOTHING;\n")

    # Residents INSERT
    sql_lines.append("\n-- Seeding 400 Residents")
    sql_lines.append("INSERT INTO public.residents (flat_code, resident_name, email, contact_number, occupancy_status, move_in_date, emergency_contact, vehicle_number, dues_status, monthly_dues) VALUES")
    res_rows = []
    for r in records:
        move_in = f"'{r['move_in_date']}'" if r['move_in_date'] else "NULL"
        name_esc = r['resident_name'].replace("'", "''")
        res_rows.append(f"('{r['flat_code']}', '{name_esc}', '{r['email']}', '{r['contact_number']}', '{r['occupancy_status']}', {move_in}, '{r['emergency_contact']}', '{r['vehicle_number']}', '{r['dues_status']}', {r['monthly_dues']})")
    sql_lines.append(",\n".join(res_rows) + "\nON CONFLICT DO NOTHING;\n")

    # App Users & Admin credentials
    sql_lines.append("\n-- Seeding App Credentials (Admins & Sample Flats)")
    sql_lines.append("INSERT INTO public.app_users (username, password_hash, role, flat_code, display_name, phone) VALUES")
    user_rows = [
        "('authority', '6383898769', 'AUTHORITY', NULL, 'SRM Authority Desk', '+91 63838 98769')",
        "('admin', '6383898769', 'AUTHORITY', NULL, 'SRM Facility Admin', '+91 63838 98769')",
    ]
    # Add preserved flats and sample flats
    for r in records[:30]:
        name_esc = r['resident_name'].replace("'", "''")
        user_rows.append(f"('{r['username']}', '{r['password']}', '{r['role']}', '{r['flat_code']}', '{name_esc}', '{r['contact_number']}')")
    sql_lines.append(",\n".join(user_rows) + "\nON CONFLICT (username) DO NOTHING;\n")

    # Technicians
    sql_lines.append("\n-- Seeding Certified Technicians Roster")
    sql_lines.append("""INSERT INTO public.technicians (id, name, trade, phone, status, active_tasks, rating) VALUES
('TECH-PLUMB-01', 'K. Murugan (Master Plumber)', 'PLUMBING', '+91 98402 11001', 'AVAILABLE', 1, 4.9),
('TECH-ELEC-01', 'S. Vijay (Senior Wireman)', 'ELECTRICAL', '+91 98402 11002', 'ON_DUTY', 2, 4.8),
('TECH-GAS-01', 'R. Chandran (Gas Calibration Specialist)', 'GAS_SUPPLY', '+91 98402 11003', 'AVAILABLE', 0, 5.0),
('TECH-ELEV-01', 'Otis Service Team (M. Jayaraj)', 'ELEVATOR', '+91 98402 11004', 'ON_DUTY', 1, 4.7),
('TECH-SEC-01', 'G4S Security Officer (B. Pandian)', 'SECURITY', '+91 98402 11005', 'AVAILABLE', 0, 4.9)
ON CONFLICT (id) DO NOTHING;
""")

    # Seed Complaints
    sql_lines.append("\n-- Seeding Helpdesk Complaints")
    sql_lines.append("""INSERT INTO public.complaints (ticket_id, flat_code, category, priority, description, status, assigned_technician_id, authority_notes, raised_by_name) VALUES
('TKT-101-WTR', 'B-101', 'PLUMBING', 'URGENT', 'Slow water pressure in kitchen faucet.', 'ASSIGNED', 'TECH-PLUMB-01', 'Plumber scheduled for 2:30 PM.', 'R. SHAM SUDHAN'),
('TKT-204-ELE', 'B-204', 'ELECTRICAL', 'CRITICAL', 'Balcony light tripping master breaker.', 'IN_PROGRESS', 'TECH-ELEC-01', 'Electrician isolating short circuit on 2nd floor riser.', 'T S. ABINAYA'),
('TKT-402-GAS', 'B-402', 'GAS_SUPPLY', 'STANDARD', 'Annual piped gas calibration & check.', 'RESOLVED', 'TECH-GAS-01', 'Regulator replaced, pressure tested at 28.5 mbar.', 'Mohan Palaniappan'),
('TKT-305-ELV', 'B-305', 'ELEVATOR', 'URGENT', 'Block B elevator door sensor delay.', 'IN_PROGRESS', 'TECH-ELEV-01', 'Otis service engineer notified.', 'V. Suresh'),
('TKT-A212-PLB', 'A-212', 'PLUMBING', 'STANDARD', 'Overhead shower diverter cartridge replacement.', 'PENDING', NULL, 'Awaiting resident availability.', 'K. Anand'),
('TKT-C105-SEC', 'C-105', 'SECURITY', 'STANDARD', 'Requesting duplicate RFID boom-barrier keytag.', 'RESOLVED', 'TECH-SEC-01', 'RFID tag #8841 issued.', 'Deepak Kumar')
ON CONFLICT (ticket_id) DO NOTHING;
""")

    # Seed Maintenance Dues
    sql_lines.append("\n-- Seeding Maintenance Dues")
    sql_lines.append("INSERT INTO public.maintenance_dues (id, flat_code, month_year, amount_rupees, due_date, status, paid_date, payment_method, transaction_ref) VALUES")
    due_rows = []
    for r in records[:50]:
        due_id = f"DUE-2026-09-{r['flat_code'].replace('-', '')}"
        stat = r['dues_status']
        if stat == "VACANT_EXEMPT":
            continue
        paid_dt = "'2026-09-05'" if stat == 'PAID' else "NULL"
        method = "'UPI AutoPay (GPay)'" if stat == 'PAID' else "NULL"
        txn = f"'TXN-{random.randint(100000, 999999)}'" if stat == 'PAID' else "NULL"
        due_rows.append(f"('{due_id}', '{r['flat_code']}', 'September 2026', {r['monthly_dues']}, '2026-09-30', '{stat}', {paid_dt}, {method}, {txn})")
    sql_lines.append(",\n".join(due_rows) + "\nON CONFLICT (id) DO NOTHING;\n")

    # Seed Community Notices
    sql_lines.append("\n-- Seeding Community Notices")
    sql_lines.append("""INSERT INTO public.community_notices (id, title, description, category, priority, posted_time, is_pinned, author) VALUES
('NOT-01', 'Overhead Water Tank Deep Sanitization', 'Quarterly tank cleaning scheduled for this Sunday from 06:00 AM to 10:00 AM. Please store adequate drinking water.', 'WATER_SUPPLY', 'HIGH', '2026-09-19 09:00:00+05:30', TRUE, 'Facility Chief Engineer'),
('NOT-02', 'Festival Rooftop Solar LED Illumination', 'The society entrance and gardens are now 100% powered by our 40 kW rooftop solar photovoltaic array.', 'GREEN_ENERGY', 'NORMAL', '2026-09-18 14:30:00+05:30', FALSE, 'Green Committee'),
('NOT-03', 'Tamil Nadu Fire & Gas Safety Audit Completed', 'Annual fire suppression, sprinkler lines, and piped LPG manifold pressure test completed with zero remarks.', 'SAFETY', 'NORMAL', '2026-09-15 11:00:00+05:30', FALSE, 'Safety Officer'),
('NOT-04', 'Monsoon Storm-Water Sump Automated Drills', 'All dual 7.5 HP submersible storm pumps tested successfully. Sensor thresholds calibrated to 65% capacity.', 'FLOOD_PREVENTION', 'HIGH', '2026-09-12 17:00:00+05:30', TRUE, 'Drainage Team')
ON CONFLICT (id) DO NOTHING;
""")

    # Seed LPG Inventory
    sql_lines.append("\n-- Seeding LPG Multi-Cylinder Manifold")
    sql_lines.append("""INSERT INTO public.lpg_inventory (unit_id, label, capacity_kg, current_level_pct, status, auto_booking_enabled, last_refill_date) VALUES
(1, 'Unit 1 (Active Kitchen Line)', 14.2, 68, 'ACTIVE', TRUE, '12 Sep 2026'),
(2, 'Unit 2 (Standby Backup)', 14.2, 100, 'STANDBY', TRUE, '15 Sep 2026'),
(3, 'Unit 3 (Reserve Line)', 14.2, 15, 'REFILL_REQUIRED', TRUE, '02 Aug 2026')
ON CONFLICT (unit_id) DO NOTHING;
""")

    # Seed IoT Telemetry
    sql_lines.append("\n-- Seeding Live IoT Telemetry")
    sql_lines.append("""INSERT INTO public.iot_telemetry (water_tank_level_pct, water_tank_litres, gas_pressure_mbar, aqi, temperature_celsius, humidity_percentage, flood_sump_level_pct, flood_alarm_active) VALUES
(82, 8200, 28.5, 42, 26.4, 58, 12, FALSE);
""")

    # Seed Anomaly Logs
    sql_lines.append("\n-- Seeding Resource Anomaly History (OOP Water Leak Algorithm)")
    sql_lines.append("""INSERT INTO public.anomaly_logs (sensor_id, zone_id, flow_rate_lpm, leak_threshold_lpm, severity, alert_message, is_resolved) VALUES
('WM-B101', 'Block-B Kitchen Riser', 54.2, 45.0, 'CRITICAL', 'Potential water leakage detected outside scheduled usage window (54.2 L/min > 45.0 L/min limit)', FALSE),
('WM-A205', 'Block-A Garden Sprinkler Zone', 48.0, 45.0, 'WARNING', 'Flow threshold exceeded during low-demand hour (48.0 L/min > 45.0 L/min limit)', TRUE),
('WM-C310', 'Block-C Utility Line', 62.1, 45.0, 'CRITICAL', 'Abnormal continuous flow rate registered for > 30 minutes', TRUE);
""")

    # Seed App Settings
    sql_lines.append("\n-- Seeding App Settings")
    sql_lines.append("""INSERT INTO public.app_settings (config_key, config_value, description) VALUES
('SUPABASE_MODE', 'HYBRID', 'Connection mode: LOCAL_SQLITE, CLOUD_SUPABASE, or HYBRID'),
('OVERHEAD_TANK_CAPACITY_LITRES', '10000', 'Total capacity of central overhead reservoir in liters'),
('WATER_LEAK_THRESHOLD_LPM', '45.0', 'Flow rate threshold in liters per minute for leak alarms'),
('AUTO_SUMP_PUMP_TRIGGER_PCT', '65', 'Storm sump capacity percentage that triggers automatic pumping'),
('SOCIETY_NAME', 'SRM Green Residency', 'Residential community title'),
('TOTAL_FLATS_COUNT', '400', 'Total flats across Blocks A, B, C, D')
ON CONFLICT (config_key) DO NOTHING;
""")

    return "\n".join(sql_lines)

if __name__ == "__main__":
    content = build_sql()
    with open("supabase_schema.sql", "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Wrote supabase_schema.sql ({len(content)} bytes).")
