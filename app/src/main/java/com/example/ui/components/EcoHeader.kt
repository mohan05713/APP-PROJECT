package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.theme.Amber400
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
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun EcoHeader(
    currentUser: UserEntity?,
    notificationCount: Int,
    isRefreshing: Boolean,
    onNotificationClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAccountClick: () -> Unit,
    onOpenDirectory: () -> Unit = {},
    onCloudSyncClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = EnterpriseCardBg,
        shadowElevation = 0.5.dp,
        border = BorderStroke(1.dp, EnterpriseBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: App Logo + Title + Current Flat Subtitle (Compact Horizontal)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onAccountClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EnterprisePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EcoApartment",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EnterpriseTextPrimary,
                                fontSize = 13.5.sp
                            )
                        )
                        if (currentUser != null) {
                            val cleanFlat = if (currentUser.flatNumber.startsWith("Flat", ignoreCase = true)) {
                                currentUser.flatNumber
                            } else {
                                "Flat ${currentUser.flatNumber}"
                            }
                            Text(
                                text = " • $cleanFlat",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = EnterpriseBlue,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "${currentUser?.residentName ?: "Resident"} (Floor ${currentUser?.floorNumber ?: 1})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EnterpriseTextMuted,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            // Right: Actions (Directory, Sync, Refresh, Bell, Logout)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Directory Button
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Slate100,
                    border = BorderStroke(1.dp, EnterpriseBorder),
                    modifier = Modifier
                        .clickable { onOpenDirectory() }
                        .padding(end = 4.dp)
                        .testTag("header_directory_button")
                ) {
                    Text(
                        text = "📖 Directory",
                        color = EnterpriseTextSecondary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // CloudSync Button
                IconButton(
                    onClick = onCloudSyncClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("header_cloud_sync_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sync",
                        tint = EnterpriseBlue,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Refresh Button
                IconButton(
                    onClick = onRefreshClick,
                    enabled = !isRefreshing,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("refresh_telemetry_button")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = EnterprisePrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = EnterpriseTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Notification Bell
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("notification_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (notificationCount > 0) {
                                Badge(
                                    containerColor = Rose500,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = notificationCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.5.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = EnterpriseTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Logout Button
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("resident_logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Rose500,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}
