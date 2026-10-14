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
import com.example.data.model.DuePaymentStatus
import com.example.data.model.NoticePriority
import com.example.data.model.UserRole
import com.example.data.model.VisitorApprovalStatus
import com.example.data.model.VisitorType

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

    @TypeConverter
    fun fromRole(value: UserRole): String = value.name

    @TypeConverter
    fun toRole(value: String): UserRole =
        try { UserRole.valueOf(value) } catch (e: Exception) { UserRole.RESIDENT }

    @TypeConverter
    fun fromVisitorType(value: VisitorType): String = value.name

    @TypeConverter
    fun toVisitorType(value: String): VisitorType =
        try { VisitorType.valueOf(value) } catch (e: Exception) { VisitorType.GUEST }

    @TypeConverter
    fun fromVisitorStatus(value: VisitorApprovalStatus): String = value.name

    @TypeConverter
    fun toVisitorStatus(value: String): VisitorApprovalStatus =
        try { VisitorApprovalStatus.valueOf(value) } catch (e: Exception) { VisitorApprovalStatus.PENDING }

    @TypeConverter
    fun fromDueStatus(value: DuePaymentStatus): String = value.name

    @TypeConverter
    fun toDueStatus(value: String): DuePaymentStatus =
        try { DuePaymentStatus.valueOf(value) } catch (e: Exception) { DuePaymentStatus.PENDING }

    @TypeConverter
    fun fromNoticePriority(value: NoticePriority): String = value.name

    @TypeConverter
    fun toNoticePriority(value: String): NoticePriority =
        try { NoticePriority.valueOf(value) } catch (e: Exception) { NoticePriority.NORMAL }
}

@Database(
    entities = [
        ComplaintEntity::class,
        UserEntity::class,
        LoginHistoryEntity::class,
        VisitorEntity::class,
        MaintenanceDueItemEntity::class,
        CommunityNoticeEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun complaintDao(): ComplaintDao
    abstract fun userDao(): UserDao
    abstract fun loginHistoryDao(): LoginHistoryDao
    abstract fun visitorDao(): VisitorDao
    abstract fun maintenanceDueDao(): MaintenanceDueDao
    abstract fun communityNoticeDao(): CommunityNoticeDao

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
