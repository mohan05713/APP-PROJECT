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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.ui.theme.Amber400
import com.example.ui.theme.Amber500
import com.example.ui.theme.Cyan500
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald900
import com.example.ui.theme.Rose500
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintPortal(
    complaints: List<ComplaintEntity>,
    isSubmitting: Boolean,
    onSubmitComplaint: (ComplaintCategory, ComplaintPriority, String, (String) -> Unit) -> Unit,
    onStatusChange: (ComplaintEntity, ComplaintStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ComplaintCategory.WATER_LEAKAGE) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var selectedPriority by remember { mutableStateOf(ComplaintPriority.STANDARD) }
    var descriptionText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var lastCreatedTicketId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize()
    ) {
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Emerald400)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Resident Complaint & Maintenance Portal",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Complaint Log Form Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .testTag("complaint_form_card"),
            color = Slate800,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Emerald500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = "Maintenance",
                            tint = Emerald400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Log Maintenance Ticket",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "SRM Residency Facility Ops • Flat B-402",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Category Dropdown
                Text(
                    text = "ISSUE CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate300,
                        letterSpacing = 0.5.sp,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

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
                                tint = Emerald400,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = Slate600,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("category_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.background(Slate900)
                    ) {
                        ComplaintCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = getCategoryIcon(category),
                                            contentDescription = null,
                                            tint = Emerald400,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = category.displayName,
                                            color = Color.White,
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

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Priority Selector: Standard (24h), Urgent (4h), Critical Emergency
                Text(
                    text = "PRIORITY LEVEL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate300,
                        letterSpacing = 0.5.sp,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ComplaintPriority.entries.forEach { priority ->
                        val isSelected = priority == selectedPriority
                        val accentColor = when (priority) {
                            ComplaintPriority.STANDARD -> Emerald400
                            ComplaintPriority.URGENT -> Amber400
                            ComplaintPriority.CRITICAL -> Rose500
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedPriority = priority }
                                .testTag("priority_chip_${priority.name}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accentColor.copy(alpha = 0.2f) else Slate900,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) accentColor else Slate700
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = priority.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) accentColor else Slate300,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = priority.slaTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) accentColor.copy(alpha = 0.8f) else Slate500,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Problem Description Text Box
                Text(
                    text = "PROBLEM DESCRIPTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate300,
                        letterSpacing = 0.5.sp,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = {
                        descriptionText = it
                        if (errorText != null) errorText = null
                    },
                    placeholder = {
                        Text(
                            text = "Detail the exact issue location and symptoms (e.g., Master bedroom faucet valve drip)...",
                            color = Slate500,
                            fontSize = 13.sp
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    isError = errorText != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (errorText != null) {
                                Text(
                                    text = errorText ?: "",
                                    color = Rose500,
                                    fontSize = 11.sp
                                )
                            } else {
                                Text(
                                    text = "Be as specific as possible",
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "${descriptionText.length}/300",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = Slate600,
                        errorBorderColor = Rose500,
                        focusedContainerColor = Slate900,
                        unfocusedContainerColor = Slate900
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("description_input")
                )

                // Quick sample suggestions for quick testing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickFillChip(
                        label = "Leak at kitchen tap",
                        onClick = { descriptionText = "Kitchen RO connection line leaking under the sink." }
                    )
                    QuickFillChip(
                        label = "MCB trip in bedroom",
                        onClick = { descriptionText = "Master bedroom 16A breaker tripping repeatedly upon AC startup." }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Submit Button with loading feedback
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
                        containerColor = Emerald500,
                        contentColor = Slate900,
                        disabledContainerColor = Emerald500.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_complaint_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Slate900,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transmitting to Facility Desk...",
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Submit Complaint Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Success alert banner if recently submitted
                AnimatedVisibility(
                    visible = lastCreatedTicketId != null,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    Surface(
                        color = Emerald500.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald400),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ticket $lastCreatedTicketId generated & prepended below!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Emerald300,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Complaints & Status List Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ConfirmationNumber,
                    contentDescription = null,
                    tint = Emerald400,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recent Complaints & Status",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Surface(
                color = Slate800,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
            ) {
                Text(
                    text = "${complaints.size} Active",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate300,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Complaint Ticket Cards List
        if (complaints.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = Slate800,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "No complaint tickets logged yet.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            complaints.forEach { ticket ->
                TicketItemCard(
                    ticket = ticket,
                    onStatusToggle = {
                        val nextStatus = when (ticket.status) {
                            ComplaintStatus.ASSIGNED -> ComplaintStatus.IN_PROGRESS
                            ComplaintStatus.IN_PROGRESS -> ComplaintStatus.RESOLVED
                            ComplaintStatus.RESOLVED -> ComplaintStatus.ASSIGNED
                        }
                        onStatusChange(ticket, nextStatus)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun TicketItemCard(
    ticket: ComplaintEntity,
    onStatusToggle: () -> Unit
) {
    val priorityColor = when (ticket.priority) {
        ComplaintPriority.STANDARD -> Emerald400
        ComplaintPriority.URGENT -> Amber400
        ComplaintPriority.CRITICAL -> Rose500
    }

    val statusBgColor = when (ticket.status) {
        ComplaintStatus.ASSIGNED -> Slate700
        ComplaintStatus.IN_PROGRESS -> Amber500.copy(alpha = 0.2f)
        ComplaintStatus.RESOLVED -> Emerald500.copy(alpha = 0.2f)
    }

    val statusTextColor = when (ticket.status) {
        ComplaintStatus.ASSIGNED -> Slate200
        ComplaintStatus.IN_PROGRESS -> Amber400
        ComplaintStatus.RESOLVED -> Emerald300
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("ticket_card_${ticket.ticketId}"),
        color = Slate800,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Ticket ID + Category + Priority Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Emerald900,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = ticket.ticketId,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Emerald300,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = ticket.category.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                // Priority Badge
                Surface(
                    color = priorityColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = ticket.priority.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate300,
                    lineHeight = 18.sp,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Status Badge ("Assigned to Maintenance" or "In Progress") + Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBgColor,
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusTextColor.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { onStatusToggle() }
                        .testTag("status_toggle_${ticket.ticketId}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusTextColor)
                        )
                        Text(
                            text = ticket.status.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = ticket.formattedTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickFillChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Slate900,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
    ) {
        Text(
            text = "+ $label",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Slate400,
                fontSize = 10.5.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
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

