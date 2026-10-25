package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ComplaintStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAtMillis DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE LOWER(flatNumber) = LOWER(:flatNumber) ORDER BY createdAtMillis DESC")
    fun getComplaintsByFlat(flatNumber: String): Flow<List<ComplaintEntity>>

    @Query("UPDATE complaints SET status = :status, authorityNotes = :notes WHERE ticketId = :ticketId")
    suspend fun updateStatusAndNotes(ticketId: String, status: ComplaintStatus, notes: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaints(complaints: List<ComplaintEntity>)

    @Query("SELECT * FROM complaints ORDER BY createdAtMillis DESC")
    suspend fun getAllComplaintsSync(): List<ComplaintEntity>

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)

    @Query("SELECT COUNT(*) FROM complaints")
    suspend fun getCount(): Int
}
