package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GasMeter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ComplaintEntity
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintStatus
import com.example.data.model.UpcomingInspection
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary

enum class MaintenanceTab(val title: String) {
    OVERVIEW("Overview"),
    RECENT_REQUESTS("Requests"),
    INSPECTIONS("Inspections")
}

@Composable
fun MaintenanceStatusSection(
    complaints: List<ComplaintEntity>,
    upcomingInspections: List<UpcomingInspection>,
    onSetInspectionReminder: (UpcomingInspection) -> Unit,
    onNavigateToNewRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MaintenanceTab.OVERVIEW) }
    var selectedInspectionDetail by remember { mutableStateOf<UpcomingInspection?>(null) }

    val activeRequests = complaints.filter { it.status != ComplaintStatus.RESOLVED }
    val resolvedRequests = complaints.filter { it.status == ComplaintStatus.RESOLVED }

    val healthScore = when {
        activeRequests.isEmpty() -> 98
        activeRequests.size <= 2 -> 92
        activeRequests.size <= 4 -> 85
        else -> 76
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .testTag("maintenance_status_section"),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, EnterpriseBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = "Maintenance Icon",
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Facility Operations",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFECFDF5))
                                    .border(0.5.dp, Color(0xFFA7F3D0), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = Color(0xFF065F46),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Services, SLAs & Inspections",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EnterpriseTextMuted,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                // Health Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, EnterpriseBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$healthScore% Health",
                            color = EnterpriseTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        )
                    }
                }
            }

            // Quick Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Metric 1: System Status
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, EnterpriseBorder)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = "System Status",
                            color = EnterpriseTextMuted,
                            fontSize = 9.sp
                        )
                        Text(
                            text = if (activeRequests.isEmpty()) "Optimal" else "${activeRequests.size} In Queue",
                            color = if (activeRequests.isEmpty()) Color(0xFF059669) else Color(0xFFD97706),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Metric 2: Active Requests
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, EnterpriseBorder)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = "Active Services",
                            color = EnterpriseTextMuted,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "${activeRequests.size} Active",
                            color = EnterprisePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Metric 3: Next Audit
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, EnterpriseBorder)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(
                            text = "Next Audit",
                            color = EnterpriseTextMuted,
                            fontSize = 9.sp
                        )
                        Text(
                            text = upcomingInspections.firstOrNull()?.status ?: "Tomorrow",
                            color = EnterpriseTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Navigation Tab Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                MaintenanceTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { selectedTab = tab },
                        color = if (isSelected) EnterprisePrimary else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else EnterpriseTextSecondary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                MaintenanceTab.OVERVIEW -> {
                    MaintenanceOverviewTab(
                        activeCount = activeRequests.size,
                        resolvedCount = resolvedRequests.size,
                        healthScore = healthScore,
                        nextInspection = upcomingInspections.firstOrNull(),
                        onGoToRequests = { selectedTab = MaintenanceTab.RECENT_REQUESTS },
                        onGoToInspections = { selectedTab = MaintenanceTab.INSPECTIONS }
                    )
                }
                MaintenanceTab.RECENT_REQUESTS -> {
                    RecentServiceRequestsTab(
                        complaints = complaints,
                        onNewRequestClick = onNavigateToNewRequest
                    )
                }
                MaintenanceTab.INSPECTIONS -> {
                    UpcomingInspectionsTab(
                        inspections = upcomingInspections,
                        selectedInspection = selectedInspectionDetail,
                        onSelectInspection = { selectedInspectionDetail = if (selectedInspectionDetail?.id == it.id) null else it },
                        onSetReminder = onSetInspectionReminder
                    )
                }
            }
        }
    }
}

