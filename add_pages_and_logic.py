import re

with open('index.html', 'r', encoding='utf-8') as f:
    html = f.read()

# 1. Add residentOwnFamilyCard inside page-directory
dir_target = '<section class="page-view" id="page-directory">'
dir_replacement = '''<section class="page-view" id="page-directory">
                <!-- RESIDENT'S OWN FLAT & FAMILY PROFILE CARD (ONLY FOR RESIDENT LOGGED IN) -->
                <div id="residentOwnFamilyCard" style="display: none; background: #ffffff; border: 2px solid #0F766E; border-radius: 14px; padding: 22px; margin-bottom: 24px; box-shadow: 0 4px 14px rgba(15, 118, 110, 0.08);">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 14px; border-bottom: 1px solid #E2E8F0; padding-bottom: 16px; margin-bottom: 16px;">
                        <div>
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <span style="background: #0F766E; color: white; font-weight: 800; font-size: 13px; padding: 4px 12px; border-radius: 6px;" id="resCardFlatBadge">Flat A-101</span>
                                <h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #0F172A;" id="resCardHeadName">R. Shyam Sundar (Head of Family)</h3>
                            </div>
                            <div style="font-size: 13px; color: #64748B; margin-top: 5px;" id="resCardMeta">
                                Block A · Floor 1 · Primary Resident & Owner · Contact: +91 77089 94065
                            </div>
                        </div>
                        <div style="display: flex; gap: 8px; flex-wrap: wrap;" id="resCardBadges">
                            <span style="background: #EFF6FF; color: #1D4ED8; font-weight: 700; font-size: 12px; padding: 6px 12px; border-radius: 8px;" id="resBadgeGents">👨 1 Gent</span>
                            <span style="background: #FDF2F8; color: #BE185D; font-weight: 700; font-size: 12px; padding: 6px 12px; border-radius: 8px;" id="resBadgeLadies">👩 1 Lady</span>
                            <span style="background: #FEF3C7; color: #B45309; font-weight: 700; font-size: 12px; padding: 6px 12px; border-radius: 8px;" id="resBadgeChildren">👶 2 Children</span>
                            <span style="background: #F0FDF4; color: #15803D; font-weight: 800; font-size: 12px; padding: 6px 12px; border-radius: 8px;" id="resBadgeTotal">👨‍👩‍👧‍👦 4 Members Total</span>
                        </div>
                    </div>

                    <div style="margin-bottom: 16px;">
                        <div style="font-weight: 800; font-size: 13px; color: #0F172A; margin-bottom: 10px;">Family Members in My Home:</div>
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Relation</th>
                                        <th>Gender</th>
                                        <th>Age</th>
                                        <th>School / Occupation</th>
                                        <th>Blood Group</th>
                                    </tr>
                                </thead>
                                <tbody id="resOwnFamilyTableBody">
                                    <!-- Populated dynamically -->
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div style="display: flex; gap: 18px; background: #F8FAFC; border: 1px solid #E2E8F0; padding: 14px 18px; border-radius: 10px; flex-wrap: wrap; font-size: 13px; align-items: center;">
                        <div>🚗 <strong>Designated Parking:</strong> <span id="resCardParkingSlot" style="color: #0F766E; font-weight: 700;">Slot P-A101 (Basement 1)</span></div>
                        <div>⚡ <strong>Registered Vehicle:</strong> <span id="resCardVehicleNo" style="font-weight: 700;">TN-09-BE-4451</span></div>
                        <div>💳 <strong>Maintenance Status:</strong> <span id="resCardDuesStatus" class="badge badge-success">PAID</span></div>
                        <div style="margin-left: auto;">
                            <button type="button" onclick="switchPage('parking')" class="btn btn-outline btn-sm" style="font-size: 11px; font-weight: 700; border-color: #0F766E; color: #0F766E;">
                                🚗 View Parking Details
                            </button>
                        </div>
                    </div>
                </div>'''

if dir_target in html:
    html = html.replace(dir_target, dir_replacement, 1)
    print("Injected residentOwnFamilyCard into page-directory")

