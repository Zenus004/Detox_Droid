package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.data.services.DndHelper
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import javax.inject.Inject

class StopDetoxSessionUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dndHelper: DndHelper
) {
    suspend operator fun invoke() {
        settingsRepository.setDetoxSessionEndTime(0L)
        settingsRepository.setDetoxModeActive(false)

        dndHelper.disableDnd()
    }
}
