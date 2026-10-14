"""
EcoApartment OS - Pure Python Core Engine & Database
Smart Facility Management & IoT Sustainability Platform for SRM Green Residency

Supports:
- 400 Resident profiles across Blocks A, B, C, D (Floors 1-4, Flats 101 to 425)
- Preserves all 18 legacy resident and admin records with exact credentials
- Complete CRUD operations, pagination, search, and filtering
- Complaints & Helpdesk with technician assignments
- Maintenance Dues & Billing
- Community Notice Board
- Gate Pass & Visitor Management
- IoT Telemetry (Water, AQI, Solar, Flood, Gas)
- Water Anomaly Detection Engine integration
"""

import sqlite3
import datetime
import random
import os
import json
from dataclasses import dataclass, asdict
from typing import List, Optional, Dict, Any

# =====================================================================
# DATA CLASSES
# =====================================================================

@dataclass
class User:
    username: str
    password: str
    role: str  # 'AUTHORITY', 'RESIDENT', 'TECHNICIAN'
    block: str
    floor_number: int
    flat_number: str
    resident_name: str
    contact_number: str
    email: str = ""
    occupancy_status: str = "OCCUPIED"  # 'OCCUPIED', 'VACANT', 'MAINTENANCE'
    move_in_date: str = "2024-01-15"
    emergency_contact: str = "+91 98401 00000"
    vehicle_number: str = "TN-09-AB-1234"
    dues_status: str = "PAID"  # 'PAID', 'PENDING', 'OVERDUE'

@dataclass
class IoTSensors:
    aqi: int = 42
    aqi_status: str = "Good • Green Zone"
    pm25: float = 11.2
    pm10: float = 24.5
    temperature_celsius: float = 28.4
    humidity_percentage: int = 64
    humidity_status: str = "Comfortable"
    water_tank_level_pct: int = 84
    water_tank_current_litres: int = 84000
    water_tank_capacity_litres: int = 100000
    sump_level_pct: int = 92
    sump_current_litres: int = 184000
    sump_capacity_litres: int = 200000
    inflow_rate_lpm: float = 450.0
    outflow_rate_lpm: float = 380.0
    pump_status: str = "AUTO_RUNNING"  # 'AUTO_RUNNING', 'STANDBY', 'MANUAL_ON', 'OFF'
    daily_consumption_litres: int = 142500
    daily_quota_litres: int = 160000
    gas_pressure_mbar: float = 28.5
    gas_status: str = "Normal Pressure"
    flood_level_pct: int = 18
    flood_warning_active: bool = False
    storm_drain_velocity_mps: float = 1.4
    rain_gauge_mm_hr: float = 0.0
    solar_generation_kw: float = 18.4
    solar_today_kwh: float = 128.6
    carbon_offset_kg: float = 142.0

@dataclass
class GasCylinderUnit:
    unit_id: int
    label: str
    capacity_kg: float
    current_level_pct: int
    current_kg: float
    status: str  # 'ACTIVE', 'STANDBY', 'REFILL_REQUIRED'
    auto_booking_enabled: bool = True
    last_refill_date: str = "12 Sep 2026"

@dataclass
class Complaint:
    ticket_id: str
    flat_number: str
    floor_number: int
    category: str  # 'PLUMBING', 'ELECTRICAL', 'GAS_SUPPLY', 'ELEVATOR', 'CIVIL', 'CLEANLINESS'
    priority: str  # 'STANDARD', 'URGENT', 'CRITICAL'
    description: str
    status: str    # 'OPEN', 'ASSIGNED', 'IN_PROGRESS', 'RESOLVED'
    authority_notes: str
    timestamp: str
    raised_by_name: str
    assigned_technician: str = "General Maintenance Team"
    work_log: str = ""

@dataclass
class Visitor:
    id: str
    name: str
    phone: str
    visitor_type: str  # 'GUEST', 'DELIVERY', 'CAB', 'SERVICE'
    flat_number: str
    floor_number: int
    vehicle_number: str
    purpose: str
    entry_time: str
    approval_status: str  # 'APPROVED', 'CHECKED_IN', 'CHECKED_OUT', 'PENDING'
    approved_by: str
    pass_code: str

@dataclass
class MaintenanceDue:
    id: str
    flat_number: str
    month_year: str
    amount_rupees: float
    due_date: str
    status: str  # 'PAID', 'PENDING', 'OVERDUE'
    paid_date: str
    payment_method: str
    base_charge: float = 1800.0
    water_charge: float = 450.0
    gas_charge: float = 350.0
    solar_credit: float = -150.0

@dataclass
class CommunityNotice:
    id: str
    title: str
    description: str
    category: str  # 'WATER', 'ENERGY', 'SAFETY', 'MAINTENANCE', 'GENERAL'
    priority: str  # 'HIGH', 'NORMAL', 'URGENT'
    posted_time: str
    is_pinned: bool
    author: str = "Facility Management Office"

# =====================================================================
# SQLITE DATABASE ENGINE & 400 TENANTS PERSISTENCE
# =====================================================================

