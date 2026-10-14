package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DuePaymentStatus

@Entity(tableName = "maintenance_dues")
data class MaintenanceDueItemEntity(
    @PrimaryKey
    val id: String, // e.g. "DUE-2026-09-FLAT101"
    val flatNumber: String,
    val residentName: String,
    val monthYear: String, // e.g. "September 2026"
    val amountRupees: Double, // e.g. 2000.0
    val dueDate: String, // e.g. "Oct 05, 2026"
    val status: DuePaymentStatus,
    val paidDate: String = "",
    val transactionRef: String = "",
    val breakdown: String = "Water ₹450, Elevator & Power ₹850, Security ₹700",
    val updatedAtMillis: Long = System.currentTimeMillis()
)
