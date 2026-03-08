package com.detox.detox_droid.data.repository_impl

import com.detox.detox_droid.data.local.room.dao.DetoxScheduleDao
import com.detox.detox_droid.data.local.room.entity.DetoxScheduleEntity
import com.detox.detox_droid.domain.repository_interfaces.DetoxScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetoxScheduleRepositoryImpl @Inject constructor(
    private val detoxScheduleDao: DetoxScheduleDao
) : DetoxScheduleRepository {

    override fun getAllSchedules(): Flow<List<DetoxScheduleEntity>> {
        return detoxScheduleDao.getAllSchedules()
    }

    override suspend fun insertSchedule(schedule: DetoxScheduleEntity) {
        detoxScheduleDao.insertSchedule(schedule)
    }

    override suspend fun updateSchedule(schedule: DetoxScheduleEntity) {
        detoxScheduleDao.updateSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: DetoxScheduleEntity) {
        detoxScheduleDao.deleteSchedule(schedule)
    }
}
