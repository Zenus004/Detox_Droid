package com.detox.detox_droid.domain.repository_interfaces

import com.detox.detox_droid.data.local.room.entity.DetoxScheduleEntity
import kotlinx.coroutines.flow.Flow

interface DetoxScheduleRepository {
    fun getAllSchedules(): Flow<List<DetoxScheduleEntity>>
    suspend fun insertSchedule(schedule: DetoxScheduleEntity)
    suspend fun updateSchedule(schedule: DetoxScheduleEntity)
    suspend fun deleteSchedule(schedule: DetoxScheduleEntity)
}
