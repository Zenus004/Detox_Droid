package com.detox.detox_droid.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isDetoxModeActive: Boolean = false,
    val dailyPauseLimit: Int = 3,
    val pausesRemainingToday: Int = 3
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        // Reactively update focus mode active status
        viewModelScope.launch {
            settingsRepository.isDetoxModeActive.collectLatest { isActive ->
                _uiState.update { it.copy(isDetoxModeActive = isActive) }
            }
        }

        // Reactively update the daily pause limit
        viewModelScope.launch {
            settingsRepository.dailyPauseLimit.collectLatest { limit ->
                _uiState.update { it.copy(dailyPauseLimit = limit) }
                // Also refresh remaining whenever the limit changes
                refreshPausesRemaining()
            }
        }

        // Reactively refresh "pauses remaining" when pause end time changes
        // (covers: a new pause being taken, or a pause expiring)
        viewModelScope.launch {
            settingsRepository.pauseEndTime.collectLatest {
                refreshPausesRemaining()
            }
        }

        // Periodic refresh every 30 seconds so the count stays accurate
        // without requiring any nav-away and back
        viewModelScope.launch {
            while (true) {
                delay(30_000L)
                refreshPausesRemaining()
            }
        }

        // Load the correct initial value from storage on startup
        viewModelScope.launch {
            refreshPausesRemaining()
        }
    }

    private suspend fun refreshPausesRemaining() {
        val remaining = settingsRepository.getDailyPausesRemaining()
        _uiState.update { it.copy(pausesRemainingToday = remaining) }
    }

    /**
     * Called by the UI only when the user FINISHES dragging the slider (onValueChangeFinished),
     * not on every frame change. This avoids hammering DataStore with writes.
     */
    fun updatePauseLimit(limit: Int) {
        viewModelScope.launch {
            settingsRepository.updateDailyPauseLimit(limit)
        }
    }

    /**
     * For live slider preview — just updates UI state without writing to storage.
     */
    fun previewPauseLimit(limit: Int) {
        _uiState.update { it.copy(dailyPauseLimit = limit) }
    }

    fun disableActiveDetoxSession() {
        viewModelScope.launch {
            settingsRepository.setDetoxModeActive(false)
            settingsRepository.setDetoxSessionEndTime(0L)
            settingsRepository.setPauseEndTime(0L)
        }
    }
}
