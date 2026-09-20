package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ComplaintEntity
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.DailyConsumption
import com.example.data.model.EnvironmentalSensors
import com.example.data.model.NotificationItem
import com.example.data.model.ResourceMetrics
import com.example.data.model.SensorReading
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
import kotlin.random.Random

class EcoApartmentViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val complaintDao = database.complaintDao()

    val complaints: StateFlow<List<ComplaintEntity>> = complaintDao.getAllComplaints()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    init {
        seedInitialComplaintsIfEmpty()
    }

    private fun seedInitialComplaintsIfEmpty() {
        viewModelScope.launch {
            val count = complaintDao.getCount()
            if (count == 0) {
                complaintDao.insertComplaint(
                    ComplaintEntity(
                        ticketId = "#ECO-401",
                        flatNumber = "Flat B-402",
                        category = ComplaintCategory.GARBAGE_BLOCK,
                        priority = ComplaintPriority.STANDARD,
                        description = "Waste disposal chute hatch jammed on 4th floor corridor near Flat 402.",
                        status = ComplaintStatus.IN_PROGRESS,
                        createdAtMillis = System.currentTimeMillis() - 86400000L,
                        formattedTime = "Yesterday, 04:30 PM"
                    )
                )
                complaintDao.insertComplaint(
                    ComplaintEntity(
                        ticketId = "#ECO-400",
                        flatNumber = "Flat B-402",
                        category = ComplaintCategory.WATER_LEAKAGE,
                        priority = ComplaintPriority.URGENT,
                        description = "Minor pinhole leakage observed at the shared utility shaft riser valve.",
                        status = ComplaintStatus.ASSIGNED,
                        createdAtMillis = System.currentTimeMillis() - (86400000L * 2),
                        formattedTime = "2 days ago"
                    )
                )
            }
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
            delay(700)
            // Subtle simulated live variation for realistic IoT feeling
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
            _userFeedbackMessage.value = "IoT telemetry synced with Flat B-402 gateway."
        }
    }

    fun submitComplaint(
        category: ComplaintCategory,
        priority: ComplaintPriority,
        description: String,
        onSuccess: (String) -> Unit
    ) {
        if (description.isBlank()) return

        viewModelScope.launch {
            _isSubmitting.value = true
            delay(750) // Realistic network roundtrip

            // Generate unique ticket ID starting with #ECO-402 or higher
            val existingCount = complaintDao.getCount()
            val ticketNumber = 402 + (existingCount - 2).coerceAtLeast(0)
            // Ensure unique ID if repeats exist
            val suffix = if (existingCount >= 2) "-${(100..999).random()}" else ""
            val ticketId = "#ECO-$ticketNumber$suffix"

            val timeFormat = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
            val formattedTime = "Today, " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            val newTicket = ComplaintEntity(
                ticketId = ticketId,
                flatNumber = "Flat B-402",
                category = category,
                priority = priority,
                description = description.trim(),
                status = ComplaintStatus.ASSIGNED,
                createdAtMillis = System.currentTimeMillis(),
                formattedTime = formattedTime
            )

            complaintDao.insertComplaint(newTicket)
            _isSubmitting.value = false
            _userFeedbackMessage.value = "Ticket $ticketId logged & assigned to SRM Maintenance!"
            onSuccess(ticketId)
        }
    }

    fun updateComplaintStatus(ticket: ComplaintEntity, nextStatus: ComplaintStatus) {
        viewModelScope.launch {
            complaintDao.updateComplaint(ticket.copy(status = nextStatus))
            _userFeedbackMessage.value = "Ticket ${ticket.ticketId} updated to ${nextStatus.displayName}"
        }
    }
}
