package com.detox.detox_droid.presentation.viewmodels

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.data.local.room.entity.BlockedAppEntity
import com.detox.detox_droid.domain.repository_interfaces.BlockedAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AppInfoUiModel(
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean,
    val isWhitelisted: Boolean = false,
    val isSystemApp: Boolean = false
)

data class AppBlockingState(
    val apps: List<AppInfoUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    // Default TRUE so all apps are visible immediately without needing the toggle
    val showSystemApps: Boolean = true
) {
    val filteredApps: List<AppInfoUiModel>
        get() {
            val visibleApps = if (showSystemApps) apps else apps.filter { !it.isSystemApp }
            return if (searchQuery.isBlank()) visibleApps
                   else visibleApps.filter { it.appName.contains(searchQuery, ignoreCase = true) }
        }

    val blockedCount: Int get() = apps.count { it.isBlocked }
}

@HiltViewModel
class AppBlockingConfigViewModel @Inject constructor(
    private val application: Application,
    private val blockedAppRepository: BlockedAppRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AppBlockingState())
    val uiState: StateFlow<AppBlockingState> = _uiState.asStateFlow()

    init {
        observeBlockedApps()
    }

    /**
     * Queries the PackageManager for ALL apps that appear in the device launcher —
     * using queryIntentActivities with ACTION_MAIN + CATEGORY_LAUNCHER.
     *
     * This is the same method Android launchers use to populate the app grid.
     * It correctly includes all default apps (Phone, Messages, Camera, Gallery, etc.)
     * that `getLaunchIntentForPackage()` can miss on some OEMs.
     */
    private fun observeBlockedApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val installedApps = withContext(Dispatchers.IO) {
                val pm = application.packageManager

                // --- Approach 1: queryIntentActivities (reliable on most devices) ---
                val launcherIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                    addCategory(android.content.Intent.CATEGORY_LAUNCHER)
                }
                val fromLauncher = pm.queryIntentActivities(launcherIntent, PackageManager.GET_META_DATA)
                    .map { it.activityInfo.packageName }
                    .toSet()

                // --- Approach 2: getInstalledApplications (catches any app missed above) ---
                // With QUERY_ALL_PACKAGES permission this returns ALL packages.
                // Without it (old APK still installed) it returns only system packages.
                // We union both sets so nothing is missed.
                val fromInstalled = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                    .map { it.packageName }
                    .toSet()

                // Union of both approaches, minus DetoxDroid itself
                val allPackages = (fromLauncher + fromInstalled) - application.packageName

                allPackages.mapNotNull { pkg ->
                    runCatching {
                        val info = pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA)
                        val appName = pm.getApplicationLabel(info).toString()
                        val isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        Triple(pkg, appName, isSystemApp)
                    }.getOrNull()
                }.sortedWith(
                    // User-installed apps first, then system apps, both alphabetical
                    compareBy({ it.third }, { it.second.lowercase() })
                )
            }

            // Subscribe to the Room Flow — any DB change re-emits automatically
            blockedAppRepository.getAllBlockedApps().collectLatest { blockedEntities ->
                val blockedMap = blockedEntities.associateBy { it.packageName }
                val uiModels = installedApps.map { (packageName, appName, isSystem) ->
                    val entity = blockedMap[packageName]
                    AppInfoUiModel(
                        packageName = packageName,
                        appName = appName,
                        isBlocked = entity?.isBlocked == true,
                        isWhitelisted = entity?.isWhitelisted == true,
                        isSystemApp = isSystem
                    )
                }
                _uiState.update { it.copy(apps = uiModels, isLoading = false) }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleShowSystemApps(show: Boolean) {
        _uiState.update { it.copy(showSystemApps = show) }
    }

    fun toggleAppBlockedState(appUiModel: AppInfoUiModel, block: Boolean) {
        viewModelScope.launch {
            if (block) {
                blockedAppRepository.insertBlockedApp(
                    BlockedAppEntity(
                        packageName = appUiModel.packageName,
                        appName = appUiModel.appName,
                        isBlocked = true,
                        isWhitelisted = false
                    )
                )
            } else {
                blockedAppRepository.deleteBlockedApp(appUiModel.packageName)
            }
        }
    }

}
