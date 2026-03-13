package com.detox.detox_droid.services

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.detox.detox_droid.domain.repository_interfaces.DetoxScheduleRepository
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import com.detox.detox_droid.domain.usecases.StartDetoxSessionUseCase
import com.detox.detox_droid.domain.usecases.StopDetoxSessionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.Calendar

@HiltWorker
class DetoxSchedulerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val detoxScheduleRepository: DetoxScheduleRepository,
    private val settingsRepository: SettingsRepository,
    private val startDetoxSessionUseCase: StartDetoxSessionUseCase,
    private val stopDetoxSessionUseCase: StopDetoxSessionUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val isCurrentlyDetoxing = settingsRepository.isDetoxModeActive.first()
            val schedules = detoxScheduleRepository.getAllSchedules().first()
            
            val calendar = Calendar.getInstance()
            val dayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> ""
            }
            
            val currentTimeOfDayMillis = calendar.get(Calendar.HOUR_OF_DAY) * 3600000L +
                    calendar.get(Calendar.MINUTE) * 60000L

            var shouldBeDetoxing = false
            var durationToEndMillis = 0L

            for (schedule in schedules) {
                if (schedule.isActive && schedule.daysOfWeek.contains(dayName)) {
                    val startCal = Calendar.getInstance().apply { timeInMillis = schedule.startTimeInMillis }
                    val endCal = Calendar.getInstance().apply { timeInMillis = schedule.endTimeInMillis }
                    
                    val start = startCal.get(Calendar.HOUR_OF_DAY) * 3600000L + startCal.get(Calendar.MINUTE) * 60000L
                    val end = endCal.get(Calendar.HOUR_OF_DAY) * 3600000L + endCal.get(Calendar.MINUTE) * 60000L
                    
                    if (start < end) {
                        if (currentTimeOfDayMillis in start..end) {
                            shouldBeDetoxing = true
                            durationToEndMillis = end - currentTimeOfDayMillis
                            break
                        }
                    } else {
                        // Wraps around midnight
                        if (currentTimeOfDayMillis >= start || currentTimeOfDayMillis <= end) {
                            shouldBeDetoxing = true
                            durationToEndMillis = if (currentTimeOfDayMillis >= start) {
                                (24 * 3600000L) - currentTimeOfDayMillis + end
                            } else {
                                end - currentTimeOfDayMillis
                            }
                            break
                        }
                    }
                }
            }

            if (shouldBeDetoxing && !isCurrentlyDetoxing) {
                Timber.d("Starting scheduled detox session")
                startDetoxSessionUseCase(durationToEndMillis, isScheduled = true)
            } else if (!shouldBeDetoxing && isCurrentlyDetoxing) {
                val sessionEndTime = settingsRepository.detoxSessionEndTime.first()
                val isScheduledSession = settingsRepository.isScheduledSession.first()

                if (isScheduledSession) {
                    Timber.d("Ending scheduled detox session (schedule over or deleted)")
                    stopDetoxSessionUseCase()
                } else if (System.currentTimeMillis() >= sessionEndTime && sessionEndTime != 0L) {
                    Timber.d("Ending manual detox session")
                    stopDetoxSessionUseCase()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error doing scheduled detox work")
            Result.retry()
        }
    }
}
