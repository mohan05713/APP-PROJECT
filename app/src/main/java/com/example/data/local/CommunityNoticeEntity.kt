package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.NoticePriority

@Entity(tableName = "community_notices")
data class CommunityNoticeEntity(
    @PrimaryKey
    val id: String, // e.g. "NOTICE-172688000"
    val title: String,
    val description: String,
    val authorName: String, // e.g. "Facility Manager", "Resident Secretary"
    val postedTime: String,
    val priority: NoticePriority,
    val category: String, // "Water", "Power", "Event", "Security", "General"
    val isPinned: Boolean = false,
    val timestampMillis: Long = System.currentTimeMillis()
)
