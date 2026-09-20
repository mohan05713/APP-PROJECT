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

data class ResourceMetrics(
    val electricityKWh: Double = 14.2,
    val electricityLimitKWh: Double = 30.0,
    val waterLitres: Int = 320,
    val waterLimitLitres: Int = 450,
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

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isEmergency: Boolean = false
)