# 2. Add Parking and CCTV pages right before page-complaints
parking_and_cctv_pages = '''
            <!-- ============================================================== -->
            <!-- PAGE: PARKING ALLOTMENTS (FOR ALL 400 FLATS & SPECIFIC FLAT) -->
            <!-- ============================================================== -->
            <section class="page-view" id="page-parking">
                <div class="page-header">
                    <div>
                        <h2>🚗 Parking Allotments & Management</h2>
                        <p class="subtitle">Designated Basement Parking Slots, RFID Access & EV Charging Bays for all 400 flats</p>
                    </div>
                    <div style="display: flex; gap: 10px;">
                        <button type="button" onclick="switchPage('cctv'); switchCctvCam('CAM-01');" class="btn btn-primary btn-sm">
                            📹 Watch Parking CCTV Live (B1/B2)
                        </button>
                    </div>
                </div>

                <!-- STATS -->
                <div class="stats-grid" style="margin-bottom: 24px;">
                    <div class="stat-card">
                        <div class="stat-icon icon-emerald">🚗</div>
                        <div>
                            <div class="stat-label">Total Parking Slots</div>
                            <div class="stat-value">400 Slots</div>
                            <div class="stat-subtext">100% Flats Designated</div>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon icon-blue">⚡</div>
                        <div>
                            <div class="stat-label">EV Fast Charging Bays</div>
                            <div class="stat-value">134 Bays</div>
                            <div class="stat-subtext">3.3 kW AC Smart Chargers</div>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon icon-amber">🏢</div>
                        <div>
                            <div class="stat-label">Basement 1 Allocation</div>
                            <div class="stat-value">200 Slots</div>
                            <div class="stat-subtext">Blocks A & B Exclusive</div>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon icon-purple">🏢</div>
                        <div>
                            <div class="stat-label">Basement 2 Allocation</div>
                            <div class="stat-value">200 Slots</div>
                            <div class="stat-subtext">Blocks C & D Exclusive</div>
                        </div>
                    </div>
                </div>

                <!-- SPECIFIC FLAT PARKING CARD (HIGHLIGHTED FOR RESIDENT) -->
                <div id="resParkingHighlightCard" style="background: linear-gradient(135deg, #065F46 0%, #047857 100%); color: white; padding: 22px; border-radius: 14px; margin-bottom: 24px; box-shadow: 0 4px 14px rgba(6, 95, 70, 0.2);">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 14px;">
                        <div>
                            <div style="display: flex; align-items: center; gap: 10px;">
                                <span style="background: rgba(255,255,255,0.2); font-size: 11px; padding: 4px 10px; border-radius: 9999px; font-weight: 700;">YOUR DESIGNATED PARKING SLOT</span>
                                <span style="background: #10B981; color: white; font-size: 11px; padding: 3px 8px; border-radius: 4px; font-weight: 800;">ACTIVE RFID</span>
                            </div>
                            <h2 style="color: white; margin: 8px 0 4px; font-size: 26px; font-weight: 800;" id="myParkingSlotId">Slot P-A101</h2>
                            <div style="font-size: 13px; opacity: 0.9;" id="myParkingLocation">Basement 1 · Bay A-North · Near Elevator Core A1</div>
                        </div>
                        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                            <button type="button" onclick="switchPage('cctv'); switchCctvCam('CAM-01');" style="background: white; color: #065F46; border: none; font-weight: 800; font-size: 12px; padding: 9px 16px; border-radius: 8px; cursor: pointer;">
                                📹 Live Bay CCTV Cam
                            </button>
                        </div>
                    </div>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 14px; margin-top: 18px; padding-top: 16px; border-top: 1px solid rgba(255,255,255,0.2); font-size: 13px;">
                        <div>Registered Vehicle: <strong id="myParkingVehicle">TN-09-BE-4451</strong></div>
                        <div>Vehicle Type: <strong id="myParkingVehicleType">4-Wheeler (SUV/Sedan)</strong></div>
                        <div>EV Charger: <strong id="myParkingEvStatus">Standard Socket</strong></div>
                        <div>Gate RFID Sticker: <strong id="myParkingRfid">RFID-SRM-A101</strong></div>
                    </div>
                </div>

                <!-- ALL 400 SLOTS DIRECTORY (SEARCH & FILTERS) -->
                <div class="card" id="allParkingDirectorySection">
                    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
                        <div>
                            <h3 style="margin: 0; font-size: 16px; font-weight: 700;">Society Parking Roster (All 400 Flats)</h3>
                            <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">Search and filter parking allotments by flat, vehicle, or basement</div>
                        </div>
                        <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                            <input type="text" id="parkingSearchInput" class="form-control" placeholder="Search flat, resident, vehicle..." style="width: 220px; font-size: 12px;" oninput="filterParkingTable()">
                            <select id="parkingBasementFilter" class="form-control" style="width: 140px; font-size: 12px;" onchange="filterParkingTable()">
                                <option value="ALL">All Basements</option>
                                <option value="Basement 1">Basement 1 (A/B)</option>
                                <option value="Basement 2">Basement 2 (C/D)</option>
                            </select>
                        </div>
                    </div>
                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Slot ID</th>
                                    <th>Flat No</th>
                                    <th>Basement & Bay</th>
                                    <th>Resident Name</th>
                                    <th>Vehicle Number</th>
                                    <th>Vehicle Type</th>
                                    <th>EV Charging</th>
                                    <th>RFID Tag</th>
                                    <th>CCTV Live</th>
                                </tr>
                            </thead>
                            <tbody id="parkingTableBody">
                                <!-- Populated dynamically -->
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>

            <!-- ============================================================== -->
            <!-- PAGE: CCTV SURVEILLANCE HUB (8 LIVE FEEDS WITH PLAYER) -->
            <!-- ============================================================== -->
            <section class="page-view" id="page-cctv">
                <div class="page-header">
                    <div>
                        <h2>📹 24/7 CCTV Surveillance System</h2>
                        <p class="subtitle">Live high-definition video surveillance feeds for Parking, Entrance Gates, Lobbies, and Grounds</p>
                    </div>
                    <div style="display: flex; gap: 10px; align-items: center;">
                        <span style="display: inline-flex; align-items: center; gap: 6px; background: #FEE2E2; color: #DC2626; font-size: 11px; font-weight: 800; padding: 5px 12px; border-radius: 9999px;">
                            <span style="width: 8px; height: 8px; background: #DC2626; border-radius: 50%; display: inline-block; animation: pulse 1.5s infinite;"></span>
                            ALL 8 CAMERAS ONLINE
                        </span>
                        <button type="button" onclick="toggleNightVision()" class="btn btn-outline btn-sm" id="btnNightVision">
                            🌓 Night Vision (IR)
                        </button>
                        <button type="button" onclick="takeCctvSnapshot()" class="btn btn-outline btn-sm">
                            📸 Snapshot
                        </button>
                    </div>
                </div>

                <!-- CAMERA SELECTION TABS -->
                <div style="display: flex; gap: 8px; overflow-x: auto; padding-bottom: 12px; margin-bottom: 16px;" id="cctvCamSelectorTabs">
                    <button type="button" onclick="switchCctvCam('CAM-01')" class="btn btn-sm btn-primary cctv-tab-btn active" data-cam="CAM-01">🅿️ CAM-01: Basement 1 Parking</button>
                    <button type="button" onclick="switchCctvCam('CAM-02')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-02">🅿️ CAM-02: Basement 2 Parking</button>
                    <button type="button" onclick="switchCctvCam('CAM-03')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-03">🚧 CAM-03: Main Gate & ANPR</button>
                    <button type="button" onclick="switchCctvCam('CAM-04')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-04">🏢 CAM-04: Tower A/B Lobby</button>
                    <button type="button" onclick="switchCctvCam('CAM-05')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-05">📦 CAM-05: Tower C/D Parcel Hub</button>
                    <button type="button" onclick="switchCctvCam('CAM-06')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-06">🌳 CAM-06: Play Park & Garden</button>
                    <button type="button" onclick="switchCctvCam('CAM-07')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-07">☀️ CAM-07: Rooftop Solar & Tank</button>
                    <button type="button" onclick="switchCctvCam('CAM-08')" class="btn btn-sm btn-outline cctv-tab-btn" data-cam="CAM-08">🏊 CAM-08: Clubhouse & Pool</button>
                </div>

                <!-- MAIN CCTV PLAYER VIEWER -->
                <div style="background: #000000; border-radius: 14px; overflow: hidden; box-shadow: 0 8px 24px rgba(0,0,0,0.4); margin-bottom: 24px; position: relative;">
                    <!-- VIDEO HUD OVERLAY -->
                    <div style="position: absolute; top: 14px; left: 16px; right: 16px; display: flex; justify-content: space-between; align-items: center; z-index: 10; pointer-events: none;">
                        <div style="background: rgba(0,0,0,0.65); backdrop-filter: blur(4px); padding: 6px 12px; border-radius: 6px; color: white; font-family: 'JetBrains Mono', monospace; font-size: 12px; font-weight: 700; display: flex; align-items: center; gap: 8px;">
                            <span style="color: #EF4444; font-size: 14px;">●</span>
                            <span id="cctvHudTitle">CAM-01: Basement 1 Parking Lot</span>
                        </div>
                        <div style="background: rgba(0,0,0,0.65); backdrop-filter: blur(4px); padding: 6px 12px; border-radius: 6px; color: #10B981; font-family: 'JetBrains Mono', monospace; font-size: 12px; font-weight: 700; display: flex; align-items: center; gap: 12px;">
                            <span>1080P FHD · 30 FPS · 4.2 Mbps</span>
                            <span id="cctvLiveClock" style="color: white;">2026-10-24 13:00:00</span>
                        </div>
                    </div>

                    <!-- ANIMATED SURVEILLANCE CANVAS -->
                    <canvas id="cctvCanvas" width="960" height="480" style="width: 100%; height: auto; display: block; background: #0F172A;"></canvas>

                    <!-- BOTTOM CONTROLS BAR -->
                    <div style="background: #111827; padding: 12px 18px; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; color: white;">
                        <div style="display: flex; align-items: center; gap: 12px;">
                            <span style="font-size: 12px; color: #94A3B8;">Zone:</span>
                            <span style="font-size: 12px; font-weight: 700;" id="cctvZoneLabel">PARKING & EV CHARGING BAYS</span>
                            <span style="background: #064E3B; color: #34D399; font-size: 10px; padding: 2px 8px; border-radius: 4px; font-weight: 700;">MOTION TRACKING ACTIVE</span>
                        </div>
                        <div style="display: flex; gap: 8px; align-items: center;">
                            <span style="font-size: 11px; color: #94A3B8;">PTZ Controls:</span>
                            <button type="button" onclick="ptzAction('LEFT')" style="background: #1F2937; color: white; border: 1px solid #374151; padding: 4px 8px; border-radius: 4px; cursor: pointer;">◀</button>
                            <button type="button" onclick="ptzAction('UP')" style="background: #1F2937; color: white; border: 1px solid #374151; padding: 4px 8px; border-radius: 4px; cursor: pointer;">▲</button>
                            <button type="button" onclick="ptzAction('DOWN')" style="background: #1F2937; color: white; border: 1px solid #374151; padding: 4px 8px; border-radius: 4px; cursor: pointer;">▼</button>
                            <button type="button" onclick="ptzAction('RIGHT')" style="background: #1F2937; color: white; border: 1px solid #374151; padding: 4px 8px; border-radius: 4px; cursor: pointer;">▶</button>
                            <button type="button" onclick="ptzAction('ZOOM_IN')" style="background: #1F2937; color: white; border: 1px solid #374151; padding: 4px 8px; border-radius: 4px; cursor: pointer;">🔍 +</button>
                            <button type="button" onclick="ptzAction('ZOOM_OUT')" style="background: #1F2937; color: white; border: 1px solid #374151; padding: 4px 8px; border-radius: 4px; cursor: pointer;">🔍 -</button>
                        </div>
                    </div>
                </div>

                <!-- 8-CAMERA MINI-GRID PREVIEW -->
                <div style="font-weight: 800; font-size: 14px; margin-bottom: 12px; color: var(--text-main);">All 8 Cameras Live Status Grid</div>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 14px;" id="cctvGridContainer">
                    <!-- Populated dynamically -->
                </div>
            </section>
'''

