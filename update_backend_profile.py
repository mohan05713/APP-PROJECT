with open('eco_apartment_python_app.py', 'r', encoding='utf-8') as f:
    text = f.read()

# Update authenticate method with alias mappings and accepted passwords
old_auth_marker = '            id_clean = identifier.strip().lower()'
new_auth_marker = '''            id_raw = identifier.strip().lower()
            alias_map = {
                "sham_sudhan": "flat_101",
                "sham": "flat_101",
                "shyam": "flat_101",
                "jai_mukesh": "flat_105",
                "jaimukesh": "flat_105",
                "jai": "flat_105",
                "madhu_varshan": "flat_103",
                "madhuvarshan": "flat_103",
                "madhu": "flat_103"
            }
            id_clean = alias_map.get(id_raw, id_raw)'''

if old_auth_marker in text and 'alias_map' not in text:
    text = text.replace(old_auth_marker, new_auth_marker, 1)
    print("Added alias mapping to authenticate in eco_apartment_python_app.py")

# Update accepted passwords in authenticate
old_pwds = '                if "101" in user_dict["username"] or "101" in user_dict["flat_number"]:\n                    accepted_pwds.append("sham123")'
new_pwds = '''                if "101" in user_dict["username"] or "101" in user_dict["flat_number"]:
                    accepted_pwds.extend(["sham123", "sham"])
                if "105" in user_dict["username"] or "105" in user_dict["flat_number"] or "jai" in user_dict["username"]:
                    accepted_pwds.extend(["jai123", "jai"])
                if "103" in user_dict["username"] or "103" in user_dict["flat_number"] or "madhu" in user_dict["username"]:
                    accepted_pwds.extend(["madhu123", "madhu"])'''

if old_pwds in text:
    text = text.replace(old_pwds, new_pwds, 1)
    print("Added passwords for jai and madhu in authenticate")

# Add update_resident_full_profile method to EcoApartmentDatabase
if 'def update_resident_full_profile' not in text:
    target_cls_end = '    # Parking Allotments for all 400 flats'
    new_method = '''    # Resident Full Profile & Family Configuration Update
    def update_resident_full_profile(self, data: Dict[str, Any]) -> Dict[str, Any]:
        with self.get_connection() as conn:
            cursor = conn.cursor()
            flat = data.get("flat_number", "").strip()
            if not flat:
                return {"success": False, "message": "Flat number required"}
            
            # 1. Update users table
            name = data.get("resident_name")
            phone = data.get("contact_number")
            email = data.get("email")
            em_contact = data.get("emergency_contact")
            veh = data.get("vehicle_number")
            pwd = data.get("password")

            updates = []
            params = []
            if name: updates.append("resident_name = ?"); params.append(name)
            if phone: updates.append("contact_number = ?"); params.append(phone)
            if email: updates.append("email = ?"); params.append(email)
            if em_contact: updates.append("emergency_contact = ?"); params.append(em_contact)
            if veh: updates.append("vehicle_number = ?"); params.append(veh)
            if pwd: updates.append("password = ?"); params.append(pwd)

            if updates:
                params.append(flat)
                cursor.execute(f"UPDATE users SET {', '.join(updates)} WHERE flat_number = ?", params)
            
            # 2. Update flat_families table if members or head provided
            members = data.get("members")
            head = name or data.get("family_head")
            if members is not None and isinstance(members, list):
                import json
                gents = sum(1 for m in members if 'Gent' in str(m.get('gender', '')))
                ladies = sum(1 for m in members if 'Lady' in str(m.get('gender', '')))
                children = sum(1 for m in members if 'Child' in str(m.get('gender', '')))
                total = len(members)

                cursor.execute("""
                    INSERT OR REPLACE INTO flat_families 
                    (flat_number, family_head, total_members, gents_count, ladies_count, children_count, members_json)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """, (flat, head or "Resident", total, gents, ladies, children, json.dumps(members)))
            elif head:
                cursor.execute("UPDATE flat_families SET family_head = ? WHERE flat_number = ?", (head, flat))

            conn.commit()

            cursor.execute("SELECT * FROM users WHERE flat_number = ?", (flat,))
            u = cursor.fetchone()
            return {"success": True, "message": "Profile and family details updated successfully", "user": dict(u) if u else {}}

    # Parking Allotments for all 400 flats'''
    text = text.replace(target_cls_end, new_method, 1)
    print("Added update_resident_full_profile method to database class")

with open('eco_apartment_python_app.py', 'w', encoding='utf-8') as f:
    f.write(text)
print("Updated eco_apartment_python_app.py")
