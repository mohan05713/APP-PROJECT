package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_history")
data class LoginHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val role: String, // "AUTHORITY" or "RESIDENT"
    val displayName: String,
    val flatNumber: String,
    val floorNumber: Int,
    val timestampMillis: Long,
    val formattedTime: String
)
