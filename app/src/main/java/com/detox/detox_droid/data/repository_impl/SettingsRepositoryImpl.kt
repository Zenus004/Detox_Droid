package com.detox.detox_droid.data.repository_impl

import com.detox.detox_droid.data.local.datastore.SecureSettingsManager
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val secureSettingsManager: SecureSettingsManager
) : SettingsRepository {

    override val dailyPauseLimit: Flow<Int>
        get() = secureSettingsManager.dailyPauseLimit

    override val isDetoxModeActive: Flow<Boolean>
        get() = secureSettingsManager.isDetoxModeActive

    override val detoxSessionEndTime: Flow<Long>
        get() = secureSettingsManager.detoxSessionEndTime

    override val isScheduledSession: Flow<Boolean>
        get() = secureSettingsManager.isScheduledSession

    override val pauseEndTime: Flow<Long>
        get() = secureSettingsManager.pauseEndTime

    override suspend fun updateDailyPauseLimit(limit: Int) {
        secureSettingsManager.updateDailyPauseLimit(limit)
    }

    override suspend fun setDetoxModeActive(isActive: Boolean) {
        secureSettingsManager.setDetoxModeActive(isActive)
    }

    override suspend fun setDetoxSessionEndTime(endTimeInMillis: Long) {
        secureSettingsManager.setDetoxSessionEndTime(endTimeInMillis)
    }

    override suspend fun setIsScheduledSession(isScheduled: Boolean) {
        secureSettingsManager.setIsScheduledSession(isScheduled)
    }

    override suspend fun setPauseEndTime(endTimeInMillis: Long) {
        secureSettingsManager.setPauseEndTime(endTimeInMillis)
    }

    override suspend fun incrementPauseCount() {
        secureSettingsManager.incrementPauseCount()
    }

    override suspend fun getDetoxSessionEndTime(): Long {
        return secureSettingsManager.getDetoxSessionEndTime()
    }

    override suspend fun getDailyPausesRemaining(): Int {
        return secureSettingsManager.getDailyPausesRemaining()
    }
}
