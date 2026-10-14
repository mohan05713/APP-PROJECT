package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.theme.EnterpriseBg
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Rose500

@Composable
fun RegisterFlatDialog(
    onDismiss: () -> Unit,
    onRegister: (floor: Int, flatNumber: String, residentName: String, password: String, contact: String) -> Unit
) {
    var floorNumber by remember { mutableIntStateOf(1) }
    var flatNumberInput by remember { mutableStateOf("") }
    var residentNameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var contactInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (flatNumberInput.isBlank() || residentNameInput.isBlank() || passwordInput.isBlank()) {
                        errorMessage = "Please fill in flat number, name, and password."
                        return@Button
                    }
                    if (passwordInput.length < 4) {
                        errorMessage = "Password must be at least 4 characters."
                        return@Button
                    }
                    onRegister(floorNumber, flatNumberInput, residentNameInput, passwordInput, contactInput)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EnterprisePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_register_flat")
            ) {
                Text("Register / Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = EnterpriseTextSecondary)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Register / Update Flat",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary
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
                Text("Floor Selection:", style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextSecondary))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..4).forEach { floor ->
                        FilterChip(
                            selected = floorNumber == floor,
                            onClick = { floorNumber = floor },
                            label = { Text("Floor $floor", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnterprisePrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF1F5F9),
                                labelColor = EnterpriseTextSecondary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = flatNumberInput,
                    onValueChange = { flatNumberInput = it },
                    label = { Text("Flat / Room Number (e.g. 105, 204, 302, 402)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = residentNameInput,
                    onValueChange = { residentNameInput = it },
                    label = { Text("Resident Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Custom / Unique Password") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contactInput,
                    onValueChange = { contactInput = it },
                    label = { Text("Phone Contact (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall.copy(color = Rose500)
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ChangePasswordDialog(
    currentFlat: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isBlank()) {
                        errorText = "Password cannot be blank."
                        return@Button
                    }
                    if (newPassword != confirmPassword) {
                        errorText = "Passwords do not match."
                        return@Button
                    }
                    if (newPassword.length < 4) {
                        errorText = "Password must be at least 4 characters."
                        return@Button
                    }
                    onConfirm(newPassword)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EnterprisePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Update Password", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                Text("Cancel", color = EnterpriseTextSecondary)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = EnterprisePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Change Password ($currentFlat)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EnterpriseTextPrimary
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter a new unique password for your flat:",
                    style = MaterialTheme.typography.bodySmall.copy(color = EnterpriseTextSecondary)
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                errorText?.let {
                    Text(it, color = Rose500, fontSize = 12.sp)
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Prominent Account Profile & Security Dialog shown when user taps the "Account" button.
 * Contains:
 * - Top Four Need: Account Name, Floor Number, Room Number, Phone Number
 * - Account New Password input
 * - Confirm New Password
 * - Change Password / Save Account action button
 */
@Composable
fun AccountDetailsDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onUpdateAccount: (newContact: String, newPassword: String?) -> Unit
) {
    var contactInput by remember(user.contactNumber) { mutableStateOf(user.contactNumber) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isNotBlank()) {
                        if (newPassword.length < 4) {
                            errorText = "New password must be at least 4 characters."
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            errorText = "Passwords do not match."
                            return@Button
                        }
                    }
                    onUpdateAccount(
                        contactInput.trim(),
                        if (newPassword.isNotBlank()) newPassword.trim() else null
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EnterprisePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_account_changes_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (newPassword.isNotBlank()) "Change Password" else "Save Account Details",
                    fontWeight = FontWeight.Bold
                )
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFECFDF5))
                            .border(1.dp, Color(0xFFA7F3D0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = EnterprisePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Account Profile",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary
                            )
                        )
                        Text(
                            text = "Apartment Resident Security & Settings",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EnterpriseTextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EnterpriseTextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // -------------------------------------------------------------
                // TOP FOUR NEED CONTAINER
                // -------------------------------------------------------------
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOP 4 ACCOUNT DETAILS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFECFDF5)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = Color(0xFF065F46),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // 1. Account Name
                        AccountDetailRow(
                            icon = Icons.Default.Person,
                            label = "Account Name",
                            value = user.residentName,
                            iconTint = EnterprisePrimary
                        )

                        // 2. Room Number
                        AccountDetailRow(
                            icon = Icons.Default.Home,
                            label = "Room Number",
                            value = user.flatNumber,
                            iconTint = Color(0xFFD97706)
                        )

                        // 3. Floor Number
                        AccountDetailRow(
                            icon = Icons.Default.Layers,
                            label = "Floor Number",
                            value = "Floor ${user.floorNumber}",
                            iconTint = EnterprisePrimary
                        )

                        // 4. Phone Number
                        AccountDetailRow(
                            icon = Icons.Default.Phone,
                            label = "Phone Number",
                            value = user.contactNumber.ifEmpty { "Not Registered" },
                            iconTint = EnterpriseTextSecondary
                        )
                    }
                }

                // -------------------------------------------------------------
                // SECURITY & CHANGE PASSWORD SECTION
                // -------------------------------------------------------------
                Text(
                    text = "Security & Contact Updates",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = EnterpriseTextPrimary
                    )
                )

                // Phone Number field
                OutlinedTextField(
                    value = contactInput,
                    onValueChange = { contactInput = it },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = EnterprisePrimary)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Account New Password field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Account New Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD97706))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = EnterpriseTextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    placeholder = { Text("Enter new password to change", color = EnterpriseTextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnterprisePrimary,
                        unfocusedBorderColor = EnterpriseBorder,
                        focusedTextColor = EnterpriseTextPrimary,
                        unfocusedTextColor = EnterpriseTextPrimary,
                        cursorColor = EnterprisePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Confirm New Password field
                if (newPassword.isNotBlank()) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm New Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD97706))
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnterprisePrimary,
                            unfocusedBorderColor = EnterpriseBorder,
                            focusedTextColor = EnterpriseTextPrimary,
                            unfocusedTextColor = EnterpriseTextPrimary,
                            cursorColor = EnterprisePrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                errorText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(color = Rose500)
                    )
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun AccountDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EnterpriseTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = EnterpriseTextPrimary,
                fontSize = 13.sp
            )
        )
    }
}

/**
 * Front Page Account Information Card.
 * Prominently displays:
 * - Account Name
 * - Phone Number
 * - Floor Number
 * - Room Number
 * Plus a direct "Account" button to manage profile or change password.
 */
@Composable
fun FrontPageAccountCard(
    user: UserEntity,
    onOpenAccount: () -> Unit,
    onExportPdf: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.EnterpriseBorder),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Resident Name & Room / Floor / Phone info (Horizontal Proportional)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.EnterprisePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.residentName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.residentName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = com.example.ui.theme.EnterpriseTextPrimary,
                                fontSize = 13.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Text(
                                text = "Active",
                                color = Color(0xFF065F46),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "Unit ${user.flatNumber} • Floor ${user.floorNumber} • ${if (user.contactNumber.isNotBlank()) user.contactNumber else "No Phone"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = com.example.ui.theme.EnterpriseTextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Right: Actions (PDF & Account)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onExportPdf != null) {
                    OutlinedButton(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = com.example.ui.theme.EnterpriseBlue
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.EnterpriseBorder),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("front_page_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = onOpenAccount,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.EnterprisePrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("front_page_account_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }
            }
        }
    }
}
