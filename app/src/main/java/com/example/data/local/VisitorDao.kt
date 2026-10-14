package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VisitorApprovalStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitorDao {
    @Query("SELECT * FROM visitor_logs ORDER BY timestampMillis DESC")
    fun getAllVisitors(): Flow<List<VisitorEntity>>

    @Query("SELECT * FROM visitor_logs ORDER BY timestampMillis DESC")
    suspend fun getAllVisitorsSync(): List<VisitorEntity>

    @Query("SELECT * FROM visitor_logs WHERE LOWER(flatNumber) = LOWER(:flatNumber) ORDER BY timestampMillis DESC")
    fun getVisitorsForFlat(flatNumber: String): Flow<List<VisitorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitor(visitor: VisitorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitors(visitors: List<VisitorEntity>)

    @Update
    suspend fun updateVisitor(visitor: VisitorEntity)

    @Query("UPDATE visitor_logs SET status = :status, approvedByResident = :approvedBy WHERE id = :id")
    suspend fun updateVisitorStatus(id: String, status: VisitorApprovalStatus, approvedBy: String)

    @Query("DELETE FROM visitor_logs WHERE id = :id")
    suspend fun deleteVisitor(id: String)
}
