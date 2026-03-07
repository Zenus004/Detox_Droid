package com.detox.detox_droid.domain.repository_interfaces

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val dailyPauseLimit: Flow<Int>
    val isDetoxModeActive: Flow<Boolean>
    val detoxSessionEndTime: Flow<Long>
    val pauseEndTime: Flow<Long>

    suspend fun updateDailyPauseLimit(limit: Int)
    suspend fun setDetoxModeActive(isActive: Boolean)
    suspend fun setDetoxSessionEndTime(endTimeInMillis: Long)
    suspend fun setPauseEndTime(endTimeInMillis: Long)
    suspend fun incrementPauseCount()
    suspend fun getDailyPausesRemaining(): Int
}
