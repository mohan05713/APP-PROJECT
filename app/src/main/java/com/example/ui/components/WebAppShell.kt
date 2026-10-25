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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EnterpriseBg
import com.example.ui.theme.EnterpriseBlue
import com.example.ui.theme.EnterpriseBorder
import com.example.ui.theme.EnterpriseCardBg
import com.example.ui.theme.EnterprisePrimary
import com.example.ui.theme.EnterpriseSecondary
import com.example.ui.theme.EnterpriseTextMuted
import com.example.ui.theme.EnterpriseTextPrimary
import com.example.ui.theme.EnterpriseTextSecondary
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun WebAppShell(
    currentUser: UserEntity?,
    onQuickResidentLogin: () -> Unit,
    onQuickAuthorityLogin: () -> Unit,
    onOpenDirectory: () -> Unit,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Slate900,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Web Browser URL & Controls Frame
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate900)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Window Control Dots (Mac/Browser style)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WEB APP PORTAL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Emerald400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                // Simulated Address Bar
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Slate800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "SSL Secure",
                            tint = Emerald400,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "https://eco-apartment.srm.app/" + when (currentUser?.role) {
                                UserRole.RESIDENT -> "resident-portal"
                                UserRole.AUTHORITY -> "authority-desk"
                                null -> "login"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Web Refresh Button
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload Web Page",
                        tint = Emerald400,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // 2. Interactive Web Application Header Navigation Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = EnterpriseCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Brand Logo & Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onOpenDirectory() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EnterprisePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SRM EcoApartment Web Portal",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseTextPrimary,
                                    fontSize = 12.5.sp
                                )
                            )
                            Text(
                                text = if (currentUser != null) "Active: ${currentUser.residentName} (${currentUser.flatNumber})" else "Status: Guest / Ready to Access",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EnterpriseTextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Right Navigation Action Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🏠 Resident Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentUser?.role == UserRole.RESIDENT) EnterprisePrimary else Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                            modifier = Modifier
                                .clickable { onQuickResidentLogin() }
                                .testTag("web_nav_resident")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = if (currentUser?.role == UserRole.RESIDENT) Color.White else EnterpriseTextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Resident",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUser?.role == UserRole.RESIDENT) Color.White else EnterpriseTextSecondary
                                )
                            }
                        }

                        // 🛡️ Authority Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (currentUser?.role == UserRole.AUTHORITY) EnterpriseSecondary else Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                            modifier = Modifier
                                .clickable { onQuickAuthorityLogin() }
                                .testTag("web_nav_authority")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = if (currentUser?.role == UserRole.AUTHORITY) Color.White else EnterpriseTextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Authority Desk",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUser?.role == UserRole.AUTHORITY) Color.White else EnterpriseTextSecondary
                                )
                            }
                        }

                        // 📖 Directory Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Slate100,
                            border = androidx.compose.foundation.BorderStroke(1.dp, EnterpriseBorder),
                            modifier = Modifier
                                .clickable { onOpenDirectory() }
                                .testTag("web_nav_directory")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = EnterpriseBlue,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Directory",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnterpriseBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
