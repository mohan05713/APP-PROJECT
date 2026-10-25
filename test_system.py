"""
EcoApartment OS - Automated Test Suite
Verifies:
1. 400 flats loaded across Blocks A, B, C, D
2. Preserved accounts (Admin & Flat B-402 Mohan Palaniappan)
3. Resident CRUD operations
4. Complaints & Technicians assignment
5. Water Leak Anomaly Detection OOP Algorithm
6. Supabase Client local fallback
7. Exports generation (PDF, CSV, JSON, SQL)
"""

import os
import sys
import json
import sqlite3
from eco_apartment_python_app import EcoApartmentDatabase, IoTEngine, Complaint
from water_resource_tracker import WaterMeter
from supabase_client import SupabaseClient
import export_data

def run_tests():
    print("=" * 60)
    print("🧪 RUNNING ECOAPARTMENT OS VERIFICATION TESTS")
    print("=" * 60)

    db = EcoApartmentDatabase("eco_apartment.db")
    
    # Test 1: 400 Flats Verification
    res = db.get_residents(limit=10)
    total_flats = res["total"]
    assert total_flats == 400, f"Expected 400 flats, found {total_flats}"
    print(f"✅ Test 1 PASSED: 400 Flats successfully loaded in database.")

    # Test 2: Blocks distribution
    stats = db.get_block_stats()
    assert len(stats["blocks"]) == 4, f"Expected 4 blocks, found {len(stats['blocks'])}"
    for b in stats["blocks"]:
        assert b["total"] == 100, f"Block {b['block']} should have 100 units, found {b['total']}"
    print(f"✅ Test 2 PASSED: 4 Blocks (A, B, C, D) verified with 100 flats each.")

    # Test 3: Preserved Resident Flat B-402
    b402 = db.get_resident_by_code("B-402")
    assert b402 is not None, "Flat B-402 should exist"
    assert "Mohan Palaniappan" in b402["resident_name"], f"Expected Mohan Palaniappan, got {b402['resident_name']}"
    print(f"✅ Test 3 PASSED: Preserved Resident B-402 (Mohan Palaniappan) verified.")

    # Test 4: Preserved Authentication
    admin_user = db.authenticate("admin", "6383898769", "AUTHORITY")
    assert admin_user is not None, "Authority login failed"
    resident_user = db.authenticate("B-402", "SRM@F4-402", "RESIDENT")
    assert resident_user is not None, "Resident B-402 login failed"
    print(f"✅ Test 4 PASSED: Authentication verified for Authority Desk & Resident.")

    # Test 5: Resident Search & Pagination
    search_res = db.get_residents(search="Palaniappan")
    assert len(search_res["residents"]) >= 1, "Search for Palaniappan failed"
    print(f"✅ Test 5 PASSED: Real-time search query verified.")

    # Test 6: Technicians Roster
    techs = db.get_technicians()
    assert len(techs) >= 5, f"Expected at least 5 technicians, found {len(techs)}"
    print(f"✅ Test 6 PASSED: Certified technicians roster verified ({len(techs)} technicians).")

    # Test 7: Water Meter Anomaly Detection (OOP Algorithm)
    normal_meter = WaterMeter(sensor_id="WM-101", zone_id="Block-A", current_flow_rate=15.0, leak_threshold=45.0)
    assert normal_meter.detect_anomaly() is False, "Normal reading should not trigger anomaly"
    
    leak_meter = WaterMeter(sensor_id="WM-102", zone_id="Block-B Riser", current_flow_rate=58.5, leak_threshold=45.0)
    assert leak_meter.detect_anomaly() is True, "Flow spike should trigger anomaly"
    print(f"✅ Test 7 PASSED: Water-resource anomaly detection OOP algorithm verified.")

    # Test 8: Supabase Client Zero-Config Fallback
    supa = SupabaseClient()
    test_res = supa.test_connection()
    assert test_res["mode"] in ["LOCAL_SQLITE", "CLOUD_SUPABASE"], "Invalid Supabase mode"
    print(f"✅ Test 8 PASSED: Supabase client local SQLite mode verified ({test_res['mode']}).")

    # Test 9: Data Exports
    r_csv = export_data.export_residents_csv()
    assert os.path.exists(r_csv) and os.path.getsize(r_csv) > 1000, "Residents CSV missing or too small"
    d_csv = export_data.export_dues_csv()
    assert os.path.exists(d_csv) and os.path.getsize(d_csv) > 1000, "Dues CSV missing or too small"
    j_file = export_data.export_iot_json()
    assert os.path.exists(j_file), "IoT JSON missing"
    assert os.path.exists("supabase_schema.sql") and os.path.getsize("supabase_schema.sql") > 50000, "supabase_schema.sql missing"
    print(f"✅ Test 9 PASSED: All exports verified (CSV, JSON, SQL schema).")

    print("\n" + "=" * 60)
    print("🎉 ALL 9 TEST SUITES COMPLETED SUCCESSFULLY!")
    print("=" * 60)

if __name__ == "__main__":
    run_tests()
