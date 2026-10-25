#!/usr/bin/env python3
"""
Generates a complete, multi-page PDF manual for EcoApartment OS containing:
1. Page 1: Key Website Features, System Overview, Primary Resident Profile (Mohan Palaniappan), Authority Credentials, and Workflow Guide.
2. Pages 2-11: Complete 400 Flat Credentials Roster (Flat No, Resident Name, Username, Password, Phone Number, Email Address) for Blocks A, B, C, and D.
"""

import os
import sys

class SimplePdfWriter:
    def __init__(self):
        self.pages = []
        
    def add_page(self, stream_content):
        self.pages.append(stream_content)
        
    def build(self, output_path):
        font_f1 = b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"
        font_f2 = b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"
        font_f3 = b"<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>"
        font_f4 = b"<< /Type /Font /Subtype /Type1 /BaseFont /Courier-Bold >>"
        
        page_count = len(self.pages)
        page_obj_ids = []
        content_obj_ids = []
        
        curr_id = 7
        for i in range(page_count):
            page_obj_ids.append(curr_id)
            content_obj_ids.append(curr_id + 1)
            curr_id += 2
            
        kids_str = " ".join(f"{pid} 0 R" for pid in page_obj_ids)
        pages_dict = f"<< /Type /Pages /Kids [{kids_str}] /Count {page_count} >>".encode("ascii")
        catalog_dict = b"<< /Type /Catalog /Pages 2 0 R >>"
        
        raw_objs = {
            1: catalog_dict,
            2: pages_dict,
            3: font_f1,
            4: font_f2,
            5: font_f3,
            6: font_f4
        }
        
        for i in range(page_count):
            p_id = page_obj_ids[i]
            c_id = content_obj_ids[i]
            stream = self.pages[i]
            
            p_dict = (
                f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                f"/Contents {c_id} 0 R "
                f"/Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R /F4 6 0 R >> >> >>"
            ).encode("ascii")
            raw_objs[p_id] = p_dict
            
            c_dict = (
                f"<< /Length {len(stream)} >>\nstream\n".encode("ascii") +
                stream +
                b"\nendstream"
            )
            raw_objs[c_id] = c_dict
            
        out = bytearray()
        out.extend(b"%PDF-1.4\n%\xe2\xe3\xcf\xd3\n")
        
        offsets = {}
        total_objs = curr_id - 1
        
        for oid in range(1, total_objs + 1):
            offsets[oid] = len(out)
            out.extend(f"{oid} 0 obj\n".encode("ascii"))
            out.extend(raw_objs[oid])
            out.extend(b"\nendobj\n")
            
        xref_offset = len(out)
        out.extend(f"xref\n0 {total_objs + 1}\n".encode("ascii"))
        out.extend(b"0000000000 65535 f \n")
        for oid in range(1, total_objs + 1):
            out.extend(f"{offsets[oid]:010d} 00000 n \n".encode("ascii"))
            
        trailer = (
            f"trailer\n<< /Size {total_objs + 1} /Root 1 0 R >>\n"
            f"startxref\n{xref_offset}\n%%EOF\n"
        ).encode("ascii")
        out.extend(trailer)
        
        with open(output_path, "wb") as f:
            f.write(out)
        print(f"Successfully generated {output_path} ({len(out)} bytes, {page_count} pages)")


class PdfCanvas:
    def __init__(self):
        self.commands = []
        
    def rect(self, x, y, w, h, fill_rgb=None, stroke_rgb=None, stroke_width=1):
        cmds = []
        if stroke_rgb:
            cmds.append(f"{stroke_width} w")
            cmds.append(f"{stroke_rgb[0]:.3f} {stroke_rgb[1]:.3f} {stroke_rgb[2]:.3f} RG")
        if fill_rgb:
            cmds.append(f"{fill_rgb[0]:.3f} {fill_rgb[1]:.3f} {fill_rgb[2]:.3f} rg")
            
        cmds.append(f"{x:.1f} {y:.1f} {w:.1f} {h:.1f} re")
        if fill_rgb and stroke_rgb:
            cmds.append("B")
        elif fill_rgb:
            cmds.append("f")
        elif stroke_rgb:
            cmds.append("S")
        self.commands.append(" ".join(cmds))
        
    def text(self, x, y, text_str, font="F1", size=10, rgb=(0, 0, 0)):
        safe = text_str.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")
        cmd = (
            f"BT\n"
            f"/{font} {size} Tf\n"
            f"{rgb[0]:.3f} {rgb[1]:.3f} {rgb[2]:.3f} rg\n"
            f"1 0 0 1 {x:.1f} {y:.1f} Tm\n"
            f"({safe}) Tj\n"
            f"ET"
        )
        self.commands.append(cmd)
        
    def line(self, x1, y1, x2, y2, rgb=(0.7, 0.7, 0.7), width=1):
        cmd = (
            f"{width} w\n"
            f"{rgb[0]:.3f} {rgb[1]:.3f} {rgb[2]:.3f} RG\n"
            f"{x1:.1f} {y1:.1f} m {x2:.1f} {y2:.1f} l S"
        )
        self.commands.append(cmd)
        
    def get_stream(self):
        return "\n".join(self.commands).encode("utf-8")


