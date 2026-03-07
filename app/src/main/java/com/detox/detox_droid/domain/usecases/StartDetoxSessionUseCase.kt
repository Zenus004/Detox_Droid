package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.data.services.DndHelper
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import javax.inject.Inject

class StartDetoxSessionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dndHelper: DndHelper
) {
    suspend operator fun invoke(durationInMillis: Long) {
        val endTime = System.currentTimeMillis() + durationInMillis
        settingsRepository.setDetoxSessionEndTime(endTime)
        settingsRepository.setDetoxModeActive(true)

        dndHelper.enableDnd()
    }
}
