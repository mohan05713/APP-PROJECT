package com.example.data.model

data class SensorReading(
    val id: String,
    val title: String,
    val value: String,
    val subValue: String,
    val status: String,
    val isOptimal: Boolean,
    val unit: String = "",
    val details: String = ""
)

data class EnvironmentalSensors(
    val aqi: Int = 46,
    val aqiStatus: String = "Good",
    val pm25: Double = 12.0,
    val temperatureCelsius: Double = 24.5,
    val humidityPercentage: Int = 62,
    val humidityStatus: String = "Optimal",
    val dewPointCelsius: Double = 16.8,
    val waterTankLevelPercentage: Int = 84,
    val waterTankInflowActive: Boolean = true,
    val waterTankCapacityLitres: Int = 1000,
    val waterTankCurrentLitres: Int = 840,
    val gasLeakagePercentage: Double = 0.00,
    val gasStatus: String = "Safe, No Leakage",
    val gasPressureMbar: Double = 21.0,
    val gasValveStatus: String = "Open • Normal"
)

data class GasCylinderUnit(
    val unitNumber: Int, // 1, 2, 3
    val unitName: String, // "Unit 1", "Unit 2", "Unit 3"
    val weightKg: Double = 14.2,
    val remainingPercentage: Int = 68,
    val status: String = "ACTIVE • CONNECTED",
    val isPrimaryActive: Boolean = false,
    val bookingId: String = ""
)

data class ResourceMetrics(
    val electricityKWh: Double = 14.2,
    val electricityLimitKWh: Double = 30.0,
    val waterLitres: Int = 320,
    val waterLimitLitres: Int = 450,
    val isLpgCylinderMode: Boolean = true, // Default to LPG Cylinder mode for Tamil Nadu / Chennai / Karnataka
    val activeCylinderUnit: Int = 1, // 1, 2, or 3
    val cylinderUnits: List<GasCylinderUnit> = listOf(
        GasCylinderUnit(1, "Unit 1", weightKg = 14.2, remainingPercentage = 68, status = "ACTIVE • CONNECTED", isPrimaryActive = true, bookingId = ""),
        GasCylinderUnit(2, "Unit 2", weightKg = 14.2, remainingPercentage = 100, status = "SEALED • STANDBY", isPrimaryActive = false, bookingId = ""),
        GasCylinderUnit(3, "Unit 3", weightKg = 14.2, remainingPercentage = 15, status = "LOW • REFILL READY", isPrimaryActive = false, bookingId = "IND-770894065")
    ),
    val gasScm: Double = 0.85,
    val gasPressureMbar: Double = 21.0
)

data class DailyConsumption(
    val dayOfWeek: String, // "Mon", "Tue", etc.
    val electricityKWh: Double,
    val waterLitres: Int
)

enum class ComplaintCategory(val displayName: String) {
    WATER_LEAKAGE("Water Pipeline Leakage"),
    ELECTRICITY_FAULT("Electricity Fault / MCB Trip"),
    GAS_CHECK("Gas Supply Check"),
    GARBAGE_BLOCK("Garbage/Waste Disposal Block"),
    BASEMENT_DRAINAGE("Basement Drainage")
}

enum class ComplaintPriority(val displayName: String, val slaTime: String) {
    STANDARD("Standard", "24h SLA"),
    URGENT("Urgent", "4h SLA"),
    CRITICAL("Critical Emergency", "Immediate")
}

enum class ComplaintStatus(val displayName: String) {
    ASSIGNED("Assigned to Maintenance"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}

enum class MaintenanceOverallStatus(
    val title: String,
    val subtitle: String,
    val badgeColorHex: Long
) {
    OPTIMAL("All Systems Operational", "Zero critical failures • Regular schedules on track", 0xFF10B981),
    ATTENTION("Scheduled Tasks Pending", "2 active service requests • Routine maintenance underway", 0xFFF59E0B),
    INSPECTION_DAY("Active Inspection Underway", "Annual fire & water safety audit taking place today", 0xFF3B82F6)
}

data class UpcomingInspection(
    val id: String,
    val title: String,
    val category: String,
    val scheduledDate: String,
    val timeSlot: String,
    val inspectorName: String,
    val status: String, // "Upcoming", "Tomorrow", "In 3 Days"
    val scope: String,
    val notes: String = ""
)

data class MaintenanceSummary(
    val overallStatus: MaintenanceOverallStatus = MaintenanceOverallStatus.OPTIMAL,
    val activeServiceRequestsCount: Int = 3,
    val resolvedThisMonthCount: Int = 18,
    val nextInspectionDays: Int = 2,
    val healthScorePercentage: Int = 94
)

enum class UserRole(val displayName: String) {
    AUTHORITY("Facility Authority"),
    RESIDENT("Apartment Resident")
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isEmergency: Boolean = false
)

enum class VisitorType(val displayName: String) {
    GUEST("Guest / Friend / Family"),
    DELIVERY("Delivery / Courier"),
    CAB("Cab / Taxi"),
    SERVICE("Service / Maintenance Tech")
}

enum class VisitorApprovalStatus(val displayName: String) {
    PENDING("Awaiting Approval"),
    APPROVED("Approved / Entry Granted"),
    DENIED("Denied / Entry Barred"),
    CHECKED_OUT("Checked Out")
}

data class VisitorLog(
    val id: String,
    val visitorName: String,
    val visitorPhone: String,
    val visitorType: VisitorType,
    val flatNumber: String,
    val floorNumber: Int,
    val vehicleNumber: String = "",
    val passCodeOrBarcode: String,
    val entryTime: String,
    val status: VisitorApprovalStatus,
    val approvedByResident: String = "",
    val purpose: String = "Delivery / Visit"
)

enum class DuePaymentStatus(val displayName: String) {
    PAID("Paid"),
    PENDING("Pending / Unpaid"),
    OVERDUE("Overdue")
}

data class MaintenanceDueItem(
    val id: String,
    val flatNumber: String,
    val residentName: String,
    val monthYear: String,
    val amountRupees: Double,
    val dueDate: String,
    val status: DuePaymentStatus,
    val paidDate: String = "",
    val transactionRef: String = "",
    val breakdown: String = "Water ₹450, Elevator & Power ₹850, Security ₹700"
)

enum class NoticePriority(val displayName: String) {
    NORMAL("Community Notice"),
    IMPORTANT("Important Advisory"),
    URGENT("Urgent Alert")
}

data class CommunityNotice(
    val id: String,
    val title: String,
    val description: String,
    val authorName: String,
    val postedTime: String,
    val priority: NoticePriority,
    val category: String, // "Water", "Power", "Event", "Security", "General"
    val isPinned: Boolean = false
)
