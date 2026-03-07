package com.detox.detox_droid.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.detox.detox_droid.data.local.room.dao.BlockedAppDao
import com.detox.detox_droid.data.local.room.dao.DetoxScheduleDao
import com.detox.detox_droid.data.local.room.entity.BlockedAppEntity
import com.detox.detox_droid.data.local.room.entity.DetoxScheduleEntity

@Database(
    entities = [BlockedAppEntity::class, DetoxScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract val blockedAppDao: BlockedAppDao
    abstract val detoxScheduleDao: DetoxScheduleDao

    companion object {
        const val DATABASE_NAME = "detox_droid_db"
    }
}
