package com.detox.detox_droid.data.local.datastore

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_settings_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // In-memory StateFlows — initialised from persisted values at startup
    private val _dailyPauseLimit = MutableStateFlow(sharedPreferences.getInt(KEY_DAILY_PAUSE_LIMIT, 3))
    val dailyPauseLimit: Flow<Int> = _dailyPauseLimit.asStateFlow()

    private val _isDetoxModeActive = MutableStateFlow(sharedPreferences.getBoolean(KEY_DETOX_MODE_ACTIVE, false))
    val isDetoxModeActive: Flow<Boolean> = _isDetoxModeActive.asStateFlow()

    private val _detoxSessionEndTime = MutableStateFlow(sharedPreferences.getLong(KEY_DETOX_SESSION_END_TIME, 0L))
    val detoxSessionEndTime: Flow<Long> = _detoxSessionEndTime.asStateFlow()

    private val _isScheduledSession = MutableStateFlow(sharedPreferences.getBoolean(KEY_IS_SCHEDULED_SESSION, false))
    val isScheduledSession: Flow<Boolean> = _isScheduledSession.asStateFlow()

    private val _pauseEndTime = MutableStateFlow(sharedPreferences.getLong(KEY_PAUSE_END_TIME, 0L))
    val pauseEndTime: Flow<Long> = _pauseEndTime.asStateFlow()

    // ── Doom Scroll settings ─────────────────────────────────────────────
    private val _doomScrollEnabled = MutableStateFlow(sharedPreferences.getBoolean(KEY_DOOM_SCROLL_ENABLED, true))
    val doomScrollEnabled: Flow<Boolean> = _doomScrollEnabled.asStateFlow()

    private val _doomScrollThresholdMinutes = MutableStateFlow(sharedPreferences.getInt(KEY_DOOM_SCROLL_THRESHOLD_MINS, 15))
    val doomScrollThresholdMinutes: Flow<Int> = _doomScrollThresholdMinutes.asStateFlow()

    private val defaultTrackedApps = listOf(
        "com.instagram.android",
        "com.zhiliaoapp.musically",
        "com.twitter.android",
        "com.facebook.katana",
        "com.reddit.frontpage",
        "com.google.android.youtube",
        "com.snapchat.android",
        "com.pinterest",
        "com.linkedin.android"
    ).joinToString(",")
    private val _doomScrollTrackedApps = MutableStateFlow(
        sharedPreferences.getString(KEY_DOOM_SCROLL_TRACKED_APPS, defaultTrackedApps) ?: defaultTrackedApps
    )
    val doomScrollTrackedApps: Flow<String> = _doomScrollTrackedApps.asStateFlow()

    // ── Write helpers — all IO work runs on Dispatchers.IO ──────────────

    suspend fun updateDailyPauseLimit(limit: Int) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putInt(KEY_DAILY_PAUSE_LIMIT, limit) }
        }
        _dailyPauseLimit.update { limit }
    }

    suspend fun setDetoxModeActive(isActive: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putBoolean(KEY_DETOX_MODE_ACTIVE, isActive) }
        }
        _isDetoxModeActive.update { isActive }
    }

    suspend fun setDetoxSessionEndTime(endTimeInMillis: Long) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putLong(KEY_DETOX_SESSION_END_TIME, endTimeInMillis) }
        }
        _detoxSessionEndTime.update { endTimeInMillis }
    }

    suspend fun setIsScheduledSession(isScheduled: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putBoolean(KEY_IS_SCHEDULED_SESSION, isScheduled) }
        }
        _isScheduledSession.update { isScheduled }
    }

    suspend fun setPauseEndTime(endTimeInMillis: Long) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putLong(KEY_PAUSE_END_TIME, endTimeInMillis) }
        }
        _pauseEndTime.update { endTimeInMillis }
    }

    suspend fun incrementPauseCount() {
        withContext(Dispatchers.IO) {
            val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
            val lastDay = sharedPreferences.getInt(KEY_LAST_PAUSE_DAY, -1)
            val count = if (today != lastDay) {
                sharedPreferences.edit { putInt(KEY_LAST_PAUSE_DAY, today) }
                1
            } else {
                sharedPreferences.getInt(KEY_CURRENT_PAUSE_COUNT, 0) + 1
            }
            sharedPreferences.edit { putInt(KEY_CURRENT_PAUSE_COUNT, count) }
        }
    }

    suspend fun getDetoxSessionEndTime(): Long {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getLong(KEY_DETOX_SESSION_END_TIME, 0L)
        }
    }

    suspend fun getDailyPausesRemaining(): Int = withContext(Dispatchers.IO) {
        val today = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val lastDay = sharedPreferences.getInt(KEY_LAST_PAUSE_DAY, -1)
        val count = if (today == lastDay) sharedPreferences.getInt(KEY_CURRENT_PAUSE_COUNT, 0) else 0
        val max = sharedPreferences.getInt(KEY_DAILY_PAUSE_LIMIT, 3)
        (max - count).coerceAtLeast(0)
    }

    suspend fun setDoomScrollEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putBoolean(KEY_DOOM_SCROLL_ENABLED, enabled) }
        }
        _doomScrollEnabled.update { enabled }
    }

    suspend fun setDoomScrollThresholdMinutes(minutes: Int) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putInt(KEY_DOOM_SCROLL_THRESHOLD_MINS, minutes) }
        }
        _doomScrollThresholdMinutes.update { minutes }
    }

    /** [trackedPackages] is a comma-separated string of package names. */
    suspend fun setDoomScrollTrackedApps(trackedPackages: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit { putString(KEY_DOOM_SCROLL_TRACKED_APPS, trackedPackages) }
        }
        _doomScrollTrackedApps.update { trackedPackages }
    }

    /** Synchronous read for the AccessibilityService (called off main thread). */
    fun getDoomScrollSettingsSync(): Triple<Boolean, Int, Set<String>> {
        val enabled = sharedPreferences.getBoolean(KEY_DOOM_SCROLL_ENABLED, true)
        val thresholdMins = sharedPreferences.getInt(KEY_DOOM_SCROLL_THRESHOLD_MINS, 15)
        val tracked = (sharedPreferences.getString(KEY_DOOM_SCROLL_TRACKED_APPS, defaultTrackedApps) ?: defaultTrackedApps)
            .split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        return Triple(enabled, thresholdMins, tracked)
    }

    companion object {
        private const val KEY_DAILY_PAUSE_LIMIT = "daily_pause_limit"
        private const val KEY_DETOX_MODE_ACTIVE = "detox_mode_active"
        private const val KEY_DETOX_SESSION_END_TIME = "detox_session_end_time"
        private const val KEY_PAUSE_END_TIME = "pause_end_time"
        private const val KEY_CURRENT_PAUSE_COUNT = "current_pause_count"
        private const val KEY_LAST_PAUSE_DAY = "last_pause_day"
        private const val KEY_DOOM_SCROLL_ENABLED = "doom_scroll_enabled"
        private const val KEY_DOOM_SCROLL_THRESHOLD_MINS = "doom_scroll_threshold_mins"
        private const val KEY_DOOM_SCROLL_TRACKED_APPS = "doom_scroll_tracked_apps"
        private const val KEY_IS_SCHEDULED_SESSION = "is_scheduled_session"
    }
}
