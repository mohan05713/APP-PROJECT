package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.data.model.ComplaintStatus
import com.example.data.model.UserRole
import com.example.data.remote.SyncMode
import com.example.util.PdfReportExporter
import com.example.ui.components.AccountDetailsDialog
import com.example.ui.components.AuthorityPortal
import com.example.ui.components.ChangePasswordDialog
import com.example.ui.components.CloudSyncHubDialog
import com.example.ui.components.CommunityNoticesSection
import com.example.ui.components.ComplaintPortal
import com.example.ui.components.DirectoryDialog
import com.example.ui.components.EcoHeader
import com.example.ui.components.EmergencyBanner
import com.example.ui.components.FrontPageAccountCard
import com.example.ui.components.IoTSensorGrid
import com.example.ui.components.LoginScreen
import com.example.ui.components.MaintenanceDuesSection
import com.example.ui.components.MaintenanceStatusSection
import com.example.ui.components.NotificationDialog
import com.example.ui.components.ResourceTrackers
import com.example.ui.components.SensorDetailDialog
import com.example.ui.components.VisitorManagementSection
import com.example.ui.components.WeeklyConsumptionChart
import com.example.ui.theme.EnterpriseBg
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary

import com.example.ui.components.WebAppShell

@Composable
fun EcoApartmentApp(
    viewModel: EcoApartmentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoggingIn by viewModel.isLoggingIn.collectAsState()
    val emergencyBannerVisible by viewModel.emergencyBannerVisible.collectAsState()
    val environmentalSensors by viewModel.environmentalSensors.collectAsState()
    val resourceMetrics by viewModel.resourceMetrics.collectAsState()
    val weeklyConsumption by viewModel.weeklyConsumption.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val upcomingInspections by viewModel.upcomingInspections.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val loginHistory by viewModel.loginHistory.collectAsState()
    val visitorLogs by viewModel.visitorLogs.collectAsState()
    val maintenanceDues by viewModel.maintenanceDues.collectAsState()
    val communityNotices by viewModel.communityNotices.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val showNotificationDialog by viewModel.showNotificationDialog.collectAsState()
    val selectedSensor by viewModel.selectedSensor.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val syncMode by viewModel.syncMode.collectAsState()
    val localServerUrl by viewModel.localServerUrl.collectAsState()
    val customWebhookUrl by viewModel.customWebhookUrl.collectAsState()
    val showCloudSyncDialog by viewModel.showCloudSyncDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showAccountDetailsDialog by remember { mutableStateOf(false) }
    var showDirectoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(EnterpriseBg)
    ) {
        // Sticky Web Application Header & Switcher
        WebAppShell(
            currentUser = currentUser,
            onQuickResidentLogin = {
                viewModel.login("9876543210", "pass", UserRole.RESIDENT) { _, _ -> }
            },
            onQuickAuthorityLogin = {
                viewModel.quickAuthorityLogin { _, _ -> }
            },
            onOpenDirectory = {
                showDirectoryDialog = true
            },
            onRefresh = {
                viewModel.refreshTelemetry()
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(EnterpriseBg)
        ) {
            if (currentUser == null) {
            // Unauthenticated: Present Login Portal (Residents & Authority)
            LoginScreen(
                isLoggingIn = isLoggingIn,
                cloudSyncStatus = cloudSyncStatus,
                onLogin = { id, pass, role, callback ->
                    viewModel.login(id, pass, role, callback)
                },
                onQuickAuthority = { callback ->
                    viewModel.quickAuthorityLogin(callback)
                },
                onRegisterResident = { floor, flat, name, pass, contact ->
                    viewModel.registerOrUpdateResident(floor, flat, name, pass, contact) {}
                },
                onOpenCloudSync = { viewModel.setShowCloudSyncDialog(true) }
            )
        } else if (currentUser?.role == UserRole.AUTHORITY) {
            // Authenticated as Authority Admin Desk
            AuthorityPortal(
                complaints = complaints,
                loginHistory = loginHistory,
                allUsers = allUsers,
                visitorLogs = visitorLogs,
                maintenanceDues = maintenanceDues,
                communityNotices = communityNotices,
                onUpdateComplaint = { ticketId, status, notes ->
                    viewModel.updateAuthorityComplaint(ticketId, status, notes)
                },
                onRegisterResident = { floor, flat, name, pass, contact ->
                    viewModel.registerOrUpdateResident(floor, flat, name, pass, contact) {}
                },
                onApproveVisitor = { id, status, approver ->
                    viewModel.updateVisitorApproval(id, status, approver)
                },
                onLogVisitor = { name, phone, type, flat, floor, vehicle, purpose ->
                    viewModel.logGateEntry(name, phone, type, flat, floor, vehicle, purpose) {}
                },
                onScanBarcode = { barcode, callback ->
                    viewModel.checkInVisitorBarcode(barcode, callback)
                },
                onPayDue = { dueId, method ->
                    viewModel.payMaintenanceDue(dueId, method) {}
                },
                onPostNotice = { title, desc, cat, prio, pinned ->
                    viewModel.postCommunityNotice(title, desc, cat, prio, pinned)
                },
                onDeleteNotice = { id ->
                    viewModel.deleteCommunityNotice(id)
                },
                onClearLoginLogs = {
                    viewModel.clearLoginAuditHistory()
                },
                onLogout = {
                    viewModel.logout()
                },
                onExportPdf = {
                    val file = PdfReportExporter.generateAndExportReport(
                        context = context,
                        user = currentUser,
                        sensors = environmentalSensors,
                        dues = maintenanceDues,
                        complaints = complaints,
                        notices = communityNotices
                    )
                    file?.let { PdfReportExporter.shareOrOpenPdf(context, it) }
                },
                cloudSyncStatus = cloudSyncStatus,
                isCloudSyncing = isCloudSyncing,
                onManualSync = { viewModel.syncWithCloud() },
                onOpenCloudSync = { viewModel.setShowCloudSyncDialog(true) }
            )
        } else {
            // Authenticated as Resident Flat
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = EnterpriseBg,
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    EcoHeader(
                        currentUser = currentUser,
                        notificationCount = notifications.size,
                        isRefreshing = isRefreshing,
                        onNotificationClick = { viewModel.toggleNotificationDialog(true) },
                        onRefreshClick = { viewModel.refreshTelemetry() },
                        onLogoutClick = { viewModel.logout() },
                        onAccountClick = { showAccountDetailsDialog = true },
                        onOpenDirectory = { showDirectoryDialog = true },
                        onCloudSyncClick = { viewModel.setShowCloudSyncDialog(true) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(EnterpriseBg),
                    contentAlignment = Alignment.TopCenter
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 680.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Front Page Account Card (Account Name, Phone Number, Floor Number, Room Number)
                        currentUser?.let { user ->
                            item(key = "front_page_account_card") {
                                FrontPageAccountCard(
                                    user = user,
                                    onOpenAccount = { showAccountDetailsDialog = true },
                                    onExportPdf = {
                                        val file = PdfReportExporter.generateAndExportReport(
                                            context = context,
                                            user = user,
                                            sensors = environmentalSensors,
                                            dues = maintenanceDues,
                                            complaints = complaints,
                                            notices = communityNotices
                                        )
                                        file?.let { PdfReportExporter.shareOrOpenPdf(context, it) }
                                    }
                                )
                            }
                        }

                        // Emergency Flood & Rain Warning Banner
                        item(key = "emergency_banner") {
                            EmergencyBanner(
                                isVisible = emergencyBannerVisible,
                                onDismiss = { viewModel.dismissEmergencyBanner() },
                                onRestore = { viewModel.restoreEmergencyBanner() }
                            )
                        }

                        // ==========================================
                        // 1. LIVE IOT ENVIRONMENT (First)
                        // ==========================================
                        item(key = "iot_sensors") {
                            IoTSensorGrid(
                                sensors = environmentalSensors,
                                onSensorCardClick = { sensor ->
                                    viewModel.selectSensor(sensor)
                                }
                            )
                        }

                        item(key = "resource_trackers") {
                            ResourceTrackers(
                                metrics = resourceMetrics,
                                onToggleGasMode = { viewModel.toggleGasSupplyMode() },
                                onSelectCylinderUnit = { unitNum -> viewModel.selectActiveCylinderUnit(unitNum) },
                                onRequestRefill = { unitNum -> viewModel.requestCylinderRefill(unitNum) }
                            )
                        }

                        item(key = "weekly_chart") {
                            WeeklyConsumptionChart(
                                weeklyData = weeklyConsumption
                            )
                        }

                        // ==========================================
                        // 2. NOTICE BOARD (Second)
                        // ==========================================
                        item(key = "community_notices_section") {
                            CommunityNoticesSection(
                                notices = communityNotices,
                                currentUser = currentUser,
                                onPostNotice = { title, desc, cat, prio, pinned ->
                                    viewModel.postCommunityNotice(title, desc, cat, prio, pinned)
                                },
                                onDeleteNotice = { id ->
                                    viewModel.deleteCommunityNotice(id)
                                },
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }

                        // ==========================================
                        // 3. VISITOR MANAGEMENT (Third)
                        // ==========================================
                        item(key = "visitor_management_section") {
                            VisitorManagementSection(
                                visitorLogs = visitorLogs,
                                currentUser = currentUser,
                                onApproveVisitor = { id, status, approver ->
                                    viewModel.updateVisitorApproval(id, status, approver)
                                },
                                onLogNewVisitor = { name, phone, type, flat, floor, vehicle, purpose ->
                                    viewModel.logGateEntry(name, phone, type, flat, floor, vehicle, purpose) {}
                                },
                                onScanBarcode = { barcode, callback ->
                                    viewModel.checkInVisitorBarcode(barcode, callback)
                                },
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }

                        // ==========================================
                        // 4. MAINTENANCE AND DUES (Fourth)
                        // ==========================================
                        item(key = "maintenance_dues_section") {
                            MaintenanceDuesSection(
                                dues = maintenanceDues,
                                currentUser = currentUser,
                                onPayDue = { dueId, method ->
                                    viewModel.payMaintenanceDue(dueId, method) {}
                                },
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }

                        item(key = "maintenance_status_section") {
                            MaintenanceStatusSection(
                                complaints = complaints,
                                upcomingInspections = upcomingInspections,
                                onSetInspectionReminder = { inspection ->
                                    viewModel.bookInspectionReminder(inspection)
                                },
                                onNavigateToNewRequest = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index = 8)
                                    }
                                }
                            )
                        }

                        item(key = "complaint_portal") {
                            ComplaintPortal(
                                complaints = complaints,
                                isSubmitting = isSubmitting,
                                onSubmitComplaint = { category, priority, description, onSuccess ->
                                    viewModel.submitComplaint(category, priority, description, onSuccess)
                                },
                                onStatusChange = { ticket, newStatus ->
                                    viewModel.updateComplaintStatus(ticket, newStatus)
                                },
                                cloudSyncStatus = cloudSyncStatus,
                                isCloudSyncing = isCloudSyncing,
                                onManualSync = { viewModel.syncWithCloud() }
                            )
                        }

                        // Footer
                        item(key = "footer") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "EcoApartment OS • Smart Green Facility Management",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EnterpriseTextMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Logged in as ${currentUser?.residentName} (${currentUser?.flatNumber})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EnterpriseTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

        // Overlays & Dialogs
        if (showNotificationDialog) {
            NotificationDialog(
                notifications = notifications,
                onDismiss = { viewModel.toggleNotificationDialog(false) }
            )
        }

        selectedSensor?.let { sensor ->
            SensorDetailDialog(
                reading = sensor,
                onDismiss = { viewModel.selectSensor(null) }
            )
        }

        if (showAccountDetailsDialog && currentUser != null) {
            currentUser?.let { user ->
                AccountDetailsDialog(
                    user = user,
                    onDismiss = { showAccountDetailsDialog = false },
                    onUpdateAccount = { newContact, newPassword ->
                        viewModel.updateResidentAccount(newContact, newPassword) { _, _ -> }
                    }
                )
            }
        }

        if (showChangePasswordDialog && currentUser != null) {
            ChangePasswordDialog(
                currentFlat = currentUser?.flatNumber ?: "",
                onDismiss = { showChangePasswordDialog = false },
                onConfirm = { newPass ->
                    viewModel.updateCurrentPassword(newPass) { success ->
                        showChangePasswordDialog = false
                    }
                }
            )
        }

        if (showCloudSyncDialog) {
            CloudSyncHubDialog(
                currentSyncMode = syncMode,
                localUrl = localServerUrl,
                customUrl = customWebhookUrl,
                cloudSyncStatus = cloudSyncStatus,
                isSyncing = isCloudSyncing,
                onModeChange = { mode -> viewModel.setSyncMode(mode) },
                onSaveUrls = { local, custom -> viewModel.saveSyncUrls(local, custom) },
                onTestConnection = { callback -> viewModel.testSyncConnection(callback) },
                onTriggerSync = { callback -> viewModel.syncWithCloud(callback) },
                onDismiss = { viewModel.setShowCloudSyncDialog(false) }
            )
        }

        if (showDirectoryDialog) {
            DirectoryDialog(
                onDismiss = { showDirectoryDialog = false }
            )
        }
    }
}
}