def generate_all_400_flats_data():
    flats = []
    
    # Priority Overrides
    overrides = {
        'B-402': {
            'name': 'Mohan Palaniappan',
            'username': '6383898769',
            'password': '6383898769',
            'phone': '+91 63838 98769',
            'email': 'mohanpalaniappan007@gmail.com',
            'status': 'PAID (PRIMARY)'
        },
        'A-101': {
            'name': 'R. SHAM SUDHAN',
            'username': 'flat_101',
            'password': '7708994065',
            'phone': '+91 77089 94065',
            'email': 'shamsudhan@gmail.com',
            'status': 'PAID'
        },
        'A-103': {
            'name': 'MADHU VARSHAN',
            'username': 'flat_103',
            'password': '9840155432',
            'phone': '+91 98401 55432',
            'email': 'madhuvarshan@srmgreen.in',
            'status': 'PAID'
        },
        'A-105': {
            'name': 'M. JAI MUGESH',
            'username': 'flat_105',
            'password': '9361077508',
            'phone': '+91 93610 77508',
            'email': 'jaimugesh@srmgreen.in',
            'status': 'PAID'
        },
        'A-108': {
            'name': 'N. MADHUVARSHAN',
            'username': 'flat_108',
            'password': '8838449468',
            'phone': '+91 88384 49468',
            'email': 'madhuvarshan108@srmgreen.in',
            'status': 'PAID'
        }
    }
    
    blocks = ['A', 'B', 'C', 'D']
    for blk in blocks:
        for floor in range(1, 5):
            for unit in range(1, 26):
                unit_str = f"{unit:02d}"
                flat_no = f"{blk}-{floor}{unit_str}"
                
                if flat_no in overrides:
                    data = overrides[flat_no]
                    flats.append({
                        'flat': flat_no,
                        'block': blk,
                        'name': data['name'],
                        'username': data['username'],
                        'password': data['password'],
                        'phone': data['phone'],
                        'email': data['email'],
                        'status': data['status']
                    })
                else:
                    flat_clean = f"{blk.lower()}{floor}{unit_str}"
                    flats.append({
                        'flat': flat_no,
                        'block': blk,
                        'name': f"Resident {flat_no}",
                        'username': f"flat_{flat_clean}",
                        'password': f"srm_{flat_clean}",
                        'phone': f"+91 9840{(floor * 100 + unit):05d}",
                        'email': f"flat_{flat_clean}@srmgreen.in",
                        'status': "VACANT" if unit % 12 == 0 else ("PENDING" if unit % 9 == 0 else "PAID")
                    })
                    
    return flats


