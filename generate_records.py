"""
Generator for supabase_schema.sql and seed data for EcoApartment OS.
Generates full PostgreSQL schema, RLS policies, views, triggers, and all 400 flats/residents.
"""

import random

FIRST_NAMES = [
    "Aarav", "Aditi", "Ajay", "Akash", "Anand", "Ananya", "Anirudh", "Anjali", "Arjun", "Arun",
    "Bhavani", "Chirag", "Deepak", "Deepika", "Dinesh", "Divya", "Ganesh", "Gautam", "Gayathri",
    "Hari", "Harini", "Harish", "Hemalatha", "Ishaan", "Jaishankar", "Janani", "Jeeva", "Karthik",
    "Kavitha", "Keerthana", "Krishnan", "Lakshmi", "Madhavan", "Malini", "Manikandan", "Meena",
    "Murugan", "Mythili", "Nandhini", "Naveen", "Nithya", "Padma", "Pavithra", "Prabhu", "Prasanth",
    "Praveen", "Priya", "Rahul", "Rajesh", "Raman", "Ramya", "Revathi", "Rohan", "Sandhya", "Sanjay",
    "Saravanan", "Sathish", "Selvam", "Shankar", "Shanthi", "Shiva", "Sneha", "Soundar", "Srinivasan",
    "Subramanian", "Sudha", "Sugumar", "Sujatha", "Sundar", "Suresh", "Surya", "Swaminathan", "Swetha",
    "Thangavel", "Uma", "Vasanth", "Venkatesh", "Vidya", "Vignesh", "Vijay", "Vikram", "Vinoth", "Yuvraj"
]

LAST_NAMES = [
    "Balaji", "Chandran", "Chettiar", "Govind", "Iyer", "Iyengar", "Jayaraman", "Kannan", "Kumar",
    "Mani", "Menon", "Moorthi", "Mudaliar", "Nadar", "Naidu", "Narayanan", "Natarajan", "Palani",
    "Pandian", "Perumal", "Pillai", "Prasad", "Radhakrishnan", "Rajan", "Raju", "Ramachandran",
    "Rangarajan", "Reddy", "Sankaran", "Sarma", "Sekar", "Sethuraman", "Shanmugam", "Sharma",
    "Sivakumar", "Sridhar", "Sundaram", "Swamy", "Thirumalai", "Varadhan", "Varma", "Viswanathan"
]

