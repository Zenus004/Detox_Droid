package com.detox.detox_droid.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.detox.detox_droid.data.local.datastore.SecureSettingsManager
import com.detox.detox_droid.domain.usecases.IsAppBlockedUseCase
import com.detox.detox_droid.presentation.screens.BlockOverlayActivity
import com.detox.detox_droid.presentation.screens.DoomScrollingOverlayActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockerAccessibilityService : AccessibilityService() {

    @Inject lateinit var isAppBlockedUseCase: IsAppBlockedUseCase
    @Inject lateinit var secureSettingsManager: SecureSettingsManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * The package currently in foreground (updated on every window change).
     * The doom scroll timer compares against this to verify the user is still in the same app.
     */
    private var currentForegroundPackage: String? = null

    /**
     * Used to prevent re-processing the same package consecutively for BLOCKING.
     * Reset to null whenever blocking fires so returning to the same app re-triggers.
     */
    private var lastBlockedPackage: String? = null

    /** Active doom-scroll countdown job. Cancelled and restarted on every app switch. */
    private var doomScrollJob: Job? = null

    /** Packages that must NEVER be sent to any overlay — prevents boot-loops. */
    private val SYSTEM_PACKAGES_BLOCKLIST = setOf(
        "android",
        "com.android.systemui",
        "com.android.launcher",
        "com.android.launcher2",
        "com.android.launcher3",
        "com.google.android.apps.nexuslauncher",
        "com.sec.android.app.launcher",
        "com.miui.home",
        "com.huawei.android.launcher",
        "com.oneplus.launcher",
        "com.motorola.launcher3"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return

        // Never act on system UI, launchers, or our own app
        if (isSystemOrOwnPackage(packageName)) return

        // Always update what's currently in foreground (for doom scroll timer check)
        currentForegroundPackage = packageName

        // Cancel any previous doom scroll timer — user switched apps
        doomScrollJob?.cancel()
        doomScrollJob = null

        serviceScope.launch {
            // ── App Blocking check ─────────────────────────────────────────────
            val isBlocked = isAppBlockedUseCase(packageName)
            if (isBlocked) {
                if (lastBlockedPackage != packageName) {
                    Timber.d("Blocking app: $packageName")
                    lastBlockedPackage = packageName
                    // Reset after launching overlay so returning to app triggers again
                    lastBlockedPackage = null
                    launchBlockOverlay(packageName)
                }
                // Blocked apps do NOT get a doom scroll timer
                return@launch
            }

            // Not a blocked app — reset block tracking when user reaches a safe app
            lastBlockedPackage = null

            // ── Doom Scroll detection ──────────────────────────────────────────
            // Read current settings synchronously (already on IO thread)
            val (doomEnabled, thresholdMins, trackedApps) =
                secureSettingsManager.getDoomScrollSettingsSync()

            if (!doomEnabled) return@launch
            if (!trackedApps.contains(packageName)) return@launch

            val thresholdMs = thresholdMins * 60_000L
            Timber.d("Starting doom scroll timer for $packageName — threshold ${thresholdMins}m")

            doomScrollJob = serviceScope.launch {
                delay(thresholdMs)
                // Only fire if the user is STILL in the same app after the delay
                if (currentForegroundPackage == packageName) {
                    Timber.d("Doom scroll threshold reached for $packageName")
                    launchDoomScrollOverlay(packageName)
                }
            }
        }
    }

    /**
     * BUG FIX: The original code had `packageName == packageName.also {}` which is ALWAYS true,
     * making the entire expression collapse to a vacuous identity check.
     * Fixed to a clean set of checks.
     */
    private fun isSystemOrOwnPackage(packageName: String): Boolean {
        return packageName == this.packageName ||
               packageName.contains("systemui", ignoreCase = true) ||
               SYSTEM_PACKAGES_BLOCKLIST.any { packageName.startsWith(it) }
    }

    private fun launchBlockOverlay(blockedPackage: String) {
        startActivity(Intent(this, BlockOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("BLOCKED_PACKAGE", blockedPackage)
        })
    }

    private fun launchDoomScrollOverlay(scrollingPackage: String) {
        startActivity(Intent(this, DoomScrollingOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("SCROLLING_PACKAGE", scrollingPackage)
        })
    }

    override fun onInterrupt() { /* required override */ }

    override fun onDestroy() {
        super.onDestroy()
        doomScrollJob?.cancel()
        serviceScope.coroutineContext[Job]?.cancel()
    }
}
