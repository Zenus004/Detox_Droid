package com.detox.detox_droid.data.services

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.detox.detox_droid.services.DetoxSchedulerWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchedulerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun enqueueDetoxWorker() {
        // Enqueue worker every 15 minutes to check schedules repeatedly
        val workRequest = PeriodicWorkRequestBuilder<DetoxSchedulerWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    companion object {
        const val WORK_NAME = "detox_schedule_worker"
    }
}