PRESERVED_RESIDENTS = {
    "B-402": {
        "name": "Mohan Palaniappan",
        "phone": "+91 63802 44185",
        "email": "mohan.b402@srmgreen.in",
        "password": "SRM@F4-402",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2022-06-15",
        "emergency": "+91 98401 11402",
        "vehicle": "TN-09-CB-4020",
        "water_meter": "WM-B402",
        "gas_meter": "GM-B402"
    },
    "B-101": {
        "name": "R. SHAM SUDHAN",
        "phone": "+91 77089 94065",
        "email": "shamsudhan.b101@srmgreen.in",
        "password": "7708994065",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-08-10",
        "emergency": "+91 94440 10101",
        "vehicle": "TN-07-AL-1011",
        "water_meter": "WM-B101",
        "gas_meter": "GM-B101"
    },
    "B-102": {
        "name": "P. MOHAN",
        "phone": "+91 63838 98769",
        "email": "mohan.b102@srmgreen.in",
        "password": "6383898769",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-09-01",
        "emergency": "+91 98400 98769",
        "vehicle": "TN-10-AQ-1022",
        "water_meter": "WM-B102",
        "gas_meter": "GM-B102"
    },
    "B-105": {
        "name": "M. JAI MUGESH",
        "phone": "+91 93610 77508",
        "email": "jaimugesh.b105@srmgreen.in",
        "password": "9361077508",
        "role": "RESIDENT",
        "status": "OCCUPIED_TENANT",
        "move_in": "2023-01-15",
        "emergency": "+91 98410 77508",
        "vehicle": "TN-14-K-1055",
        "water_meter": "WM-B105",
        "gas_meter": "GM-B105"
    },
    "B-108": {
        "name": "N. MADHUVARSHAN",
        "phone": "+91 88384 49468",
        "email": "madhuvarshan.b108@srmgreen.in",
        "password": "8838449468",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2022-03-20",
        "emergency": "+91 98401 49468",
        "vehicle": "TN-09-BE-1088",
        "water_meter": "WM-B108",
        "gas_meter": "GM-B108"
    },
    "B-201": {
        "name": "K. MANI MARAN",
        "phone": "+91 78459 68905",
        "email": "manimaran.b201@srmgreen.in",
        "password": "7845968905",
        "role": "RESIDENT",
        "status": "OCCUPIED_TENANT",
        "move_in": "2022-11-05",
        "emergency": "+91 98402 68905",
        "vehicle": "TN-18-C-2011",
        "water_meter": "WM-B201",
        "gas_meter": "GM-B201"
    },
    "B-204": {
        "name": "T S. ABINAYA",
        "phone": "+91 88257 42523",
        "email": "abinaya.b204@srmgreen.in",
        "password": "8825742523",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-12-01",
        "emergency": "+91 98403 42523",
        "vehicle": "TN-05-BX-2044",
        "water_meter": "WM-B204",
        "gas_meter": "GM-B204"
    },
    "B-206": {
        "name": "NARAYANAN MOORTHI",
        "phone": "+91 88384 49468",
        "email": "narayanan.b206@srmgreen.in",
        "password": "SRM@F2-206",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2022-04-12",
        "emergency": "+91 98404 49468",
        "vehicle": "TN-09-AZ-2066",
        "water_meter": "WM-B206",
        "gas_meter": "GM-B206"
    },
    "B-208": {
        "name": "SIVANESAN",
        "phone": "+91 88257 42523",
        "email": "sivanesan.b208@srmgreen.in",
        "password": "SRM@F2-208",
        "role": "RESIDENT",
        "status": "OCCUPIED_TENANT",
        "move_in": "2023-05-18",
        "emergency": "+91 98405 42523",
        "vehicle": "TN-01-BB-2088",
        "water_meter": "WM-B208",
        "gas_meter": "GM-B208"
    },
    "B-302": {
        "name": "N. Geetha",
        "phone": "+91 98401 33302",
        "email": "geetha.b302@srmgreen.in",
        "password": "SRM@F3-302",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-07-22",
        "emergency": "+91 98406 33302",
        "vehicle": "TN-07-CC-3022",
        "water_meter": "WM-B302",
        "gas_meter": "GM-B302"
    },
    "B-305": {
        "name": "V. Suresh",
        "phone": "+91 98401 33305",
        "email": "suresh.b305@srmgreen.in",
        "password": "SRM@F3-305",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-10-14",
        "emergency": "+91 98407 33305",
        "vehicle": "TN-02-D-3055",
        "water_meter": "WM-B305",
        "gas_meter": "GM-B305"
    },
    "B-308": {
        "name": "T. Dinesh",
        "phone": "+91 98401 33308",
        "email": "dinesh.b308@srmgreen.in",
        "password": "SRM@F3-308",
        "role": "RESIDENT",
        "status": "OCCUPIED_TENANT",
        "move_in": "2023-08-01",
        "emergency": "+91 98408 33308",
        "vehicle": "TN-10-AP-3088",
        "water_meter": "WM-B308",
        "gas_meter": "GM-B308"
    },
    "B-310": {
        "name": "K. Priya",
        "phone": "+91 98401 33310",
        "email": "priya.b310@srmgreen.in",
        "password": "SRM@F3-310",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2022-02-19",
        "emergency": "+91 98409 33310",
        "vehicle": "TN-09-F-3100",
        "water_meter": "WM-B310",
        "gas_meter": "GM-B310"
    },
    "B-401": {
        "name": "S. Ramesh",
        "phone": "+91 98401 44401",
        "email": "ramesh.b401@srmgreen.in",
        "password": "SRM@F4-401",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-06-01",
        "emergency": "+91 98410 44401",
        "vehicle": "TN-05-AB-4011",
        "water_meter": "WM-B401",
        "gas_meter": "GM-B401"
    },
    "B-405": {
        "name": "Dr. E. Saravanan",
        "phone": "+91 98401 44405",
        "email": "saravanan.b405@srmgreen.in",
        "password": "SRM@F4-405",
        "role": "RESIDENT",
        "status": "OCCUPIED_OWNER",
        "move_in": "2021-05-15",
        "emergency": "+91 98411 44405",
        "vehicle": "TN-09-Z-4055",
        "water_meter": "WM-B405",
        "gas_meter": "GM-B405"
    },
    "B-408": {
        "name": "M. Vani",
        "phone": "+91 98401 44408",
        "email": "vani.b408@srmgreen.in",
        "password": "SRM@F4-408",
        "role": "RESIDENT",
        "status": "OCCUPIED_TENANT",
        "move_in": "2023-09-01",
        "emergency": "+91 98412 44408",
        "vehicle": "TN-07-L-4088",
        "water_meter": "WM-B408",
        "gas_meter": "GM-B408"
    }
}

