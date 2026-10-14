package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CommunityNoticeEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.LoginHistoryEntity
import com.example.data.local.MaintenanceDueItemEntity
import com.example.data.local.UserEntity
import com.example.data.local.VisitorEntity
import com.example.data.model.ApartmentDirectory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.NoticePriority
import com.example.data.model.UserRole
import com.example.data.model.VisitorApprovalStatus
import com.example.data.model.VisitorType
import com.example.ui.theme.EnterpriseBg
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseSecondary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100

@Composable
fun AuthorityPortal(
    complaints: List<ComplaintEntity>,
    loginHistory: List<LoginHistoryEntity>,
    allUsers: List<UserEntity>,
    visitorLogs: List<VisitorEntity> = emptyList(),
    maintenanceDues: List<MaintenanceDueItemEntity> = emptyList(),
    communityNotices: List<CommunityNoticeEntity> = emptyList(),
    onUpdateComplaint: (ticketId: String, status: ComplaintStatus, notes: String) -> Unit,
    onRegisterResident: (floor: Int, flat: String, name: String, pass: String, contact: String) -> Unit,
    onApproveVisitor: (id: String, status: VisitorApprovalStatus, approvedBy: String) -> Unit = { _, _, _ -> },
    onLogVisitor: (name: String, phone: String, type: VisitorType, flat: String, floor: Int, vehicle: String, purpose: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onScanBarcode: (barcode: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _ -> },
    onPayDue: (dueId: String, method: String) -> Unit = { _, _ -> },
    onPostNotice: (title: String, description: String, category: String, priority: NoticePriority, isPinned: Boolean) -> Unit = { _, _, _, _, _ -> },
    onDeleteNotice: (id: String) -> Unit = {},
    onClearLoginLogs: () -> Unit,
    onLogout: () -> Unit,
    onExportPdf: (() -> Unit)? = null,
    cloudSyncStatus: String = "🟢 SRM Cloud: Online",
    isCloudSyncing: Boolean = false,
    onManualSync: () -> Unit = {},
    onOpenCloudSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFloorFilter by remember { mutableStateOf<Int?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<ComplaintStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state for updating notes on a complaint
    var editingComplaint by remember { mutableStateOf<ComplaintEntity?>(null) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    val filteredComplaints = remember(complaints, selectedFloorFilter, selectedStatusFilter, searchQuery) {
        complaints.filter { item ->
            val matchesFloor = selectedFloorFilter == null || item.floorNumber == selectedFloorFilter
            val matchesStatus = selectedStatusFilter == null || item.status == selectedStatusFilter
            val matchesSearch = searchQuery.isBlank() ||
                    item.ticketId.contains(searchQuery, ignoreCase = true) ||
                    item.flatNumber.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.raisedByName.contains(searchQuery, ignoreCase = true)
            matchesFloor && matchesStatus && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EnterpriseBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Authority Header - Clean Enterprise Card
        Surface(
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 700.dp)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EnterprisePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Authority Portal",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EnterpriseTextPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFECFDF5),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Text(
                                        text = "LIVE DESK",
                                        color = Color(0xFF065F46),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "SRM Facility Management & Security Office",
                                style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 11.sp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onExportPdf != null) {
                            OutlinedButton(
                                onClick = onExportPdf,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = EnterprisePrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EnterprisePrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("authority_export_pdf_button")
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Logout Action
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEF2F2),
                                contentColor = Rose500
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("authority_logout_button")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Logout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cloud Sync Status Strip
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenCloudSync() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = EnterprisePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = cloudSyncStatus,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EnterpriseTextSecondary,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = onOpenCloudSync,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Server Settings",
                                    tint = EnterpriseSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Server",
                                    color = EnterpriseSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            TextButton(
                                onClick = onManualSync,
                                enabled = !isCloudSyncing,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                    tint = EnterprisePrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isCloudSyncing) "Syncing..." else "Sync",
                                    color = EnterprisePrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // KPI Quick Counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val pendingCount = complaints.count { it.status == ComplaintStatus.ASSIGNED }

                    KpiBadge("Tickets", "${complaints.size}", EnterpriseTextPrimary, Modifier.weight(1f))
                    KpiBadge("Pending", "$pendingCount", Color(0xFFD97706), Modifier.weight(1f))
                    KpiBadge("Visitors", "${visitorLogs.size}", EnterpriseSecondary, Modifier.weight(1f))
                    KpiBadge("Dues", "${maintenanceDues.count { it.status == com.example.data.model.DuePaymentStatus.PAID }}/${maintenanceDues.size}", EnterprisePrimary, Modifier.weight(1.1f))
                }
            }
        }

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = EnterpriseTextPrimary,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EnterprisePrimary,
                    height = 2.5.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = if (selectedTab == 0) EnterprisePrimary else EnterpriseTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tickets (${complaints.size})",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) EnterprisePrimary else EnterpriseTextSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = if (selectedTab == 1) EnterprisePrimary else EnterpriseTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Gate Access (${visitorLogs.size})",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) EnterprisePrimary else EnterpriseTextSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = null,
                            tint = if (selectedTab == 2) EnterprisePrimary else EnterpriseTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Dues Billing",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 2) EnterprisePrimary else EnterpriseTextSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = if (selectedTab == 3) EnterprisePrimary else EnterpriseTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Notices (${communityNotices.size})",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 3) EnterprisePrimary else EnterpriseTextSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { selectedTab = 4 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Key,
                            contentDescription = null,
                            tint = if (selectedTab == 4) EnterprisePrimary else EnterpriseTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Directory",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 4) EnterprisePrimary else EnterpriseTextSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 5,
                onClick = { selectedTab = 5 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            tint = if (selectedTab == 5) EnterprisePrimary else EnterpriseTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Login Audit",
                            fontSize = 11.5.sp,
                            fontWeight = if (selectedTab == 5) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 5) EnterprisePrimary else EnterpriseTextSecondary
                        )
                    }
                }
            )
        }

        // Main Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 700.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: Real-time Complaints Desk
                    ComplaintsDeskTab(
                        complaints = filteredComplaints,
                        selectedFloor = selectedFloorFilter,
                        selectedStatus = selectedStatusFilter,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onSelectFloor = { selectedFloorFilter = it },
                        onSelectStatus = { selectedStatusFilter = it },
                        onQuickStatus = { ticket, newStatus ->
                            onUpdateComplaint(ticket.ticketId, newStatus, ticket.authorityNotes)
                        },
                        onEditNotes = { ticket ->
                            editingComplaint = ticket
                        }
                    )
                }
                1 -> {
                    // TAB 1: Visitor Management & Gate Pass Scanner
                    VisitorManagementSection(
                        visitorLogs = visitorLogs,
                        currentUser = UserEntity("authority", "admin123", UserRole.AUTHORITY, 0, "Admin Office", "SRM Authority Desk", "044-2450981"),
                        onApproveVisitor = onApproveVisitor,
                        onLogNewVisitor = onLogVisitor,
                        onScanBarcode = onScanBarcode
                    )
                }
                2 -> {
                    // TAB 2: Maintenance & Dues Management
                    MaintenanceDuesSection(
                        dues = maintenanceDues,
                        currentUser = UserEntity("authority", "admin123", UserRole.AUTHORITY, 0, "Admin Office", "SRM Authority Desk", "044-2450981"),
                        onPayDue = onPayDue
                    )
                }
                3 -> {
                    // TAB 3: Community Notices & Announcements
                    CommunityNoticesSection(
                        notices = communityNotices,
                        currentUser = UserEntity("authority", "admin123", UserRole.AUTHORITY, 0, "Admin Office", "SRM Authority Desk", "044-2450981"),
                        onPostNotice = onPostNotice,
                        onDeleteNotice = onDeleteNotice
                    )
                }
                4 -> {
                    // TAB 4: Floor & Password Directory
                    ApartmentDirectoryTab(
                        onRegisterNew = { showRegisterDialog = true }
                    )
                }
                5 -> {
                    // TAB 5: Login History / Audit Logs
                    LoginHistoryTab(
                        logs = loginHistory,
                        onClear = onClearLoginLogs
                    )
                }
            }
        }
    }

    // Modal for editing Authority Notes / Resolution
    editingComplaint?.let { ticket ->
        var notesInput by remember { mutableStateOf(ticket.authorityNotes) }
        var statusSelection by remember { mutableStateOf(ticket.status) }

        AlertDialog(
            onDismissRequest = { editingComplaint = null },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateComplaint(ticket.ticketId, statusSelection, notesInput)
                        editingComplaint = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EnterprisePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save & Dispatch Update", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { editingComplaint = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", color = EnterpriseTextSecondary, fontSize = 12.sp)
                }
            },
            title = {
                Column {
                    Text(
                        text = "Update ${ticket.ticketId} (${ticket.flatNumber})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "Resident: ${ticket.raisedByName} • Floor ${ticket.floorNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Issue: ${ticket.description}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = EnterpriseTextSecondary,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )

                    Text("Update Status:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ComplaintStatus.values().forEach { st ->
                            FilterChip(
                                selected = statusSelection == st,
                                onClick = { statusSelection = st },
                                label = { Text(st.displayName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (st) {
                                        ComplaintStatus.RESOLVED -> Color(0xFF10B981)
                                        ComplaintStatus.IN_PROGRESS -> Color(0xFFD97706)
                                        ComplaintStatus.ASSIGNED -> Color(0xFF64748B)
                                    },
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF1F5F9),
                                    labelColor = EnterpriseTextSecondary
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Official Authority Action Notes", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Technician dispatched with replacement valve.", color = EnterpriseTextMuted, fontSize = 11.sp) },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnterprisePrimary,
                            unfocusedBorderColor = EnterpriseBorder,
                            focusedTextColor = EnterpriseTextPrimary,
                            unfocusedTextColor = EnterpriseTextPrimary,
                            cursorColor = EnterprisePrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showRegisterDialog) {
        RegisterFlatDialog(
            onDismiss = { showRegisterDialog = false },
            onRegister = { floor, flat, name, pass, contact ->
                onRegisterResident(floor, flat, name, pass, contact)
                showRegisterDialog = false
            }
        )
    }
}

@Composable
private fun KpiBadge(
    label: String,
    count: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accentColor)
            Text(text = label, fontSize = 10.sp, color = EnterpriseTextMuted)
        }
    }
}

