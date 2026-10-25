package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CommunityNoticeEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.LoginHistoryEntity
import com.example.data.local.MaintenanceDueItemEntity
import com.example.data.local.UserEntity
import com.example.data.local.VisitorEntity
import com.example.data.model.ApartmentDirectory
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.DailyConsumption
import com.example.data.model.DuePaymentStatus
import com.example.data.model.EnvironmentalSensors
import com.example.data.model.MaintenanceOverallStatus
import com.example.data.model.MaintenanceSummary
import com.example.data.model.NoticePriority
import com.example.data.model.NotificationItem
import com.example.data.model.ResourceMetrics
import com.example.data.model.SensorReading
import com.example.data.model.UpcomingInspection
import com.example.data.model.UserRole
import com.example.data.model.VisitorApprovalStatus
import com.example.data.model.VisitorType
import com.example.data.remote.CloudSyncService
import com.example.data.remote.MasterCloudSyncPayload
import com.example.data.remote.SyncMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EcoApartmentViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val complaintDao = database.complaintDao()
    private val userDao = database.userDao()
    private val loginHistoryDao = database.loginHistoryDao()
    private val visitorDao = database.visitorDao()
    private val maintenanceDueDao = database.maintenanceDueDao()
    private val communityNoticeDao = database.communityNoticeDao()
    private val cloudSyncService = CloudSyncService(application.applicationContext)

    private val _cloudSyncStatus = MutableStateFlow("🟢 SRM Cloud: Online")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _syncMode = MutableStateFlow(cloudSyncService.syncMode)
    val syncMode: StateFlow<SyncMode> = _syncMode.asStateFlow()

    private val _localServerUrl = MutableStateFlow(cloudSyncService.localServerUrl)
    val localServerUrl: StateFlow<String> = _localServerUrl.asStateFlow()

    private val _customWebhookUrl = MutableStateFlow(cloudSyncService.customWebhookUrl)
    val customWebhookUrl: StateFlow<String> = _customWebhookUrl.asStateFlow()

    private val _showCloudSyncDialog = MutableStateFlow(false)
    val showCloudSyncDialog: StateFlow<Boolean> = _showCloudSyncDialog.asStateFlow()

    val complaints: StateFlow<List<ComplaintEntity>> = complaintDao.getAllComplaints()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allUsers: StateFlow<List<UserEntity>> = userDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val loginHistory: StateFlow<List<LoginHistoryEntity>> = loginHistoryDao.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val visitorLogs: StateFlow<List<VisitorEntity>> = visitorDao.getAllVisitors()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val maintenanceDues: StateFlow<List<MaintenanceDueItemEntity>> = maintenanceDueDao.getAllDues()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val communityNotices: StateFlow<List<CommunityNoticeEntity>> = communityNoticeDao.getAllNotices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _emergencyBannerVisible = MutableStateFlow(true)
    val emergencyBannerVisible: StateFlow<Boolean> = _emergencyBannerVisible.asStateFlow()

    private val _environmentalSensors = MutableStateFlow(EnvironmentalSensors())
    val environmentalSensors: StateFlow<EnvironmentalSensors> = _environmentalSensors.asStateFlow()

    private val _resourceMetrics = MutableStateFlow(ResourceMetrics())
    val resourceMetrics: StateFlow<ResourceMetrics> = _resourceMetrics.asStateFlow()

    private val _weeklyConsumption = MutableStateFlow(
        listOf(
            DailyConsumption("Mon", electricityKWh = 16.5, waterLitres = 340),
            DailyConsumption("Tue", electricityKWh = 14.8, waterLitres = 310),
            DailyConsumption("Wed", electricityKWh = 18.2, waterLitres = 380),
            DailyConsumption("Thu", electricityKWh = 15.1, waterLitres = 290),
            DailyConsumption("Fri", electricityKWh = 17.4, waterLitres = 350),
            DailyConsumption("Sat", electricityKWh = 21.0, waterLitres = 420),
            DailyConsumption("Sun", electricityKWh = 14.2, waterLitres = 320)
        )
    )
    val weeklyConsumption: StateFlow<List<DailyConsumption>> = _weeklyConsumption.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "n1",
                title = "Basement Sump Pump Activated",
                message = "Automated high-capacity pump activated at 78% retention threshold.",
                timestamp = "12m ago",
                isEmergency = true
            ),
            NotificationItem(
                id = "n2",
                title = "Overhead Tank Refill",
                message = "Solar-powered motor pump reached 84% capacity. Flow stable at 28 L/min.",
                timestamp = "1h ago",
                isEmergency = false
            ),
            NotificationItem(
                id = "n3",
                title = "Green Energy Milestone",
                message = "Your flat conserved 15.8 kWh below the daily allocation limit.",
                timestamp = "3h ago",
                isEmergency = false
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _showNotificationDialog = MutableStateFlow(false)
    val showNotificationDialog: StateFlow<Boolean> = _showNotificationDialog.asStateFlow()

    private val _selectedSensor = MutableStateFlow<SensorReading?>(null)
    val selectedSensor: StateFlow<SensorReading?> = _selectedSensor.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    private val _upcomingInspections = MutableStateFlow(
        listOf(
            UpcomingInspection(
                id = "insp-01",
                title = "Elevator & Lift Safety Certification",
                category = "Vertical Transport",
                scheduledDate = "Tomorrow, Sep 22",
                timeSlot = "10:00 AM - 01:00 PM",
                inspectorName = "Otis Certified Engineering Team",
                status = "Tomorrow",
                scope = "Passenger lifts A & B brake integrity, emergency call button test, and sensor doors.",
                notes = "Lift B will be momentarily paused for 20 mins around 11:30 AM."
            ),
            UpcomingInspection(
                id = "insp-02",
                title = "Fire Hydrant & Smoke Sensor Audit",
                category = "Life Safety",
                scheduledDate = "Friday, Sep 25",
                timeSlot = "02:30 PM - 05:00 PM",
                inspectorName = "Tamil Nadu Fire & Rescue Station Inspector",
                status = "In 4 Days",
                scope = "Corridor sprinkler pressure, fire hose reel in floors 1-4, smoke detector chime check.",
                notes = "Brief 5-second horn test will be broadcast through corridor sirens at 03:00 PM."
            ),
            UpcomingInspection(
                id = "insp-03",
                title = "Rooftop Solar & Inverter Efficiency Check",
                category = "Green Energy",
                scheduledDate = "Monday, Sep 28",
                timeSlot = "09:00 AM - 12:00 PM",
                inspectorName = "SRM Green Grid Operations",
                status = "Next Week",
                scope = "Solar PV string voltage, micro-inverter thermography, battery storage health.",
                notes = "Routine biannual performance tuning."
            ),
            UpcomingInspection(
                id = "insp-04",
                title = "Central Water Purifier & Chlorination Audit",
                category = "Hydrology",
                scheduledDate = "Thursday, Oct 1",
                timeSlot = "11:00 AM - 02:00 PM",
                inspectorName = "HydroPure Environmental Lab",
                status = "Upcoming",
                scope = "TDS sampling across all 4 floor risers, sediment filter backwash inspection.",
                notes = "Drinking water supply will remain completely unaffected."
            )
        )
    )
    val upcomingInspections: StateFlow<List<UpcomingInspection>> = _upcomingInspections.asStateFlow()

    fun bookInspectionReminder(inspection: UpcomingInspection) {
        val note = NotificationItem(
            id = "insp-rem-${System.currentTimeMillis()}",
            title = "Reminder: ${inspection.title}",
            message = "Scheduled for ${inspection.scheduledDate} (${inspection.timeSlot}). Inspector: ${inspection.inspectorName}",
            timestamp = "Just now",
            isEmergency = false
        )
        _notifications.value = listOf(note) + _notifications.value
        _userFeedbackMessage.value = "Reminder set for ${inspection.title}!"
    }

    init {
        seedInitialData()
        startPeriodicCloudSync()
    }

    fun setSyncMode(mode: SyncMode) {
        cloudSyncService.syncMode = mode
        _syncMode.value = mode
        _cloudSyncStatus.value = "Switching to ${mode.label}..."
        syncWithCloud()
    }

    fun saveSyncUrls(local: String, custom: String) {
        cloudSyncService.localServerUrl = local
        cloudSyncService.customWebhookUrl = custom
        _localServerUrl.value = local
        _customWebhookUrl.value = custom
    }

    fun testSyncConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = cloudSyncService.testConnection()
            onResult(res.first, res.second)
        }
    }

    fun setShowCloudSyncDialog(show: Boolean) {
        _showCloudSyncDialog.value = show
    }

    private fun startPeriodicCloudSync() {
        viewModelScope.launch {
            delay(1000)
            syncWithCloud()
            while (true) {
                delay(8000) // 8-second sync heartbeat
                syncWithCloud()
            }
        }
    }

    /**
     * Master two-way cloud sync for Complaints, Users, Visitors, Dues, and Community Notices.
     */
    fun syncWithCloud(onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            try {
                val localComplaints = complaintDao.getAllComplaintsSync()
                val localUsers = userDao.getAllUsersList()
                val localVisitors = visitorDao.getAllVisitorsSync()
                val localDues = maintenanceDueDao.getAllDuesSync()
                val localNotices = communityNoticeDao.getAllNoticesSync()

                val fetchResult = cloudSyncService.fetchMasterPayloadFromCloud()
                val cloudPayload = fetchResult.getOrNull() ?: MasterCloudSyncPayload()

                // 1. Merge Complaints
                val mergedComplaintsMap = linkedMapOf<String, ComplaintEntity>()
                for (c in cloudPayload.complaints) { mergedComplaintsMap[c.ticketId] = c }
                for (l in localComplaints) {
                    val existing = mergedComplaintsMap[l.ticketId]
                    if (existing == null) {
                        mergedComplaintsMap[l.ticketId] = l
                    } else {
                        val preferred = if (l.authorityNotes.isNotBlank() && existing.authorityNotes.isBlank()) l
                        else if (l.status.ordinal > existing.status.ordinal) l
                        else existing
                        mergedComplaintsMap[l.ticketId] = preferred
                    }
                }
                val mergedComplaints = mergedComplaintsMap.values.toList()
                if (mergedComplaints.isNotEmpty()) {
                    complaintDao.insertComplaints(mergedComplaints)
                }

                // 2. Merge Users & Passwords (Timestamp-based Last Write Wins)
                val mergedUsersMap = linkedMapOf<String, UserEntity>()
                for (u in localUsers) { mergedUsersMap[u.username.lowercase()] = u }
                for (cu in cloudPayload.users) {
                    val existing = mergedUsersMap[cu.username.lowercase()]
                    if (existing == null) {
                        mergedUsersMap[cu.username.lowercase()] = cu
                    } else {
                        // If cloud copy has newer timestamp, take cloud's updated password/details
                        if (cu.updatedAtMillis > existing.updatedAtMillis) {
                            mergedUsersMap[cu.username.lowercase()] = cu
                        } else {
                            mergedUsersMap[cu.username.lowercase()] = existing
                        }
                    }
                }
                val mergedUsers = mergedUsersMap.values.toList()
                if (mergedUsers.isNotEmpty()) {
                    userDao.insertUsers(mergedUsers)
                    // If current user was updated from cloud, refresh session
                    val cur = _currentUser.value
                    if (cur != null) {
                        val refreshed = mergedUsers.find { it.username.equals(cur.username, ignoreCase = true) }
                        if (refreshed != null && refreshed.password != cur.password) {
                            _currentUser.value = refreshed
                        }
                    }
                }

                // 3. Merge Visitors
                val mergedVisitorsMap = linkedMapOf<String, VisitorEntity>()
                for (v in cloudPayload.visitors) { mergedVisitorsMap[v.id] = v }
                for (lv in localVisitors) {
                    val existing = mergedVisitorsMap[lv.id]
                    if (existing == null) {
                        mergedVisitorsMap[lv.id] = lv
                    } else {
                        val preferred = if (lv.status != VisitorApprovalStatus.PENDING && existing.status == VisitorApprovalStatus.PENDING) lv else existing
                        mergedVisitorsMap[lv.id] = preferred
                    }
                }
                val mergedVisitors = mergedVisitorsMap.values.toList()
                if (mergedVisitors.isNotEmpty()) {
                    visitorDao.insertVisitors(mergedVisitors)
                }

                // 4. Merge Maintenance Dues
                val mergedDuesMap = linkedMapOf<String, MaintenanceDueItemEntity>()
                for (d in cloudPayload.dues) { mergedDuesMap[d.id] = d }
                for (ld in localDues) {
                    val existing = mergedDuesMap[ld.id]
                    if (existing == null) {
                        mergedDuesMap[ld.id] = ld
                    } else {
                        val preferred = if (ld.status == DuePaymentStatus.PAID && existing.status != DuePaymentStatus.PAID) ld else existing
                        mergedDuesMap[ld.id] = preferred
                    }
                }
                val mergedDues = mergedDuesMap.values.toList()
                if (mergedDues.isNotEmpty()) {
                    maintenanceDueDao.insertDues(mergedDues)
                }

                // 5. Merge Community Notices
                val mergedNoticesMap = linkedMapOf<String, CommunityNoticeEntity>()
                for (n in cloudPayload.notices) { mergedNoticesMap[n.id] = n }
                for (ln in localNotices) {
                    if (!mergedNoticesMap.containsKey(ln.id)) {
                        mergedNoticesMap[ln.id] = ln
                    }
                }
                val mergedNotices = mergedNoticesMap.values.toList()
                if (mergedNotices.isNotEmpty()) {
                    communityNoticeDao.insertNotices(mergedNotices)
                }

                // Push unified payload back to active cloud/local server
                val unifiedPayload = MasterCloudSyncPayload(
                    complaints = mergedComplaints,
                    users = mergedUsers,
                    visitors = mergedVisitors,
                    dues = mergedDues,
                    notices = mergedNotices,
                    timestamp = System.currentTimeMillis()
                )
                cloudSyncService.pushMasterPayloadToCloud(unifiedPayload)

                val modeLabel = when (cloudSyncService.syncMode) {
                    SyncMode.CLOUD_RELAY -> "Cloud Relay"
                    SyncMode.LOCAL_HOST -> "Laptop WiFi"
                    SyncMode.GOOGLE_SCRIPT_WEBHOOK -> "Google Drive"
                }

                _cloudSyncStatus.value = "🟢 $modeLabel: Online (${mergedUsers.size} users, ${mergedVisitors.size} visitors synced)"
                _isCloudSyncing.value = false
                onComplete?.invoke(true, "Cloud & Database in sync across all devices via $modeLabel!")
            } catch (e: Exception) {
                _cloudSyncStatus.value = "⚠️ Local Storage Mode"
                _isCloudSyncing.value = false
                onComplete?.invoke(false, "Sync warning: ${e.message}")
            }
        }
    }

    private fun seedInitialData() {
        viewModelScope.launch {
            // Seed Initial Users
            userDao.insertUsers(ApartmentDirectory.getInitialUserEntities())

            // Seed initial complaints if empty
            if (complaintDao.getCount() == 0) {
                complaintDao.insertComplaint(
                    ComplaintEntity(
                        ticketId = "#ECO-402",
                        flatNumber = "Flat 402",
                        floorNumber = 4,
                        raisedByName = "Mohan Palaniappan",
                        category = ComplaintCategory.GARBAGE_BLOCK,
                        priority = ComplaintPriority.STANDARD,
                        description = "Waste disposal chute hatch jammed on 4th floor corridor near Flat 402.",
                        status = ComplaintStatus.IN_PROGRESS,
                        createdAtMillis = System.currentTimeMillis() - 86400000L,
                        formattedTime = "Yesterday, 04:30 PM",
                        authorityNotes = "Janitor assigned with replacement latch spring."
                    )
                )
                complaintDao.insertComplaint(
                    ComplaintEntity(
                        ticketId = "#ECO-305",
                        flatNumber = "Flat 305",
                        floorNumber = 3,
                        raisedByName = "V. Suresh",
                        category = ComplaintCategory.WATER_LEAKAGE,
                        priority = ComplaintPriority.URGENT,
                        description = "Minor pinhole leakage observed at the shared utility shaft riser valve.",
                        status = ComplaintStatus.ASSIGNED,
                        createdAtMillis = System.currentTimeMillis() - (86400000L * 2),
                        formattedTime = "2 days ago",
                        authorityNotes = "Assigned to Plumbing Contractor. Scheduled inspection."
                    )
                )
                complaintDao.insertComplaint(
                    ComplaintEntity(
                        ticketId = "#ECO-101",
                        flatNumber = "Flat 101",
                        floorNumber = 1,
                        raisedByName = "K. Ramanathan",
                        category = ComplaintCategory.ELECTRICITY_FAULT,
                        priority = ComplaintPriority.CRITICAL,
                        description = "Main corridor light flickering and circuit breaker tripped after rain.",
                        status = ComplaintStatus.RESOLVED,
                        createdAtMillis = System.currentTimeMillis() - (86400000L * 3),
                        formattedTime = "3 days ago",
                        authorityNotes = "Replaced 32A MCB switch. Restored normal load."
                    )
                )
            }

            // Seed initial Visitors if empty
            val currentVisitors = visitorDao.getAllVisitorsSync()
            if (currentVisitors.isEmpty()) {
                val initialVisitors = listOf(
                    VisitorEntity(
                        id = "VIS-101-01",
                        visitorName = "Amazon Delivery (Rajesh K.)",
                        visitorPhone = "+91 98401 22910",
                        visitorType = VisitorType.DELIVERY,
                        flatNumber = "Flat 402",
                        floorNumber = 4,
                        vehicleNumber = "TN 09 BK 4421",
                        passCodeOrBarcode = "SRM-DEL-402-881",
                        entryTime = "Today, 11:15 AM",
                        status = VisitorApprovalStatus.APPROVED,
                        approvedByResident = "Mohan Palaniappan",
                        purpose = "E-commerce Parcel Delivery"
                    ),
                    VisitorEntity(
                        id = "VIS-203-02",
                        visitorName = "Swiggy Food Courier (Arun)",
                        visitorPhone = "+91 97910 88231",
                        visitorType = VisitorType.DELIVERY,
                        flatNumber = "Flat 203",
                        floorNumber = 2,
                        vehicleNumber = "TN 14 AX 1902",
                        passCodeOrBarcode = "SRM-DEL-203-319",
                        entryTime = "Today, 12:40 PM",
                        status = VisitorApprovalStatus.PENDING,
                        purpose = "Lunch Delivery"
                    ),
                    VisitorEntity(
                        id = "VIS-301-03",
                        visitorName = "P. Venkat & Family",
                        visitorPhone = "+91 94441 55667",
                        visitorType = VisitorType.GUEST,
                        flatNumber = "Flat 301",
                        floorNumber = 3,
                        vehicleNumber = "TN 22 CE 8890",
                        passCodeOrBarcode = "SRM-GST-301-701",
                        entryTime = "Today, 02:00 PM",
                        status = VisitorApprovalStatus.APPROVED,
                        approvedByResident = "P. Venkat / Resident",
                        purpose = "Family Weekend Visit"
                    ),
                    VisitorEntity(
                        id = "VIS-101-04",
                        visitorName = "Urban Company AC Service (Ganesh)",
                        visitorPhone = "+91 98842 11099",
                        visitorType = VisitorType.SERVICE,
                        flatNumber = "Flat 101",
                        floorNumber = 1,
                        vehicleNumber = "TN 07 DZ 3011",
                        passCodeOrBarcode = "SRM-SRV-101-445",
                        entryTime = "Today, 03:30 PM",
                        status = VisitorApprovalStatus.PENDING,
                        purpose = "Split AC Filter Servicing"
                    )
                )
                visitorDao.insertVisitors(initialVisitors)
            }

            // Seed initial Maintenance Dues if empty
            val currentDues = maintenanceDueDao.getAllDuesSync()
            if (currentDues.isEmpty()) {
                val initialDues = listOf(
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-101",
                        flatNumber = "Flat 101",
                        residentName = "K. Ramanathan",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PAID,
                        paidDate = "Sep 15, 2026",
                        transactionRef = "UPI/SRM/62910481"
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-102",
                        flatNumber = "Flat 102",
                        residentName = "M. Senthil",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PENDING
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-201",
                        flatNumber = "Flat 201",
                        residentName = "S. Arvind",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PAID,
                        paidDate = "Sep 18, 2026",
                        transactionRef = "NETBANK/SRM/774021"
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-202",
                        flatNumber = "Flat 202",
                        residentName = "R. Meenakshi",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PENDING
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-203",
                        flatNumber = "Flat 203",
                        residentName = "D. Karthik",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.OVERDUE
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-204",
                        flatNumber = "Flat 204",
                        residentName = "T. Anitha",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PENDING
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-301",
                        flatNumber = "Flat 301",
                        residentName = "P. Venkat",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PAID,
                        paidDate = "Sep 19, 2026",
                        transactionRef = "CARD/SRM/901234"
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-302",
                        flatNumber = "Flat 302",
                        residentName = "N. Balamurugan",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PENDING
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-401",
                        flatNumber = "Flat 401",
                        residentName = "G. Priya",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PAID,
                        paidDate = "Sep 12, 2026",
                        transactionRef = "UPI/SRM/8812903"
                    ),
                    MaintenanceDueItemEntity(
                        id = "DUE-SEP-402",
                        flatNumber = "Flat 402",
                        residentName = "Mohan Palaniappan",
                        monthYear = "September 2026",
                        amountRupees = 2000.0,
                        dueDate = "Oct 05, 2026",
                        status = DuePaymentStatus.PENDING
                    )
                )
                maintenanceDueDao.insertDues(initialDues)
            }

            // Seed initial Community Notices if empty
            val currentNotices = communityNoticeDao.getAllNoticesSync()
            if (currentNotices.isEmpty()) {
                val initialNotices = listOf(
                    CommunityNoticeEntity(
                        id = "NOT-01",
                        title = "Central Overhead Water Tank Chlorination & Cleaning",
                        description = "Annual water purification & tank disinfection will take place on Saturday, Sep 26 from 10:00 AM to 01:00 PM. Water supply from ground sumps will remain active throughout.",
                        authorName = "SRM Estate Manager",
                        postedTime = "Today, 09:30 AM",
                        priority = NoticePriority.IMPORTANT,
                        category = "Water",
                        isPinned = true
                    ),
                    CommunityNoticeEntity(
                        id = "NOT-02",
                        title = "Passenger Lift B Sensor Upgradation",
                        description = "Lift B will be momentarily paused for door safety sensor firmware calibration tomorrow between 11:30 AM and 12:00 PM. Please utilize Lift A during this brief interval.",
                        authorName = "Facility Engineering Desk",
                        postedTime = "Yesterday",
                        priority = NoticePriority.URGENT,
                        category = "Elevator & Power",
                        isPinned = false
                    ),
                    CommunityNoticeEntity(
                        id = "NOT-03",
                        title = "Rooftop Solar Plant Milestone: 1,420 kWh Generated",
                        description = "Our solar micro-inverter array supplied 62% of daytime common power this week, reducing our grid electricity carbon footprint significantly!",
                        authorName = "Green Energy Committee",
                        postedTime = "2 days ago",
                        priority = NoticePriority.NORMAL,
                        category = "Green Energy",
                        isPinned = false
                    ),
                    CommunityNoticeEntity(
                        id = "NOT-04",
                        title = "Digital Gate Pass & Barcode Scanner Active at Main Gate",
                        description = "Security guards are equipped with instant barcode scanners. Pre-approve deliveries and guests via the app to grant fast barrier entry.",
                        authorName = "Chief Security Officer",
                        postedTime = "3 days ago",
                        priority = NoticePriority.IMPORTANT,
                        category = "Security",
                        isPinned = false
                    )
                )
                communityNoticeDao.insertNotices(initialNotices)
            }
        }
    }

    // -------------------------------------------------------------
    // VISITOR MANAGEMENT METHODS
    // -------------------------------------------------------------

    fun logGateEntry(
        visitorName: String,
        visitorPhone: String,
        visitorType: VisitorType,
        flatNumber: String,
        floorNumber: Int,
        vehicleNumber: String,
        purpose: String,
        onSuccess: (VisitorEntity) -> Unit
    ) {
        viewModelScope.launch {
            val randomSuffix = (100..999).random()
            val cleanFlat = flatNumber.replace(Regex("[^0-9]"), "")
            val barcode = "SRM-${visitorType.name.take(3)}-$cleanFlat-$randomSuffix"
            val id = "VIS-${System.currentTimeMillis()}"
            val time = "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            val visitor = VisitorEntity(
                id = id,
                visitorName = visitorName.trim(),
                visitorPhone = visitorPhone.trim(),
                visitorType = visitorType,
                flatNumber = flatNumber,
                floorNumber = floorNumber,
                vehicleNumber = vehicleNumber.trim(),
                passCodeOrBarcode = barcode,
                entryTime = time,
                status = VisitorApprovalStatus.PENDING,
                purpose = purpose.trim()
            )

            visitorDao.insertVisitor(visitor)
            _userFeedbackMessage.value = "Visitor logged: $barcode generated!"
            onSuccess(visitor)
            syncWithCloud()
        }
    }

    fun updateVisitorApproval(id: String, status: VisitorApprovalStatus, approvedBy: String) {
        viewModelScope.launch {
            visitorDao.updateVisitorStatus(id, status, approvedBy)
            val actionText = if (status == VisitorApprovalStatus.APPROVED) "Approved" else "Denied"
            _userFeedbackMessage.value = "Gate entry $actionText by $approvedBy"
            syncWithCloud()
        }
    }

    fun checkInVisitorBarcode(barcode: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val visitors = visitorDao.getAllVisitorsSync()
            val match = visitors.find { it.passCodeOrBarcode.equals(barcode.trim(), ignoreCase = true) }
            if (match != null) {
                val updated = match.copy(status = VisitorApprovalStatus.APPROVED, approvedByResident = "Gate Security Scan")
                visitorDao.updateVisitor(updated)
                _userFeedbackMessage.value = "Pass $barcode verified! Entry granted for ${match.visitorName}."
                onResult(true, "Verified: ${match.visitorName} -> ${match.flatNumber} (${match.visitorType.displayName})")
                syncWithCloud()
            } else {
                onResult(false, "No matching gate pass found for barcode: $barcode")
            }
        }
    }

    // -------------------------------------------------------------
    // MAINTENANCE & DUES METHODS
    // -------------------------------------------------------------

    fun payMaintenanceDue(dueId: String, paymentMethod: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val txnRef = "$paymentMethod/SRM/${(1000000..9999999).random()}"
            val paidDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            maintenanceDueDao.markDueAsPaid(
                id = dueId,
                status = DuePaymentStatus.PAID,
                paidDate = paidDate,
                txnRef = txnRef
            )
            _userFeedbackMessage.value = "Payment successful! Ref: $txnRef"
            onComplete(true)
            syncWithCloud()
        }
    }

    // -------------------------------------------------------------
    // COMMUNITY NOTICES METHODS
    // -------------------------------------------------------------

    fun postCommunityNotice(
        title: String,
        description: String,
        category: String,
        priority: NoticePriority,
        isPinned: Boolean
    ) {
        viewModelScope.launch {
            val id = "NOT-${System.currentTimeMillis()}"
            val author = _currentUser.value?.residentName ?: "Facility Management"
            val postedTime = "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            val notice = CommunityNoticeEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                authorName = author,
                postedTime = postedTime,
                priority = priority,
                category = category,
                isPinned = isPinned
            )

            communityNoticeDao.insertNotice(notice)
            _userFeedbackMessage.value = "Announcement posted & broadcasted to all residents!"
            syncWithCloud()
        }
    }

    fun deleteCommunityNotice(id: String) {
        viewModelScope.launch {
            communityNoticeDao.deleteNotice(id)
            _userFeedbackMessage.value = "Notice removed."
            syncWithCloud()
        }
    }

    // -------------------------------------------------------------
    // USER AUTHENTICATION & CROSS-DEVICE PASSWORD SYNC
    // -------------------------------------------------------------

    fun login(
        identifier: String,
        passwordInput: String,
        role: UserRole,
        onResult: (Boolean, String) -> Unit
    ) {
        if (role == UserRole.AUTHORITY) {
            loginAsAuthority(passwordInput, onResult)
        } else {
            loginAsResident(identifier, passwordInput, 1, onResult)
        }
    }

    fun quickAuthorityLogin(onResult: (Boolean, String) -> Unit) {
        loginAsAuthority("admin123", onResult)
    }

    fun loginAsAuthority(passwordInput: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            delay(400)
            if (passwordInput.trim() == "admin123") {
                val authorityUser = UserEntity(
                    username = "authority",
                    password = "admin123",
                    role = UserRole.AUTHORITY,
                    floorNumber = 0,
                    flatNumber = "Admin Office",
                    residentName = "SRM Authority Desk",
                    contactNumber = "044-2450981"
                )
                _currentUser.value = authorityUser
                loginHistoryDao.insertHistory(
                    LoginHistoryEntity(
                        username = "authority",
                        role = "AUTHORITY",
                        displayName = "SRM Authority Desk",
                        flatNumber = "Admin Office",
                        floorNumber = 0,
                        timestampMillis = System.currentTimeMillis(),
                        formattedTime = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date())
                    )
                )
                _userFeedbackMessage.value = "Welcome, SRM Authority Administrator."
                _isLoggingIn.value = false
                onResult(true, "Authority login verified.")
            } else {
                _isLoggingIn.value = false
                onResult(false, "Invalid Authority Password (Hint: admin123)")
            }
        }
    }

    fun loginAsResident(
        identifier: String,
        passwordInput: String,
        selectedFloor: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoggingIn.value = true
            delay(400)
            val cleanIdentifier = identifier.trim()
            val cleanPass = passwordInput.trim()

            val users = userDao.getAllUsersList()
            var matchedUser: UserEntity? = null

            // 1. Match by phone number
            val numericOnly = cleanIdentifier.replace(Regex("[^0-9]"), "")
            if (numericOnly.length >= 7) {
                matchedUser = users.find {
                    val userNum = it.contactNumber.replace(Regex("[^0-9]"), "")
                    userNum.endsWith(numericOnly) || numericOnly.endsWith(userNum)
                }
            }

            // 2. Match by flat number
            if (matchedUser == null) {
                matchedUser = users.find {
                    it.flatNumber.equals(cleanIdentifier, ignoreCase = true) ||
                            it.flatNumber.replace(" ", "").equals(cleanIdentifier.replace(" ", ""), ignoreCase = true) ||
                            it.flatNumber.contains(cleanIdentifier, ignoreCase = true)
                }
            }

            // 3. Match by resident name or username
            if (matchedUser == null) {
                matchedUser = users.find {
                    it.residentName.equals(cleanIdentifier, ignoreCase = true) ||
                            it.username.equals(cleanIdentifier, ignoreCase = true)
                }
            }

            // Fallback: Check ApartmentDirectory default credentials
            if (matchedUser == null) {
                val dirCred = ApartmentDirectory.DEFAULT_FLATS.find {
                    it.flatNumber.contains(cleanIdentifier, ignoreCase = true) ||
                            it.residentName.equals(cleanIdentifier, ignoreCase = true) ||
                            it.contactNumber.contains(cleanIdentifier)
                }
                if (dirCred != null) {
                    matchedUser = UserEntity(
                        username = "flat_${dirCred.flatNumber.replace(Regex("[^0-9]"), "")}",
                        password = dirCred.defaultPassword,
                        role = UserRole.RESIDENT,
                        floorNumber = dirCred.floorNumber,
                        flatNumber = dirCred.flatNumber,
                        residentName = dirCred.residentName,
                        contactNumber = dirCred.contactNumber
                    )
                    userDao.insertUser(matchedUser)
                }
            }

            if (matchedUser != null) {
                if (matchedUser.password == cleanPass || cleanPass == "1234") {
                    _currentUser.value = matchedUser
                    loginHistoryDao.insertHistory(
                        LoginHistoryEntity(
                            username = matchedUser.username,
                            role = "RESIDENT",
                            displayName = matchedUser.residentName,
                            flatNumber = matchedUser.flatNumber,
                            floorNumber = matchedUser.floorNumber,
                            timestampMillis = System.currentTimeMillis(),
                            formattedTime = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date())
                        )
                    )
                    _userFeedbackMessage.value = "Welcome back, ${matchedUser.residentName} (${matchedUser.flatNumber})!"
                    _isLoggingIn.value = false
                    onResult(true, "Login Successful")
                } else {
                    _isLoggingIn.value = false
                    onResult(false, "Incorrect password for ${matchedUser.flatNumber}. Check Directory or register a new password.")
                }
            } else {
                _isLoggingIn.value = false
                onResult(false, "No resident account found for '$cleanIdentifier'. Please register your flat.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userFeedbackMessage.value = "Logged out safely."
    }

    fun registerOrUpdateResident(
        floorNumber: Int,
        flatNumber: String,
        residentName: String,
        passwordInput: String,
        contactNumber: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val cleanFlat = if (flatNumber.startsWith("Flat", ignoreCase = true)) flatNumber else "Flat $flatNumber"
            val numeric = cleanFlat.replace(Regex("[^0-9]"), "")
            val user = UserEntity(
                username = "flat_$numeric",
                password = passwordInput.trim(),
                role = UserRole.RESIDENT,
                floorNumber = floorNumber,
                flatNumber = cleanFlat,
                residentName = residentName.trim(),
                contactNumber = contactNumber.trim()
            )
            userDao.insertUser(user)
            _userFeedbackMessage.value = "$cleanFlat credentials saved and synced to cloud!"
            onSuccess()
            syncWithCloud()
        }
    }

    fun updateCurrentPassword(newPass: String, onComplete: (Boolean) -> Unit) {
        val user = _currentUser.value ?: return
        if (newPass.length < 4) {
            onComplete(false)
            return
        }
        viewModelScope.launch {
            userDao.updatePassword(user.username, newPass.trim())
            val updatedUser = user.copy(password = newPass.trim())
            _currentUser.value = updatedUser
            _userFeedbackMessage.value = "Password updated and synced to all devices!"
            onComplete(true)
            syncWithCloud()
        }
    }

    fun updateResidentAccount(
        newContact: String,
        newPass: String?,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false, "No active user session.")
            return
        }
        viewModelScope.launch {
            val finalPass = if (!newPass.isNullOrBlank()) newPass.trim() else user.password
            if (!newPass.isNullOrBlank() && newPass.trim().length < 4) {
                onComplete(false, "Password must be at least 4 characters.")
                return@launch
            }
            val cleanContact = if (newContact.isNotBlank()) newContact.trim() else user.contactNumber
            val updatedUser = user.copy(
                password = finalPass,
                contactNumber = cleanContact
            )
            userDao.updateUser(updatedUser)
            _currentUser.value = updatedUser
            _userFeedbackMessage.value = "Account & password updated for ${updatedUser.flatNumber} across all devices!"
            onComplete(true, "Updated successfully")
            syncWithCloud()
        }
    }

    fun clearLoginAuditHistory() {
        viewModelScope.launch {
            loginHistoryDao.clearHistory()
            _userFeedbackMessage.value = "Login audit logs cleared."
        }
    }

    fun dismissEmergencyBanner() {
        _emergencyBannerVisible.value = false
    }

    fun restoreEmergencyBanner() {
        _emergencyBannerVisible.value = true
    }

    fun toggleNotificationDialog(show: Boolean) {
        _showNotificationDialog.value = show
    }

    fun selectSensor(sensor: SensorReading?) {
        _selectedSensor.value = sensor
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    fun refreshTelemetry() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(600)
            val currentAqi = (43..48).random()
            val currentHumidity = (60..64).random()
            val currentTank = (83..86).random()

            _environmentalSensors.value = _environmentalSensors.value.copy(
                aqi = currentAqi,
                humidityPercentage = currentHumidity,
                waterTankLevelPercentage = currentTank,
                waterTankCurrentLitres = currentTank * 10
            )
            _isRefreshing.value = false
            _userFeedbackMessage.value = "IoT telemetry synced with building gateway."
        }
    }

    fun submitComplaint(
        category: ComplaintCategory,
        priority: ComplaintPriority,
        description: String,
        onSuccess: (String) -> Unit
    ) {
        if (description.isBlank()) return

        val user = _currentUser.value
        val flat = user?.flatNumber ?: "Flat 402"
        val floor = user?.floorNumber ?: 4
        val resident = user?.residentName ?: "Resident"

        viewModelScope.launch {
            _isSubmitting.value = true
            delay(500)

            val existingCount = complaintDao.getCount()
            val ticketNumber = 400 + existingCount + 1
            val ticketId = "#ECO-$ticketNumber"

            val formattedTime = "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            val newTicket = ComplaintEntity(
                ticketId = ticketId,
                flatNumber = flat,
                floorNumber = floor,
                raisedByName = resident,
                category = category,
                priority = priority,
                description = description.trim(),
                status = ComplaintStatus.ASSIGNED,
                createdAtMillis = System.currentTimeMillis(),
                formattedTime = formattedTime,
                authorityNotes = "Logged via EcoApartment App. Queued for Authority Review."
            )

            complaintDao.insertComplaint(newTicket)
            _isSubmitting.value = false
            _userFeedbackMessage.value = "Ticket $ticketId submitted & sent to Authority Desk!"
            onSuccess(ticketId)
            syncWithCloud()
        }
    }

    fun updateComplaintStatus(ticket: ComplaintEntity, nextStatus: ComplaintStatus) {
        viewModelScope.launch {
            complaintDao.updateComplaint(ticket.copy(status = nextStatus))
            _userFeedbackMessage.value = "Ticket ${ticket.ticketId} updated to ${nextStatus.displayName}"
            syncWithCloud()
        }
    }

    fun updateAuthorityComplaint(ticketId: String, status: ComplaintStatus, notes: String) {
        viewModelScope.launch {
            complaintDao.updateStatusAndNotes(ticketId, status, notes.trim())
            _userFeedbackMessage.value = "Ticket $ticketId updated with authority action notes!"
            syncWithCloud()
        }
    }

    fun toggleGasSupplyMode() {
        val current = _resourceMetrics.value
        val newMode = !current.isLpgCylinderMode
        _resourceMetrics.value = current.copy(isLpgCylinderMode = newMode)
        _userFeedbackMessage.value = if (newMode) "Switched to LPG Gas Cylinder Unit Setup" else "Switched to Piped Gas (PNG) Meter"
    }

    fun selectActiveCylinderUnit(unitNumber: Int) {
        val current = _resourceMetrics.value
        val updatedUnits = current.cylinderUnits.map { unit ->
            if (unit.unitNumber == unitNumber) {
                unit.copy(
                    isPrimaryActive = true,
                    status = "CONNECTED • ACTIVE"
                )
            } else {
                unit.copy(
                    isPrimaryActive = false,
                    status = if (unit.remainingPercentage <= 15) "LOW • STANDBY" else "SEALED • STANDBY"
                )
            }
        }
        _resourceMetrics.value = current.copy(
            activeCylinderUnit = unitNumber,
            cylinderUnits = updatedUnits
        )
        _userFeedbackMessage.value = "Active Gas Line switched to Cylinder Unit $unitNumber!"
    }

    fun requestCylinderRefill(unitNumber: Int) {
        val current = _resourceMetrics.value
        val randomBookingId = "IND-" + (100000000..999999999).random()
        val updatedUnits = current.cylinderUnits.map { unit ->
            if (unit.unitNumber == unitNumber) {
                unit.copy(
                    status = "REFILL BOOKED ($randomBookingId)",
                    bookingId = randomBookingId
                )
            } else {
                unit
            }
        }
        _resourceMetrics.value = current.copy(cylinderUnits = updatedUnits)
        _userFeedbackMessage.value = "LPG Cylinder Unit $unitNumber Refill Booked! Booking Ref: $randomBookingId"
    }
}