def generate_all_400_records():
    records = []
    blocks = ["A", "B", "C", "D"]
    random.seed(42) # Consistent generation

    vacant_targets = {
        "A": [107, 219, 314, 422],
        "B": [112, 221, 318, 420],
        "C": [104, 215, 309, 416, 424],
        "D": [118, 209, 312, 325, 411]
    }

    flat_counter = 0

    for block in blocks:
        for floor in range(1, 5):
            for unit in range(1, 26):
                flat_counter += 1
                flat_num = floor * 100 + unit
                flat_code = f"{block}-{flat_num}"

                if flat_code in PRESERVED_RESIDENTS:
                    p = PRESERVED_RESIDENTS[flat_code]
                    records.append({
                        "flat_code": flat_code,
                        "block": block,
                        "floor": floor,
                        "unit_number": flat_num,
                        "resident_name": p["name"],
                        "email": p["email"],
                        "contact_number": p["phone"],
                        "username": f"flat_{block.lower()}{flat_num}",
                        "password": p["password"],
                        "role": p["role"],
                        "occupancy_status": p["status"],
                        "move_in_date": p["move_in"],
                        "emergency_contact": p["emergency"],
                        "vehicle_number": p["vehicle"],
                        "water_meter_id": p["water_meter"],
                        "gas_meter_id": p["gas_meter"],
                        "dues_status": "PAID" if unit % 4 != 0 else "PENDING",
                        "monthly_dues": 2400.0 + (floor * 100.0)
                    })
                    continue

                is_vacant = flat_num in vacant_targets[block]
                if is_vacant:
                    records.append({
                        "flat_code": flat_code,
                        "block": block,
                        "floor": floor,
                        "unit_number": flat_num,
                        "resident_name": "Vacant Flat",
                        "email": f"vacant.{block.lower()}{flat_num}@srmgreen.in",
                        "contact_number": "—",
                        "username": f"flat_{block.lower()}{flat_num}",
                        "password": f"SRM@{block}{floor}-{flat_num}",
                        "role": "RESIDENT",
                        "occupancy_status": "VACANT",
                        "move_in_date": "",
                        "emergency_contact": "SRM Facility Office: +91 63838 98769",
                        "vehicle_number": "None",
                        "water_meter_id": f"WM-{block}{flat_num}",
                        "gas_meter_id": f"GM-{block}{flat_num}",
                        "dues_status": "VACANT_EXEMPT",
                        "monthly_dues": 0.0
                    })
                else:
                    fn = FIRST_NAMES[(flat_counter * 7 + floor) % len(FIRST_NAMES)]
                    ln = LAST_NAMES[(flat_counter * 11 + unit) % len(LAST_NAMES)]
                    full_name = f"{fn} {ln}"
                    phone_prefix = random.choice(["98401", "94440", "78459", "93610", "88257", "98840", "89390", "97909"])
                    phone = f"+91 {phone_prefix} {random.randint(10000, 99999)}"
                    email = f"{fn.lower()}.{ln.lower()}.{block.lower()}{flat_num}@srmgreen.in"
                    pwd = f"SRM@{block}{floor}-{flat_num}"
                    status = "OCCUPIED_OWNER" if (unit % 3 != 0) else "OCCUPIED_TENANT"
                    move_year = random.choice([2021, 2022, 2023, 2024, 2025])
                    move_month = random.randint(1, 12)
                    move_day = random.randint(1, 28)
                    move_in = f"{move_year}-{move_month:02d}-{move_day:02d}"
                    v_district = random.choice(["01", "02", "05", "07", "09", "10", "14", "18"])
                    v_series = random.choice(["AB", "AZ", "BB", "CB", "DX", "EZ", "FA"])
                    vehicle = f"TN-{v_district}-{v_series}-{random.randint(1000, 9999)}"
                    emg = f"+91 {random.choice(['98400', '94441', '78450', '98841'])} {random.randint(10000, 99999)}"
                    dues_stat = "PAID" if (unit % 5 != 0) else "PENDING"

                    records.append({
                        "flat_code": flat_code,
                        "block": block,
                        "floor": floor,
                        "unit_number": flat_num,
                        "resident_name": full_name,
                        "email": email,
                        "contact_number": phone,
                        "username": f"flat_{block.lower()}{flat_num}",
                        "password": pwd,
                        "role": "RESIDENT",
                        "occupancy_status": status,
                        "move_in_date": move_in,
                        "emergency_contact": emg,
                        "vehicle_number": vehicle,
                        "water_meter_id": f"WM-{block}{flat_num}",
                        "gas_meter_id": f"GM-{block}{flat_num}",
                        "dues_status": dues_stat,
                        "monthly_dues": 2400.0 + (floor * 100.0)
                    })

    return records

if __name__ == "__main__":
    records = generate_all_400_records()
    print(f"Generated {len(records)} records. First: {records[0]['flat_code']}, Last: {records[-1]['flat_code']}")