@Composable
private fun MaintenanceOverviewTab(
    activeCount: Int,
    resolvedCount: Int,
    healthScore: Int,
    nextInspection: UpcomingInspection?,
    onGoToRequests: () -> Unit,
    onGoToInspections: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFF8FAFC),
            border = BorderStroke(1.dp, EnterpriseBorder)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Infrastructure Health",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                    Text(
                        text = "$healthScore / 100",
                        color = EnterprisePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (healthScore / 100f).coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(EnterprisePrimary)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "• Water Pumps: 100% OK", color = EnterpriseTextSecondary, fontSize = 9.5.sp)
                    Text(text = "• Fire Systems: Certified", color = EnterpriseTextSecondary, fontSize = 9.5.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "• Solar Grid: 98.4% Output", color = EnterpriseTextSecondary, fontSize = 9.5.sp)
                    Text(text = "• Common Areas: All Lit", color = EnterpriseTextSecondary, fontSize = 9.5.sp)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onGoToRequests() },
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Service Requests",
                            color = EnterpriseTextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "$activeCount In-Flight",
                        color = EnterprisePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$resolvedCount tickets resolved",
                        color = EnterpriseTextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onGoToInspections() },
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Audit",
                            color = EnterpriseTextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = nextInspection?.status ?: "Tomorrow",
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = nextInspection?.title?.take(20) ?: "Lift & Safety",
                        color = EnterpriseTextMuted,
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentServiceRequestsTab(
    complaints: List<ComplaintEntity>,
    onNewRequestClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Work Orders",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EnterpriseTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, EnterpriseBorder),
                modifier = Modifier.clickable { onNewRequestClick() }
            ) {
                Text(
                    text = "+ New Request",
                    color = EnterprisePrimary,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        if (complaints.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, EnterpriseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No maintenance tickets filed yet.",
                    color = EnterpriseTextMuted,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        } else {
            complaints.take(3).forEach { item ->
                ServiceRequestCard(ticket = item)
            }
        }
    }
}

@Composable
private fun ServiceRequestCard(ticket: ComplaintEntity) {
    val (statusBg, statusFg) = when (ticket.status) {
        ComplaintStatus.ASSIGNED -> Pair(Color(0xFFF1F5F9), EnterpriseTextSecondary)
        ComplaintStatus.IN_PROGRESS -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
        ComplaintStatus.RESOLVED -> Pair(Color(0xFFECFDF5), Color(0xFF065F46))
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        border = BorderStroke(1.dp, EnterpriseBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ticket.ticketId,
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 10.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${ticket.flatNumber}",
                        color = EnterpriseTextMuted,
                        fontSize = 9.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = statusBg,
                    border = BorderStroke(0.5.dp, statusFg.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = ticket.status.displayName,
                        color = statusFg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Text(
                text = ticket.description,
                color = EnterpriseTextSecondary,
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "By ${ticket.raisedByName}",
                    color = EnterpriseTextMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = ticket.formattedTime,
                    color = EnterpriseTextMuted,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun UpcomingInspectionsTab(
    inspections: List<UpcomingInspection>,
    selectedInspection: UpcomingInspection?,
    onSelectInspection: (UpcomingInspection) -> Unit,
    onSetReminder: (UpcomingInspection) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scheduled Routine Audits",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EnterpriseTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
            Text(
                text = "${inspections.size} Scheduled",
                color = EnterprisePrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        inspections.forEach { inspection ->
            val isExpanded = selectedInspection?.id == inspection.id
            InspectionItemCard(
                inspection = inspection,
                isExpanded = isExpanded,
                onToggleExpand = { onSelectInspection(inspection) },
                onSetReminder = { onSetReminder(inspection) }
            )
        }
    }
}

@Composable
private fun InspectionItemCard(
    inspection: UpcomingInspection,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSetReminder: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        border = BorderStroke(1.dp, EnterpriseBorder),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(0.5.dp, Color(0xFFFDE68A))
                        ) {
                            Text(
                                text = inspection.status,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.5.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = inspection.category,
                            color = EnterpriseTextMuted,
                            fontSize = 9.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = inspection.title,
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 11.5.sp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = EnterpriseTextMuted,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${inspection.scheduledDate} • ${inspection.timeSlot}",
                            color = EnterpriseTextSecondary,
                            fontSize = 9.5.sp
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = EnterpriseTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = EnterpriseBorder)
                Spacer(modifier = Modifier.height(4.dp))

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row {
                        Text(text = "Inspector: ", color = EnterpriseTextMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        Text(text = inspection.inspectorName, color = EnterpriseTextPrimary, fontSize = 9.5.sp)
                    }
                    Row {
                        Text(text = "Scope: ", color = EnterpriseTextMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        Text(text = inspection.scope, color = EnterpriseTextSecondary, fontSize = 9.5.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onSetReminder,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EnterprisePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Set Reminder", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
