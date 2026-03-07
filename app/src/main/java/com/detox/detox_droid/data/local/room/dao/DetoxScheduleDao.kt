package com.detox.detox_droid.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.detox.detox_droid.data.local.room.entity.DetoxScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetoxScheduleDao {

    @Query("SELECT * FROM detox_schedules")
    fun getAllSchedules(): Flow<List<DetoxScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: DetoxScheduleEntity)

    @Update
    suspend fun updateSchedule(schedule: DetoxScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: DetoxScheduleEntity)
}
