package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LoginHistoryDao {
    @Query("SELECT * FROM login_history ORDER BY timestampMillis DESC")
    fun getAllHistory(): Flow<List<LoginHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: LoginHistoryEntity)

    @Query("DELETE FROM login_history")
    suspend fun clearHistory()

    @Query("SELECT COUNT(*) FROM login_history")
    suspend fun getCount(): Int
}
