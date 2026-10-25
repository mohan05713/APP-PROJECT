package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DuePaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDueDao {
    @Query("SELECT * FROM maintenance_dues ORDER BY flatNumber ASC")
    fun getAllDues(): Flow<List<MaintenanceDueItemEntity>>

    @Query("SELECT * FROM maintenance_dues ORDER BY flatNumber ASC")
    suspend fun getAllDuesSync(): List<MaintenanceDueItemEntity>

    @Query("SELECT * FROM maintenance_dues WHERE LOWER(flatNumber) = LOWER(:flatNumber)")
    fun getDuesForFlat(flatNumber: String): Flow<List<MaintenanceDueItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDue(due: MaintenanceDueItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDues(dues: List<MaintenanceDueItemEntity>)

    @Update
    suspend fun updateDue(due: MaintenanceDueItemEntity)

    @Query("UPDATE maintenance_dues SET status = :status, paidDate = :paidDate, transactionRef = :txnRef, updatedAtMillis = :updatedAt WHERE id = :id")
    suspend fun markDueAsPaid(id: String, status: DuePaymentStatus, paidDate: String, txnRef: String, updatedAt: Long = System.currentTimeMillis())
}
