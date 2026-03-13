package com.detox.detox_droid.data.services

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.detox.detox_droid.services.DetoxSchedulerWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchedulerManager @Inject constructor(
    @field:ApplicationContext private val context: Context
) {
    @Suppress("unused")
    fun enqueueDetoxWorker() {
        // Enqueue worker every 15 minutes to check schedules repeatedly
        val workRequest = PeriodicWorkRequestBuilder<DetoxSchedulerWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun checkNow() {
        val workRequest = OneTimeWorkRequestBuilder<DetoxSchedulerWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${WORK_NAME}_immediate",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    companion object {
        const val WORK_NAME = "detox_schedule_worker"
    }
}
