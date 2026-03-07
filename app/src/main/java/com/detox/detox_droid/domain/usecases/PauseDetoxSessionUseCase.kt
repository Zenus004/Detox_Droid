package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import javax.inject.Inject

class PauseDetoxSessionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(durationInMillis: Long): Boolean {
        val remainingPauses = settingsRepository.getDailyPausesRemaining()
        if (remainingPauses > 0) {
            settingsRepository.incrementPauseCount()
            val newPauseEndTime = System.currentTimeMillis() + durationInMillis
            settingsRepository.setPauseEndTime(newPauseEndTime)
            return true
        }
        return false
    }
}
