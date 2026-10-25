package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.local.VisitorEntity
import com.example.data.model.UserRole
import com.example.data.model.VisitorApprovalStatus
import com.example.data.model.VisitorType
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
fun VisitorManagementSection(
    visitorLogs: List<VisitorEntity>,
    currentUser: UserEntity?,
    onApproveVisitor: (id: String, status: VisitorApprovalStatus, approvedBy: String) -> Unit,
    onLogNewVisitor: (name: String, phone: String, type: VisitorType, flat: String, floor: Int, vehicle: String, purpose: String) -> Unit,
    onScanBarcode: (barcode: String, onResult: (Boolean, String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<VisitorType?>(null) }
    var showOnlyPending by remember { mutableStateOf(false) }
    var showLogModal by remember { mutableStateOf(false) }
    var showScannerModal by remember { mutableStateOf(false) }
    var selectedPassForBarcodeModal by remember { mutableStateOf<VisitorEntity?>(null) }

    val filteredLogs = remember(visitorLogs, selectedFilter, showOnlyPending, currentUser) {
        visitorLogs.filter { log ->
            val matchesType = selectedFilter == null || log.visitorType == selectedFilter
            val matchesPending = !showOnlyPending || log.status == VisitorApprovalStatus.PENDING
            matchesType && matchesPending
        }
    }

    val pendingCount = remember(visitorLogs) {
        visitorLogs.count { it.status == VisitorApprovalStatus.PENDING }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("visitor_management_section"),
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
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Visitor & Gate Access",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 13.5.sp
                                )
                            )
                            if (pendingCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                                ) {
                                    Text(
                                        text = "$pendingCount Pending",
                                        color = Color(0xFF92400E),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Barcode passes & resident gate approval log",
                            style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.5.sp)
                        )
                    }
                }

                // Action buttons: Barcode Scanner & + Gate Pass
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showScannerModal = true },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                            .border(1.dp, EnterpriseBorder, RoundedCornerShape(6.dp))
                            .testTag("button_open_barcode_scanner")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Barcode Pass",
                            tint = EnterpriseBlue,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Button(
                        onClick = { showLogModal = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EnterprisePrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("button_preapprove_visitor")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("New Pass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips (Horizontal Proportional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null && !showOnlyPending,
                    onClick = {
                        selectedFilter = null
                        showOnlyPending = false
                    },
                    label = { Text("All (${visitorLogs.size})", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EnterprisePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF8FAFC),
                        labelColor = EnterpriseTextSecondary
                    ),
                    modifier = Modifier.height(26.dp)
                )

                FilterChip(
                    selected = showOnlyPending,
                    onClick = {
                        showOnlyPending = !showOnlyPending
                        selectedFilter = null
                    },
                    label = { Text("Pending ($pendingCount)", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD97706),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF8FAFC),
                        labelColor = EnterpriseTextSecondary
                    ),
                    modifier = Modifier.height(26.dp)
                )

                FilterChip(
                    selected = selectedFilter == VisitorType.DELIVERY,
                    onClick = {
                        selectedFilter = if (selectedFilter == VisitorType.DELIVERY) null else VisitorType.DELIVERY
                        showOnlyPending = false
                    },
                    label = { Text("Delivery", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EnterpriseBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF8FAFC),
                        labelColor = EnterpriseTextSecondary
                    ),
                    modifier = Modifier.height(26.dp)
                )

                FilterChip(
                    selected = selectedFilter == VisitorType.GUEST,
                    onClick = {
                        selectedFilter = if (selectedFilter == VisitorType.GUEST) null else VisitorType.GUEST
                        showOnlyPending = false
                    },
                    label = { Text("Guest", fontSize = 10.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF059669),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF8FAFC),
                        labelColor = EnterpriseTextSecondary
                    ),
                    modifier = Modifier.height(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Visitor Cards
            if (filteredLogs.isEmpty()) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = EnterpriseTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No visitor entries matching criteria. Gate access is calm.",
                            style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp)
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    filteredLogs.forEach { log ->
                        VisitorLogCard(
                            visitor = log,
                            currentUser = currentUser,
                            onApprove = {
                                val approver = currentUser?.residentName ?: "Resident"
                                onApproveVisitor(log.id, VisitorApprovalStatus.APPROVED, approver)
                            },
                            onDeny = {
                                val approver = currentUser?.residentName ?: "Resident"
                                onApproveVisitor(log.id, VisitorApprovalStatus.DENIED, approver)
                            },
                            onShowBarcode = {
                                selectedPassForBarcodeModal = log
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal: Pre-Approve or Log New Visitor
    if (showLogModal) {
        LogVisitorDialog(
            currentUser = currentUser,
            onDismiss = { showLogModal = false },
            onSave = { name, phone, type, flat, floor, vehicle, purpose ->
                onLogNewVisitor(name, phone, type, flat, floor, vehicle, purpose)
                showLogModal = false
            }
        )
    }

    // Modal: Barcode Scanner Simulation
    if (showScannerModal) {
        BarcodeScannerDialog(
            onDismiss = { showScannerModal = false },
            onScan = onScanBarcode
        )
    }

    // Modal: Display Barcode Pass
    selectedPassForBarcodeModal?.let { pass ->
        BarcodePassDetailDialog(
            visitor = pass,
            onDismiss = { selectedPassForBarcodeModal = null }
        )
    }
}

@Composable
private fun VisitorLogCard(
    visitor: VisitorEntity,
    currentUser: UserEntity?,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
    onShowBarcode: () -> Unit
) {
    val isMyFlat = currentUser?.flatNumber?.equals(visitor.flatNumber, ignoreCase = true) == true || currentUser?.role == UserRole.AUTHORITY

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (visitor.status == VisitorApprovalStatus.PENDING) Color(0xFFFDE68A) else EnterpriseBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowBarcode() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Horizontal Top Row: Icon + Name/Info + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    val icon = when (visitor.visitorType) {
                        VisitorType.DELIVERY -> Icons.Default.LocalShipping
                        VisitorType.CAB -> Icons.Default.DirectionsCar
                        VisitorType.SERVICE -> Icons.Default.Security
                        VisitorType.GUEST -> Icons.Default.Person
                    }
                    val iconColor = when (visitor.visitorType) {
                        VisitorType.DELIVERY -> EnterpriseBlue
                        VisitorType.GUEST -> Color(0xFF059669)
                        VisitorType.CAB -> Color(0xFFD97706)
                        VisitorType.SERVICE -> Color(0xFF7C3AED)
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = visitor.visitorName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 12.5.sp
                                ),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            val cleanFlat = visitor.flatNumber.replace("Flat ", "", ignoreCase = true)
                            Text(
                                text = "• Unit $cleanFlat (Fl ${visitor.floorNumber})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EnterpriseTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Text(
                            text = "${visitor.purpose} • ${visitor.entryTime}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EnterpriseTextMuted,
                                fontSize = 10.5.sp
                            ),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                // Status Badge
                when (visitor.status) {
                    VisitorApprovalStatus.PENDING -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF3C7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Text(
                                text = "Pending",
                                color = Color(0xFF92400E),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    VisitorApprovalStatus.APPROVED -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Text(
                                text = "Approved",
                                color = Color(0xFF065F46),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    VisitorApprovalStatus.DENIED -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Text(
                                text = "Denied",
                                color = Color(0xFF991B1B),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    VisitorApprovalStatus.CHECKED_OUT -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Checked Out",
                                color = EnterpriseTextSecondary,
                                fontSize = 9.5.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Barcode & Action Strip (Horizontal Proportional)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onShowBarcode() }
                ) {
                    MiniBarcodeGraphic(modifier = Modifier.size(width = 38.dp, height = 14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = visitor.passCodeOrBarcode,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseBlue,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                if (visitor.status == VisitorApprovalStatus.PENDING) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDeny,
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose500),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Deny", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onApprove,
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EnterprisePrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("Approve", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (visitor.approvedByResident.isNotBlank()) {
                    Text(
                        text = "Approved by: ${visitor.approvedByResident}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF059669), fontSize = 10.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun MiniBarcodeGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barCount = 18
        val widthPerBar = size.width / (barCount * 1.5f)
        for (i in 0 until barCount) {
            val isThick = (i % 3 == 0 || i % 7 == 0)
            val strokeWidth = if (isThick) widthPerBar * 1.3f else widthPerBar * 0.7f
            val x = i * (widthPerBar * 1.5f)
            drawLine(
                color = Color(0xFF334155),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
fun FullBarcodeGraphic(barcodeText: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
            .border(1.dp, EnterpriseBorder, RoundedCornerShape(6.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            val barCount = 36
            val widthPerBar = size.width / (barCount * 1.4f)
            for (i in 0 until barCount) {
                val isThick = (i % 2 == 0 || i % 5 == 0)
                val stroke = if (isThick) widthPerBar * 1.2f else widthPerBar * 0.6f
                val x = i * (widthPerBar * 1.4f) + 2.dp.toPx()
                drawLine(
                    color = Color.Black,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = stroke
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = barcodeText,
            color = EnterpriseTextPrimary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BarcodePassDetailDialog(
    visitor: VisitorEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Close", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Digital Gate Pass", style = MaterialTheme.typography.titleSmall.copy(color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp))
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EnterpriseTextMuted, modifier = Modifier.size(16.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Present at SRM Green Residency Gate Security Scanner",
                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 10.5.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FullBarcodeGraphic(
                    barcodeText = visitor.passCodeOrBarcode,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        DetailRow("Visitor", visitor.visitorName)
                        DetailRow("Phone", visitor.visitorPhone.ifBlank { "Not provided" })
                        DetailRow("Unit", "Flat ${visitor.flatNumber} (Floor ${visitor.floorNumber})")
                        DetailRow("Vehicle", visitor.vehicleNumber.ifBlank { "N/A (Pedestrian)" })
                        DetailRow("Type", visitor.visitorType.displayName)
                        DetailRow("Status", visitor.status.displayName)
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = EnterpriseTextMuted, fontSize = 11.sp)
        Text(value, color = EnterpriseTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
    }
}

@Composable
fun LogVisitorDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, type: VisitorType, flat: String, floor: Int, vehicle: String, purpose: String) -> Unit
) {
    var visitorName by remember { mutableStateOf("") }
    var visitorPhone by remember { mutableStateOf("") }
    var visitorType by remember { mutableStateOf(VisitorType.DELIVERY) }
    var floorNumber by remember { mutableIntStateOf(currentUser?.floorNumber ?: 1) }
    var flatNumber by remember { mutableStateOf(currentUser?.flatNumber ?: "Flat 101") }
    var vehicleNumber by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("Package Delivery") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (visitorName.isBlank()) {
                        errorMsg = "Please enter visitor / delivery agent name."
                        return@Button
                    }
                    onSave(visitorName, visitorPhone, visitorType, flatNumber, floorNumber, vehicleNumber, purpose)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("submit_log_visitor")
            ) {
                Text("Generate Gate Pass", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
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
            Text("Issue Gate Pass", style = MaterialTheme.typography.titleSmall.copy(color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp))
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Visitor Type:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 10.5.sp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VisitorType.values().forEach { type ->
                        FilterChip(
                            selected = visitorType == type,
                            onClick = {
                                visitorType = type
                                if (type == VisitorType.DELIVERY) purpose = "Package / Food Delivery"
                                else if (type == VisitorType.GUEST) purpose = "Personal Visit"
                                else if (type == VisitorType.CAB) purpose = "Taxi Pickup / Drop"
                                else purpose = "Home Maintenance Servicing"
                            },
                            label = { Text(type.name.take(4), fontSize = 9.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnterpriseBlue,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = EnterpriseTextSecondary
                            ),
                            modifier = Modifier.height(26.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = visitorName,
                    onValueChange = { visitorName = it },
                    label = { Text("Visitor Name (e.g. Swiggy, Amazon)", color = EnterpriseTextMuted, fontSize = 11.sp) },
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
                    value = visitorPhone,
                    onValueChange = { visitorPhone = it },
                    label = { Text("Phone Number", color = EnterpriseTextMuted, fontSize = 11.sp) },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = flatNumber,
                        onValueChange = { flatNumber = it },
                        label = { Text("Flat No", color = EnterpriseTextMuted, fontSize = 11.sp) },
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
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = vehicleNumber,
                        onValueChange = { vehicleNumber = it },
                        label = { Text("Vehicle (Opt)", color = EnterpriseTextMuted, fontSize = 11.sp) },
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
                        modifier = Modifier.weight(1.2f)
                    )
                }

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Purpose of Visit", color = EnterpriseTextMuted, fontSize = 11.sp) },
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

                errorMsg?.let {
                    Text(text = it, color = Rose500, fontSize = 10.5.sp)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onScan: (barcode: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var scannedCodeInput by remember { mutableStateOf("") }
    var scanResultFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (scannedCodeInput.isBlank()) return@Button
                    onScan(scannedCodeInput) { success, msg ->
                        scanResultFeedback = Pair(success, msg)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EnterpriseBlue, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("submit_verify_barcode")
            ) {
                Text("Verify Pass", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Close", color = EnterpriseTextSecondary, fontSize = 11.sp)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = EnterpriseBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Gate Barcode Scanner", style = MaterialTheme.typography.titleSmall.copy(color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Viewfinder simulation
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EnterpriseBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            MiniBarcodeGraphic(modifier = Modifier.size(width = 100.dp, height = 24.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Optical Barcode Sensor Active",
                                style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseBlue, fontSize = 10.sp)
                            )
                        }
                    }
                }

                // Quick test shortcuts
                Text("Presets:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.sp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = {
                            scannedCodeInput = "SRM-DEL-402-881"
                            onScan("SRM-DEL-402-881") { success, msg -> scanResultFeedback = Pair(success, msg) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = EnterpriseTextPrimary),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    ) {
                        Text("402 Deliv", fontSize = 9.5.sp)
                    }

                    Button(
                        onClick = {
                            scannedCodeInput = "SRM-DEL-203-319"
                            onScan("SRM-DEL-203-319") { success, msg -> scanResultFeedback = Pair(success, msg) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = EnterpriseTextPrimary),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    ) {
                        Text("203 Swiggy", fontSize = 9.5.sp)
                    }

                    Button(
                        onClick = {
                            scannedCodeInput = "SRM-GST-301-701"
                            onScan("SRM-GST-301-701") { success, msg -> scanResultFeedback = Pair(success, msg) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = EnterpriseTextPrimary),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    ) {
                        Text("301 Guest", fontSize = 9.5.sp)
                    }
                }

                OutlinedTextField(
                    value = scannedCodeInput,
                    onValueChange = { scannedCodeInput = it },
                    label = { Text("Barcode Code (e.g. SRM-DEL-402-881)", color = EnterpriseTextMuted, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterpriseBlue,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                scanResultFeedback?.let { (success, message) ->
                    Surface(
                        color = if (success) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (success) Color(0xFFA7F3D0) else Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (success) Color(0xFF065F46) else Color(0xFF991B1B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message,
                                color = if (success) Color(0xFF065F46) else Color(0xFF991B1B),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(10.dp)
    )
}
