package com.detox.detox_droid.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.data.services.UsageStatsHelper
import com.detox.detox_droid.domain.models.AppUsage
import com.detox.detox_droid.domain.usecases.FetchWeeklyUsageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsState(
    val weeklyUsage: List<AppUsage> = emptyList(),
    val totalWeeklyTimeMs: Long = 0L,
    val previousWeeklyTimeMs: Long = 0L,
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val fetchWeeklyUsageUseCase: FetchWeeklyUsageUseCase,
    private val usageStatsHelper: UsageStatsHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsState())
    val uiState: StateFlow<StatisticsState> = _uiState

    init {
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        if (!usageStatsHelper.hasUsageStatsPermission()) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val apps = fetchWeeklyUsageUseCase()
            val totalTime = apps.sumOf { it.totalTimeInForeground }
            val previousTotalTime = usageStatsHelper.getPreviousWeekTotalTime()
            _uiState.update { 
                it.copy(
                    weeklyUsage = apps, 
                    totalWeeklyTimeMs = totalTime, 
                    previousWeeklyTimeMs = previousTotalTime,
                    isLoading = false
                ) 
            }
        }
    }
}
