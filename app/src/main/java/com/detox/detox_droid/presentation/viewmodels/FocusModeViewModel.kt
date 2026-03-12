package com.detox.detox_droid.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import com.detox.detox_droid.domain.usecases.PauseDetoxSessionUseCase
import com.detox.detox_droid.domain.usecases.StartDetoxSessionUseCase
import com.detox.detox_droid.domain.usecases.StopDetoxSessionUseCase
import com.detox.detox_droid.data.services.DndHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A sentinel value stored as sessionEndTimeMs when the user starts an INDEFINITE session.
 * It is large enough to never be reached, but NOT Long.MAX_VALUE (which would overflow
 * arithmetic and cause garbled countdown displays).
 */
private const val INDEFINITE_SESSION_SENTINEL = Long.MAX_VALUE / 2L

data class FocusModeState(
    val isDetoxActive: Boolean = false,
    val isLoading: Boolean = false,
    val sessionEndTimeMs: Long = 0L,
    val timeRemainingMs: Long = 0L,
    val pauseEndTimeMs: Long = 0L,
    val pausesRemainingToday: Int = 0,
    val totalDurationMs: Long = 0
) {
    /** True only if this is a timed session (not indefinite). */
    val isTimedSession: Boolean get() = sessionEndTimeMs in 1L until INDEFINITE_SESSION_SENTINEL

    val isPaused: Boolean get() = pauseEndTimeMs > System.currentTimeMillis()
    val pauseRemainingMs: Long
        get() = (pauseEndTimeMs - System.currentTimeMillis()).coerceAtLeast(
            0L
        )
}

@HiltViewModel
class FocusModeViewModel @Inject constructor(
    private val startDetoxSessionUseCase: StartDetoxSessionUseCase,
    private val stopDetoxSessionUseCase: StopDetoxSessionUseCase,
    private val pauseDetoxSessionUseCase: PauseDetoxSessionUseCase,
    private val settingsRepository: SettingsRepository,
    private val dndHelper: DndHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusModeState())
    val uiState: StateFlow<FocusModeState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        // Reactive: detox mode on/off
        viewModelScope.launch {
            settingsRepository.isDetoxModeActive.collectLatest { isActive ->
                _uiState.update { it.copy(isDetoxActive = isActive) }
                // Session ended externally (e.g. from Settings "Kill") — clear local state
                if (!isActive) {
                    _uiState.update { it.copy(timeRemainingMs = 0L, sessionEndTimeMs = 0L) }
                }
            }
        }

        // Reactive: session end time (set by StartDetoxSessionUseCase)
        viewModelScope.launch {
            settingsRepository.detoxSessionEndTime.collectLatest { endTime ->
                _uiState.update { it.copy(sessionEndTimeMs = endTime) }
            }
        }

        // Reactive: pause end time — also refreshes pausesRemainingToday
        viewModelScope.launch {
            settingsRepository.pauseEndTime.collectLatest { pauseEnd ->
                _uiState.update { it.copy(pauseEndTimeMs = pauseEnd) }
                // Refresh remaining count whenever a pause is taken or expires
                val remaining = settingsRepository.getDailyPausesRemaining()
                _uiState.update { it.copy(pausesRemainingToday = remaining) }
            }
        }

        // Load correct initial pauses remaining from storage
        viewModelScope.launch {
            val remaining = settingsRepository.getDailyPausesRemaining()
            _uiState.update { it.copy(pausesRemainingToday = remaining) }
        }

        startCountdownTicker()
    }

    private fun startCountdownTicker() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var wasPaused = false

            while (true) {
                delay(1000L)
                val state = _uiState.value
                val endTime = state.sessionEndTimeMs

                // Automatically restore DND when a pause naturally expires
                if (wasPaused && !state.isPaused) {
                    dndHelper.enableDnd()
                }
                wasPaused = state.isPaused

                if (!state.isDetoxActive || endTime <= 0L) continue

                // Indefinite sessions: keep timeRemainingMs at 0 so the UI shows ∞
                if (endTime >= INDEFINITE_SESSION_SENTINEL) {
                    _uiState.update { it.copy(timeRemainingMs = 0L) }
                    continue
                }

                val remaining = (endTime - System.currentTimeMillis()).coerceAtLeast(0L)
                _uiState.update { it.copy(timeRemainingMs = remaining) }

                // Auto-stop when the session expires
                if (remaining == 0L) {
                    stopDetoxSessionUseCase()
                    // Flow will emit isDetoxModeActive=false → init collector clears state
                }
            }
        }
    }

    /**
     * @param totalMinutes 0 = indefinite (blocks until manually stopped), 1–360 = timed.
     */
    fun startDetox(totalMinutes: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val durationMs =
                if (totalMinutes > 0L) totalMinutes * 60_000L
                else INDEFINITE_SESSION_SENTINEL

            startDetoxSessionUseCase(durationMs)

            val remaining = settingsRepository.getDailyPausesRemaining()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    pausesRemainingToday = remaining,
                    totalDurationMs = if (durationMs >= INDEFINITE_SESSION_SENTINEL) 0L else durationMs
                )
            }
        }
    }

    /**
     * Uses [PauseDetoxSessionUseCase] — single source of truth for pause logic.
     * No duplication of incrementPauseCount / setPauseEndTime here.
     */
    fun pauseSession(minutes: Int) {
        viewModelScope.launch {
            val success = pauseDetoxSessionUseCase(minutes * 60_000L)
            if (success) {
                val newRemaining = settingsRepository.getDailyPausesRemaining()
                _uiState.update { it.copy(pausesRemainingToday = newRemaining) }
            }
        }
    }

    fun resumeSession() {
        viewModelScope.launch {
            val state = _uiState.value
            val now = System.currentTimeMillis()
            val unusedTime = (state.pauseEndTimeMs - now).coerceAtLeast(0L)

            if (unusedTime > 0L) {
                val currentSessionEnd = settingsRepository.getDetoxSessionEndTime()
                val indefiniteSentinel = Long.MAX_VALUE / 2L
                if (currentSessionEnd in 1L until indefiniteSentinel) {
                    settingsRepository.setDetoxSessionEndTime(currentSessionEnd - unusedTime)
                }
            }

            settingsRepository.setPauseEndTime(0L)
            dndHelper.enableDnd()
        }
    }

    fun stopDetox() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            stopDetoxSessionUseCase()
            settingsRepository.setPauseEndTime(0L)
            // isDetoxModeActive flow will emit false → init collector resets the rest
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