target_complaints = '<section class="page-view" id="page-complaints">'
if target_complaints in html:
    html = html.replace(target_complaints, parking_and_cctv_pages + '\n' + target_complaints, 1)
    print("Injected Parking and CCTV pages before complaints")

# 3. Add page-iot-hub (Consolidated All-In-One IoT Page)
iot_hub_page = '''
            <!-- ============================================================== -->
            <!-- CONSOLIDATED IOT & SUSTAINABILITY HUB (SCROLLABLE ALL-IN-ONE) -->
            <!-- ============================================================== -->
            <section class="page-view" id="page-iot-hub">
                <div class="page-header">
                    <div>
                        <h2>🌱 IoT & Sustainability Telemetry Hub</h2>
                        <p class="subtitle">Consolidated real-time monitoring: Water, Solar & Air Quality, Flood Drainage, LPG Manifold, and Leak Anomalies</p>
                    </div>
                    <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                        <button type="button" onclick="document.getElementById('iot-sec-water').scrollIntoView({behavior: 'smooth'})" class="btn btn-outline btn-sm">💧 Water & Pumps</button>
                        <button type="button" onclick="document.getElementById('iot-sec-solar').scrollIntoView({behavior: 'smooth'})" class="btn btn-outline btn-sm">☀️ Solar & AQI</button>
                        <button type="button" onclick="document.getElementById('iot-sec-flood').scrollIntoView({behavior: 'smooth'})" class="btn btn-outline btn-sm">🌧️ Stormwater</button>
                        <button type="button" onclick="document.getElementById('iot-sec-lpg').scrollIntoView({behavior: 'smooth'})" class="btn btn-outline btn-sm">🔥 Smart LPG</button>
                        <button type="button" onclick="document.getElementById('iot-sec-anomalies').scrollIntoView({behavior: 'smooth'})" class="btn btn-outline btn-sm">⚠️ Anomalies</button>
                    </div>
                </div>

                <!-- 1. WATER RESOURCE SECTION -->
                <div id="iot-sec-water" style="margin-bottom: 32px; padding-top: 10px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; border-bottom: 2px solid #0F766E; padding-bottom: 8px;">
                        <h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #0F766E;">💧 1. Water Resource Tracker & Smart Pumps</h3>
                        <span style="font-size: 12px; font-weight: 700; color: #059669;">● Live Telemetry Synced</span>
                    </div>
                    <div class="stats-grid" style="margin-bottom: 18px;">
                        <div class="stat-card">
                            <div class="stat-icon icon-blue">💧</div>
                            <div>
                                <div class="stat-label">Overhead Tank Level</div>
                                <div class="stat-value" id="iotHubWaterTank">84%</div>
                                <div class="stat-subtext">42,000 / 50,000 Liters</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-emerald">🌊</div>
                            <div>
                                <div class="stat-label">Underground Sump</div>
                                <div class="stat-value" id="iotHubSump">76%</div>
                                <div class="stat-subtext">76,000 / 100,000 Liters</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-amber">⚙️</div>
                            <div>
                                <div class="stat-label">Primary Pump #1</div>
                                <div class="stat-value" id="iotHubPump1">ACTIVE</div>
                                <div class="stat-subtext">Flow: 240 L/min</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-purple">🔄</div>
                            <div>
                                <div class="stat-label">Backup Pump #2</div>
                                <div class="stat-value" id="iotHubPump2">STANDBY</div>
                                <div class="stat-subtext">Auto-Failover Ready</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 2. SOLAR & AIR QUALITY SECTION -->
                <div id="iot-sec-solar" style="margin-bottom: 32px; padding-top: 10px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; border-bottom: 2px solid #0284C7; padding-bottom: 8px;">
                        <h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #0284C7;">☀️ 2. Solar Microgrid & Environmental Telemetry</h3>
                        <span style="font-size: 12px; font-weight: 700; color: #0284C7;">● Rooftop 120kW Array</span>
                    </div>
                    <div class="stats-grid" style="margin-bottom: 18px;">
                        <div class="stat-card">
                            <div class="stat-icon icon-amber">☀️</div>
                            <div>
                                <div class="stat-label">Solar Real-Time Generation</div>
                                <div class="stat-value" id="iotHubSolarPower">48.5 kW</div>
                                <div class="stat-subtext">312 kWh generated today</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-emerald">🌿</div>
                            <div>
                                <div class="stat-label">Air Quality Index (AQI)</div>
                                <div class="stat-value" id="iotHubAqi">42 (Good)</div>
                                <div class="stat-subtext">PM2.5: 12 µg/m³ · PM10: 28 µg/m³</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-blue">🌡️</div>
                            <div>
                                <div class="stat-label">Ambient Temperature</div>
                                <div class="stat-value" id="iotHubTemp">31.4°C</div>
                                <div class="stat-subtext">Humidity: 68% · Kattankulathur</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-purple">🌱</div>
                            <div>
                                <div class="stat-label">Carbon Offset</div>
                                <div class="stat-value">246 kg CO₂</div>
                                <div class="stat-subtext">Clean Solar Power Share: 62%</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 3. FLOOD & STORMWATER SECTION -->
                <div id="iot-sec-flood" style="margin-bottom: 32px; padding-top: 10px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; border-bottom: 2px solid #2563EB; padding-bottom: 8px;">
                        <h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #2563EB;">🌧️ 3. Flood Sensor & Stormwater Drainage</h3>
                        <span style="font-size: 12px; font-weight: 700; color: #16A34A;">● Status: NORMAL / NO INUNDATION</span>
                    </div>
                    <div class="stats-grid" style="margin-bottom: 18px;">
                        <div class="stat-card">
                            <div class="stat-icon icon-blue">🌧️</div>
                            <div>
                                <div class="stat-label">Rain Intensity</div>
                                <div class="stat-value">0.0 mm/hr</div>
                                <div class="stat-subtext">Clear dry conditions</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-emerald">🌊</div>
                            <div>
                                <div class="stat-label">Storm Drain Water Level</div>
                                <div class="stat-value">12% Capacity</div>
                                <div class="stat-subtext">Flow: 45 L/min unobstructed</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-amber">🛡️</div>
                            <div>
                                <div class="stat-label">Basement Sump Pump #1</div>
                                <div class="stat-value">READY</div>
                                <div class="stat-subtext">Automatic float switch active</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-purple">💧</div>
                            <div>
                                <div class="stat-label">Rainwater Harvest Deck</div>
                                <div class="stat-value">65,000 L</div>
                                <div class="stat-subtext">Groundwater recharge active</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 4. SMART LPG SECTION -->
                <div id="iot-sec-lpg" style="margin-bottom: 32px; padding-top: 10px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; border-bottom: 2px solid #EA580C; padding-bottom: 8px;">
                        <h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #EA580C;">🔥 4. Smart LPG Piped Gas Manifold</h3>
                        <span style="font-size: 12px; font-weight: 700; color: #16A34A;">● Line Pressure: 1.85 BAR (Optimal)</span>
                    </div>
                    <div class="stats-grid" style="margin-bottom: 18px;">
                        <div class="stat-card">
                            <div class="stat-icon icon-amber">🔥</div>
                            <div>
                                <div class="stat-label">Manifold Pressure</div>
                                <div class="stat-value">1.85 Bar</div>
                                <div class="stat-subtext">Regulated safe pipeline supply</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-emerald">🛢️</div>
                            <div>
                                <div class="stat-label">Bank A (Active Bank)</div>
                                <div class="stat-value">84% Fill</div>
                                <div class="stat-subtext">4 x 47.5 kg Industrial Cylinders</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-blue">🛢️</div>
                            <div>
                                <div class="stat-label">Bank B (Reserve Bank)</div>
                                <div class="stat-value">100% Full</div>
                                <div class="stat-subtext">Auto-Switchover Valve Arm</div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon icon-purple">🛡️</div>
                            <div>
                                <div class="stat-label">Gas Leak Sensors</div>
                                <div class="stat-value">0 PPM</div>
                                <div class="stat-subtext">400 Risers Monitored · Safe</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 5. ANOMALY DETECTION SECTION -->
                <div id="iot-sec-anomalies" style="margin-bottom: 32px; padding-top: 10px;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; border-bottom: 2px solid #DC2626; padding-bottom: 8px;">
                        <h3 style="margin: 0; font-size: 18px; font-weight: 800; color: #DC2626;">⚠️ 5. Resource Anomaly & Acoustic Leak Log</h3>
                        <span style="font-size: 12px; font-weight: 700; color: #DC2626;">● AI Acoustic Vibration Engine</span>
                    </div>
                    <div class="card">
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Zone / Riser</th>
                                        <th>Sensor ID</th>
                                        <th>Anomaly Type</th>
                                        <th>Flow Discrepancy</th>
                                        <th>Timestamp</th>
                                        <th>Severity</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><strong>Block B - Floor 3 Riser</strong></td>
                                        <td>SEN-W-B3-04</td>
                                        <td>Micro-drip Acoustic Vibration</td>
                                        <td>+2.1 L/hr above baseline</td>
                                        <td>Today 04:12 AM</td>
                                        <td><span class="badge badge-warning">LOW</span></td>
                                        <td><span class="badge badge-success">RESOLVED</span></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Tower A - Main OHT Outlet</strong></td>
                                        <td>SEN-W-A-OHT</td>
                                        <td>Night Flow Analysis</td>
                                        <td>Nominal 0.0 L/hr</td>
                                        <td>Yesterday 02:00 AM</td>
                                        <td><span class="badge badge-success">INFO</span></td>
                                        <td><span class="badge badge-success">NORMAL</span></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Block C - Garden Sprinkler</strong></td>
                                        <td>SEN-W-C-GRD</td>
                                        <td>Timer auto shut-off</td>
                                        <td>Shut-off confirmed</td>
                                        <td>Yesterday 06:45 PM</td>
                                        <td><span class="badge badge-info">ROUTINE</span></td>
                                        <td><span class="badge badge-success">NORMAL</span></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </section>
'''

