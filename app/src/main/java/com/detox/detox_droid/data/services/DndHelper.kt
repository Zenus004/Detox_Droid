package com.detox.detox_droid.data.services

import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DndHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun isDndPermissionGranted(): Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun enableDnd() {
        if (isDndPermissionGranted()) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }

    fun disableDnd() {
        if (isDndPermissionGranted()) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}
