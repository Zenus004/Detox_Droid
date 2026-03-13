package com.detox.detox_droid.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.detox.detox_droid.data.local.room.entity.DetoxScheduleEntity
import com.detox.detox_droid.data.services.SchedulerManager
import com.detox.detox_droid.domain.repository_interfaces.DetoxScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleState(
    val schedules: List<DetoxScheduleEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: DetoxScheduleRepository,
    private val schedulerManager: SchedulerManager
) : ViewModel() {

    val uiState: StateFlow<ScheduleState> = scheduleRepository.getAllSchedules()
        .map { ScheduleState(schedules = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ScheduleState(isLoading = true)
        )

    fun addSchedule(startTimeInMillis: Long, endTimeInMillis: Long, daysOfWeek: String) {
        val entity = DetoxScheduleEntity(
            startTimeInMillis = startTimeInMillis,
            endTimeInMillis = endTimeInMillis,
            daysOfWeek = daysOfWeek,
            isActive = true
        )
        viewModelScope.launch {
            scheduleRepository.insertSchedule(entity)
            schedulerManager.checkNow()
        }
    }

    fun deleteSchedule(schedule: DetoxScheduleEntity) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            schedulerManager.checkNow()
        }
    }

    fun toggleSchedule(schedule: DetoxScheduleEntity, isActive: Boolean) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule.copy(isActive = isActive))
            schedulerManager.checkNow()
        }
    }
}