target_reports = '<section class="page-view" id="page-reports">'
if target_reports in html:
    html = html.replace(target_reports, iot_hub_page + '\n' + target_reports, 1)
    print("Injected Consolidated IoT Hub page before reports")

# 4. Add modalAuthLogin and modalFamilyRoster right before </body>
modals_html = '''
    <!-- SECURE AUTH / SWITCH USER MODAL -->
    <div class="modal-overlay" id="modalAuthLogin">
        <div class="modal-dialog" style="max-width: 480px;">
            <div class="modal-header" style="background: linear-gradient(135deg, #0F766E 0%, #115E59 100%); color: white;">
                <div>
                    <h3 style="color: white; margin: 0; font-size: 18px; font-weight: 800;">🔐 Secure Account Authentication</h3>
                    <p style="margin: 4px 0 0; font-size: 12px; opacity: 0.9;">Enter username & password to securely switch role or account</p>
                </div>
                <button type="button" class="modal-close" style="color: white;" onclick="closeModal('modalAuthLogin')">✕</button>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <form id="formSecureLoginModal" onsubmit="handleSecureModalLogin(event)">
                    <div style="background: #F1F5F9; padding: 12px 14px; border-radius: 10px; margin-bottom: 18px;">
                        <div style="font-size: 11px; font-weight: 700; color: #475569; text-transform: uppercase; margin-bottom: 8px;">Quick Test Profiles (Click to fill):</div>
                        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
                            <button type="button" onclick="fillAuthForm('flat_101', 'sham123')" style="background: white; border: 1.5px solid #0F766E; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #0F766E;">
                                👤 Shyam Sundar (A-101)
                            </button>
                            <button type="button" onclick="fillAuthForm('flat_402', 'mohan123')" style="background: white; border: 1.5px solid #0F766E; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #0F766E;">
                                👤 Mohan P. (B-402)
                            </button>
                            <button type="button" onclick="fillAuthForm('admin', 'admin123')" style="background: #FEF3C7; border: 1.5px solid #F59E0B; padding: 6px 12px; border-radius: 6px; font-size: 11px; font-weight: 800; cursor: pointer; color: #92400E;">
                                ⚡ Admin Authority
                            </button>
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom: 14px;">
                        <label class="form-label" style="font-weight: 700; font-size: 12px;">Username / Flat Number</label>
                        <input type="text" class="form-control" id="authModalUsername" placeholder="e.g. flat_101, flat_402, or admin" required style="font-size: 13px;">
                    </div>

                    <div class="form-group" style="margin-bottom: 20px;">
                        <label class="form-label" style="font-weight: 700; font-size: 12px;">Password</label>
                        <div style="position: relative;">
                            <input type="password" class="form-control" id="authModalPassword" placeholder="Enter secure password" required style="font-size: 13px; padding-right: 40px;">
                            <button type="button" onclick="togglePasswordVisibility('authModalPassword', 'eyeIconModal')" id="eyeIconModal" style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; font-size: 16px;">👁️</button>
                        </div>
                        <div style="font-size: 11px; color: #64748B; margin-top: 4px;">Password is strictly verified against encrypted database credentials.</div>
                    </div>

                    <div style="display: flex; gap: 10px; justify-content: flex-end;">
                        <button type="button" class="btn btn-outline btn-sm" onclick="closeModal('modalAuthLogin')">Cancel</button>
                        <button type="submit" class="btn btn-primary btn-sm" style="font-weight: 800; padding: 9px 18px;">🔓 Authenticate & Log In</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- FULL FAMILY DETAILS MODAL (FOR ALL 400 FLATS) -->
    <div class="modal-overlay" id="modalFamilyRoster">
        <div class="modal-dialog" style="max-width: 680px;">
            <div class="modal-header" style="background: linear-gradient(135deg, #0F766E 0%, #115E59 100%); color: white;">
                <div>
                    <h3 style="color: white; margin: 0; font-size: 18px; font-weight: 800;" id="familyModalFlatTitle">Flat Family Census Roster</h3>
                    <p style="margin: 4px 0 0; font-size: 12px; opacity: 0.9;" id="familyModalSubtitle">Verified SRM Green Residency Family Details</p>
                </div>
                <button type="button" class="modal-close" style="color: white;" onclick="closeModal('modalFamilyRoster')">✕</button>
            </div>
            <div class="modal-body" style="padding: 24px;">
                <div style="display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap;">
                    <div style="background: #F0FDF4; border: 1px solid #BBF7D0; padding: 10px 14px; border-radius: 8px; flex: 1; min-width: 140px;">
                        <div style="font-size: 11px; font-weight: 700; color: #166534;">HEAD OF FAMILY</div>
                        <div style="font-size: 15px; font-weight: 800; color: #14532D;" id="famHeadName">--</div>
                    </div>
                    <div style="background: #EFF6FF; border: 1px solid #BFDBFE; padding: 10px 14px; border-radius: 8px;">
                        <div style="font-size: 11px; font-weight: 700; color: #1E40AF;">GENTS</div>
                        <div style="font-size: 16px; font-weight: 800; color: #1E3A8A;" id="famGentsCount">0</div>
                    </div>
                    <div style="background: #FDF2F8; border: 1px solid #FBCFE8; padding: 10px 14px; border-radius: 8px;">
                        <div style="font-size: 11px; font-weight: 700; color: #9D174D;">LADIES</div>
                        <div style="font-size: 16px; font-weight: 800; color: #831843;" id="famLadiesCount">0</div>
                    </div>
                    <div style="background: #FEF3C7; border: 1px solid #FDE68A; padding: 10px 14px; border-radius: 8px;">
                        <div style="font-size: 11px; font-weight: 700; color: #92400E;">CHILDREN</div>
                        <div style="font-size: 16px; font-weight: 800; color: #78350F;" id="famChildrenCount">0</div>
                    </div>
                    <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 10px 14px; border-radius: 8px;">
                        <div style="font-size: 11px; font-weight: 700; color: #475569;">TOTAL MEMBERS</div>
                        <div style="font-size: 16px; font-weight: 800; color: #0F172A;" id="famTotalCount">0</div>
                    </div>
                </div>

                <div style="font-weight: 800; font-size: 13px; color: #1E293B; margin-bottom: 10px; display: flex; align-items: center; justify-content: space-between;">
                    <span>Family Members Breakdown:</span>
                    <span style="font-size: 11px; font-weight: 600; color: #64748B;">Official SRM Green Residency Census Record</span>
                </div>

                <div class="table-container" style="max-height: 280px; overflow-y: auto;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Relation</th>
                                <th>Gender</th>
                                <th>Age</th>
                                <th>School / Occupation</th>
                                <th>Blood Group</th>
                            </tr>
                        </thead>
                        <tbody id="familyMembersTableBody">
                            <!-- Populated dynamically -->
                        </tbody>
                    </table>
                </div>

                <div style="display: flex; justify-content: flex-end; margin-top: 20px;">
                    <button type="button" class="btn btn-primary btn-sm" onclick="closeModal('modalFamilyRoster')">Close Roster</button>
                </div>
            </div>
        </div>
    </div>
'''

html = html.replace('</body>', modals_html + '\n</body>', 1)
print("Injected auth modal and family modal")

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html)
print("Saved pages and modals to index.html")