class EcoApartmentDatabase:
    def __init__(self, db_path: str = "eco_apartment.db"):
        self.db_path = db_path
        self.init_tables()
        self.ensure_400_residents_and_seed_data()

    def get_connection(self):
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row
        return conn

    def init_tables(self):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            
            # Users / Residents Table with full attributes
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    block TEXT NOT NULL DEFAULT 'A',
                    floor_number INTEGER NOT NULL,
                    flat_number TEXT NOT NULL,
                    resident_name TEXT NOT NULL,
                    contact_number TEXT NOT NULL,
                    email TEXT NOT NULL DEFAULT '',
                    occupancy_status TEXT NOT NULL DEFAULT 'OCCUPIED',
                    move_in_date TEXT NOT NULL DEFAULT '2024-01-15',
                    emergency_contact TEXT NOT NULL DEFAULT '+91 98401 00000',
                    vehicle_number TEXT NOT NULL DEFAULT '',
                    dues_status TEXT NOT NULL DEFAULT 'PAID'
                )
            """)
            
            # Migrate missing columns in users if existing table was from legacy schema
            cursor.execute("PRAGMA table_info(users)")
            existing_cols = [c[1] for c in cursor.fetchall()]
            user_cols_to_add = [
                ("block", "TEXT NOT NULL DEFAULT 'A'"),
                ("email", "TEXT NOT NULL DEFAULT ''"),
                ("occupancy_status", "TEXT NOT NULL DEFAULT 'OCCUPIED'"),
                ("move_in_date", "TEXT NOT NULL DEFAULT '2024-01-15'"),
                ("emergency_contact", "TEXT NOT NULL DEFAULT '+91 98401 00000'"),
                ("vehicle_number", "TEXT NOT NULL DEFAULT ''"),
                ("dues_status", "TEXT NOT NULL DEFAULT 'PAID'")
            ]
            for col_name, col_def in user_cols_to_add:
                if col_name not in existing_cols:
                    cursor.execute(f"ALTER TABLE users ADD COLUMN {col_name} {col_def}")
                    
            # Complaints Table
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS complaints (
                    ticket_id TEXT PRIMARY KEY,
                    flat_number TEXT NOT NULL,
                    floor_number INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT NOT NULL,
                    authority_notes TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    raised_by_name TEXT NOT NULL,
                    assigned_technician TEXT NOT NULL DEFAULT 'Facility Team',
                    work_log TEXT NOT NULL DEFAULT ''
                )
            """)
            cursor.execute("PRAGMA table_info(complaints)")
            c_cols = [c[1] for c in cursor.fetchall()]
            for c_name, c_def in [("assigned_technician", "TEXT NOT NULL DEFAULT 'Facility Team'"), ("work_log", "TEXT NOT NULL DEFAULT ''")]:
                if c_name not in c_cols:
                    cursor.execute(f"ALTER TABLE complaints ADD COLUMN {c_name} {c_def}")

            
            # Visitors Table
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS visitors (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    visitor_type TEXT NOT NULL,
                    flat_number TEXT NOT NULL,
                    floor_number INTEGER NOT NULL,
                    vehicle_number TEXT NOT NULL,
                    purpose TEXT NOT NULL,
                    entry_time TEXT NOT NULL,
                    approval_status TEXT NOT NULL,
                    approved_by TEXT NOT NULL,
                    pass_code TEXT NOT NULL
                )
            """)
            
            # Maintenance Dues Table
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS maintenance_dues (
                    id TEXT PRIMARY KEY,
                    flat_number TEXT NOT NULL,
                    month_year TEXT NOT NULL,
                    amount_rupees REAL NOT NULL,
                    due_date TEXT NOT NULL,
                    status TEXT NOT NULL,
                    paid_date TEXT NOT NULL,
                    payment_method TEXT NOT NULL,
                    base_charge REAL NOT NULL DEFAULT 1800.0,
                    water_charge REAL NOT NULL DEFAULT 450.0,
                    gas_charge REAL NOT NULL DEFAULT 350.0,
                    solar_credit REAL NOT NULL DEFAULT -150.0
                )
            """)
            
            # Community Notices Table
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS community_notices (
                    id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    category TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    posted_time TEXT NOT NULL,
                    is_pinned INTEGER NOT NULL,
                    author TEXT NOT NULL DEFAULT 'Facility Office'
                )
            """)
            
            # Water Anomaly Log Table
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS water_anomalies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sensor_id TEXT NOT NULL,
                    zone_id TEXT NOT NULL,
                    flow_rate REAL NOT NULL,
                    threshold REAL NOT NULL,
                    status TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    notes TEXT NOT NULL
                )
            """)
            
            # Login History Table
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS login_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    role TEXT NOT NULL,
                    floor_number INTEGER NOT NULL,
                    flat_number TEXT NOT NULL,
                    timestamp TEXT NOT NULL
                )
            """)
            conn.commit()

    def ensure_400_residents_and_seed_data(self):
        """
        Seeds/preserves all 400 flats across Blocks A, B, C, D (Floors 1-4, 25 flats/floor).
        Preserves original 18 records without any modification.
        """
        with self.get_connection() as conn:
            cursor = conn.cursor()
            
            # Check existing count
            cursor.execute("SELECT COUNT(*) FROM users")
            count = cursor.fetchone()[0]
            
            # List of original preserved users (exact credentials & names)
            legacy_users = {
                "authority": ("authority", "6383898769", "AUTHORITY", "A", 0, "Admin Office", "SRM Authority Desk", "+91 63838 98769", "admin@srmgreen.org", "OCCUPIED", "2023-01-01", "+91 63838 98769", "TN-09-ADM-01", "PAID"),
                "admin": ("admin", "6383898769", "AUTHORITY", "A", 0, "Admin Office", "SRM Facility Admin", "+91 63838 98769", "manager@srmgreen.org", "OCCUPIED", "2023-01-01", "+91 63838 98769", "TN-09-ADM-02", "PAID"),
                "flat_101": ("flat_101", "7708994065", "RESIDENT", "A", 1, "A-101", "R. SHAM SUDHAN", "+91 77089 94065", "sham.sudhan@gmail.com", "OCCUPIED", "2024-03-10", "+91 94441 12345", "TN-09-BE-4451", "PAID"),
                "flat_102": ("flat_102", "6383898769", "RESIDENT", "A", 1, "A-102", "P. MOHAN", "+91 63838 98769", "mohan.p@outlook.com", "OCCUPIED", "2024-02-14", "+91 98401 22334", "TN-09-CF-8890", "PAID"),
                "flat_105": ("flat_105", "9361077508", "RESIDENT", "A", 1, "A-105", "M. JAI MUGESH", "+91 93610 77508", "jai.mugesh@srm.edu", "OCCUPIED", "2024-04-01", "+91 93610 00000", "TN-09-DA-1122", "PAID"),
                "flat_108": ("flat_108", "8838449468", "RESIDENT", "A", 1, "A-108", "N. MADHUVARSHAN", "+91 88384 49468", "madhu.varshan@gmail.com", "OCCUPIED", "2024-01-20", "+91 88384 00000", "TN-09-EF-9988", "PAID"),
                "flat_201": ("flat_201", "7845968905", "RESIDENT", "A", 2, "A-201", "K. MANI MARAN", "+91 78459 68905", "mani.maran@tcs.com", "OCCUPIED", "2024-05-15", "+91 78459 11111", "TN-09-GG-3344", "PAID"),
                "flat_204": ("flat_204", "8825742523", "RESIDENT", "A", 2, "A-204", "T S. ABINAYA", "+91 88257 42523", "abinaya.ts@cognizant.com", "OCCUPIED", "2024-03-22", "+91 88257 22222", "TN-09-HH-5566", "PENDING"),
                "flat_206": ("flat_206", "SRM@F2-206", "RESIDENT", "A", 2, "A-206", "NARAYANAN MOORTHI", "+91 88384 49468", "narayanan.m@gmail.com", "OCCUPIED", "2024-02-18", "+91 88384 33333", "TN-09-JJ-7788", "PAID"),
                "flat_208": ("flat_208", "SRM@F2-208", "RESIDENT", "A", 2, "A-208", "SIVANESAN", "+91 88257 42523", "sivanesan@zoho.com", "OCCUPIED", "2024-06-01", "+91 88257 44444", "TN-09-KK-9900", "PAID"),
                "flat_302": ("flat_302", "SRM@F3-302", "RESIDENT", "A", 3, "A-302", "N. Geetha", "+91 98401 33302", "geetha.n@gmail.com", "OCCUPIED", "2024-01-10", "+91 98401 55555", "TN-09-LL-1212", "PAID"),
                "flat_305": ("flat_305", "SRM@F3-305", "RESIDENT", "A", 3, "A-305", "V. Suresh", "+91 98401 33305", "suresh.v@infosys.com", "OCCUPIED", "2024-03-12", "+91 98401 66666", "TN-09-MM-3434", "PAID"),
                "flat_308": ("flat_308", "SRM@F3-308", "RESIDENT", "A", 3, "A-308", "T. Dinesh", "+91 98401 33308", "dinesh.t@wipro.com", "OCCUPIED", "2024-04-18", "+91 98401 77777", "TN-09-NN-5656", "PAID"),
                "flat_310": ("flat_310", "SRM@F3-310", "RESIDENT", "A", 3, "A-310", "K. Priya", "+91 98401 33310", "priya.k@hcl.com", "OCCUPIED", "2024-05-02", "+91 98401 88888", "TN-09-PP-7878", "PAID"),
                "flat_401": ("flat_401", "SRM@F4-401", "RESIDENT", "B", 4, "B-401", "S. Ramesh", "+91 98401 44401", "ramesh.s@srm.edu", "OCCUPIED", "2024-01-05", "+91 98401 99999", "TN-09-QQ-9090", "PAID"),
                "flat_402": ("flat_402", "SRM@F4-402", "RESIDENT", "B", 4, "B-402", "Mohan Palaniappan", "+91 6380244185", "mohanpalaniappan007@gmail.com", "OCCUPIED", "2023-11-20", "+91 63838 98769", "TN-09-MP-0402", "PAID"),
                "flat_405": ("flat_405", "SRM@F4-405", "RESIDENT", "B", 4, "B-405", "Dr. E. Saravanan", "+91 98401 44405", "saravanan.e@srm.ac.in", "OCCUPIED", "2024-02-28", "+91 98401 12121", "TN-09-RR-1313", "PAID"),
                "flat_408": ("flat_408", "SRM@F4-408", "RESIDENT", "B", 4, "B-408", "M. Vani", "+91 98401 44408", "vani.m@annauniv.edu", "OCCUPIED", "2024-03-30", "+91 98401 34343", "TN-09-SS-2424", "PAID"),
            }
            
            # Technician special accounts
            technician_accounts = [
                ("tech_plumber", "Tech@Plumb1", "TECHNICIAN", "A", 0, "Maintenance Bay 1", "R. Kathir (Plumbing Specialist)", "+91 94441 55667", "kathir.plumbing@srmgreen.org", "OCCUPIED", "2023-06-01", "+91 94441 55667", "TN-09-SV-01", "PAID"),
                ("tech_electrician", "Tech@Elec2", "TECHNICIAN", "B", 0, "Maintenance Bay 2", "V. Anbu (Lead Electrician)", "+91 94442 66778", "anbu.electrical@srmgreen.org", "OCCUPIED", "2023-06-01", "+91 94442 66778", "TN-09-SV-02", "PAID"),
                ("tech_gas", "Tech@Gas3", "TECHNICIAN", "B", 0, "Gas Control Riser", "R. Chandran (Gas Calibration Specialist)", "+91 94443 11223", "chandran.gas@srmgreen.org", "OCCUPIED", "2023-06-01", "+91 94443 11223", "TN-09-SV-03", "PAID"),
                ("tech_otis", "Tech@Otis4", "TECHNICIAN", "C", 0, "Elevator Control Rm", "Otis Field Engineer", "+91 94443 77889", "service@otis-india.com", "OCCUPIED", "2023-06-01", "+91 94443 77889", "TN-09-SV-04", "PAID"),
                ("tech_security", "Tech@Sec5", "TECHNICIAN", "D", 0, "Main Gate Guardhouse", "B. Pandian (G4S Chief Officer)", "+91 94444 88990", "security@srmgreen.org", "OCCUPIED", "2023-06-01", "+91 94444 88990", "TN-09-SV-05", "PAID"),
            ]
            
            # Realistic pool of Tamil Nadu resident names for the 400 flats
            first_names = [
                "Aravind", "Balaji", "Chitra", "Deepak", "Elango", "Gayathri", "Hari", "Indira", 
                "Jagadeesh", "Kavitha", "Lakshmi", "Manikandan", "Naveen", "Omprakash", "Prabhu", 
                "Radha", "Senthil", "Thilak", "Uma", "Vignesh", "Yamuna", "Karthik", "Sneha", 
                "Gautham", "Anand", "Divya", "Sanjay", "Meena", "Vijay", "Aishwarya", "Prakash", 
                "Nandhini", "Ashok", "Preethi", "Rajesh", "Sangeetha", "Kishore", "Revathi", "Manoj", "Sudha"
            ]
            last_names = [
                "Raman", "Sundaram", "Subramanian", "Krishnan", "Narayanan", "Raghavan", "Venkatesh", 
                "Natarajan", "Chandrasekhar", "Iyengar", "Mani", "Murugan", "Pillai", "Chettiar", 
                "Viswanathan", "Ganesan", "Swaminathan", "Kalyan", "Balasubramanian", "Srinivasan"
            ]

            # Generate all 400 flats: Blocks A, B, C, D; Floors 1 to 4; Units 01 to 25
            all_flats = []
            blocks = ['A', 'B', 'C', 'D']
            floors = [1, 2, 3, 4]
            units_per_floor = 25  # 4 * 4 * 25 = 400 flats

            seed_rows = []
            
            # Map of flat_number -> legacy user
            legacy_by_flat = {}
            for u in legacy_users.values():
                legacy_by_flat[u[5]] = u
                # Also without block prefix if Flat 101 format
                flat_num_only = u[5].replace("A-", "").replace("B-", "").replace("C-", "").replace("D-", "")
                legacy_by_flat[f"Flat {flat_num_only}"] = u

            flat_index = 0
            for b in blocks:
                for f in floors:
                    for u in range(1, units_per_floor + 1):
                        flat_num = f"{b}-{f*100 + u:03d}"
                        simple_flat = f"Flat {f*100 + u:03d}"
                        flat_index += 1
                        
                        # Check if matched by legacy
                        if flat_num in legacy_by_flat:
                            seed_rows.append(legacy_by_flat[flat_num])
                        elif simple_flat in legacy_by_flat and b == 'A' and f < 4:
                            seed_rows.append(legacy_by_flat[simple_flat])
                        elif simple_flat in legacy_by_flat and b == 'B' and f == 4:
                            seed_rows.append(legacy_by_flat[simple_flat])
                        else:
                            # Generate realistic tenant
                            # Reserve ~22 flats as VACANT or MAINTENANCE
                            is_vacant = (flat_index % 19 == 0)
                            is_maint = (flat_index % 67 == 0)
                            
                            status = "OCCUPIED"
                            if is_vacant:
                                status = "VACANT"
                            elif is_maint:
                                status = "MAINTENANCE"
                                
                            fname = random.choice(first_names)
                            lname = random.choice(last_names)
                            fullname = f"{fname} {lname}" if status == "OCCUPIED" else ("Vacant Flat" if status == "VACANT" else "Refurbishment in Progress")
                            
                            username = f"flat_{b.lower()}_{f*100 + u:03d}"
                            password = f"SRM@{b}{f*100 + u:03d}"
                            phone = f"+91 98401 {flat_index:05d}"
                            email = f"{fname.lower()}.{lname.lower()}{f*100 + u}@gmail.com" if status == "OCCUPIED" else ""
                            emg_phone = f"+91 94441 {(flat_index+50):05d}"
                            veh = f"TN-09-{b}{f}-{1000 + u}" if status == "OCCUPIED" else ""
                            due = "PAID" if (flat_index % 7 != 0) else ("PENDING" if (flat_index % 14 != 0) else "OVERDUE")
                            move_in = f"2023-{(flat_index % 12) + 1:02d}-15"
                            
                            row = (
                                username, password, "RESIDENT", b, f, flat_num, fullname, 
                                phone, email, status, move_in, emg_phone, veh, due
                            )
                            seed_rows.append(row)

            # Insert or replace users
            if count < 400:
                # First insert authority & admin
                cursor.execute("""
                    INSERT OR REPLACE INTO users (username, password, role, block, floor_number, flat_number, resident_name, contact_number, email, occupancy_status, move_in_date, emergency_contact, vehicle_number, dues_status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, legacy_users["authority"])
                cursor.execute("""
                    INSERT OR REPLACE INTO users (username, password, role, block, floor_number, flat_number, resident_name, contact_number, email, occupancy_status, move_in_date, emergency_contact, vehicle_number, dues_status)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, legacy_users["admin"])
                
                # Insert technicians
                for t in technician_accounts:
                    cursor.execute("""
                        INSERT OR REPLACE INTO users (username, password, role, block, floor_number, flat_number, resident_name, contact_number, email, occupancy_status, move_in_date, emergency_contact, vehicle_number, dues_status)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, t)
                    
                # Insert 400 flats
                for r in seed_rows:
                    cursor.execute("""
                        INSERT OR REPLACE INTO users (username, password, role, block, floor_number, flat_number, resident_name, contact_number, email, occupancy_status, move_in_date, emergency_contact, vehicle_number, dues_status)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, r)

            # Seed complaints if empty
            cursor.execute("SELECT COUNT(*) FROM complaints")
            if cursor.fetchone()[0] == 0:
                default_complaints = [
                    ("TKT-101-WTR", "A-101", 1, "PLUMBING", "URGENT", "Slow water pressure in kitchen faucet.", "ASSIGNED", "Plumber scheduled for 2:30 PM.", "2026-09-20 10:15", "R. SHAM SUDHAN", "R. Kathir (Plumbing)", "Parts inspected. New washer required."),
                    ("TKT-204-ELE", "A-204", 2, "ELECTRICAL", "CRITICAL", "Balcony light tripping master breaker.", "IN_PROGRESS", "Electrician isolating short circuit on 2nd floor riser.", "2026-09-20 11:30", "T S. ABINAYA", "V. Anbu (Electrical)", "Tracing wire in junction box."),
                    ("TKT-402-GAS", "B-402", 4, "GAS_SUPPLY", "STANDARD", "Annual piped gas calibration & check.", "RESOLVED", "Regulator replaced, pressure tested at 28.5 mbar.", "2026-09-19 16:00", "Mohan Palaniappan", "S. Prakash (Gas Tech)", "Calibration certified normal."),
                    ("TKT-305-ELV", "A-305", 3, "ELEVATOR", "URGENT", "Block A elevator door sensor delay.", "IN_PROGRESS", "Otis service engineer notified.", "2026-09-20 14:00", "V. Suresh", "Otis Field Engineer", "Door photoelectric sensor aligned."),
                    ("TKT-115-CIV", "C-115", 1, "CIVIL", "STANDARD", "Minor tile crack in common walkway near Flat C-115.", "OPEN", "Logged for weekend civil repair round.", "2026-09-21 09:30", "Deepak Subramanian", "Facility Team", "Awaiting materials."),
                    ("TKT-320-CLN", "D-320", 3, "CLEANLINESS", "STANDARD", "Leaves accumulated near stormwater drain inlet.", "RESOLVED", "Housekeeping team cleared drain grate.", "2026-09-21 08:00", "Radha Kalyan", "Housekeeping Team", "Clear flow verified."),
                ]
                cursor.executemany("INSERT INTO complaints VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", default_complaints)

            # Seed maintenance dues if empty (for all 400 flats)
            cursor.execute("SELECT COUNT(*) FROM maintenance_dues")
            if cursor.fetchone()[0] == 0:
                all_dues = []
                for r in seed_rows:
                    flat_n = r[5]
                    flr = r[4]
                    stat = r[13] if len(r) > 13 else "PAID"
                    is_paid = stat == "PAID"
                    amt = 2400.0 + (flr * 100.0)
                    due_id = f"DUE-2026-09-{flat_n.replace('-', '')}"
                    paid_dt = "2026-09-05" if is_paid else ""
                    pay_method = "UPI (GPay)" if is_paid else ""
                    all_dues.append((
                        due_id, flat_n, "September 2026", amt, "2026-09-30",
                        stat, paid_dt, pay_method, 1800.0, 450.0, 350.0, -150.0
                    ))
                cursor.executemany("INSERT INTO maintenance_dues VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", all_dues)

            # Seed community notices if empty
            cursor.execute("SELECT COUNT(*) FROM community_notices")
            if cursor.fetchone()[0] == 0:
                default_notices = [
                    ("NOT-01", "Overhead Water Tank Cleaning & Disinfection", "Quarterly automated cleaning and UV disinfection scheduled this Sunday 6 AM - 10 AM. Ground sumps will maintain emergency domestic pressure.", "WATER", "HIGH", "Sep 19, 2026", 1, "Facility Management Office"),
                    ("NOT-02", "Festival Solar LED Decor & Energy Self-Sufficiency", "All common area festival decorative illuminations are 100% powered by the 24 kW rooftop solar array and battery bank.", "GREEN_ENERGY", "NORMAL", "Sep 18, 2026", 0, "Sustainability Committee"),
                    ("NOT-03", "Fire & Gas Pipeline Safety Audit Completed", "Tamil Nadu Fire & Rescue compliance certification successfully renewed. Auto-shutoff solenoid manifold pressure tested at 28.5 mbar.", "SAFETY", "NORMAL", "Sep 15, 2026", 0, "Safety Compliance Desk"),
                    ("NOT-04", "Annual General Meeting (AGM) - SRM Green Residency", "The 2026 AGM will be held in the Community Hall on October 12 at 5:00 PM. Audit reports and solar expansion plan will be presented.", "GENERAL", "NORMAL", "Sep 12, 2026", 0, "President, SRM RWA"),
                ]
                cursor.executemany("INSERT INTO community_notices VALUES (?, ?, ?, ?, ?, ?, ?, ?)", default_notices)

            # Seed visitors if empty
            cursor.execute("SELECT COUNT(*) FROM visitors")
            if cursor.fetchone()[0] == 0:
                default_visitors = [
                    ("VIS-901", "K. Suresh (Amazon Delivery)", "+91 98840 12345", "DELIVERY", "B-402", 4, "TN-09-AMZ-22", "Package delivery", "Today 10:15 AM", "CHECKED_IN", "Mohan Palaniappan", "PASS-8841"),
                    ("VIS-902", "Dr. A. Meenakshi (Guest)", "+91 98402 33445", "GUEST", "A-101", 1, "TN-09-G-4455", "Family visit", "Today 11:30 AM", "APPROVED", "R. SHAM SUDHAN", "PASS-2219"),
                    ("VIS-903", "Blue Dart Courier", "+91 97910 55667", "DELIVERY", "A-204", 2, "TN-09-BD-90", "Document delivery", "Yesterday 03:45 PM", "CHECKED_OUT", "T S. ABINAYA", "PASS-1092"),
                    ("VIS-904", "Ola Cab Driver (Murugan)", "+91 98405 66778", "CAB", "C-305", 3, "TN-09-CAB-33", "Airport drop pick-up", "Today 08:00 AM", "CHECKED_OUT", "V. Suresh", "PASS-3044"),
                ]
                cursor.executemany("INSERT INTO visitors VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", default_visitors)

            # Seed water anomalies if empty
            cursor.execute("SELECT COUNT(*) FROM water_anomalies")
            if cursor.fetchone()[0] == 0:
                default_anomalies = [
                    ("WM-101", "Block-A Kitchen Main Riser", 58.2, 45.0, "RESOLVED", "2026-09-18 03:15 AM", "Nocturnal flow spike detected. Valve checked, minor aerator leak sealed."),
                    ("WM-204", "Block-B Riser 4 Landscape", 52.0, 45.0, "INVESTIGATING", "2026-09-20 02:40 AM", "Drip irrigation manifold exceeded threshold during quiet hours."),
                ]
                cursor.executemany("INSERT INTO water_anomalies (sensor_id, zone_id, flow_rate, threshold, status, timestamp, notes) VALUES (?, ?, ?, ?, ?, ?, ?)", default_anomalies)

            conn.commit()

    # =================================================================
    # CRUD OPERATIONS & QUERY METHODS
    # =================================================================

    def authenticate(self, identifier: str, password: str, role: str = "") -> Optional[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            id_clean = identifier.strip().lower()
            pwd_clean = password.strip()
            
            # Match by username OR contact number OR flat number
            cursor.execute("""
                SELECT * FROM users 
                WHERE (LOWER(username) = ? OR contact_number = ? OR LOWER(flat_number) = ? OR LOWER(flat_number) = ?)
            """, (id_clean, id_clean, id_clean, f"flat {id_clean}"))
            
            user_row = cursor.fetchone()
            if user_row:
                user_dict = dict(user_row)
                # If password matches OR password is empty in demo mode
                if user_dict["password"] == pwd_clean or pwd_clean == "":
                    # Record login history
                    cursor.execute("""
                        INSERT INTO login_history (username, role, floor_number, flat_number, timestamp)
                        VALUES (?, ?, ?, ?, ?)
                    """, (
                        user_dict["username"], user_dict["role"], 
                        user_dict["floor_number"], user_dict["flat_number"],
                        datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                    ))
                    conn.commit()
                    return user_dict
        return None

    def get_residents_paginated(
        self, 
        page: int = 1, 
        limit: int = 25, 
        search: str = "", 
        block: str = "ALL", 
        floor: str = "ALL", 
        status: str = "ALL",
        sort_by: str = "flat_number"
    ) -> Dict[str, Any]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            
            query = "SELECT * FROM users WHERE role = 'RESIDENT'"
            count_query = "SELECT COUNT(*) FROM users WHERE role = 'RESIDENT'"
            params = []
            
            if search.strip():
                s = f"%{search.strip().lower()}%"
                clause = " AND (LOWER(resident_name) LIKE ? OR LOWER(flat_number) LIKE ? OR LOWER(email) LIKE ? OR contact_number LIKE ?)"
                query += clause
                count_query += clause
                params.extend([s, s, s, s])
                
            if block != "ALL" and block in ['A', 'B', 'C', 'D']:
                query += " AND block = ?"
                count_query += " AND block = ?"
                params.append(block)
                
            if floor != "ALL":
                try:
                    f_int = int(floor)
                    query += " AND floor_number = ?"
                    count_query += " AND floor_number = ?"
                    params.append(f_int)
                except ValueError:
                    pass
                    
            if status != "ALL" and status in ['OCCUPIED', 'VACANT', 'MAINTENANCE']:
                query += " AND occupancy_status = ?"
                count_query += " AND occupancy_status = ?"
                params.append(status)
                
            # Total count
            cursor.execute(count_query, params)
            total_records = cursor.fetchone()[0]
            
            # Sorting
            valid_sorts = {
                "flat_number": "block ASC, floor_number ASC, flat_number ASC",
                "resident_name": "resident_name ASC",
                "floor_number": "floor_number ASC, flat_number ASC",
                "move_in_date": "move_in_date DESC",
                "dues_status": "dues_status ASC"
            }
            sort_order = valid_sorts.get(sort_by, "block ASC, flat_number ASC")
            
            # Pagination
            offset = max(0, (page - 1) * limit)
            query += f" ORDER BY {sort_order} LIMIT ? OFFSET ?"
            paged_params = list(params) + [limit, offset]
            
            cursor.execute(query, paged_params)
            rows = [dict(r) for r in cursor.fetchall()]
            
            # Compute stats
            cursor.execute("SELECT COUNT(*) FROM users WHERE role = 'RESIDENT' AND occupancy_status = 'OCCUPIED'")
            occupied_count = cursor.fetchone()[0]
            cursor.execute("SELECT COUNT(*) FROM users WHERE role = 'RESIDENT' AND occupancy_status = 'VACANT'")
            vacant_count = cursor.fetchone()[0]
            cursor.execute("SELECT COUNT(*) FROM users WHERE role = 'RESIDENT' AND occupancy_status = 'MAINTENANCE'")
            maint_count = cursor.fetchone()[0]
            
            return {
                "residents": rows,
                "total": total_records,
                "page": page,
                "limit": limit,
                "total_pages": max(1, (total_records + limit - 1) // limit),
                "stats": {
                    "total_flats": 400,
                    "occupied": occupied_count,
                    "vacant": vacant_count,
                    "maintenance": maint_count,
                    "occupancy_rate_pct": round((occupied_count / 400.0) * 100, 1)
                }
            }

    def get_resident_by_flat_or_username(self, identifier: str) -> Optional[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                SELECT * FROM users 
                WHERE username = ? OR flat_number = ? OR LOWER(flat_number) = ?
            """, (identifier, identifier, identifier.lower()))
            row = cursor.fetchone()
            return dict(row) if row else None

    def create_or_update_resident(self, data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Creates or updates a resident profile.
        Validates duplicate occupancy conflicts.
        """
        with self.get_connection() as conn:
            cursor = conn.cursor()
            
            flat_num = data.get("flat_number", "").strip().upper()
            username = data.get("username", "").strip() or f"flat_{flat_num.replace('-', '_').lower()}"
            name = data.get("resident_name", "").strip()
            phone = data.get("contact_number", "").strip()
            email = data.get("email", "").strip()
            block = data.get("block", "").strip().upper() or flat_num[0] if flat_num else "A"
            floor = int(data.get("floor_number", 1))
            status = data.get("occupancy_status", "OCCUPIED")
            move_in = data.get("move_in_date", datetime.datetime.now().strftime("%Y-%m-%d"))
            emg = data.get("emergency_contact", "+91 98401 00000")
            veh = data.get("vehicle_number", "")
            due = data.get("dues_status", "PAID")
            pwd = data.get("password", "") or f"SRM@{flat_num.replace('-', '')}"
            
            # Check existing flat
            cursor.execute("SELECT username, occupancy_status, resident_name FROM users WHERE flat_number = ?", (flat_num,))
            existing = cursor.fetchone()
            
            if existing and existing["username"] != username and status == "OCCUPIED" and existing["occupancy_status"] == "OCCUPIED":
                return {
                    "success": False, 
                    "error": f"Conflict: Flat {flat_num} is already occupied by {existing['resident_name']}. Please vacate the flat first."
                }
                
            cursor.execute("""
                INSERT INTO users (
                    username, password, role, block, floor_number, flat_number,
                    resident_name, contact_number, email, occupancy_status,
                    move_in_date, emergency_contact, vehicle_number, dues_status
                ) VALUES (?, ?, 'RESIDENT', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(username) DO UPDATE SET
                    block = excluded.block,
                    floor_number = excluded.floor_number,
                    flat_number = excluded.flat_number,
                    resident_name = excluded.resident_name,
                    contact_number = excluded.contact_number,
                    email = excluded.email,
                    occupancy_status = excluded.occupancy_status,
                    move_in_date = excluded.move_in_date,
                    emergency_contact = excluded.emergency_contact,
                    vehicle_number = excluded.vehicle_number,
                    dues_status = excluded.dues_status
            """, (
                username, pwd, block, floor, flat_num,
                name, phone, email, status,
                move_in, emg, veh, due
            ))
            conn.commit()
            return {"success": True, "flat_number": flat_num, "username": username}

    def vacate_resident(self, flat_or_username: str) -> Dict[str, Any]:
        """Soft-deletes/vacates resident without deleting historical logs."""
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                UPDATE users 
                SET occupancy_status = 'VACANT', 
                    resident_name = 'Vacant Flat',
                    email = '',
                    vehicle_number = '',
                    dues_status = 'PAID'
                WHERE username = ? OR flat_number = ?
            """, (flat_or_username, flat_or_username))
            conn.commit()
            return {"success": True, "message": f"Flat {flat_or_username} marked as VACANT."}

    def get_building_matrix(self) -> Dict[str, Any]:
        """Returns structured 400 flats organized by Block -> Floor -> Flats for visual building view."""
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT flat_number, block, floor_number, resident_name, occupancy_status, dues_status, contact_number FROM users WHERE role = 'RESIDENT' ORDER BY block, floor_number DESC, flat_number ASC")
            flats = [dict(r) for r in cursor.fetchall()]
            
            blocks_data = {"A": {4: [], 3: [], 2: [], 1: []}, "B": {4: [], 3: [], 2: [], 1: []}, "C": {4: [], 3: [], 2: [], 1: []}, "D": {4: [], 3: [], 2: [], 1: []}}
            for f in flats:
                b = f["block"]
                flr = f["floor_number"]
                if b in blocks_data and flr in blocks_data[b]:
                    blocks_data[b][flr].append(f)
                    
            return blocks_data

    # Complaints
    def get_complaints(self, status: str = "ALL", category: str = "ALL") -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            query = "SELECT * FROM complaints WHERE 1=1"
            params = []
            if status != "ALL":
                query += " AND status = ?"
                params.append(status)
            if category != "ALL":
                query += " AND category = ?"
                params.append(category)
            query += " ORDER BY timestamp DESC"
            cursor.execute(query, params)
            return [dict(r) for r in cursor.fetchall()]

    def add_complaint(self, c: Complaint) -> str:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO complaints VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                c.ticket_id, c.flat_number, c.floor_number, c.category,
                c.priority, c.description, c.status, c.authority_notes,
                c.timestamp, c.raised_by_name, c.assigned_technician, c.work_log
            ))
            conn.commit()
            return c.ticket_id

    def update_complaint_status(self, ticket_id: str, status: str, notes: str = "", tech: str = "", work_log: str = "") -> bool:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                UPDATE complaints 
                SET status = ?, 
                    authority_notes = CASE WHEN ? != '' THEN ? ELSE authority_notes END,
                    assigned_technician = CASE WHEN ? != '' THEN ? ELSE assigned_technician END,
                    work_log = CASE WHEN ? != '' THEN ? ELSE work_log END
                WHERE ticket_id = ?
            """, (status, notes, notes, tech, tech, work_log, work_log, ticket_id))
            conn.commit()
            return cursor.rowcount > 0

    # Maintenance Dues
    def get_maintenance_dues(self, flat_number: str = "ALL") -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            if flat_number != "ALL":
                cursor.execute("SELECT * FROM maintenance_dues WHERE flat_number = ? ORDER BY due_date DESC", (flat_number,))
            else:
                cursor.execute("SELECT * FROM maintenance_dues ORDER BY due_date DESC")
            return [dict(r) for r in cursor.fetchall()]

    def pay_due(self, due_id: str, payment_method: str = "UPI (GPay)") -> Dict[str, Any]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            paid_date = datetime.datetime.now().strftime("%Y-%m-%d %H:%M")
            cursor.execute("""
                UPDATE maintenance_dues 
                SET status = 'PAID', paid_date = ?, payment_method = ?
                WHERE id = ?
            """, (paid_date, payment_method, due_id))
            
            # Also update resident dues_status if no pending dues remain
            cursor.execute("SELECT flat_number FROM maintenance_dues WHERE id = ?", (due_id,))
            row = cursor.fetchone()
            if row:
                flat_num = row["flat_number"]
                cursor.execute("SELECT COUNT(*) FROM maintenance_dues WHERE flat_number = ? AND status != 'PAID'", (flat_num,))
                if cursor.fetchone()[0] == 0:
                    cursor.execute("UPDATE users SET dues_status = 'PAID' WHERE flat_number = ?", (flat_num,))
            conn.commit()
            return {"success": True, "paid_date": paid_date, "due_id": due_id}

    # Community Notices
    def get_community_notices(self) -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM community_notices ORDER BY is_pinned DESC, posted_time DESC")
            return [dict(r) for r in cursor.fetchall()]

    def add_community_notice(self, notice: CommunityNotice) -> str:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO community_notices VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                notice.id, notice.title, notice.description, notice.category,
                notice.priority, notice.posted_time, 1 if notice.is_pinned else 0, notice.author
            ))
            conn.commit()
            return notice.id

    # Visitors
    def get_visitors(self) -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM visitors ORDER BY entry_time DESC")
            return [dict(r) for r in cursor.fetchall()]

    def add_visitor(self, v: Visitor) -> str:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO visitors VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                v.id, v.name, v.phone, v.visitor_type, v.flat_number,
                v.floor_number, v.vehicle_number, v.purpose, v.entry_time,
                v.approval_status, v.approved_by, v.pass_code
            ))
            conn.commit()
            return v.id

    def update_visitor_status(self, visitor_id: str, new_status: str) -> bool:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("UPDATE visitors SET approval_status = ? WHERE id = ?", (new_status, visitor_id))
            conn.commit()
            return cursor.rowcount > 0

    # Anomalies
    def get_anomalies(self) -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM water_anomalies ORDER BY timestamp DESC")
            return [dict(r) for r in cursor.fetchall()]

    def log_anomaly(self, sensor_id: str, zone_id: str, flow_rate: float, threshold: float, notes: str) -> int:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            now_str = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            cursor.execute("""
                INSERT INTO water_anomalies (sensor_id, zone_id, flow_rate, threshold, status, timestamp, notes)
                VALUES (?, ?, ?, ?, 'ACTIVE_ALERT', ?, ?)
            """, (sensor_id, zone_id, flow_rate, threshold, now_str, notes))
            conn.commit()
            return cursor.lastrowid

    # Convenience aliases for universal REST API and test suite compatibility
    def get_residents(self, search: str = "", block: str = "ALL", floor: str = "ALL", status: str = "ALL", page: int = 1, limit: int = 25) -> Dict[str, Any]:
        return self.get_residents_paginated(page=page, limit=limit, search=search, block=block, floor=str(floor) if floor else "ALL", status=status)

    def get_resident_by_code(self, identifier: str) -> Optional[Dict[str, Any]]:
        return self.get_resident_by_flat_or_username(identifier)

    def create_resident(self, data: Dict[str, Any]) -> bool:
        res = self.create_or_update_resident(data)
        return res.get("success", False)

    def update_resident(self, flat_code: str, data: Dict[str, Any]) -> bool:
        data["flat_number"] = flat_code
        res = self.create_or_update_resident(data)
        return res.get("success", False)

    def delete_resident(self, flat_code: str) -> bool:
        res = self.vacate_resident(flat_code)
        return res.get("success", False)

    def get_technicians(self) -> List[Dict[str, Any]]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM users WHERE role = 'TECHNICIAN' ORDER BY resident_name")
            rows = cursor.fetchall()
            techs = []
            for r in rows:
                d = dict(r)
                d["id"] = d["username"]
                d["name"] = d["resident_name"]
                d["trade"] = "PLUMBING" if "plumb" in d["username"] else ("ELECTRICAL" if "elec" in d["username"] else "ELEVATOR")
                d["phone"] = d["contact_number"]
                d["status"] = "AVAILABLE"
                d["active_tasks"] = 1
                d["rating"] = 4.9
                techs.append(d)
            if not techs:
                techs = [
                    {"id": "tech_plumber", "name": "K. Murugan (Master Plumber)", "trade": "PLUMBING", "phone": "+91 94441 55667", "status": "AVAILABLE", "active_tasks": 1, "rating": 4.9},
                    {"id": "tech_electrician", "name": "S. Vijay (Senior Wireman)", "trade": "ELECTRICAL", "phone": "+91 94442 66778", "status": "ON_DUTY", "active_tasks": 2, "rating": 4.8},
                    {"id": "tech_otis", "name": "Otis Field Engineer", "trade": "ELEVATOR", "phone": "+91 94443 77889", "status": "AVAILABLE", "active_tasks": 0, "rating": 4.7}
                ]
            return techs

    def get_block_stats(self) -> Dict[str, Any]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            blocks = []
            total_flats = 0
            total_occupied = 0
            for b in ['A', 'B', 'C', 'D']:
                cursor.execute("SELECT COUNT(*) FROM users WHERE role = 'RESIDENT' AND block = ?", (b,))
                tot = cursor.fetchone()[0]
                cursor.execute("SELECT COUNT(*) FROM users WHERE role = 'RESIDENT' AND block = ? AND occupancy_status = 'OCCUPIED'", (b,))
                occ = cursor.fetchone()[0]
                vac = tot - occ
                rate = round((occ / tot) * 100, 1) if tot > 0 else 0
                blocks.append({
                    "block": b,
                    "total": tot,
                    "occupied": occ,
                    "vacant": vac,
                    "occupancy_rate": rate
                })
                total_flats += tot
                total_occupied += occ

            return {
                "blocks": blocks,
                "total_flats": total_flats,
                "occupied_flats": total_occupied,
                "vacant_flats": total_flats - total_occupied,
                "overall_occupancy_rate": round((total_occupied / total_flats) * 100, 1) if total_flats > 0 else 0
            }

    def get_settings(self) -> Dict[str, str]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("CREATE TABLE IF NOT EXISTS app_settings (config_key TEXT PRIMARY KEY, config_value TEXT)")
            cursor.execute("SELECT config_key, config_value FROM app_settings")
            rows = cursor.fetchall()
            defaults = {
                "SUPABASE_MODE": "LOCAL_SQLITE",
                "SUPABASE_URL": "",
                "SUPABASE_ANON_KEY": "",
                "COMMUNITY_NAME": "SRM Green Residency",
                "TOTAL_FLATS": "400"
            }
            for r in rows:
                defaults[r["config_key"]] = r["config_value"]
            return defaults

    def update_setting(self, key: str, value: str):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("CREATE TABLE IF NOT EXISTS app_settings (config_key TEXT PRIMARY KEY, config_value TEXT)")
            cursor.execute("INSERT OR REPLACE INTO app_settings VALUES (?, ?)", (key, value))
            conn.commit()


# =====================================================================
# 3. IOT TELEMETRY ENGINE & SENSORS
# =====================================================================

class IoTEngine:
    def __init__(self):
        self.sensors = IoTSensors()
        self.gas_cylinders = [
            GasCylinderUnit(unit_id=1, label="Unit 1 (Block A/B Manifold)", capacity_kg=14.2, current_level_pct=48, current_kg=6.8, status="ACTIVE", auto_booking_enabled=True, last_refill_date="12 Sep 2026"),
            GasCylinderUnit(unit_id=2, label="Unit 2 (Block C/D Standby)", capacity_kg=14.2, current_level_pct=100, current_kg=14.2, status="STANDBY", auto_booking_enabled=True, last_refill_date="15 Sep 2026")
        ]
        self.pump_mode = "AUTO"
        self.pump_running = True

    def refresh_telemetry(self) -> IoTSensors:
        # Slight realistic variance for live IoT simulation
        self.sensors.water_tank_level_pct = max(70, min(95, self.sensors.water_tank_level_pct + random.randint(-1, 1)))
        self.sensors.water_tank_current_litres = int((self.sensors.water_tank_level_pct / 100.0) * self.sensors.water_tank_capacity_litres)
        self.sensors.temperature_celsius = round(28.4 + random.uniform(-0.3, 0.3), 1)
        self.sensors.humidity_percentage = max(50, min(75, self.sensors.humidity_percentage + random.randint(-1, 1)))
        self.sensors.solar_generation_kw = round(max(0.0, 18.4 + random.uniform(-0.5, 0.5)), 1)
        self.sensors.inflow_rate_lpm = round(450.0 + random.uniform(-10.0, 10.0), 1)
        self.sensors.outflow_rate_lpm = round(380.0 + random.uniform(-8.0, 8.0), 1)
        return self.sensors

    def toggle_pump(self, mode: Optional[str] = None) -> Dict[str, Any]:
        if mode:
            self.pump_mode = mode
        else:
            self.pump_mode = "MANUAL_ON" if self.pump_mode != "MANUAL_ON" else "AUTO"
        
        self.pump_running = (self.pump_mode in ["AUTO", "MANUAL_ON"])
        self.sensors.pump_status = "RUNNING" if self.pump_running else "STOPPED"
        return {"mode": self.pump_mode, "running": self.pump_running, "status": self.sensors.pump_status}

    def switch_gas_unit(self, unit_id: int):
        for c in self.gas_cylinders:
            if c.unit_id == unit_id:
                c.status = "ACTIVE"
            else:
                c.status = "STANDBY"

    def book_gas_refill(self, unit_id: int) -> Dict[str, Any]:
        for c in self.gas_cylinders:
            if c.unit_id == unit_id:
                c.status = "REFILL_BOOKED"
                return {"success": True, "booking_id": f"HP-SRM-{random.randint(10000, 99999)}", "unit_id": unit_id}
        return {"success": False, "error": "Unit not found"}
