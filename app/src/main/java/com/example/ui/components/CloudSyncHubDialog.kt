package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.CloudSyncService
import com.example.data.remote.SyncMode
import com.example.ui.theme.Amber400
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Emerald300
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.launch

@Composable
fun CloudSyncHubDialog(
    currentSyncMode: SyncMode,
    localUrl: String,
    customUrl: String,
    cloudSyncStatus: String,
    isSyncing: Boolean,
    onModeChange: (SyncMode) -> Unit,
    onSaveUrls: (local: String, custom: String) -> Unit,
    onTriggerSync: (onResult: (Boolean, String) -> Unit) -> Unit,
    onTestConnection: (onResult: (Boolean, String) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedMode by remember { mutableStateOf(currentSyncMode) }
    var localUrlInput by remember { mutableStateOf(localUrl) }
    var customUrlInput by remember { mutableStateOf(customUrl) }
    var testResultMsg by remember { mutableStateOf<String?>(null) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var guideTab by remember { mutableIntStateOf(0) }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Slate900,
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(max = 680.dp)
                .testTag("dialog_cloud_sync_hub")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Cyan400.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Cyan400,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CloudSync & Server Hub",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Cross-device password & database sync",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (cloudSyncStatus.contains("Online") || cloudSyncStatus.contains("synced")) Emerald500.copy(alpha = 0.15f) else Amber400.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (cloudSyncStatus.contains("Online") || cloudSyncStatus.contains("synced")) Emerald400 else Amber400)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(if (cloudSyncStatus.contains("Online") || cloudSyncStatus.contains("synced")) Emerald400 else Amber400, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (cloudSyncStatus.contains("Online") || cloudSyncStatus.contains("synced")) "LIVE SYNC" else "STANDBY",
                                color = if (cloudSyncStatus.contains("Online") || cloudSyncStatus.contains("synced")) Emerald300 else Amber400,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NetworkCheck,
                                contentDescription = null,
                                tint = Cyan400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Active Sync Status",
                                color = Slate300,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cloudSyncStatus,
                            color = Slate200,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Engine Choice Selector
                Text(
                    text = "SELECT SYNC ENGINE",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SyncMode.values().forEach { mode ->
                    val isSelected = selectedMode == mode
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Slate800 else Slate900,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Cyan400 else Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedMode = mode
                                onModeChange(mode)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isSelected) Cyan400.copy(alpha = 0.2f) else Slate800,
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (mode) {
                                        SyncMode.CLOUD_RELAY -> Icons.Default.Cloud
                                        SyncMode.LOCAL_HOST -> Icons.Default.Computer
                                        SyncMode.GOOGLE_SCRIPT_WEBHOOK -> Icons.Default.Folder
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Cyan400 else Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.label,
                                    color = if (isSelected) Color.White else Slate300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = mode.description,
                                    color = Slate400,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Cyan400,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Specific Config
                when (selectedMode) {
                    SyncMode.LOCAL_HOST -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate800.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Laptop / Localhost Server URL",
                                    color = Slate200,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "When your laptop is on the same WiFi, enter its IP address or emulator host (e.g. http://192.168.1.10:8080/sync or http://10.0.2.2:8080/sync):",
                                    color = Slate400,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                OutlinedTextField(
                                    value = localUrlInput,
                                    onValueChange = {
                                        localUrlInput = it
                                        onSaveUrls(it, customUrlInput)
                                    },
                                    placeholder = { Text("http://192.168.1.100:8080/sync", color = Slate600) },
                                    leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null, tint = Cyan400) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Slate200,
                                        focusedBorderColor = Cyan400,
                                        unfocusedBorderColor = Slate600,
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    SyncMode.GOOGLE_SCRIPT_WEBHOOK -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate800.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Google Drive / Google Apps Script Web App URL",
                                    color = Slate200,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Enter your Google Apps Script Web App or custom API endpoint URL:",
                                    color = Slate400,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                                OutlinedTextField(
                                    value = customUrlInput,
                                    onValueChange = {
                                        customUrlInput = it
                                        onSaveUrls(localUrlInput, it)
                                    },
                                    placeholder = { Text("https://script.google.com/macros/s/.../exec", color = Slate600) },
                                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = Cyan400) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Slate200,
                                        focusedBorderColor = Cyan400,
                                        unfocusedBorderColor = Slate600,
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    SyncMode.CLOUD_RELAY -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Emerald500.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald500.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Emerald400, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "High-Availability Global Cloud Relay Active",
                                        color = Emerald300,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                    Text(
                                        text = "Syncs automatically across all mobile devices, laptops, and authority portal in real-time.",
                                        color = Slate300,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (testResultMsg != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (testResultMsg!!.contains("Connected") || testResultMsg!!.contains("Online")) Emerald500.copy(alpha = 0.15f) else Rose500.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (testResultMsg!!.contains("Connected") || testResultMsg!!.contains("Online")) Emerald400 else Rose500),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = testResultMsg!!,
                            color = if (testResultMsg!!.contains("Connected") || testResultMsg!!.contains("Online")) Emerald300 else Rose500,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Test Connection & Sync Now
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isTestingConnection = true
                            testResultMsg = "Pinging endpoint..."
                            onTestConnection { success, message ->
                                isTestingConnection = false
                                testResultMsg = message
                            }
                        },
                        enabled = !isTestingConnection,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan400),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(color = Cyan400, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Ping", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            onTriggerSync { success, message ->
                                testResultMsg = if (success) "⚡ $message" else "⚠️ $message"
                            }
                        },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500, contentColor = Slate900),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Slate900, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Syncing...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Slate800)
                Spacer(modifier = Modifier.height(14.dp))

                // Developer & Setup Guides (Python laptop script & Google Apps Script)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Laptop / Google Drive Setup Snippets",
                        color = Slate300,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TabRow(
                    selectedTabIndex = guideTab,
                    containerColor = Slate800,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[guideTab]),
                            color = Cyan400
                        )
                    }
                ) {
                    Tab(
                        selected = guideTab == 0,
                        onClick = { guideTab = 0 },
                        text = { Text("💻 Laptop Server (Python)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = guideTab == 1,
                        onClick = { guideTab = 1 },
                        text = { Text("📁 Google Apps Script", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (guideTab) {
                    0 -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF020617),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Run on Laptop in Terminal:", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { copyToClipboard(CloudSyncService.Templates.PYTHON_SERVER_SCRIPT, "Python Server Script") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate300, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text(
                                    text = CloudSyncService.Templates.PYTHON_SERVER_SCRIPT,
                                    color = Slate300,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    1 -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF020617),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Paste into script.google.com:", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { copyToClipboard(CloudSyncService.Templates.GOOGLE_APPS_SCRIPT, "Google Apps Script") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate300, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Text(
                                    text = CloudSyncService.Templates.GOOGLE_APPS_SCRIPT,
                                    color = Slate300,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    lineHeight = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Slate700, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close & Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