def build_credentials_pdf():
    writer = SimplePdfWriter()
    flats = generate_all_400_flats_data()
    
    # -----------------------------------------------------------------
    # PAGE 1: KEY WEBSITE FEATURES & SYSTEM OVERVIEW
    # -----------------------------------------------------------------
    p1 = PdfCanvas()
    
    # Header Banner
    p1.rect(0, 715, 612, 77, fill_rgb=(0.06, 0.12, 0.18))
    p1.rect(0, 711, 612, 4, fill_rgb=(0.06, 0.73, 0.51))
    
    p1.text(30, 760, "EcoApartment OS — Key Features & System Manual", font="F2", size=20, rgb=(1, 1, 1))
    p1.text(30, 742, "SRM Green Residency • Official Operations Guidebook & Credentials Roster", font="F1", size=10.5, rgb=(0.7, 0.85, 0.78))
    p1.text(30, 726, "Comprehensive 400 Apartments Directory (Blocks A, B, C, D) • Total Residents: 1,480", font="F1", size=9, rgb=(0.6, 0.65, 0.7))
    
    p1.text(460, 760, "DOCUMENT ID", font="F2", size=8, rgb=(0.6, 0.7, 0.8))
    p1.text(460, 747, "SRM-ECO-400-v2", font="F3", size=9, rgb=(0.2, 0.9, 0.6))
    p1.text(460, 732, "Date: October 2026", font="F1", size=8.5, rgb=(0.8, 0.8, 0.8))
    
    # Primary Resident Profile Card
    p1.rect(30, 580, 552, 118, fill_rgb=(0.95, 0.98, 0.96), stroke_rgb=(0.06, 0.73, 0.51), stroke_width=1.5)
    p1.rect(30, 672, 552, 26, fill_rgb=(0.06, 0.73, 0.51))
    p1.text(42, 680, "PRIMARY RESIDENT SPOTLIGHT PROFILE (FLAT B-402)", font="F2", size=11, rgb=(1, 1, 1))
    
    p1.text(42, 652, "Resident Name:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(150, 652, "Mohan Palaniappan (Flat B-402, Floor 4)", font="F2", size=10, rgb=(0.05, 0.4, 0.25))
    
    p1.text(42, 634, "Login Username:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(150, 634, "6383898769   (or  flat_402)", font="F4", size=11, rgb=(0.0, 0.4, 0.2))
    
    p1.text(42, 616, "Password:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(150, 616, "6383898769   (or  mohan123)", font="F4", size=11, rgb=(0.8, 0.1, 0.1))
    
    p1.text(42, 598, "Contact Phone:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(150, 598, "+91 63838 98769", font="F1", size=9.5, rgb=(0.1, 0.1, 0.1))
    p1.text(280, 598, "Email:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(330, 598, "mohanpalaniappan007@gmail.com", font="F1", size=9.5, rgb=(0.1, 0.1, 0.1))
    
    # Authority Management Desk Card
    p1.rect(30, 480, 552, 90, fill_rgb=(0.98, 0.97, 0.92), stroke_rgb=(0.85, 0.65, 0.13), stroke_width=1.5)
    p1.rect(30, 546, 552, 24, fill_rgb=(0.92, 0.72, 0.15))
    p1.text(42, 553, "FACILITY AUTHORITY & SECURITY DESK CREDENTIALS", font="F2", size=10.5, rgb=(0.1, 0.1, 0.1))
    
    p1.text(42, 528, "Authority Username:", font="F2", size=9, rgb=(0.2, 0.2, 0.2))
    p1.text(160, 528, "admin   (or  authority)", font="F4", size=10.5, rgb=(0.0, 0.35, 0.2))
    
    p1.text(42, 510, "Authority Password:", font="F2", size=9, rgb=(0.2, 0.2, 0.2))
    p1.text(160, 510, "6383898769", font="F4", size=11, rgb=(0.8, 0.1, 0.1))
    
    p1.text(42, 492, "Security Email:", font="F2", size=9, rgb=(0.2, 0.2, 0.2))
    p1.text(160, 492, "admin@srmgreen.in", font="F1", size=9, rgb=(0.1, 0.1, 0.1))
    p1.text(320, 492, "Contact Phone: +91 63838 98769", font="F1", size=9, rgb=(0.1, 0.1, 0.1))

    # Key Features Grid Box
    p1.rect(30, 160, 552, 308, fill_rgb=(0.98, 0.99, 1.0), stroke_rgb=(0.8, 0.85, 0.9), stroke_width=1)
    p1.rect(30, 444, 552, 24, fill_rgb=(0.1, 0.35, 0.5))
    p1.text(42, 451, "KEY WEBSITE MODULES & SYSTEM CAPABILITIES", font="F2", size=11, rgb=(1, 1, 1))
    
    features = [
        ("[1] Clean White-Screen Login Portal", "Minimalist centered login card with full white background, watermark logo, password eye toggle, and a Quick Login Toggle modal for instant Mohan P. profile selection."),
        ("[2] Technical Dispatch & Helpdesk", "Resident complaint logging for Plumbing, Electrical, Elevator, and Carpentry. Real-time technician assignment (R. Kathir, K. Selvam, S. Arumugam) with SLAs and status progression."),
        ("[3] Digital Visitor Gate Passes", "Instant pass code generation (GP-98401) for delivery agents (Swiggy/Zomato), guests, and cabs with vehicle registration and gatekeeper approval logs."),
        ("[4] Community Notices Bulletin", "Pinned RWA announcements for Annual General Body meetings, eco audits, and scheduled water tank cleaning with category filtering."),
        ("[5] Maintenance & Dues Ledger", "Itemized monthly billing for Water (Rs 450), Piped LPG (Rs 350), Electricity (Rs 600), and Maintenance (Rs 1,050) with digital payment receipt export."),
        ("[6] IoT & Sustainability Mesh", "Dual-stage pump controller (Inflow 450 L/min / Outflow 380 L/min), AQI meter (42 Good), 18.4 kW solar array generation, dual LPG cylinder switchover, and leak anomaly detection."),
        ("[7] 400 Flats Resident Census", "Comprehensive population registry cataloging 1,480 residents (480 Gents, 460 Ladies, 540 Children) with family member rosters, blood groups, and emergency contacts."),
        ("[8] Multi-Format Data Export", "Download full 400 flats directory CSV (residents_400_export.csv), IoT telemetry JSON, and this official PDF credentials manual directly from the web app.")
    ]
    
    fy = 422
    for title, desc in features:
        p1.text(42, fy, title, font="F2", size=9, rgb=(0.06, 0.3, 0.45))
        p1.text(42, fy - 13, desc, font="F1", size=8, rgb=(0.25, 0.25, 0.25))
        fy -= 32

    # Operations Workflow Footer
    p1.rect(30, 65, 552, 85, fill_rgb=(0.95, 0.97, 0.96), stroke_rgb=(0.82, 0.88, 0.85), stroke_width=1)
    p1.text(42, 134, "QUICK OPERATIONAL WORKFLOW", font="F2", size=9.5, rgb=(0.1, 0.35, 0.25))
    p1.text(42, 118, "• Resident Login: Enter username '6383898769' or 'flat_402' with password '6383898769' to access Flat B-402.", font="F1", size=8.5, rgb=(0.2, 0.2, 0.2))
    p1.text(42, 102, "• Authority Login: Enter username 'admin' or 'authority' with password '6383898769' for facility management.", font="F1", size=8.5, rgb=(0.2, 0.2, 0.2))
    p1.text(42, 86, "• Quick Toggle: Click the toggle button on the login screen to open a pop-up and select Mohan Palaniappan.", font="F1", size=8.5, rgb=(0.2, 0.2, 0.2))
    p1.text(42, 70, "• Complete Roster: Pages 2 to 11 contain all 400 apartment usernames, passwords, phone numbers, and emails.", font="F1", size=8.5, rgb=(0.2, 0.2, 0.2))

    # Page 1 Footer
    p1.line(30, 48, 582, 48, rgb=(0.8, 0.8, 0.8), width=0.8)
    p1.text(30, 35, "EcoApartment OS v2.0 • SRM Green Residency • Certified Official Credentials Document", font="F1", size=8, rgb=(0.5, 0.5, 0.5))
    p1.text(525, 35, "Page 1 of 11", font="F2", size=8, rgb=(0.3, 0.3, 0.3))
    
    writer.add_page(p1.get_stream())
    
    # -----------------------------------------------------------------
    # PAGES 2 TO 11: ALL 400 FLATS CREDENTIALS DIRECTORY
    # -----------------------------------------------------------------
    rows_per_page = 40
    total_flats = len(flats)
    total_pages = 1 + (total_flats + rows_per_page - 1) // rows_per_page
    
    for page_idx in range((total_flats + rows_per_page - 1) // rows_per_page):
        p_num = page_idx + 2
        p = PdfCanvas()
        
        # Header Banner
        p.rect(0, 735, 612, 57, fill_rgb=(0.06, 0.12, 0.18))
        p.rect(0, 731, 612, 4, fill_rgb=(0.06, 0.73, 0.51))
        
        p.text(30, 762, "EcoApartment OS — 400 Flat Credentials Directory", font="F2", size=15, rgb=(1, 1, 1))
        p.text(30, 745, f"SRM Green Residency • Official Flat Logins, Passwords, Contacts & Emails (Page {p_num} of 11)", font="F1", size=9.5, rgb=(0.7, 0.85, 0.78))
        
        # Directory Table
        table_top = 715
        table_height = 655
        p.rect(30, table_top - table_height, 552, table_height, fill_rgb=(1, 1, 1), stroke_rgb=(0.8, 0.85, 0.9), stroke_width=1)
        
        # Table Header
        p.rect(30, table_top - 22, 552, 22, fill_rgb=(0.06, 0.35, 0.3))
        p.text(36, table_top - 15, "FLAT NO", font="F2", size=8.5, rgb=(1, 1, 1))
        p.text(90, table_top - 15, "RESIDENT NAME", font="F2", size=8.5, rgb=(0.85, 0.95, 0.9))
        p.text(210, table_top - 15, "USERNAME", font="F2", size=8.5, rgb=(0.85, 0.95, 0.9))
        p.text(295, table_top - 15, "PASSWORD", font="F2", size=8.5, rgb=(1, 0.9, 0.4))
        p.text(380, table_top - 15, "PHONE NUMBER", font="F2", size=8.5, rgb=(0.85, 0.95, 0.9))
        p.text(480, table_top - 15, "EMAIL ADDRESS", font="F2", size=8.5, rgb=(0.85, 0.95, 0.9))
        
        start_flat = page_idx * rows_per_page
        end_flat = min(start_flat + rows_per_page, total_flats)
        chunk = flats[start_flat:end_flat]
        
        y = table_top - 36
        for idx, item in enumerate(chunk):
            bg_fill = (0.95, 0.98, 0.96) if idx % 2 == 1 else (1, 1, 1)
            if item['flat'] == 'B-402':
                bg_fill = (1.0, 0.95, 0.8) # Highlight B-402
            
            p.rect(31, y - 3, 550, 15, fill_rgb=bg_fill)
            
            p.text(36, y, item['flat'], font="F2", size=8, rgb=(0.1, 0.1, 0.1))
            p.text(90, y, item['name'][:20], font="F2" if item['flat'] in ['B-402', 'A-101', 'A-103', 'A-105', 'A-108'] else "F1", size=8, rgb=(0.05, 0.35, 0.2) if item['flat'] == 'B-402' else (0.15, 0.15, 0.15))
            p.text(210, y, item['username'], font="F3", size=7.5, rgb=(0.2, 0.2, 0.2))
            p.text(295, y, item['password'], font="F4", size=8, rgb=(0.8, 0.1, 0.1) if item['flat'] == 'B-402' else (0.6, 0.25, 0.0))
            p.text(380, y, item['phone'], font="F1", size=7.5, rgb=(0.2, 0.2, 0.2))
            p.text(480, y, item['email'][:22], font="F1", size=7, rgb=(0.3, 0.3, 0.3))
            
            p.line(30, y - 4, 582, y - 4, rgb=(0.9, 0.92, 0.94), width=0.4)
            y -= 15.5
            
        # Page Footer
        p.line(30, 48, 582, 48, rgb=(0.8, 0.8, 0.8), width=0.8)
        p.text(30, 35, "EcoApartment OS v2.0 • SRM Green Residency 400 Apartments Master Credentials Directory", font="F1", size=8, rgb=(0.5, 0.5, 0.5))
        p.text(520, 35, f"Page {p_num} of {total_pages}", font="F2", size=8, rgb=(0.3, 0.3, 0.3))
        
        writer.add_page(p.get_stream())

    # Ensure output directories
    os.makedirs("public", exist_ok=True)
    os.makedirs("APK_DOWNLOAD", exist_ok=True)
    os.makedirs("APK DOWNLOAD", exist_ok=True)
    os.makedirs("build-outputs", exist_ok=True)
    
    # Write to all PDF destination paths
    writer.build("EcoApartment_Credentials_and_Manual.pdf")
    writer.build("EcoApartment_Credentials_Directory.pdf")
    writer.build("public/EcoApartment_Credentials_and_Manual.pdf")
    writer.build("public/EcoApartment_Credentials_Directory.pdf")
    writer.build("APK_DOWNLOAD/EcoApartment_Credentials_and_Manual.pdf")
    writer.build("APK DOWNLOAD/EcoApartment_Credentials_and_Manual.pdf")
    writer.build("build-outputs/EcoApartment_Credentials_and_Manual.pdf")

if __name__ == "__main__":
    build_credentials_pdf()