@Composable
private fun ComplaintsDeskTab(
    complaints: List<ComplaintEntity>,
    selectedFloor: Int?,
    selectedStatus: ComplaintStatus?,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectFloor: (Int?) -> Unit,
    onSelectStatus: (ComplaintStatus?) -> Unit,
    onQuickStatus: (ComplaintEntity, ComplaintStatus) -> Unit,
    onEditNotes: (ComplaintEntity) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Search & Filters Header
        item(key = "filters_section") {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by ticket, flat or keywords...", color = EnterpriseTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EnterpriseTextMuted, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Floor Filter row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedFloor == null,
                        onClick = { onSelectFloor(null) },
                        label = { Text("All Floors", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EnterprisePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = EnterpriseTextSecondary
                        )
                    )
                    (1..4).forEach { floor ->
                        FilterChip(
                            selected = selectedFloor == floor,
                            onClick = { onSelectFloor(if (selectedFloor == floor) null else floor) },
                            label = { Text("Floor $floor", fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnterprisePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = EnterpriseTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Status Filter row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { onSelectStatus(null) },
                        label = { Text("All Statuses", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EnterpriseSecondary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = EnterpriseTextSecondary
                        )
                    )
                    ComplaintStatus.values().forEach { st ->
                        FilterChip(
                            selected = selectedStatus == st,
                            onClick = { onSelectStatus(if (selectedStatus == st) null else st) },
                            label = { Text(st.displayName, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnterpriseSecondary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = EnterpriseTextSecondary
                            )
                        )
                    }
                }
            }
        }

        if (complaints.isEmpty()) {
            item(key = "empty_complaints") {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No complaints found for selected filters.", color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("All issues resolved or no records match.", color = EnterpriseTextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(complaints, key = { it.ticketId }) { ticket ->
                AuthorityComplaintCard(
                    ticket = ticket,
                    onQuickStatus = { onQuickStatus(ticket, it) },
                    onEditNotes = { onEditNotes(ticket) }
                )
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AuthorityComplaintCard(
    ticket: ComplaintEntity,
    onQuickStatus: (ComplaintStatus) -> Unit,
    onEditNotes: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Top Row: Ticket ID, Flat & Floor, Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticket.ticketId,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary,
                            fontSize = 12.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                    ) {
                        Text(
                            text = "${ticket.flatNumber} (F-${ticket.floorNumber})",
                            color = Color(0xFF065F46),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Priority Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (ticket.priority) {
                        ComplaintPriority.CRITICAL -> Color(0xFFFEF2F2)
                        ComplaintPriority.URGENT -> Color(0xFFFFFBEB)
                        ComplaintPriority.STANDARD -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = ticket.priority.displayName,
                        color = when (ticket.priority) {
                            ComplaintPriority.CRITICAL -> Color(0xFFDC2626)
                            ComplaintPriority.URGENT -> Color(0xFFD97706)
                            ComplaintPriority.STANDARD -> Color(0xFF475569)
                        },
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Resident info and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Resident: ${ticket.raisedByName} • ${ticket.category.displayName}",
                    style = MaterialTheme.typography.bodySmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp)
                )
                Text(
                    text = ticket.formattedTime,
                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Description
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = EnterpriseTextPrimary, fontSize = 12.sp)
            )

            // Authority Notes if present
            if (ticket.authorityNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFFBEB),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Column {
                            Text("Authority Action Notes:", color = Color(0xFF92400E), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            Text(ticket.authorityNotes, color = Color(0xFF78350F), fontSize = 10.5.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Row: Status Pills & Update button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status pill
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (ticket.status) {
                        ComplaintStatus.RESOLVED -> Color(0xFFECFDF5)
                        ComplaintStatus.IN_PROGRESS -> Color(0xFFFFFBEB)
                        ComplaintStatus.ASSIGNED -> Color(0xFFF1F5F9)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (ticket.status) {
                            ComplaintStatus.RESOLVED -> Color(0xFFA7F3D0)
                            ComplaintStatus.IN_PROGRESS -> Color(0xFFFDE68A)
                            ComplaintStatus.ASSIGNED -> Color(0xFFCBD5E1)
                        }
                    )
                ) {
                    Text(
                        text = "Status: ${ticket.status.displayName}",
                        color = when (ticket.status) {
                            ComplaintStatus.RESOLVED -> Color(0xFF065F46)
                            ComplaintStatus.IN_PROGRESS -> Color(0xFF92400E)
                            ComplaintStatus.ASSIGNED -> Color(0xFF334155)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (ticket.status != ComplaintStatus.RESOLVED) {
                        Button(
                            onClick = { onQuickStatus(ComplaintStatus.RESOLVED) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EnterprisePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Resolve", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = onEditNotes,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EnterpriseTextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = EnterpriseTextSecondary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Update", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ApartmentDirectoryTab(
    onRegisterNew: () -> Unit
) {
    var selectedFloor by remember { mutableStateOf<Int?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(selectedFloor, searchQuery) {
        ApartmentDirectory.DEFAULT_FLATS.filter { flat ->
            val matchesFloor = selectedFloor == null || flat.floorNumber == selectedFloor
            val matchesSearch = searchQuery.isBlank() ||
                    flat.flatNumber.contains(searchQuery, ignoreCase = true) ||
                    flat.residentName.contains(searchQuery, ignoreCase = true)
            matchesFloor && matchesSearch
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "header_directory") {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Floor & Flat Password Registry",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Unique passwords generated for SRM Green Residency",
                            style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 11.sp)
                        )
                    }
                    Button(
                        onClick = onRegisterNew,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EnterprisePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("+ Add Flat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter flats or resident names...", color = EnterpriseTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EnterpriseTextMuted, modifier = Modifier.size(16.dp)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedFloor == null,
                        onClick = { selectedFloor = null },
                        label = { Text("All", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EnterprisePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F5F9),
                            labelColor = EnterpriseTextSecondary
                        )
                    )
                    (1..4).forEach { floor ->
                        FilterChip(
                            selected = selectedFloor == floor,
                            onClick = { selectedFloor = if (selectedFloor == floor) null else floor },
                            label = { Text("Floor $floor", fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnterprisePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = EnterpriseTextSecondary
                            )
                        )
                    }
                }
            }
        }

        items(filtered) { flat ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFECFDF5),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                            ) {
                                Text(
                                    text = "Floor ${flat.floorNumber}",
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = flat.flatNumber,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Resident: ${flat.residentName}",
                            style = MaterialTheme.typography.bodySmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp)
                        )
                        Text(
                            text = "Contact: ${flat.contactNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.5.sp)
                        )
                    }

                    // Password Card
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("Unique Password", color = EnterpriseTextMuted, fontSize = 8.5.sp)
                            Text(
                                text = flat.defaultPassword,
                                color = EnterprisePrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
        }

        item(key = "bottom_spacer_dir") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LoginHistoryTab(
    logs: List<LoginHistoryEntity>,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Login Audit Trail",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "Real-time records saved in local database",
                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 11.sp)
                )
            }

            if (logs.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClear,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EnterpriseTextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.ClearAll, contentDescription = null, tint = EnterpriseTextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Clear Logs", fontSize = 10.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (logs.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = EnterpriseTextMuted, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("No login events recorded yet.", color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("All future logins from residents and authority will appear here.", color = EnterpriseTextMuted, fontSize = 11.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs, key = { it.id }) { log ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (log.role == "AUTHORITY") Color(0xFFFEF3C7)
                                            else Color(0xFFECFDF5)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (log.role == "AUTHORITY") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (log.role == "AUTHORITY") Color(0xFFD97706) else Color(0xFF059669),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = log.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = EnterpriseTextPrimary,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (log.role == "AUTHORITY") Color(0xFFFEF3C7) else Color(0xFFECFDF5)
                                        ) {
                                            Text(
                                                text = if (log.role == "AUTHORITY") "AUTHORITY" else "${log.flatNumber} (F-${log.floorNumber})",
                                                color = if (log.role == "AUTHORITY") Color(0xFF92400E) else Color(0xFF065F46),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.5.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "User ID: ${log.username}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.sp)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = log.formattedTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EnterpriseTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "Status: Authenticated",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EnterprisePrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }

                item(key = "bottom_logs_spacer") {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
