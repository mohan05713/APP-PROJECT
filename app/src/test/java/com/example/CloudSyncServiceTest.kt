package com.example

import com.example.data.local.ComplaintEntity
import com.example.data.local.UserEntity
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus
import com.example.data.model.UserRole
import com.example.data.remote.CloudSyncService
import com.example.data.remote.MasterCloudSyncPayload
import com.example.data.remote.SyncMode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CloudSyncServiceTest {

    @Test
    fun testCloudFetchAndMergeMasterPayload() = runBlocking {
        val service = CloudSyncService()
        val sample = ComplaintEntity(
            ticketId = "#ECO-TEST-99",
            flatNumber = "Flat 101",
            floorNumber = 1,
            raisedByName = "Test Resident",
            category = ComplaintCategory.WATER_LEAKAGE,
            priority = ComplaintPriority.STANDARD,
            description = "Test cloud sync pipeline",
            status = ComplaintStatus.ASSIGNED,
            createdAtMillis = System.currentTimeMillis(),
            formattedTime = "Today, 12:00 PM",
            authorityNotes = "Test notes"
        )
        val initialPayload = MasterCloudSyncPayload(complaints = listOf(sample))
        val pushResult = service.pushMasterPayloadToCloud(initialPayload)
        assertTrue("Cloud push should succeed", pushResult.isSuccess)

        val fetchResult = service.fetchMasterPayloadFromCloud()
        assertTrue("Cloud fetch should succeed or handle gracefully", fetchResult.isSuccess)
        val cloudPayload = fetchResult.getOrNull()
        assertNotNull(cloudPayload)
        assertTrue("Merged complaints list should contain tickets", cloudPayload!!.complaints.isNotEmpty())
    }

    @Test
    fun testSyncModeSwitching() {
        val service = CloudSyncService()
        assertEquals(SyncMode.CLOUD_RELAY, service.syncMode)

        service.syncMode = SyncMode.LOCAL_HOST
        service.localServerUrl = "http://192.168.1.100:8080/sync"
        assertEquals(SyncMode.LOCAL_HOST, service.syncMode)
        assertEquals("http://192.168.1.100:8080/sync", service.localServerUrl)

        service.syncMode = SyncMode.GOOGLE_SCRIPT_WEBHOOK
        service.customWebhookUrl = "https://script.google.com/macros/s/AKfycb/exec"
        assertEquals(SyncMode.GOOGLE_SCRIPT_WEBHOOK, service.syncMode)
        assertEquals("https://script.google.com/macros/s/AKfycb/exec", service.customWebhookUrl)
    }

    @Test
    fun testMasterPayloadPushAndFetch() = runBlocking {
        val service = CloudSyncService()
        val user = UserEntity(
            username = "9876543210",
            password = "newSecretPassword123",
            role = UserRole.RESIDENT,
            floorNumber = 2,
            flatNumber = "Flat 201",
            residentName = "Test Kumar",
            contactNumber = "9876543210",
            updatedAtMillis = System.currentTimeMillis()
        )
        val payload = MasterCloudSyncPayload(
            users = listOf(user)
        )
        val pushResult = service.pushMasterPayloadToCloud(payload)
        assertTrue("Master payload push should succeed", pushResult.isSuccess)

        val fetchResult = service.fetchMasterPayloadFromCloud()
        assertTrue("Master payload fetch should succeed", fetchResult.isSuccess)
        val fetchedPayload = fetchResult.getOrNull()
        assertNotNull(fetchedPayload)
        val syncedUser = fetchedPayload?.users?.find { it.username == "9876543210" }
        assertNotNull(syncedUser)
        assertEquals("newSecretPassword123", syncedUser?.password)
    }
}


