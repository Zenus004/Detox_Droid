package com.detox.detox_droid.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detox_schedules")
data class DetoxScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTimeInMillis: Long,
    val endTimeInMillis: Long,
    val daysOfWeek: String, // Comma separated days
    val isActive: Boolean = true
)
