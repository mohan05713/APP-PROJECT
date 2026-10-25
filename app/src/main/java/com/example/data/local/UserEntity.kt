package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val username: String, // e.g. "flat_402", "flat_101", "authority"
    val password: String,
    val role: UserRole,
    val floorNumber: Int,
    val flatNumber: String,
    val residentName: String,
    val contactNumber: String,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

