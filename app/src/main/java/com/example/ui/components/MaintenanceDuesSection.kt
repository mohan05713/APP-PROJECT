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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MaintenanceDueItemEntity
import com.example.data.local.UserEntity
import com.example.data.model.DuePaymentStatus
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
fun MaintenanceDuesSection(
    dues: List<MaintenanceDueItemEntity>,
    currentUser: UserEntity?,
    onPayDue: (dueId: String, method: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatusFilter by remember { mutableStateOf<DuePaymentStatus?>(null) }
    var selectedDueForPayment by remember { mutableStateOf<MaintenanceDueItemEntity?>(null) }
    var selectedDueForReceipt by remember { mutableStateOf<MaintenanceDueItemEntity?>(null) }

    val myFlatDue = remember(dues, currentUser) {
        if (currentUser == null || currentUser.role == UserRole.AUTHORITY) null
        else dues.find { it.flatNumber.equals(currentUser.flatNumber, ignoreCase = true) }
    }

    val filteredDues = remember(dues, selectedStatusFilter) {
        if (selectedStatusFilter == null) dues
        else dues.filter { it.status == selectedStatusFilter }
    }

    val totalPaidCount = remember(dues) { dues.count { it.status == DuePaymentStatus.PAID } }
    val totalPendingCount = remember(dues) { dues.count { it.status == DuePaymentStatus.PENDING } }
    val totalOverdueCount = remember(dues) { dues.count { it.status == DuePaymentStatus.OVERDUE } }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("maintenance_dues_section"),
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
            // Section Header (Horizontal Proportional)
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
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Maintenance & Dues",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = "Automated monthly billing & digital receipts",
                            style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.5.sp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFECFDF5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Text(
                        text = "₹2,000 / mo",
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats Row (Horizontal Proportional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DueStatChip(
                    title = "Collected",
                    value = "$totalPaidCount / ${dues.size}",
                    color = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
                DueStatChip(
                    title = "Pending",
                    value = "$totalPendingCount",
                    color = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                DueStatChip(
                    title = "Overdue",
                    value = "$totalOverdueCount",
                    color = Rose500,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Resident Personal Bill Card (If logged in as resident)
            if (myFlatDue != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (myFlatDue.status == DuePaymentStatus.PAID) Color(0xFFA7F3D0) else Color(0xFFFDE68A)
                    ),
                    modifier = Modifier.fillMaxWidth()
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
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Unit ${myFlatDue.flatNumber} • ${myFlatDue.monthYear}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EnterpriseTextPrimary,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Due: ${myFlatDue.dueDate}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.5.sp)
                                    )
                                }
                                Text(
                                    text = myFlatDue.breakdown,
                                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 10.5.sp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "₹${myFlatDue.amountRupees.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = EnterpriseTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.padding(end = 6.dp)
                                )

                                if (myFlatDue.status == DuePaymentStatus.PAID) {
                                    OutlinedButton(
                                        onClick = { selectedDueForReceipt = myFlatDue },
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Receipt", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = { selectedDueForPayment = myFlatDue },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EnterprisePrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier
                                            .height(24.dp)
                                            .testTag("button_pay_dues_online")
                                    ) {
                                        Text("Pay", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // All Flats Register Header & Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dues Register:",
                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 10.5.sp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = selectedStatusFilter == null,
                        onClick = { selectedStatusFilter = null },
                        label = { Text("All (${dues.size})", fontSize = 9.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EnterprisePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF8FAFC),
                            labelColor = EnterpriseTextSecondary
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    FilterChip(
                        selected = selectedStatusFilter == DuePaymentStatus.PENDING,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == DuePaymentStatus.PENDING) null else DuePaymentStatus.PENDING
                        },
                        label = { Text("Pending ($totalPendingCount)", fontSize = 9.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFD97706),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF8FAFC),
                            labelColor = EnterpriseTextSecondary
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    FilterChip(
                        selected = selectedStatusFilter == DuePaymentStatus.PAID,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == DuePaymentStatus.PAID) null else DuePaymentStatus.PAID
                        },
                        label = { Text("Paid ($totalPaidCount)", fontSize = 9.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF059669),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF8FAFC),
                            labelColor = EnterpriseTextSecondary
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mini list of flats
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                filteredDues.forEach { item ->
                    DueItemRow(
                        item = item,
                        onPay = { selectedDueForPayment = item },
                        onViewReceipt = { selectedDueForReceipt = item }
                    )
                }
            }
        }
    }

    // Payment Sheet Dialog
    selectedDueForPayment?.let { due ->
        PaymentCheckoutDialog(
            due = due,
            onDismiss = { selectedDueForPayment = null },
            onConfirmPayment = { method ->
                onPayDue(due.id, method)
                selectedDueForPayment = null
            }
        )
    }

    // Receipt Dialog
    selectedDueForReceipt?.let { due ->
        ReceiptDialog(
            due = due,
            onDismiss = { selectedDueForReceipt = null }
        )
    }
}

@Composable
private fun DueStatChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = EnterpriseTextMuted, fontSize = 10.sp)
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
        }
    }
}

