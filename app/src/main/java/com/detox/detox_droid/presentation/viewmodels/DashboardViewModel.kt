package com.detox.detox_droid.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.data.services.UsageStatsHelper
import com.detox.detox_droid.domain.models.AppUsage
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import com.detox.detox_droid.domain.usecases.FetchDailyUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isDetoxActive: Boolean = false,
    val topUsedApps: List<AppUsage> = emptyList(),
    val totalScreenTimeMs: Long = 0L,
    val hasUsagePermission: Boolean = false,
    val hasAccessibilityPermission: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchDailyUsageUseCase: FetchDailyUsageUseCase,
    private val usageStatsHelper: UsageStatsHelper,
    private val settingsRepository: SettingsRepository,
    private val application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        checkPermissionsAndFetchData()

        // Reactively update focus mode indicator so the status card reflects live state
        viewModelScope.launch {
            settingsRepository.isDetoxModeActive.collectLatest { isActive ->
                _uiState.update { it.copy(isDetoxActive = isActive) }

                // Refresh usage data when session ends so screen time is up-to-date
                if (!isActive && _uiState.value.hasUsagePermission) {
                    fetchUsageData()
                }
            }
        }
    }

    fun checkPermissionsAndFetchData() {
        val hasUsage = usageStatsHelper.hasUsageStatsPermission()
        
        // 1. Check Accessibility Service
        val accessibilityEnabledStr = android.provider.Settings.Secure.getString(
            application.contentResolver,
            android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val hasAccessibility = accessibilityEnabledStr?.contains(application.packageName) == true

        // 2. Check Display Over Other Apps (Overlay)
        val hasOverlay = android.provider.Settings.canDrawOverlays(application)

        _uiState.update { 
            it.copy(
                hasUsagePermission = hasUsage,
                hasAccessibilityPermission = hasAccessibility,
                hasOverlayPermission = hasOverlay
            ) 
        }

        if (hasUsage) {
            viewModelScope.launch { fetchUsageData() }
        }
    }

    /** Fetches daily usage and updates the state. Always runs on IO via the use case. */
    private suspend fun fetchUsageData() {
        _uiState.update { it.copy(isLoading = true) }

        val usageList = fetchDailyUsageUseCase()

        val totalTime = usageList.sumOf { it.totalTimeInForeground }
        // Show top 5 apps, exclude DetoxDroid and system packages (no display name)
        val topApps = usageList
            .filter { it.appName.isNotBlank() && it.appName != it.packageName }
            .take(5)

        _uiState.update {
            it.copy(
                topUsedApps = topApps,
                totalScreenTimeMs = totalTime,
                isLoading = false
            )
        }
    }
}
