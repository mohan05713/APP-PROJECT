package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.VisitorApprovalStatus
import com.example.data.model.VisitorType

@Entity(tableName = "visitor_logs")
data class VisitorEntity(
    @PrimaryKey
    val id: String, // e.g. "VIS-172688000"
    val visitorName: String,
    val visitorPhone: String,
    val visitorType: VisitorType,
    val flatNumber: String,
    val floorNumber: Int,
    val vehicleNumber: String = "",
    val passCodeOrBarcode: String, // e.g. "SRM-402-9821"
    val entryTime: String,
    val status: VisitorApprovalStatus,
    val approvedByResident: String = "",
    val purpose: String = "Delivery / Guest Visit",
    val timestampMillis: Long = System.currentTimeMillis()
)