@Composable
private fun DueItemRow(
    item: MaintenanceDueItemEntity,
    onPay: () -> Unit,
    onViewReceipt: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (item.status == DuePaymentStatus.PAID) onViewReceipt() else onPay()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Unit ${item.flatNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary,
                        fontSize = 11.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${item.residentName}",
                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 11.sp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(₹${item.amountRupees.toInt()})",
                    style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 10.5.sp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                when (item.status) {
                    DuePaymentStatus.PAID -> {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                            modifier = Modifier.clickable { onViewReceipt() }
                        ) {
                            Text(
                                text = "Paid (Receipt)",
                                color = Color(0xFF065F46),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    DuePaymentStatus.PENDING -> {
                        Button(
                            onClick = onPay,
                            colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(22.dp)
                        ) {
                            Text("Pay ₹2k", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    DuePaymentStatus.OVERDUE -> {
                        Button(
                            onClick = onPay,
                            colors = ButtonDefaults.buttonColors(containerColor = Rose500, contentColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(22.dp)
                        ) {
                            Text("Overdue", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCheckoutDialog(
    due: MaintenanceDueItemEntity,
    onDismiss: () -> Unit,
    onConfirmPayment: (method: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onConfirmPayment(selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("button_confirm_checkout")
            ) {
                Text("Pay ₹${due.amountRupees.toInt()} Now", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
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
                Icon(Icons.Default.Payment, contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Maintenance Bill Payment", style = MaterialTheme.typography.titleSmall.copy(color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        DueDetailRow("Unit", "Flat ${due.flatNumber} (${due.residentName})")
                        DueDetailRow("Period", due.monthYear)
                        DueDetailRow("Water & Chute", "₹450")
                        DueDetailRow("Elevator & Power", "₹850")
                        DueDetailRow("Security & Sump", "₹700")
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable", color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("₹${due.amountRupees.toInt()}", color = EnterprisePrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Text("Payment Mode:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary, fontSize = 10.5.sp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PaymentMethodChip(
                        name = "UPI",
                        icon = Icons.Default.QrCode,
                        selected = selectedMethod == "UPI",
                        onClick = { selectedMethod = "UPI" },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentMethodChip(
                        name = "Card",
                        icon = Icons.Default.CreditCard,
                        selected = selectedMethod == "CARD",
                        onClick = { selectedMethod = "CARD" },
                        modifier = Modifier.weight(1f)
                    )
                    PaymentMethodChip(
                        name = "NetBank",
                        icon = Icons.Default.AccountBalance,
                        selected = selectedMethod == "NETBANK",
                        onClick = { selectedMethod = "NETBANK" },
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun PaymentMethodChip(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (selected) Color(0xFFF1F5F9) else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) EnterprisePrimary else EnterpriseBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) EnterprisePrimary else EnterpriseTextSecondary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                fontSize = 10.5.sp,
                color = if (selected) EnterprisePrimary else EnterpriseTextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ReceiptDialog(
    due: MaintenanceDueItemEntity,
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
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Digital Tax Invoice", style = MaterialTheme.typography.titleSmall.copy(color = EnterpriseTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp))
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EnterpriseTextMuted, modifier = Modifier.size(16.dp))
                }
            }
        },
        text = {
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "SRM GREEN RESIDENCY ASSOCIATION",
                        style = MaterialTheme.typography.labelSmall.copy(color = EnterprisePrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    )
                    Text(
                        text = "GSTIN: 33AAACG9921D1ZM",
                        style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 9.5.sp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    DueDetailRow("Receipt No", due.transactionRef.ifBlank { "REC-SEP-${due.flatNumber}" })
                    DueDetailRow("Unit", "Flat ${due.flatNumber} (${due.residentName})")
                    DueDetailRow("Month", due.monthYear)
                    DueDetailRow("Date", due.paidDate.ifBlank { "Paid Online" })
                    DueDetailRow("Total Paid", "₹${due.amountRupees.toInt()} (Settled)")

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECFDF5), RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Payment confirmed to SRM Association account.",
                            color = Color(0xFF065F46),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun DueDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = EnterpriseTextSecondary,
                fontSize = 11.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = EnterpriseTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}
