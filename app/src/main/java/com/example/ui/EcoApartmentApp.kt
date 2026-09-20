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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ComplaintPortal
import com.example.ui.components.EcoHeader
import com.example.ui.components.EmergencyBanner
import com.example.ui.components.IoTSensorGrid
import com.example.ui.components.NotificationDialog
import com.example.ui.components.ResourceTrackers
import com.example.ui.components.SensorDetailDialog
import com.example.ui.components.WeeklyConsumptionChart
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun EcoApartmentApp(
    viewModel: EcoApartmentViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val emergencyBannerVisible by viewModel.emergencyBannerVisible.collectAsState()
    val environmentalSensors by viewModel.environmentalSensors.collectAsState()
    val resourceMetrics by viewModel.resourceMetrics.collectAsState()
    val weeklyConsumption by viewModel.weeklyConsumption.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val showNotificationDialog by viewModel.showNotificationDialog.collectAsState()
    val selectedSensor by viewModel.selectedSensor.collectAsState()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = Slate900,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EcoHeader(
                notificationCount = notifications.size,
                isRefreshing = isRefreshing,
                onNotificationClick = { viewModel.toggleNotificationDialog(true) },
                onRefreshClick = { viewModel.refreshTelemetry() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Slate900,
                            Color(0xFF0D1B2A),
                            Slate900
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp), // Responsive centering for wide screens/tablets
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Emergency Flood & Rain Warning Banner
                item(key = "emergency_banner") {
                    EmergencyBanner(
                        isVisible = emergencyBannerVisible,
                        onDismiss = { viewModel.dismissEmergencyBanner() },
                        onRestore = { viewModel.restoreEmergencyBanner() }
                    )
                }

                // 2. Live IoT Environmental Sensors (2x2 Grid)
                item(key = "iot_sensors") {
                    IoTSensorGrid(
                        sensors = environmentalSensors,
                        onSensorCardClick = { sensor ->
                            viewModel.selectSensor(sensor)
                        }
                    )
                }

                // 3. Daily Resource Consumption Trackers
                item(key = "resource_trackers") {
                    ResourceTrackers(
                        metrics = resourceMetrics
                    )
                }

                // 4. Interactive Weekly Consumption Comparison Chart (Mon-Sun)
                item(key = "weekly_chart") {
                    WeeklyConsumptionChart(
                        weeklyData = weeklyConsumption
                    )
                }

                // 5. Resident Complaint & Maintenance Portal
                item(key = "complaint_portal") {
                    ComplaintPortal(
                        complaints = complaints,
                        isSubmitting = isSubmitting,
                        onSubmitComplaint = { category, priority, description, onSuccess ->
                            viewModel.submitComplaint(category, priority, description, onSuccess)
                        },
                        onStatusChange = { ticket, newStatus ->
                            viewModel.updateComplaintStatus(ticket, newStatus)
                        }
                    )
                }

                // Footer
                item(key = "footer") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "EcoApartment OS • Smart Green Facility Mesh",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connected Gateway: SRM-NODE-B402-MESH • Firmware v2.4.1",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400.copy(alpha = 0.6f),
                                fontSize = 10.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Modal Dialogs
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
}
