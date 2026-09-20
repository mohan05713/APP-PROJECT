package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.ComplaintCategory
import com.example.data.model.ComplaintPriority
import com.example.data.model.ComplaintStatus

class Converters {
    @TypeConverter
    fun fromCategory(value: ComplaintCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): ComplaintCategory =
        try { ComplaintCategory.valueOf(value) } catch (e: Exception) { ComplaintCategory.WATER_LEAKAGE }

    @TypeConverter
    fun fromPriority(value: ComplaintPriority): String = value.name

    @TypeConverter
    fun toPriority(value: String): ComplaintPriority =
        try { ComplaintPriority.valueOf(value) } catch (e: Exception) { ComplaintPriority.STANDARD }

    @TypeConverter
    fun fromStatus(value: ComplaintStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): ComplaintStatus =
        try { ComplaintStatus.valueOf(value) } catch (e: Exception) { ComplaintStatus.ASSIGNED }
}

@Database(entities = [ComplaintEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun complaintDao(): ComplaintDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "eco_apartment_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
