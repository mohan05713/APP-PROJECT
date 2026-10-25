package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY floorNumber ASC, flatNumber ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(flatNumber) = LOWER(:flatNumber) LIMIT 1")
    suspend fun getUserByFlat(flatNumber: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(residentName) = LOWER(:name) LIMIT 1")
    suspend fun getUserByResidentName(name: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsersList(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET password = :newPassword, updatedAtMillis = :updatedAt WHERE username = :username")
    suspend fun updatePassword(username: String, newPassword: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getCount(): Int
}
