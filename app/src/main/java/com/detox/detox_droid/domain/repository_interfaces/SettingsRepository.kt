package com.detox.detox_droid.domain.repository_interfaces

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val dailyPauseLimit: Flow<Int>
    val isDetoxModeActive: Flow<Boolean>
    val detoxSessionEndTime: Flow<Long>
    val isScheduledSession: Flow<Boolean>
    val pauseEndTime: Flow<Long>

    suspend fun updateDailyPauseLimit(limit: Int)
    suspend fun setDetoxModeActive(isActive: Boolean)
    suspend fun setDetoxSessionEndTime(endTimeInMillis: Long)
    suspend fun setIsScheduledSession(isScheduled: Boolean)
    suspend fun setPauseEndTime(endTimeInMillis: Long)
    suspend fun incrementPauseCount()
    suspend fun getDetoxSessionEndTime(): Long
    suspend fun getDailyPausesRemaining(): Int
}
