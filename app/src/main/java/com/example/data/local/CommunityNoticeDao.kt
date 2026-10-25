package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityNoticeDao {
    @Query("SELECT * FROM community_notices ORDER BY isPinned DESC, timestampMillis DESC")
    fun getAllNotices(): Flow<List<CommunityNoticeEntity>>

    @Query("SELECT * FROM community_notices ORDER BY isPinned DESC, timestampMillis DESC")
    suspend fun getAllNoticesSync(): List<CommunityNoticeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: CommunityNoticeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<CommunityNoticeEntity>)

    @Update
    suspend fun updateNotice(notice: CommunityNoticeEntity)

    @Query("DELETE FROM community_notices WHERE id = :id")
    suspend fun deleteNotice(id: String)
}
