package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApartmentDirectory
import com.example.data.model.UserRole
import com.example.ui.theme.EnterpriseBg
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseSecondary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

enum class ResidentLoginMode(val label: String) {
    PHONE("Phone Number"),
    NAME("Username / Name"),
    FLAT("Room / Flat")
}

@Composable
fun LoginScreen(
    isLoggingIn: Boolean,
    cloudSyncStatus: String = "🟢 SRM Cloud: Online",
    onLogin: (identifier: String, password: String, role: UserRole, onResult: (Boolean, String) -> Unit) -> Unit,
    onQuickAuthority: (onResult: (Boolean, String) -> Unit) -> Unit,
    onRegisterResident: (floor: Int, flat: String, name: String, pass: String, contact: String) -> Unit,
    onOpenCloudSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.RESIDENT) }
    var residentLoginMode by remember { mutableStateOf(ResidentLoginMode.PHONE) }
    var selectedFloor by remember { mutableIntStateOf(4) }
    var residentIdentifier by remember { mutableStateOf("9876543210") }
    var password by remember { mutableStateOf("pass") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showFloorHelper by remember { mutableStateOf(false) }

    // Authority specific credentials state
    var authorityUsername by remember { mutableStateOf(ApartmentDirectory.AUTHORITY_USERNAME) }
    var authorityPassword by remember { mutableStateOf(ApartmentDirectory.AUTHORITY_PASSWORD) }

    var statusFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var showDirectoryDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EnterpriseBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Proportional Enterprise Header (Horizontal Branding)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EnterprisePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "EcoApartment Resident Portal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EnterpriseTextPrimary,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "SRM Green Residency • Smart Facility Management",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = EnterpriseTextSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cloud Sync Indicator Pill (Clean corporate pill)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                shadowElevation = 0.5.dp,
                modifier = Modifier
                    .clickable { onOpenCloudSync() }
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Icon(
                        Icons.Default.CloudSync,
                        contentDescription = "Cloud Sync",
                        tint = EnterpriseBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cloudSyncStatus,
                        color = EnterpriseTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Configure",
                        color = EnterpriseBlue,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1-Tap Instant Demo Access Header Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0FDF4),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚡ Instant Demo Access (1-Tap Entry)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                statusFeedback = null
                                onLogin("9876543210", "pass", UserRole.RESIDENT) { success, msg ->
                                    statusFeedback = Pair(success, msg)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EnterprisePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("quick_resident_login_header")
                        ) {
                            Text("🏠 Resident Portal", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                statusFeedback = null
                                onQuickAuthority { success, msg ->
                                    statusFeedback = Pair(success, msg)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EnterpriseSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("quick_authority_login_header")
                        ) {
                            Text("🛡️ Authority Desk", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Role Selector: Clean Segmented Control
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Slate100,
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RoleTabItem(
                        selected = selectedRole == UserRole.RESIDENT,
                        title = "Resident Portal",
                        icon = Icons.Default.Home,
                        onClick = {
                            selectedRole = UserRole.RESIDENT
                            statusFeedback = null
                        },
                        modifier = Modifier.weight(1f).testTag("tab_resident_role")
                    )
                    RoleTabItem(
                        selected = selectedRole == UserRole.AUTHORITY,
                        title = "Authority Desk",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = {
                            selectedRole = UserRole.AUTHORITY
                            statusFeedback = null
                        },
                        modifier = Modifier.weight(1f).testTag("tab_authority_role")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // White Card Container for Credentials
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EnterpriseCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedRole == UserRole.RESIDENT) {
                        // Resident Mode Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resident Sign In",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 14.sp
                                )
                            )
                            // View Password Directory
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate100,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                                modifier = Modifier
                                    .clickable { showDirectoryDialog = true }
                                    .testTag("open_directory_button")
                            ) {
                                Text(
                                    text = "📖 Directory",
                                    color = EnterpriseTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3 Login Option Filter Chips (Horizontal Segmented Row)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ResidentLoginMode.values().forEach { mode ->
                                val isSelected = residentLoginMode == mode
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) EnterprisePrimary else Color.White,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) EnterprisePrimary else EnterpriseBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { residentLoginMode = mode }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (mode) {
                                                ResidentLoginMode.PHONE -> Icons.Default.Phone
                                                ResidentLoginMode.NAME -> Icons.Default.Person
                                                ResidentLoginMode.FLAT -> Icons.Default.Home
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp),
                                            tint = if (isSelected) Color.White else EnterpriseTextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = mode.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else EnterpriseTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Unified Identifier Input Field
                        OutlinedTextField(
                            value = residentIdentifier,
                            onValueChange = { residentIdentifier = it },
                            label = {
                                Text(
                                    text = when (residentLoginMode) {
                                        ResidentLoginMode.PHONE -> "Phone Number"
                                        ResidentLoginMode.NAME -> "Registered Resident Name"
                                        ResidentLoginMode.FLAT -> "Flat / Unit Number"
                                    },
                                    color = EnterpriseTextSecondary,
                                    fontSize = 12.sp
                                )
                            },
                            placeholder = {
                                Text(
                                    text = when (residentLoginMode) {
                                        ResidentLoginMode.PHONE -> "Enter 10-digit mobile number"
                                        ResidentLoginMode.NAME -> "Enter your full name"
                                        ResidentLoginMode.FLAT -> "e.g. 101, 204, 302, 402"
                                    },
                                    color = EnterpriseTextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (residentLoginMode) {
                                        ResidentLoginMode.PHONE -> Icons.Default.Phone
                                        ResidentLoginMode.NAME -> Icons.Default.Person
                                        ResidentLoginMode.FLAT -> Icons.Default.Home
                                    },
                                    contentDescription = null,
                                    tint = EnterpriseSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = if (residentLoginMode == ResidentLoginMode.PHONE) KeyboardType.Phone else KeyboardType.Text
                            ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnterprisePrimary,
                                unfocusedBorderColor = EnterpriseBorder,
                                focusedTextColor = EnterpriseTextPrimary,
                                unfocusedTextColor = EnterpriseTextPrimary,
                                cursorColor = EnterprisePrimary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = EnterpriseBg
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("resident_identifier_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Account Password", color = EnterpriseTextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EnterpriseSecondary, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = EnterpriseTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
                                unfocusedContainerColor = EnterpriseBg
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("resident_password_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Professional Proportional Sign In Button (Top enterprise corporate styling)
                        Button(
                            onClick = {
                                statusFeedback = null
                                onLogin(residentIdentifier, password, UserRole.RESIDENT) { success, msg ->
                                    statusFeedback = Pair(success, msg)
                                }
                            },
                            enabled = !isLoggingIn && residentIdentifier.isNotBlank() && password.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EnterprisePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("login_submit_button")
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "Sign In",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Optional Floor Selector Accordion
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showFloorHelper = !showFloorHelper }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🏢 Browse Flats by Floor",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = EnterpriseTextSecondary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                Icon(
                                    imageVector = if (showFloorHelper) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = EnterpriseTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (showFloorHelper) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                (1..4).forEach { floor ->
                                    val isFloorSel = selectedFloor == floor
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isFloorSel) EnterpriseSecondary else Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedFloor = floor }
                                    ) {
                                        Text(
                                            text = "Floor $floor",
                                            fontSize = 11.sp,
                                            fontWeight = if (isFloorSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isFloorSel) Color.White else EnterpriseTextSecondary,
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Register / Change Flat credentials trigger
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "New resident or change password? ",
                                style = MaterialTheme.typography.bodySmall.copy(color = EnterpriseTextMuted, fontSize = 11.5.sp)
                            )
                            Text(
                                text = "Register Here",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EnterpriseBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                ),
                                modifier = Modifier
                                    .clickable { showRegisterDialog = true }
                                    .testTag("open_register_flat_button")
                            )
                        }

                    } else {
                        // Authority Mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = EnterprisePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Facility & Security Desk",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EnterpriseTextPrimary,
                                            fontSize = 12.sp
                                        )
                                    )
                                    Text(
                                        text = "Maintenance monitoring & administration",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EnterpriseTextMuted,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = authorityUsername,
                            onValueChange = { authorityUsername = it },
                            label = { Text("Username", color = EnterpriseTextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = EnterpriseSecondary, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnterprisePrimary,
                                unfocusedBorderColor = EnterpriseBorder,
                                focusedTextColor = EnterpriseTextPrimary,
                                unfocusedTextColor = EnterpriseTextPrimary,
                                cursorColor = EnterprisePrimary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = EnterpriseBg
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("authority_username_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = authorityPassword,
                            onValueChange = { authorityPassword = it },
                            label = { Text("Password", color = EnterpriseTextSecondary, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EnterpriseSecondary, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = EnterpriseTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
                                unfocusedContainerColor = EnterpriseBg
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("authority_password_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Button: Sign In as Authority
                        Button(
                            onClick = {
                                statusFeedback = null
                                onLogin(authorityUsername, authorityPassword, UserRole.AUTHORITY) { success, msg ->
                                    statusFeedback = Pair(success, msg)
                                }
                            },
                            enabled = !isLoggingIn && authorityUsername.isNotBlank() && authorityPassword.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EnterprisePrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("authority_login_button")
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    text = "Sign In as Admin",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Authority Button
                        OutlinedButton(
                            onClick = {
                                onQuickAuthority { success, msg ->
                                    statusFeedback = Pair(success, msg)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EnterpriseTextSecondary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                            modifier = Modifier.fillMaxWidth().testTag("quick_authority_login")
                        ) {
                            Text("⚡ 1-Tap Quick Authority Login", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Status / Error feedback message
                    statusFeedback?.let { (success, message) ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (success) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (success) Color(0xFFA7F3D0) else Color(0xFFFECACA)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = message,
                                color = if (success) Color(0xFF065F46) else Rose500,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer info
            Text(
                text = "EcoApartment OS • Secure Encrypted Session",
                style = MaterialTheme.typography.labelSmall.copy(color = EnterpriseTextMuted, fontSize = 11.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Modal Dialogs
    if (showDirectoryDialog) {
        DirectoryDialog(
            onDismiss = { showDirectoryDialog = false },
            onSelectCredential = { flat, pass ->
                val cred = ApartmentDirectory.DEFAULT_FLATS.find { it.flatNumber.equals(flat, ignoreCase = true) }
                if (cred != null) {
                    when (residentLoginMode) {
                        ResidentLoginMode.PHONE -> residentIdentifier = cred.contactNumber
                        ResidentLoginMode.NAME -> residentIdentifier = cred.residentName
                        ResidentLoginMode.FLAT -> residentIdentifier = cred.flatNumber
                    }
                    password = cred.defaultPassword
                    selectedFloor = cred.floorNumber
                } else {
                    residentIdentifier = flat
                    password = pass
                }
                showDirectoryDialog = false
            }
        )
    }

    if (showRegisterDialog) {
        RegisterFlatDialog(
            onDismiss = { showRegisterDialog = false },
            onRegister = { floor, flat, name, pass, contact ->
                onRegisterResident(floor, flat, name, pass, contact)
                when (residentLoginMode) {
                    ResidentLoginMode.PHONE -> residentIdentifier = contact
                    ResidentLoginMode.NAME -> residentIdentifier = name
                    ResidentLoginMode.FLAT -> residentIdentifier = flat
                }
                password = pass
                selectedFloor = floor
                showRegisterDialog = false
            }
        )
    }
}

@Composable
private fun RoleTabItem(
    selected: Boolean,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color.White else Color.Transparent,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder) else null,
        shadowElevation = if (selected) 1.dp else 0.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) EnterprisePrimary else EnterpriseTextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) EnterpriseTextPrimary else EnterpriseTextSecondary,
                    fontSize = 12.sp
                )
            )
        }
    }
}
