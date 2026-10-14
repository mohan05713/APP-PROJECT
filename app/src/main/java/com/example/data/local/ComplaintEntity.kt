package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey
    val ticketId: String,
    val flatNumber: String,
    val floorNumber: Int = 4,
    val raisedByName: String = "Resident",
    val category: ComplaintCategory,
    val priority: ComplaintPriority,
    val description: String,
    val status: ComplaintStatus,
    val createdAtMillis: Long,
    val formattedTime: String,
    val authorityNotes: String = ""
)
