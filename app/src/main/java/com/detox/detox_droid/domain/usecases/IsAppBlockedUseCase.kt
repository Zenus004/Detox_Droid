package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.domain.repository_interfaces.BlockedAppRepository
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Checks whether a given app package should currently be blocked.
 *
 * Logic chain:
 *  1. If detox mode is not active → never block.
 *  2. If an emergency pause is currently active → never block.
 *  3. If the package is in the blocked list (isBlocked=true, isWhitelisted=false) → block.
 */
@Singleton
class IsAppBlockedUseCase @Inject constructor(
    private val blockedAppRepository: BlockedAppRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(packageName: String): Boolean {
        // 1. Emergency pause overrides blocking
        val pauseEndTime = settingsRepository.pauseEndTime.first()
        if (System.currentTimeMillis() < pauseEndTime) return false

        // 2. Check Room for this app
        val blockedApp = blockedAppRepository.getBlockedAppByPackage(packageName)
        return blockedApp != null && blockedApp.isBlocked && !blockedApp.isWhitelisted
    }
}
