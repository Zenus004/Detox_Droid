package com.detox.detox_droid.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.data.local.datastore.SecureSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackedApp(
    val packageName: String,
    val displayName: String,
    val isTracked: Boolean = true
)

data class DoomScrollingState(
    val isEnabled: Boolean = true,
    val thresholdMinutes: Int = 15,
    val trackedApps: List<TrackedApp> = emptyList()
)

// Default social apps with initial tracking state
private val ALL_KNOWN_SOCIAL_APPS = listOf(
    TrackedApp(packageName = "com.instagram.android",     displayName = "Instagram",    isTracked = true),
    TrackedApp(packageName = "com.zhiliaoapp.musically",  displayName = "TikTok",       isTracked = true),
    TrackedApp(packageName = "com.twitter.android",       displayName = "Twitter / X",  isTracked = true),
    TrackedApp(packageName = "com.facebook.katana",       displayName = "Facebook",     isTracked = true),
    TrackedApp(packageName = "com.reddit.frontpage",      displayName = "Reddit",       isTracked = true),
    TrackedApp(packageName = "com.google.android.youtube",displayName = "YouTube",      isTracked = true),
    TrackedApp(packageName = "com.snapchat.android",      displayName = "Snapchat",     isTracked = false),
    TrackedApp(packageName = "com.pinterest",             displayName = "Pinterest",    isTracked = false),
    TrackedApp(packageName = "com.linkedin.android",      displayName = "LinkedIn",     isTracked = false)
)

@HiltViewModel
class DoomScrollingViewModel @Inject constructor(
    private val secureSettingsManager: SecureSettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoomScrollingState())
    val uiState: StateFlow<DoomScrollingState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            secureSettingsManager.doomScrollEnabled.collectLatest { enabled ->
                _uiState.update { it.copy(isEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            secureSettingsManager.doomScrollThresholdMinutes.collectLatest { mins ->
                _uiState.update { it.copy(thresholdMinutes = mins) }
            }
        }
        viewModelScope.launch {
            secureSettingsManager.doomScrollTrackedApps.collectLatest { trackedCsv ->
                val trackedSet = trackedCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                val apps = ALL_KNOWN_SOCIAL_APPS.map { app ->
                    app.copy(isTracked = app.packageName in trackedSet)
                }
                _uiState.update { it.copy(trackedApps = apps) }
            }
        }
    }

    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            secureSettingsManager.setDoomScrollEnabled(enabled)
        }
    }

    fun setThresholdMinutes(minutes: Int) {
        viewModelScope.launch {
            secureSettingsManager.setDoomScrollThresholdMinutes(minutes)
        }
    }

    fun toggleTrackedApp(packageName: String, tracked: Boolean) {
        val updatedApps = _uiState.value.trackedApps.map { app ->
            if (app.packageName == packageName) app.copy(isTracked = tracked) else app
        }
        _uiState.update { it.copy(trackedApps = updatedApps) }
        viewModelScope.launch {
            val csv = updatedApps.filter { it.isTracked }.joinToString(",") { it.packageName }
            secureSettingsManager.setDoomScrollTrackedApps(csv)
        }
    }
}
