#!/usr/bin/env python3
"""
Generates a multi-page, formatted PDF manual for EcoApartment OS.
"""

import sys

class SimplePdfWriter:
    def __init__(self):
        self.pages = []
        self.objects = []
        
    def add_page(self, stream_content):
        self.pages.append(stream_content)
        
    def build(self, output_path):
        # 1 0 obj: Catalog
        # 2 0 obj: Pages
        # For each page:
        #   Page object
        #   Content stream object
        # Fonts: F1 (Helvetica), F2 (Helvetica-Bold), F3 (Courier), F4 (Courier-Bold)
        
        objects = []
        
        # Will assemble objects
        # Catalog is obj 1
        # Pages is obj 2
        # Font F1 is obj 3
        # Font F2 is obj 4
        # Font F3 is obj 5
        # Font F4 is obj 6
        
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
            
        # Write PDF
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
        print(f"Successfully generated {output_path} ({len(out)} bytes)")


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
        # Escape parenthesis
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


def generate_credentials_pdf():
    writer = SimplePdfWriter()
    
    # -------------------------------------------------------------
    # PAGE 1: Authority & Floors 1 & 2 Directory
    # -------------------------------------------------------------
    p1 = PdfCanvas()
    
    # Top Header Banner
    p1.rect(0, 710, 612, 82, fill_rgb=(0.06, 0.12, 0.18)) # Dark Navy
    p1.rect(0, 706, 612, 4, fill_rgb=(0.06, 0.73, 0.51))  # Emerald accent line
    
    p1.text(36, 755, "EcoApartment OS", font="F2", size=22, rgb=(1, 1, 1))
    p1.text(36, 738, "SRM Green Residency - Resident & Facility Authority Portal", font="F1", size=11, rgb=(0.7, 0.85, 0.78))
    p1.text(36, 722, "Official Credentials Directory & Operations Guidebook", font="F1", size=9.5, rgb=(0.6, 0.65, 0.7))
    p1.text(450, 755, "DOCUMENT ID", font="F2", size=8, rgb=(0.6, 0.7, 0.8))
    p1.text(450, 742, "SRM-ECO-2026-v2", font="F3", size=9, rgb=(0.2, 0.9, 0.6))
    p1.text(450, 728, "Date: Sep 20, 2026", font="F1", size=8.5, rgb=(0.8, 0.8, 0.8))
    
    # Authority Management Card
    p1.rect(36, 585, 540, 105, fill_rgb=(0.97, 0.98, 1.0), stroke_rgb=(0.85, 0.65, 0.13), stroke_width=1.5)
    p1.rect(36, 665, 540, 25, fill_rgb=(0.92, 0.72, 0.15))
    p1.text(48, 672, "[!] FACILITY AUTHORITY / MANAGEMENT DESK CREDENTIALS", font="F2", size=10.5, rgb=(0.1, 0.1, 0.1))
    
    p1.text(48, 642, "Role / Designation:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(175, 642, "SRM Facility Management & Security Office", font="F1", size=9.5, rgb=(0.1, 0.1, 0.1))
    
    p1.text(48, 624, "Authority Username:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(175, 624, "authority", font="F4", size=11, rgb=(0.0, 0.4, 0.2))
    p1.text(260, 624, "(also accepts 'admin')", font="F1", size=8.5, rgb=(0.5, 0.5, 0.5))
    
    p1.text(48, 606, "Unique Password:", font="F2", size=9.5, rgb=(0.2, 0.2, 0.2))
    p1.text(175, 606, "6383898769", font="F4", size=11.5, rgb=(0.8, 0.1, 0.1))
    p1.text(320, 606, "Security Contact: +91 63838 98769", font="F1", size=8.5, rgb=(0.3, 0.3, 0.3))
    
    p1.text(48, 592, "Authority Privileges: Real-time complaint dispatch, status updates, floor passwords registry & login audit logs.", font="F1", size=8, rgb=(0.35, 0.35, 0.35))
    
    # Section: Resident Credentials Directory
    p1.text(36, 560, "1. Apartment Resident Credentials Directory (Floor 1 & Floor 2)", font="F2", size=13, rgb=(0.06, 0.2, 0.35))
    p1.text(36, 547, "Each apartment flat is assigned a pre-configured unique password stored securely in the local database.", font="F1", size=8.5, rgb=(0.4, 0.45, 0.5))
    
    # TABLE 1: Floor 1
    p1.rect(36, 420, 540, 115, fill_rgb=(1, 1, 1), stroke_rgb=(0.8, 0.85, 0.9), stroke_width=1)
    # Header
    p1.rect(36, 510, 540, 25, fill_rgb=(0.06, 0.4, 0.3))
    p1.text(46, 518, "FLOOR 1 APARTMENTS", font="F2", size=9.5, rgb=(1, 1, 1))
    p1.text(150, 518, "RESIDENT NAME", font="F2", size=9, rgb=(0.8, 0.95, 0.88))
    p1.text(280, 518, "USERNAME", font="F2", size=9, rgb=(0.8, 0.95, 0.88))
    p1.text(370, 518, "UNIQUE PASSWORD", font="F2", size=9, rgb=(1, 0.9, 0.4))
    p1.text(480, 518, "CONTACT NO", font="F2", size=9, rgb=(0.8, 0.95, 0.88))
    
    # Rows Floor 1
    f1_rows = [
        ("Flat 101", "R. SHAM SUDHAN", "flat_101", "7708994065", "+91 77089 94065"),
        ("Flat 102", "P. MOHAN", "flat_102", "6383898769", "+91 63838 98769"),
        ("Flat 105", "M. JAI MUGESH", "flat_105", "9361077508", "+91 93610 77508"),
        ("Flat 108", "N. MADHUVARSHAN", "flat_108", "8838449468", "+91 88384 49468"),
    ]
    y = 488
    for idx, r in enumerate(f1_rows):
        if idx % 2 == 1:
            p1.rect(37, y - 5, 538, 22, fill_rgb=(0.95, 0.97, 0.96))
        p1.text(46, y, r[0], font="F2", size=9, rgb=(0.1, 0.1, 0.1))
        p1.text(150, y, r[1], font="F1", size=9, rgb=(0.15, 0.15, 0.15))
        p1.text(280, y, r[2], font="F3", size=8.5, rgb=(0.3, 0.3, 0.3))
        p1.text(370, y, r[3], font="F4", size=9.5, rgb=(0.7, 0.3, 0.0))
        p1.text(480, y, r[4], font="F1", size=8.5, rgb=(0.3, 0.3, 0.3))
        p1.line(36, y - 6, 576, y - 6, rgb=(0.88, 0.9, 0.92), width=0.5)
        y -= 22
        
    # TABLE 2: Floor 2
    p1.rect(36, 275, 540, 115, fill_rgb=(1, 1, 1), stroke_rgb=(0.8, 0.85, 0.9), stroke_width=1)
    # Header
    p1.rect(36, 365, 540, 25, fill_rgb=(0.1, 0.35, 0.5))
    p1.text(46, 373, "FLOOR 2 APARTMENTS", font="F2", size=9.5, rgb=(1, 1, 1))
    p1.text(150, 373, "RESIDENT NAME", font="F2", size=9, rgb=(0.85, 0.92, 1.0))
    p1.text(280, 373, "USERNAME", font="F2", size=9, rgb=(0.85, 0.92, 1.0))
    p1.text(370, 373, "UNIQUE PASSWORD", font="F2", size=9, rgb=(1, 0.9, 0.4))
    p1.text(480, 373, "CONTACT NO", font="F2", size=9, rgb=(0.85, 0.92, 1.0))
    
    # Rows Floor 2
    f2_rows = [
        ("Flat 201", "K. MANI MARAN", "flat_201", "7845968905", "+91 78459 68905"),
        ("Flat 204", "T S. ABINAYA", "flat_204", "8825742523", "+91 88257 42523"),
        ("Flat 206", "NARAYANAN MOORTHI", "flat_206", "SRM@F2-206", "+91 88384 49468"),
        ("Flat 208", "SIVANESAN", "flat_208", "SRM@F2-208", "+91 88257 42523"),
    ]
    y = 343
    for idx, r in enumerate(f2_rows):
        if idx % 2 == 1:
            p1.rect(37, y - 5, 538, 22, fill_rgb=(0.95, 0.97, 0.99))
        p1.text(46, y, r[0], font="F2", size=9, rgb=(0.1, 0.1, 0.1))
        p1.text(150, y, r[1], font="F1", size=9, rgb=(0.15, 0.15, 0.15))
        p1.text(280, y, r[2], font="F3", size=8.5, rgb=(0.3, 0.3, 0.3))
        p1.text(370, y, r[3], font="F4", size=9.5, rgb=(0.7, 0.3, 0.0))
        p1.text(480, y, r[4], font="F1", size=8.5, rgb=(0.3, 0.3, 0.3))
        p1.line(36, y - 6, 576, y - 6, rgb=(0.88, 0.9, 0.92), width=0.5)
        y -= 22

    # Quick Summary / Tips Box at bottom of Page 1
    p1.rect(36, 120, 540, 130, fill_rgb=(0.97, 0.97, 0.98), stroke_rgb=(0.85, 0.85, 0.9), stroke_width=1)
    p1.text(48, 230, "SYSTEM HIGHLIGHTS & USAGE INSTRUCTIONS", font="F2", size=10, rgb=(0.1, 0.25, 0.4))
    
    tips = [
        "- Automatic Autofill: Tap 'View Passwords' on the login screen to autofill any flat with 1 click.",
        "- Custom Passwords: Any resident can update their password via the Key icon on the top header.",
        "- New Flat Registration: Use 'Register / Update Flat' to add custom rooms or change contact details.",
        "- Login Session Logs: Every login is recorded in local database (login_history) with timestamp.",
        "- Live Sync: When Authority updates a complaint status or adds dispatch notes, it updates instantly."
    ]
    ty = 210
    for t in tips:
        p1.text(48, ty, t, font="F1", size=8.5, rgb=(0.25, 0.3, 0.35))
        ty -= 18
        
    # Footer
    p1.line(36, 50, 576, 50, rgb=(0.8, 0.8, 0.8), width=0.8)
    p1.text(36, 38, "EcoApartment OS v2.0 - SRM Green Residency Maintenance & Facility Mesh", font="F1", size=8, rgb=(0.5, 0.5, 0.5))
    p1.text(530, 38, "Page 1 of 2", font="F1", size=8, rgb=(0.5, 0.5, 0.5))
    
    writer.add_page(p1.get_stream())
    
    # -------------------------------------------------------------
    # PAGE 2: Floors 3 & 4 Directory + Operations & APK Guide
    # -------------------------------------------------------------
    p2 = PdfCanvas()
    
    # Top Header Banner
    p2.rect(0, 725, 612, 67, fill_rgb=(0.06, 0.12, 0.18))
    p2.rect(0, 721, 612, 4, fill_rgb=(0.06, 0.73, 0.51))
    p2.text(36, 762, "EcoApartment OS", font="F2", size=18, rgb=(1, 1, 1))
    p2.text(36, 746, "Floors 3 & 4 Credentials Directory & Authority Management Guide", font="F1", size=10, rgb=(0.7, 0.85, 0.78))
    p2.text(460, 755, "Page 2 of 2", font="F2", size=9, rgb=(0.6, 0.7, 0.8))
    
    # Section: Floor 3 & 4
    p2.text(36, 695, "2. Apartment Resident Credentials Directory (Floor 3 & Floor 4)", font="F2", size=13, rgb=(0.06, 0.2, 0.35))
    
    # TABLE 3: Floor 3
    p2.rect(36, 555, 540, 115, fill_rgb=(1, 1, 1), stroke_rgb=(0.8, 0.85, 0.9), stroke_width=1)
    p2.rect(36, 645, 540, 25, fill_rgb=(0.35, 0.2, 0.5))
    p2.text(46, 653, "FLOOR 3 APARTMENTS", font="F2", size=9.5, rgb=(1, 1, 1))
    p2.text(150, 653, "RESIDENT NAME", font="F2", size=9, rgb=(0.95, 0.9, 1.0))
    p2.text(280, 653, "USERNAME", font="F2", size=9, rgb=(0.95, 0.9, 1.0))
    p2.text(370, 653, "UNIQUE PASSWORD", font="F2", size=9, rgb=(1, 0.9, 0.4))
    p2.text(480, 653, "CONTACT NO", font="F2", size=9, rgb=(0.95, 0.9, 1.0))
    
    f3_rows = [
        ("Flat 302", "N. Geetha", "flat_302", "SRM@F3-302", "+91 98401 33302"),
        ("Flat 305", "V. Suresh", "flat_305", "SRM@F3-305", "+91 98401 33305"),
        ("Flat 308", "T. Dinesh", "flat_308", "SRM@F3-308", "+91 98401 33308"),
        ("Flat 310", "K. Priya", "flat_310", "SRM@F3-310", "+91 98401 33310"),
    ]
    y = 623
    for idx, r in enumerate(f3_rows):
        if idx % 2 == 1:
            p2.rect(37, y - 5, 538, 22, fill_rgb=(0.98, 0.96, 0.99))
        p2.text(46, y, r[0], font="F2", size=9, rgb=(0.1, 0.1, 0.1))
        p2.text(150, y, r[1], font="F1", size=9, rgb=(0.15, 0.15, 0.15))
        p2.text(280, y, r[2], font="F3", size=8.5, rgb=(0.3, 0.3, 0.3))
        p2.text(370, y, r[3], font="F4", size=9.5, rgb=(0.7, 0.3, 0.0))
        p2.text(480, y, r[4], font="F1", size=8.5, rgb=(0.3, 0.3, 0.3))
        p2.line(36, y - 6, 576, y - 6, rgb=(0.88, 0.9, 0.92), width=0.5)
        y -= 22
        
    # TABLE 4: Floor 4
    p2.rect(36, 410, 540, 115, fill_rgb=(1, 1, 1), stroke_rgb=(0.8, 0.85, 0.9), stroke_width=1)
    p2.rect(36, 500, 540, 25, fill_rgb=(0.55, 0.25, 0.15))
    p2.text(46, 508, "FLOOR 4 APARTMENTS", font="F2", size=9.5, rgb=(1, 1, 1))
    p2.text(150, 508, "RESIDENT NAME", font="F2", size=9, rgb=(1.0, 0.92, 0.88))
    p2.text(280, 508, "USERNAME", font="F2", size=9, rgb=(1.0, 0.92, 0.88))
    p2.text(370, 508, "UNIQUE PASSWORD", font="F2", size=9, rgb=(1, 0.9, 0.4))
    p2.text(480, 508, "CONTACT NO", font="F2", size=9, rgb=(1.0, 0.92, 0.88))
    
    f4_rows = [
        ("Flat 401", "S. Ramesh", "flat_401", "SRM@F4-401", "+91 98401 44401"),
        ("Flat 402", "Mohan Palaniappan", "flat_402", "SRM@F4-402", "+91 6380244185"),
        ("Flat 405", "Dr. E. Saravanan", "flat_405", "SRM@F4-405", "+91 98401 44405"),
        ("Flat 408", "M. Vani", "flat_408", "SRM@F4-408", "+91 98401 44408"),
    ]
    y = 478
    for idx, r in enumerate(f4_rows):
        if idx % 2 == 1:
            p2.rect(37, y - 5, 538, 22, fill_rgb=(1.0, 0.97, 0.95))
        p2.text(46, y, r[0], font="F2", size=9, rgb=(0.1, 0.1, 0.1))
        p2.text(150, y, r[1], font="F1", size=9, rgb=(0.15, 0.15, 0.15))
        p2.text(280, y, r[2], font="F3", size=8.5, rgb=(0.3, 0.3, 0.3))
        p2.text(370, y, r[3], font="F4", size=9.5, rgb=(0.7, 0.3, 0.0))
        p2.text(480, y, r[4], font="F1", size=8.5, rgb=(0.3, 0.3, 0.3))
        p2.line(36, y - 6, 576, y - 6, rgb=(0.88, 0.9, 0.92), width=0.5)
        y -= 22

    # Section 3: Operations & Features Guide
    p2.text(36, 380, "3. System Operations & Roles Workflow", font="F2", size=13, rgb=(0.06, 0.2, 0.35))
    
    p2.rect(36, 215, 540, 150, fill_rgb=(0.98, 0.98, 0.99), stroke_rgb=(0.82, 0.85, 0.88), stroke_width=1)
    
    # Left column: Resident Workflow
    p2.rect(46, 335, 240, 20, fill_rgb=(0.06, 0.73, 0.51))
    p2.text(54, 341, "RESIDENT PORTAL WORKFLOW", font="F2", size=9, rgb=(0.05, 0.1, 0.1))
    
    rw_text = [
        "1. Select 'Resident / House' on login page.",
        "2. Choose your floor (1-4) & tap your flat number.",
        "3. Enter unique password (e.g. SRM@F4-402).",
        "4. Submit complaints with SLA priority rating.",
        "5. View live Authority action & dispatch notes.",
        "6. Change password anytime via top Key icon."
    ]
    ry = 320
    for line in rw_text:
        p2.text(50, ry, line, font="F1", size=8, rgb=(0.2, 0.2, 0.2))
        ry -= 15
        
    # Right column: Authority Workflow
    p2.rect(300, 335, 260, 20, fill_rgb=(0.92, 0.72, 0.15))
    p2.text(308, 341, "AUTHORITY MANAGEMENT DESK", font="F2", size=9, rgb=(0.1, 0.1, 0.1))
    
    aw_text = [
        "1. Select 'Authority / Admin' on login page.",
        "2. Login with 'authority' & '6383898769'.",
        "3. Live Complaints Desk: Filter by floor & status.",
        "4. Tap 'Update' on ticket to dispatch technicians.",
        "5. Floors & Passwords: View & add any flat.",
        "6. Cloud Sync & Logs: Cross-device live complaint sync."
    ]
    ay = 320
    for line in aw_text:
        p2.text(304, ay, line, font="F1", size=8, rgb=(0.2, 0.2, 0.2))
        ay -= 15

    # Section 4: APK Download Details Box
    p2.rect(36, 95, 540, 100, fill_rgb=(0.95, 0.98, 0.96), stroke_rgb=(0.06, 0.73, 0.51), stroke_width=1.5)
    p2.text(48, 175, "4. ANDROID DEBUG APK INSTALLATION DETAILS", font="F2", size=10, rgb=(0.05, 0.4, 0.25))
    
    p2.text(48, 155, "Folder Location:", font="F2", size=8.5, rgb=(0.2, 0.2, 0.2))
    p2.text(140, 155, "APK_DOWNLOAD/app-debug.apk   &   build-outputs/app-debug.apk", font="F4", size=8.5, rgb=(0.05, 0.5, 0.3))
    
    p2.text(48, 138, "File Size & Type:", font="F2", size=8.5, rgb=(0.2, 0.2, 0.2))
    p2.text(140, 138, "22 MB - Real Standalone Installable Android Debug APK", font="F1", size=8.5, rgb=(0.2, 0.2, 0.2))
    
    p2.text(48, 121, "Installation:", font="F2", size=8.5, rgb=(0.2, 0.2, 0.2))
    p2.text(140, 121, "Download project ZIP or APK -> Copy to phone -> Enable 'Install Unknown Apps' -> Tap Install.", font="F1", size=8, rgb=(0.3, 0.3, 0.3))
    
    p2.text(48, 106, "Database Engine:", font="F2", size=8.5, rgb=(0.2, 0.2, 0.2))
    p2.text(140, 106, "Room SQLite (Local persistence for Complaints, Users, and Login History).", font="F1", size=8, rgb=(0.3, 0.3, 0.3))

    # Footer
    p2.line(36, 50, 576, 50, rgb=(0.8, 0.8, 0.8), width=0.8)
    p2.text(36, 38, "EcoApartment OS v2.0 - Certified Official Facility Document - SRM Green Residency", font="F1", size=8, rgb=(0.5, 0.5, 0.5))
    p2.text(530, 38, "Page 2 of 2", font="F1", size=8, rgb=(0.5, 0.5, 0.5))
    
    writer.add_page(p2.get_stream())
    
    # Save files
    import os
    os.makedirs("APK_DOWNLOAD", exist_ok=True)
    os.makedirs("APK DOWNLOAD", exist_ok=True)
    os.makedirs("build-outputs", exist_ok=True)
    
    writer.build("EcoApartment_Credentials_and_Manual.pdf")
    writer.build("APK_DOWNLOAD/EcoApartment_Credentials_and_Manual.pdf")
    writer.build("APK DOWNLOAD/EcoApartment_Credentials_and_Manual.pdf")
    writer.build("build-outputs/EcoApartment_Credentials_and_Manual.pdf")

if __name__ == "__main__":
    generate_credentials_pdf()
