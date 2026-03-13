package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.data.services.DndHelper
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import javax.inject.Inject

class PauseDetoxSessionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dndHelper: DndHelper
) {
    suspend operator fun invoke(durationInMillis: Long): Boolean {
        val remainingPauses = settingsRepository.getDailyPausesRemaining()
        if (remainingPauses > 0) {
            settingsRepository.incrementPauseCount()
            val newPauseEndTime = System.currentTimeMillis() + durationInMillis
            settingsRepository.setPauseEndTime(newPauseEndTime)

            // Disable DND for the duration of the pause
            dndHelper.disableDnd()

            // Extend the overall session end time by the pause duration
            // (Only if it's a fixed-time session, not an indefinite sentinel 9223372036854775807L / 2)
            val currentEndTime = settingsRepository.getDetoxSessionEndTime()
            val indefiniteSentinel = Long.MAX_VALUE / 2L
            if (currentEndTime in 1L until indefiniteSentinel) {
                settingsRepository.setDetoxSessionEndTime(currentEndTime + durationInMillis)
            }

            return true
        }
        return false
    }
}
