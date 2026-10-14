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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CommunityNoticeEntity
import com.example.data.local.UserEntity
import com.example.data.model.NoticePriority
import com.example.data.model.UserRole
import com.example.ui.theme.EnterpriseBg
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun CommunityNoticesSection(
    notices: List<CommunityNoticeEntity>,
    currentUser: UserEntity?,
    onPostNotice: (title: String, description: String, category: String, priority: NoticePriority, isPinned: Boolean) -> Unit,
    onDeleteNotice: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    var showPostModal by remember { mutableStateOf(false) }
    var selectedNoticeForDetail by remember { mutableStateOf<CommunityNoticeEntity?>(null) }

    val categories = listOf("All", "Water", "Elevator", "Security", "Solar")

    val filteredNotices = remember(notices, selectedCategoryFilter) {
        if (selectedCategoryFilter == null || selectedCategoryFilter == "All") {
            notices.sortedWith(compareByDescending<CommunityNoticeEntity> { it.isPinned }.thenByDescending { it.timestampMillis })
        } else {
            notices.filter { it.category.contains(selectedCategoryFilter!!, ignoreCase = true) }
                .sortedWith(compareByDescending<CommunityNoticeEntity> { it.isPinned }.thenByDescending { it.timestampMillis })
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("community_notices_section"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EnterpriseCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row (Horizontal Proportional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Notice Board & Circulars",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = "Official announcements • Tap item for details",
                            style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.5.sp)
                        )
                    }
                }

                Button(
                    onClick = { showPostModal = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EnterprisePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("button_post_notice")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Post", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = (selectedCategoryFilter == null && cat == "All") || selectedCategoryFilter == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategoryFilter = if (cat == "All") null else cat
                        },
                        label = { Text(cat, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EnterprisePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF8FAFC),
                            labelColor = EnterpriseTextSecondary
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notices List
            if (filteredNotices.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = EnterpriseTextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("No notices posted in this category.", color = EnterpriseTextSecondary, fontSize = 11.sp)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    filteredNotices.forEach { notice ->
                        NoticeCard(
                            notice = notice,
                            canDelete = currentUser?.role == UserRole.AUTHORITY || notice.authorName == currentUser?.residentName,
                            onClick = { selectedNoticeForDetail = notice },
                            onDelete = { onDeleteNotice(notice.id) }
                        )
                    }
                }
            }
        }
    }

    if (showPostModal) {
        PostNoticeDialog(
            onDismiss = { showPostModal = false },
            onPost = { title, desc, cat, prio, pinned ->
                onPostNotice(title, desc, cat, prio, pinned)
                showPostModal = false
            }
        )
    }

    selectedNoticeForDetail?.let { notice ->
        NoticeDetailModal(
            notice = notice,
            onDismiss = { selectedNoticeForDetail = null }
        )
    }
}

@Composable
private fun NoticeCard(
    notice: CommunityNoticeEntity,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryColor = when {
        notice.category.contains("Water", ignoreCase = true) -> EnterpriseBlue
        notice.category.contains("Elevator", ignoreCase = true) || notice.category.contains("Power", ignoreCase = true) -> Color(0xFFD97706)
        notice.category.contains("Security", ignoreCase = true) -> Color(0xFF7C3AED)
        notice.category.contains("Solar", ignoreCase = true) || notice.category.contains("Green", ignoreCase = true) -> Color(0xFF059669)
        else -> EnterprisePrimary
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (notice.priority) {
                NoticePriority.URGENT -> Color(0xFFFECACA)
                NoticePriority.IMPORTANT -> Color(0xFFFDE68A)
                NoticePriority.NORMAL -> EnterpriseBorder
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (notice.isPinned) {
                        Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = Color(0xFFD97706), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = notice.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary,
                            fontSize = 12.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (notice.priority) {
                        NoticePriority.URGENT -> {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEF2F2),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                            ) {
                                Text(
                                    text = "URGENT",
                                    color = Color(0xFF991B1B),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        NoticePriority.IMPORTANT -> {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFEF3C7),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Text(
                                    text = "IMPORTANT",
                                    color = Color(0xFF92400E),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        NoticePriority.NORMAL -> {}
                    }

                    if (canDelete) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EnterpriseTextMuted, modifier = Modifier.size(13.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = notice.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EnterpriseTextSecondary,
                    lineHeight = 15.sp,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "${notice.authorName} • ${notice.postedTime} • ${notice.category}",
                style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 9.5.sp)
            )
        }
    }
}

@Composable
fun PostNoticeDialog(
    onDismiss: () -> Unit,
    onPost: (title: String, description: String, category: String, priority: NoticePriority, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Water") }
    var priority by remember { mutableStateOf(NoticePriority.IMPORTANT) }
    var isPinned by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val categoryOptions = listOf("Water", "Elevator & Power", "Security", "Solar", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank()) {
                        errorMsg = "Please enter both title and description."
                        return@Button
                    }
                    onPost(title, description, category, priority, isPinned)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("submit_post_notice")
            ) {
                Text("Broadcast Notice", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Cancel", color = EnterpriseTextSecondary, fontSize = 11.sp)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publish Community Notice", style = MaterialTheme.typography.titleSmall.copy(color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title", color = EnterpriseTextMuted, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details & Action Items for Residents", color = EnterpriseTextMuted, fontSize = 11.sp) },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 10.sp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.take(7), fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnterprisePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = EnterpriseTextSecondary
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Text("Priority Level:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 10.sp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NoticePriority.values().forEach { prio ->
                        FilterChip(
                            selected = priority == prio,
                            onClick = { priority = prio },
                            label = { Text(prio.displayName, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (prio == NoticePriority.URGENT) Rose500 else if (prio == NoticePriority.IMPORTANT) Color(0xFFD97706) else Color(0xFF059669),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = EnterpriseTextSecondary
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isPinned = !isPinned }
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = EnterprisePrimary,
                            checkmarkColor = Color.White,
                            uncheckedColor = EnterpriseBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pin this notice to top of board", color = EnterpriseTextSecondary, fontSize = 11.sp)
                }

                errorMsg?.let {
                    Text(it, color = Rose500, fontSize = 10.5.sp)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun NoticeDetailModal(
    notice: CommunityNoticeEntity,
    onDismiss: () -> Unit
) {
    var acknowledged by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    acknowledged = true
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (acknowledged) "✓ Acknowledged" else "👍 Acknowledge Notice", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
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
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notice.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary,
                            fontSize = 15.sp
                        )
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
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFEFF6FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                    ) {
                        Text(
                            text = notice.category,
                            color = EnterpriseBlue,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (notice.priority == NoticePriority.URGENT) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Text(
                                text = "🚨 URGENT NOTICE",
                                color = Rose500,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notice.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = EnterpriseTextPrimary,
                            lineHeight = 20.sp,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Column {
                    Text(
                        text = "Broadcast Metadata:",
                        style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "• Posted By: ${notice.authorName}\n• Timestamp: ${notice.postedTime}\n• Category: ${notice.category}\n• SRM Residency Green Complex Board",
                        style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp)
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}

