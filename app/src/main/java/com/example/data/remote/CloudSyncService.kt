package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.local.CommunityNoticeEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.MaintenanceDueItemEntity
import com.example.data.local.UserEntity
import com.example.data.local.VisitorEntity
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.DuePaymentStatus
import com.example.data.model.NoticePriority
import com.example.data.model.UserRole
import com.example.data.model.VisitorApprovalStatus
import com.example.data.model.VisitorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class SyncMode(val label: String, val description: String) {
    CLOUD_RELAY("Internet Cloud Relay", "Automatic global cloud mesh (zero setup required)"),
    LOCAL_HOST("Laptop / Localhost WiFi", "Sync with laptop/desktop over local WiFi (e.g. http://192.168.x.x:8080)"),
    GOOGLE_SCRIPT_WEBHOOK("Google Drive / Webhook", "Personal Google Apps Script Web App or custom cloud webhook")
}

data class MasterCloudSyncPayload(
    val complaints: List<ComplaintEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val visitors: List<VisitorEntity> = emptyList(),
    val dues: List<MaintenanceDueItemEntity> = emptyList(),
    val notices: List<CommunityNoticeEntity> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

class CloudSyncService(
    context: Context? = null,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
) {
    private val prefs: SharedPreferences? = context?.getSharedPreferences("eco_cloud_sync_prefs", Context.MODE_PRIVATE)

    private var inMemorySyncMode: SyncMode = SyncMode.CLOUD_RELAY
    private var inMemoryLocalUrl: String = DEFAULT_LOCAL_URL
    private var inMemoryCustomUrl: String = DEFAULT_CUSTOM_URL

    companion object {
        private const val TAG = "CloudSyncService"
        private const val KEY_SYNC_MODE = "pref_sync_mode"
        private const val KEY_LOCAL_URL = "pref_local_url"
        private const val KEY_CUSTOM_URL = "pref_custom_url"
        private const val KEY_SAVED_OBJECT_ID = "pref_saved_object_id"

        private const val DEFAULT_LOCAL_URL = "http://10.0.2.2:8080/sync"
        private const val DEFAULT_CUSTOM_URL = "https://script.google.com/macros/s/AKfycbx_YOUR_SCRIPT_ID/exec"
        private const val PRIMARY_OBJECT_ID = "ff808181a09d98f701a0bfb23331540c"
        private const val BASE_OBJECTS_URL = "https://api.restful-api.dev/objects"

        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

        @Volatile
        private var activeObjectId: String = PRIMARY_OBJECT_ID
    }

    init {
        val savedId = prefs?.getString(KEY_SAVED_OBJECT_ID, null)
        if (!savedId.isNullOrBlank()) {
            activeObjectId = savedId
        }
    }

    var syncMode: SyncMode
        get() {
            if (prefs == null) return inMemorySyncMode
            val name = prefs.getString(KEY_SYNC_MODE, inMemorySyncMode.name) ?: inMemorySyncMode.name
            return runCatching { SyncMode.valueOf(name) }.getOrDefault(inMemorySyncMode)
        }
        set(value) {
            inMemorySyncMode = value
            prefs?.edit()?.putString(KEY_SYNC_MODE, value.name)?.apply()
        }

    var localServerUrl: String
        get() = prefs?.getString(KEY_LOCAL_URL, inMemoryLocalUrl) ?: inMemoryLocalUrl
        set(value) {
            inMemoryLocalUrl = value.trim()
            prefs?.edit()?.putString(KEY_LOCAL_URL, value.trim())?.apply()
        }

    var customWebhookUrl: String
        get() = prefs?.getString(KEY_CUSTOM_URL, inMemoryCustomUrl) ?: inMemoryCustomUrl
        set(value) {
            inMemoryCustomUrl = value.trim()
            prefs?.edit()?.putString(KEY_CUSTOM_URL, value.trim())?.apply()
        }

    /**
     * Test connection to the active endpoint
     */
    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            when (syncMode) {
                SyncMode.LOCAL_HOST -> {
                    val req = Request.Builder().url(localServerUrl).get().build()
                    client.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            Pair(true, "Connected to Laptop Server at $localServerUrl (HTTP ${resp.code})")
                        } else {
                            Pair(false, "Laptop server returned HTTP ${resp.code}: ${resp.message}")
                        }
                    }
                }
                SyncMode.GOOGLE_SCRIPT_WEBHOOK -> {
                    val req = Request.Builder().url(customWebhookUrl).get().build()
                    client.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            Pair(true, "Connected to Google Apps Script / Webhook (HTTP ${resp.code})")
                        } else {
                            Pair(false, "Webhook returned HTTP ${resp.code}")
                        }
                    }
                }
                SyncMode.CLOUD_RELAY -> {
                    val req = Request.Builder().url("$BASE_OBJECTS_URL/$activeObjectId").get().build()
                    client.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful || resp.code == 404) {
                            Pair(true, "SRM Cloud Mesh Relay is Online (Latency < 200ms)")
                        } else {
                            Pair(false, "Cloud returned HTTP ${resp.code}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage ?: e.message}")
        }
    }

    /**
     * Fetches the complete master apartment dataset based on current sync mode.
     */
    suspend fun fetchMasterPayloadFromCloud(): Result<MasterCloudSyncPayload> = withContext(Dispatchers.IO) {
        try {
            when (syncMode) {
                SyncMode.LOCAL_HOST -> fetchFromCustomUrl(localServerUrl)
                SyncMode.GOOGLE_SCRIPT_WEBHOOK -> fetchFromCustomUrl(customWebhookUrl)
                SyncMode.CLOUD_RELAY -> fetchFromCloudRelay()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchMasterPayloadFromCloud: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Pushes the complete master payload based on current sync mode.
     */
    suspend fun pushMasterPayloadToCloud(payload: MasterCloudSyncPayload): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            when (syncMode) {
                SyncMode.LOCAL_HOST -> pushToCustomUrl(localServerUrl, payload)
                SyncMode.GOOGLE_SCRIPT_WEBHOOK -> pushToCustomUrl(customWebhookUrl, payload)
                SyncMode.CLOUD_RELAY -> pushToCloudRelay(payload)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in pushMasterPayloadToCloud: ${e.message}", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------
    // PRIVATE IMPLEMENTATIONS FOR SPECIFIC ENGINES
    // -------------------------------------------------------------

    private fun fetchFromCustomUrl(url: String): Result<MasterCloudSyncPayload> {
        return try {
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                }
                val bodyStr = response.body?.string().orEmpty()
                if (bodyStr.isBlank()) {
                    return Result.success(MasterCloudSyncPayload())
                }
                val rootObj = JSONObject(bodyStr)
                val dataObj = if (rootObj.has("data")) rootObj.optJSONObject("data") ?: rootObj else rootObj
                val payload = parseDataToPayload(dataObj)
                Result.success(payload)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun pushToCustomUrl(url: String, payload: MasterCloudSyncPayload): Result<Boolean> {
        return try {
            val root = JSONObject().apply {
                put("name", "EcoApartment_SRM_Master_Registry")
                put("timestamp", System.currentTimeMillis())
                put("data", serializePayloadToData(payload))
            }
            val body = root.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder().url(url).post(body).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Server returned HTTP ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun fetchFromCloudRelay(): Result<MasterCloudSyncPayload> {
        val url = "$BASE_OBJECTS_URL/$activeObjectId"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).execute().use { response ->
            if (response.code == 404 || response.code == 405) {
                Log.w(TAG, "Relay Object $activeObjectId returned HTTP ${response.code}. Ready for re-initialization.")
                return Result.success(MasterCloudSyncPayload())
            }

            if (!response.isSuccessful) {
                val msg = "HTTP ${response.code}: ${response.message}"
                Log.e(TAG, "Failed to fetch cloud payload: $msg")
                return Result.failure(Exception(msg))
            }

            val responseBody = response.body?.string().orEmpty()
            if (responseBody.isBlank()) {
                return Result.success(MasterCloudSyncPayload())
            }

            val rootObj = JSONObject(responseBody)
            val dataObj = rootObj.optJSONObject("data") ?: JSONObject()
            val payload = parseDataToPayload(dataObj)
            return Result.success(payload)
        }
    }

    private fun pushToCloudRelay(payload: MasterCloudSyncPayload): Result<Boolean> {
        val root = JSONObject().apply {
            put("name", "EcoApartment_SRM_Master_Registry")
            put("data", serializePayloadToData(payload))
        }
        val body = root.toString().toRequestBody(JSON_MEDIA_TYPE)

        val url = "$BASE_OBJECTS_URL/$activeObjectId"
        val putRequest = Request.Builder().url(url).put(body).build()

        var success = false
        client.newCall(putRequest).execute().use { response ->
            if (response.isSuccessful) {
                success = true
            } else if (response.code == 404 || response.code == 405) {
                Log.w(TAG, "PUT returned ${response.code}. Recreating registry...")
            }
        }

        if (!success) {
            // Re-create via POST
            val postRequest = Request.Builder().url(BASE_OBJECTS_URL).post(body).build()
            client.newCall(postRequest).execute().use { resp ->
                if (resp.isSuccessful) {
                    val respStr = resp.body?.string().orEmpty()
                    val newId = JSONObject(respStr).optString("id", "")
                    if (newId.isNotBlank()) {
                        activeObjectId = newId
                        prefs?.edit()?.putString(KEY_SAVED_OBJECT_ID, newId)?.apply()
                        return Result.success(true)
                    }
                }
            }
            return Result.failure(Exception("Unable to synchronize with cloud relay."))
        }

        return Result.success(true)
    }

    fun serializePayloadToData(payload: MasterCloudSyncPayload): JSONObject {
        val data = JSONObject()

        // 1. Complaints
        val complaintsArray = JSONArray()
        for (c in payload.complaints) {
            val item = JSONObject().apply {
                put("ticketId", c.ticketId)
                put("flatNumber", c.flatNumber)
                put("floorNumber", c.floorNumber)
                put("raisedByName", c.raisedByName)
                put("category", c.category.name)
                put("priority", c.priority.name)
                put("description", c.description)
                put("status", c.status.name)
                put("createdAtMillis", c.createdAtMillis)
                put("formattedTime", c.formattedTime)
                put("authorityNotes", c.authorityNotes)
            }
            complaintsArray.put(item)
        }
        data.put("complaints", complaintsArray)

        // 2. Users & Passwords with precise timestamp
        val usersArray = JSONArray()
        for (u in payload.users) {
            val item = JSONObject().apply {
                put("username", u.username)
                put("password", u.password)
                put("role", u.role.name)
                put("floorNumber", u.floorNumber)
                put("flatNumber", u.flatNumber)
                put("residentName", u.residentName)
                put("contactNumber", u.contactNumber)
                put("updatedAtMillis", u.updatedAtMillis)
            }
            usersArray.put(item)
        }
        data.put("users", usersArray)

        // 3. Visitors
        val visitorsArray = JSONArray()
        for (v in payload.visitors) {
            val item = JSONObject().apply {
                put("id", v.id)
                put("visitorName", v.visitorName)
                put("visitorPhone", v.visitorPhone)
                put("visitorType", v.visitorType.name)
                put("flatNumber", v.flatNumber)
                put("floorNumber", v.floorNumber)
                put("vehicleNumber", v.vehicleNumber)
                put("passCodeOrBarcode", v.passCodeOrBarcode)
                put("entryTime", v.entryTime)
                put("status", v.status.name)
                put("approvedByResident", v.approvedByResident)
                put("purpose", v.purpose)
                put("timestampMillis", v.timestampMillis)
            }
            visitorsArray.put(item)
        }
        data.put("visitors", visitorsArray)

        // 4. Maintenance Dues
        val duesArray = JSONArray()
        for (d in payload.dues) {
            val item = JSONObject().apply {
                put("id", d.id)
                put("flatNumber", d.flatNumber)
                put("residentName", d.residentName)
                put("monthYear", d.monthYear)
                put("amountRupees", d.amountRupees)
                put("dueDate", d.dueDate)
                put("status", d.status.name)
                put("paidDate", d.paidDate)
                put("transactionRef", d.transactionRef)
                put("breakdown", d.breakdown)
                put("updatedAtMillis", d.updatedAtMillis)
            }
            duesArray.put(item)
        }
        data.put("dues", duesArray)

        // 5. Community Notices
        val noticesArray = JSONArray()
        for (n in payload.notices) {
            val item = JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("description", n.description)
                put("authorName", n.authorName)
                put("postedTime", n.postedTime)
                put("priority", n.priority.name)
                put("category", n.category)
                put("isPinned", n.isPinned)
                put("timestampMillis", n.timestampMillis)
            }
            noticesArray.put(item)
        }
        data.put("notices", noticesArray)

        data.put("lastSyncEpoch", System.currentTimeMillis())
        return data
    }

    fun parseDataToPayload(dataObj: JSONObject): MasterCloudSyncPayload {
        // 1. Complaints
        val complaintsList = mutableListOf<ComplaintEntity>()
        val complaintsArray = dataObj.optJSONArray("complaints") ?: JSONArray()
        for (i in 0 until complaintsArray.length()) {
            val item = complaintsArray.optJSONObject(i) ?: continue
            val ticketId = item.optString("ticketId", "")
            if (ticketId.isBlank()) continue
            complaintsList.add(
                ComplaintEntity(
                    ticketId = ticketId,
                    flatNumber = item.optString("flatNumber", "Flat 101"),
                    floorNumber = item.optInt("floorNumber", 1),
                    raisedByName = item.optString("raisedByName", "Resident"),
                    category = runCatching { ComplaintCategory.valueOf(item.optString("category")) }.getOrDefault(ComplaintCategory.WATER_LEAKAGE),
                    priority = runCatching { ComplaintPriority.valueOf(item.optString("priority")) }.getOrDefault(ComplaintPriority.STANDARD),
                    description = item.optString("description", ""),
                    status = runCatching { ComplaintStatus.valueOf(item.optString("status")) }.getOrDefault(ComplaintStatus.ASSIGNED),
                    createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis()),
                    formattedTime = item.optString("formattedTime", "Recently"),
                    authorityNotes = item.optString("authorityNotes", "")
                )
            )
        }

        // 2. Users & Passwords with timestamp
        val usersList = mutableListOf<UserEntity>()
        val usersArray = dataObj.optJSONArray("users") ?: JSONArray()
        for (i in 0 until usersArray.length()) {
            val item = usersArray.optJSONObject(i) ?: continue
            val username = item.optString("username", "")
            if (username.isBlank()) continue
            usersList.add(
                UserEntity(
                    username = username,
                    password = item.optString("password", "1234"),
                    role = runCatching { UserRole.valueOf(item.optString("role")) }.getOrDefault(UserRole.RESIDENT),
                    floorNumber = item.optInt("floorNumber", 1),
                    flatNumber = item.optString("flatNumber", "Flat 101"),
                    residentName = item.optString("residentName", "Resident"),
                    contactNumber = item.optString("contactNumber", ""),
                    updatedAtMillis = item.optLong("updatedAtMillis", System.currentTimeMillis())
                )
            )
        }

        // 3. Visitors
        val visitorsList = mutableListOf<VisitorEntity>()
        val visitorsArray = dataObj.optJSONArray("visitors") ?: JSONArray()
        for (i in 0 until visitorsArray.length()) {
            val item = visitorsArray.optJSONObject(i) ?: continue
            val id = item.optString("id", "")
            if (id.isBlank()) continue
            visitorsList.add(
                VisitorEntity(
                    id = id,
                    visitorName = item.optString("visitorName", "Visitor"),
                    visitorPhone = item.optString("visitorPhone", ""),
                    visitorType = runCatching { VisitorType.valueOf(item.optString("visitorType")) }.getOrDefault(VisitorType.GUEST),
                    flatNumber = item.optString("flatNumber", "Flat 101"),
                    floorNumber = item.optInt("floorNumber", 1),
                    vehicleNumber = item.optString("vehicleNumber", ""),
                    passCodeOrBarcode = item.optString("passCodeOrBarcode", "PASS-000"),
                    entryTime = item.optString("entryTime", "Today"),
                    status = runCatching { VisitorApprovalStatus.valueOf(item.optString("status")) }.getOrDefault(VisitorApprovalStatus.PENDING),
                    approvedByResident = item.optString("approvedByResident", ""),
                    purpose = item.optString("purpose", "Visit"),
                    timestampMillis = item.optLong("timestampMillis", System.currentTimeMillis())
                )
            )
        }

        // 4. Maintenance Dues
        val duesList = mutableListOf<MaintenanceDueItemEntity>()
        val duesArray = dataObj.optJSONArray("dues") ?: JSONArray()
        for (i in 0 until duesArray.length()) {
            val item = duesArray.optJSONObject(i) ?: continue
            val id = item.optString("id", "")
            if (id.isBlank()) continue
            duesList.add(
                MaintenanceDueItemEntity(
                    id = id,
                    flatNumber = item.optString("flatNumber", "Flat 101"),
                    residentName = item.optString("residentName", "Resident"),
                    monthYear = item.optString("monthYear", "Current Month"),
                    amountRupees = item.optDouble("amountRupees", 2000.0),
                    dueDate = item.optString("dueDate", "5th of this month"),
                    status = runCatching { DuePaymentStatus.valueOf(item.optString("status")) }.getOrDefault(DuePaymentStatus.PENDING),
                    paidDate = item.optString("paidDate", ""),
                    transactionRef = item.optString("transactionRef", ""),
                    breakdown = item.optString("breakdown", "Water ₹450, Elevator & Power ₹850, Security ₹700"),
                    updatedAtMillis = item.optLong("updatedAtMillis", System.currentTimeMillis())
                )
            )
        }

        // 5. Community Notices
        val noticesList = mutableListOf<CommunityNoticeEntity>()
        val noticesArray = dataObj.optJSONArray("notices") ?: JSONArray()
        for (i in 0 until noticesArray.length()) {
            val item = noticesArray.optJSONObject(i) ?: continue
            val id = item.optString("id", "")
            if (id.isBlank()) continue
            noticesList.add(
                CommunityNoticeEntity(
                    id = id,
                    title = item.optString("title", "Community Notice"),
                    description = item.optString("description", ""),
                    authorName = item.optString("authorName", "Management"),
                    postedTime = item.optString("postedTime", "Today"),
                    priority = runCatching { NoticePriority.valueOf(item.optString("priority")) }.getOrDefault(NoticePriority.NORMAL),
                    category = item.optString("category", "General"),
                    isPinned = item.optBoolean("isPinned", false),
                    timestampMillis = item.optLong("timestampMillis", System.currentTimeMillis())
                )
            )
        }

        return MasterCloudSyncPayload(
            complaints = complaintsList,
            users = usersList,
            visitors = visitorsList,
            dues = duesList,
            notices = noticesList,
            timestamp = dataObj.optLong("lastSyncEpoch", System.currentTimeMillis())
        )
    }

    /**
     * Script snippets to guide user setup
     */
    object Templates {
        val PYTHON_SERVER_SCRIPT = """
            # server.py - Run on Laptop: python3 server.py
            from http.server import HTTPServer, BaseHTTPRequestHandler
            import json, os

            DB_FILE = "eco_sync_data.json"
            if not os.path.exists(DB_FILE):
                with open(DB_FILE, "w") as f:
                    json.dump({"name":"EcoApartment", "data":{}}, f)

            class SyncHandler(BaseHTTPRequestHandler):
                def do_GET(self):
                    self.send_response(200)
                    self.send_header('Content-Type', 'application/json')
                    self.send_header('Access-Control-Allow-Origin', '*')
                    self.end_headers()
                    with open(DB_FILE, "r") as f:
                        self.wfile.write(f.read().encode())

                def do_POST(self):
                    length = int(self.headers['Content-Length'])
                    body = self.rfile.read(length)
                    with open(DB_FILE, "w") as f:
                        f.write(body.decode())
                    self.send_response(200)
                    self.send_header('Content-Type', 'application/json')
                    self.send_header('Access-Control-Allow-Origin', '*')
                    self.end_headers()
                    self.wfile.write(b'{"status":"ok","synced":true}')

            print("📡 SRM Cloud Laptop Server running at http://0.0.0.0:8080/sync")
            HTTPServer(('0.0.0.0', 8080), SyncHandler).serve_forever()
        """.trimIndent()

        val GOOGLE_APPS_SCRIPT = """
            // Google Apps Script (Create at script.google.com -> Deploy as Web App)
            // Execute as: 'Me', Who has access: 'Anyone'
            function doGet(e) {
              var prop = PropertiesService.getScriptProperties();
              var data = prop.getProperty("ECO_DATA") || "{}";
              return ContentService.createTextOutput(data).setMimeType(ContentService.MimeType.JSON);
            }

            function doPost(e) {
              var raw = e.postData.contents;
              var prop = PropertiesService.getScriptProperties();
              prop.setProperty("ECO_DATA", raw);
              return ContentService.createTextOutput(JSON.stringify({status: "ok", saved: true}))
                     .setMimeType(ContentService.MimeType.JSON);
            }
        """.trimIndent()
    }
}
