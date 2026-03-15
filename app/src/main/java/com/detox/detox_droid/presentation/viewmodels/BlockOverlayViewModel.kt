package com.detox.detox_droid.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.detox.detox_droid.domain.usecases.PauseDetoxSessionUseCase

data class BlockOverlayState(
    val detoxEndTimeMs: Long = 0L,
    val pausesRemaining: Int = 0,
    val isDetoxActive: Boolean = false
)

@HiltViewModel
class BlockOverlayViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val pauseDetoxSessionUseCase: PauseDetoxSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockOverlayState())
    val uiState: StateFlow<BlockOverlayState> = _uiState.asStateFlow()

    init {
        // Subscribe to all relevant flows separately and write them into _uiState.
        // Using collectLatest here avoids the bug of calling suspend fns inside combine().
        viewModelScope.launch {
            settingsRepository.detoxSessionEndTime.collectLatest { endTime ->
                _uiState.update { it.copy(detoxEndTimeMs = endTime) }
            }
        }
        viewModelScope.launch {
            settingsRepository.isDetoxModeActive.collectLatest { isActive ->
                _uiState.update { it.copy(isDetoxActive = isActive) }
                // Whenever activation state changes, refresh the pauses count
                val remaining = settingsRepository.getDailyPausesRemaining()
                _uiState.update { it.copy(pausesRemaining = remaining) }
            }
        }
    }

    fun pauseDetox(minutes: Int) {
        viewModelScope.launch {
            val success = pauseDetoxSessionUseCase(minutes * 60_000L)
            if (success) {
                // Refresh the pausesRemaining in state immediately
                val newRemaining = settingsRepository.getDailyPausesRemaining()
                _uiState.update { it.copy(pausesRemaining = newRemaining) }
            }
        }
    }
}
