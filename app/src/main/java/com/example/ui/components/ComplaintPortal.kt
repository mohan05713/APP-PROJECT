package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Rose500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintPortal(
    complaints: List<ComplaintEntity>,
    isSubmitting: Boolean,
    onSubmitComplaint: (ComplaintCategory, ComplaintPriority, String, (String) -> Unit) -> Unit,
    onStatusChange: (ComplaintEntity, ComplaintStatus) -> Unit,
    cloudSyncStatus: String = "🟢 SRM Cloud: Online",
    isCloudSyncing: Boolean = false,
    onManualSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ComplaintCategory.WATER_LEAKAGE) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var selectedPriority by remember { mutableStateOf(ComplaintPriority.STANDARD) }
    var descriptionText by remember { mutableStateOf("Low water pressure in kitchen pipeline during peak hours.") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var lastCreatedTicketId by remember { mutableStateOf<String?>(null) }

    var selectedTicketForDetail by remember { mutableStateOf<ComplaintEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .animateContentSize()
    ) {
        // Section Title & Cloud Sync Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(EnterprisePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Facility & Maintenance Desk",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 13.5.sp
                    )
                )
            }

            TextButton(
                onClick = onManualSync,
                enabled = !isCloudSyncing,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                modifier = Modifier.height(26.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync",
                    tint = EnterprisePrimary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (isCloudSyncing) "Syncing..." else "Sync Cloud",
                    color = EnterprisePrimary,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Complaint Log Form Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .testTag("complaint_form_card"),
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = "Maintenance",
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Log Maintenance Ticket",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "Facility Operations Desk",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EnterpriseTextMuted,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 1. Category Dropdown
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextSecondary,
                        fontSize = 9.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = getCategoryIcon(selectedCategory),
                                contentDescription = null,
                                tint = EnterprisePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = EnterpriseTextPrimary,
                            unfocusedTextColor = EnterpriseTextPrimary,
                            focusedBorderColor = EnterprisePrimary,
                            unfocusedBorderColor = EnterpriseBorder,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("category_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        ComplaintCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = getCategoryIcon(category),
                                            contentDescription = null,
                                            tint = EnterprisePrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = category.displayName,
                                            color = EnterpriseTextPrimary,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryDropdownExpanded = false
                                },
                                modifier = Modifier.testTag("dropdown_item_${category.name}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Priority Selector
                Text(
                    text = "PRIORITY LEVEL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextSecondary,
                        fontSize = 9.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ComplaintPriority.entries.forEach { priority ->
                        val isSelected = priority == selectedPriority
                        val accentColor = when (priority) {
                            ComplaintPriority.STANDARD -> EnterprisePrimary
                            ComplaintPriority.URGENT -> Color(0xFFD97706)
                            ComplaintPriority.CRITICAL -> Rose500
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedPriority = priority }
                                .testTag("priority_chip_${priority.name}"),
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.1f) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) accentColor else EnterpriseBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = priority.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accentColor else EnterpriseTextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = priority.slaTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EnterpriseTextMuted,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Problem Description
                Text(
                    text = "PROBLEM DESCRIPTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextSecondary,
                        fontSize = 9.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = {
                        descriptionText = it
                        if (errorText != null) errorText = null
                    },
                    placeholder = {
                        Text(
                            text = "Detail the exact issue location and symptoms...",
                            color = EnterpriseTextMuted,
                            fontSize = 11.sp
                        )
                    },
                    minLines = 2,
                    maxLines = 4,
                    isError = errorText != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        errorBorderColor = Rose500,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("description_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 4. Submit Button
                Button(
                    onClick = {
                        if (descriptionText.isBlank()) {
                            errorText = "Please provide a problem description."
                            return@Button
                        }
                        onSubmitComplaint(
                            selectedCategory,
                            selectedPriority,
                            descriptionText
                        ) { newId ->
                            lastCreatedTicketId = newId
                            descriptionText = ""
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EnterprisePrimary,
                        contentColor = Color.White,
                        disabledContainerColor = EnterprisePrimary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("submit_complaint_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Submitting...", fontSize = 11.5.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Submit Complaint Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Success alert banner
                AnimatedVisibility(
                    visible = lastCreatedTicketId != null,
                    enter = fadeIn(animationSpec = tween(200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ticket $lastCreatedTicketId created successfully!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recent Complaints & Status List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Complaints & Status",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EnterpriseTextPrimary,
                    fontSize = 12.5.sp
                )
            )

            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Text(
                    text = "${complaints.size} Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EnterpriseTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Complaint Ticket Cards List
        if (complaints.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Text(
                    text = "No complaint tickets logged yet.",
                    style = MaterialTheme.typography.bodySmall.copy(color = EnterpriseTextMuted, fontSize = 11.sp),
                    modifier = Modifier.padding(10.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                complaints.forEach { ticket ->
                    TicketItemCard(
                        ticket = ticket,
                        onClick = { selectedTicketForDetail = ticket },
                        onStatusToggle = {
                            val nextStatus = when (ticket.status) {
                                ComplaintStatus.ASSIGNED -> ComplaintStatus.IN_PROGRESS
                                ComplaintStatus.IN_PROGRESS -> ComplaintStatus.RESOLVED
                                ComplaintStatus.RESOLVED -> ComplaintStatus.ASSIGNED
                            }
                            onStatusChange(ticket, nextStatus)
                        }
                    )
                }
            }
        }

        selectedTicketForDetail?.let { ticket ->
            ComplaintTicketDetailModal(
                ticket = ticket,
                onDismiss = { selectedTicketForDetail = null },
                onStatusChange = onStatusChange
            )
        }
    }
}

@Composable
private fun TicketItemCard(
    ticket: ComplaintEntity,
    onClick: () -> Unit,
    onStatusToggle: () -> Unit
) {
    val priorityColor = when (ticket.priority) {
        ComplaintPriority.STANDARD -> EnterprisePrimary
        ComplaintPriority.URGENT -> Color(0xFFD97706)
        ComplaintPriority.CRITICAL -> Rose500
    }

    val statusBgColor = when (ticket.status) {
        ComplaintStatus.ASSIGNED -> Color(0xFFF1F5F9)
        ComplaintStatus.IN_PROGRESS -> Color(0xFFFEF3C7)
        ComplaintStatus.RESOLVED -> Color(0xFFECFDF5)
    }

    val statusTextColor = when (ticket.status) {
        ComplaintStatus.ASSIGNED -> EnterpriseTextSecondary
        ComplaintStatus.IN_PROGRESS -> Color(0xFF92400E)
        ComplaintStatus.RESOLVED -> Color(0xFF065F46)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .testTag("ticket_card_${ticket.ticketId}"),
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Header Row: Ticket ID + Category + Priority Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
                    ) {
                        Text(
                            text = ticket.ticketId,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = ticket.category.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Surface(
                    color = priorityColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = ticket.priority.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EnterpriseTextSecondary,
                    lineHeight = 15.sp,
                    fontSize = 11.sp
                )
            )

            if (ticket.authorityNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = Color(0xFF92400E),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Action Update:",
                                color = Color(0xFF92400E),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ticket.authorityNotes,
                                color = EnterpriseTextPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Footer: Status Pill + Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusBgColor,
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusTextColor.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clickable { onStatusToggle() }
                        .testTag("status_toggle_${ticket.ticketId}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(statusTextColor)
                        )
                        Text(
                            text = ticket.status.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = EnterpriseTextMuted,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = ticket.formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseTextMuted,
                            fontSize = 9.5.sp
                        )
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(category: ComplaintCategory): ImageVector {
    return when (category) {
        ComplaintCategory.WATER_LEAKAGE -> Icons.Default.WaterDamage
        ComplaintCategory.ELECTRICITY_FAULT -> Icons.Default.ElectricBolt
        ComplaintCategory.GAS_CHECK -> Icons.Default.LocalGasStation
        ComplaintCategory.GARBAGE_BLOCK -> Icons.Default.DeleteOutline
        ComplaintCategory.BASEMENT_DRAINAGE -> Icons.Default.Build
    }
}

@Composable
fun ComplaintTicketDetailModal(
    ticket: ComplaintEntity,
    onDismiss: () -> Unit,
    onStatusChange: (ComplaintEntity, ComplaintStatus) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val nextStatus = when (ticket.status) {
                        ComplaintStatus.ASSIGNED -> ComplaintStatus.IN_PROGRESS
                        ComplaintStatus.IN_PROGRESS -> ComplaintStatus.RESOLVED
                        ComplaintStatus.RESOLVED -> ComplaintStatus.ASSIGNED
                    }
                    onStatusChange(ticket, nextStatus)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Update Status to Next Phase", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Close", color = EnterpriseTextSecondary)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(getCategoryIcon(ticket.category), contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ticket ${ticket.ticketId}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EnterpriseTextPrimary)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EnterpriseTextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFF1F5F9)) {
                        Text("Category: ${ticket.category.displayName}", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = EnterpriseTextPrimary, modifier = Modifier.padding(6.dp))
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFEF3C7)) {
                        Text("Priority: ${ticket.priority.displayName}", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(6.dp))
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Issue Description:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EnterpriseTextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(ticket.description, fontSize = 12.5.sp, color = EnterpriseTextPrimary)
                    }
                }

                Column {
                    Text("Ticket Information:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EnterpriseTextMuted)
                    Text("• Raised By: ${ticket.raisedByName} (Flat ${ticket.flatNumber}, Floor ${ticket.floorNumber})", fontSize = 11.sp, color = EnterpriseTextSecondary)
                    Text("• Raised Time: ${ticket.formattedTime}", fontSize = 11.sp, color = EnterpriseTextSecondary)
                    Text("• Current Status: ${ticket.status.displayName}", fontSize = 11.sp, color = EnterprisePrimary, fontWeight = FontWeight.Bold)
                    Text("• SLA Deadline: Within ${ticket.priority.slaTime}", fontSize = 11.sp, color = EnterpriseTextSecondary)
                    if (ticket.authorityNotes.isNotBlank()) {
                        Text("• Authority Action Note: ${ticket.authorityNotes}", fontSize = 11.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}

