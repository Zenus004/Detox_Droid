package com.detox.detox_droid.data.services

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import com.detox.detox_droid.domain.models.AppUsage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val packageManager = context.packageManager

    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getDailyUsageStats(): List<AppUsage> {
        if (!hasUsageStatsPermission()) return emptyList()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return getUsageStatsFromEvents(startTime, endTime)
    }

    fun getWeeklyUsageStats(): List<AppUsage> {
        if (!hasUsageStatsPermission()) return emptyList()

        val calendar = Calendar.getInstance()
        
        // Find the start of the current week (Monday)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // We force Monday as the start of "This Week" for insights.
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        return getUsageStatsFromEvents(startTime, endTime)
    }

    fun getPreviousWeekTotalTime(): Long {
        if (!hasUsageStatsPermission()) return 0L

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (stats.isNullOrEmpty()) return 0L

        val packageTimeMap = mutableMapOf<String, Long>()

        for (uStat in stats) {
            val pkg = uStat.packageName
            val time = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uStat.totalTimeVisible
            } else {
                uStat.totalTimeInForeground
            }
            if (time > 0) {
                packageTimeMap[pkg] = (packageTimeMap[pkg] ?: 0L) + time
            }
        }

        val appUsageList = packageTimeMap.map { (pkg, totalTime) ->
            AppUsage(pkg, pkg, totalTime, 0L)
        }

        return filterAndSortUsage(appUsageList).sumOf { it.totalTimeInForeground }
    }



    private fun getUsageStatsFromEvents(startTime: Long, endTime: Long): List<AppUsage> {
        val queryStartTime = startTime - (24 * 3600 * 1000L)
        val events = usageStatsManager.queryEvents(queryStartTime, endTime)

        val totalTimeMap = mutableMapOf<String, Long>()
        val lastUsedTime = mutableMapOf<String, Long>()

        var currentPkg: String? = null
        var currentPkgStartTime = 0L

        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName
            val eventType = event.eventType
            val timeStamp = event.timeStamp

            // Screen/Device physically goes dormant or user explicitly locks it
            if (eventType == 16 || eventType == 18) { // SCREEN_NON_INTERACTIVE or KEYGUARD_SHOWN
                val pkgToClose = currentPkg
                if (pkgToClose != null) {
                    val boundedStart = kotlin.math.max(currentPkgStartTime, startTime)
                    val boundedEnd = kotlin.math.min(timeStamp, endTime)
                    if (boundedEnd > boundedStart) {
                        totalTimeMap[pkgToClose] = (totalTimeMap[pkgToClose] ?: 0L) + (boundedEnd - boundedStart)
                    }
                    currentPkg = null
                }
            } 
            // Hardware wakeups (15/17) are safely ignored. We defer exclusively to app intents below.
            
            // Application strictly seizes the foreground locally
            else if (eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                // By unequivocally trusting ACTIVITY_RESUMED, we completely immune ourselves against
                // missing SCREEN_INTERACTIVE (15) signals caused by Under-Display Fingerprint Scanners (UDFPS).
                if (currentPkg != pkg) {
                    val pkgToClose = currentPkg
                    if (pkgToClose != null) {
                        val boundedStart = kotlin.math.max(currentPkgStartTime, startTime)
                        val boundedEnd = kotlin.math.min(timeStamp, endTime)
                        if (boundedEnd > boundedStart) {
                            totalTimeMap[pkgToClose] = (totalTimeMap[pkgToClose] ?: 0L) + (boundedEnd - boundedStart)
                        }
                    }
                    currentPkg = pkg
                    currentPkgStartTime = timeStamp
                }
                lastUsedTime[pkg] = kotlin.math.max(lastUsedTime[pkg] ?: 0L, timeStamp)
            }
        }

        // Close hanging sessions at query boundary
        val pkgToClose = currentPkg
        if (pkgToClose != null) {
            val boundedStart = kotlin.math.max(currentPkgStartTime, startTime)
            if (endTime > boundedStart) {
                totalTimeMap[pkgToClose] = (totalTimeMap[pkgToClose] ?: 0L) + (endTime - boundedStart)
            }
        }

        val appUsageList = totalTimeMap.map { (pkg, totalTime) ->
            val appName = getAppName(pkg)
            AppUsage(pkg, appName, totalTime, lastUsedTime[pkg] ?: 0L)
        }

        return filterAndSortUsage(appUsageList)
    }

    private fun getAppName(pkg: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(pkg, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            pkg
        }
    }

    private fun filterAndSortUsage(usageList: List<AppUsage>): List<AppUsage> {
        val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_HOME)
        }
        // Exclude launcher apps mathematically
        val launcherPackages = packageManager.queryIntentActivities(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            .map { it.activityInfo.packageName }
            .toSet()

        // Match all true launchable apps rigorously using MATCH_ALL (or 0) so no social apps drop off
        val launchIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        val userLaunchablePackages = packageManager.queryIntentActivities(
            launchIntent, 
            PackageManager.MATCH_ALL
        ).map { it.activityInfo.packageName }.toSet()

        return usageList
            .filter { it.totalTimeInForeground > 0 }
            .filter { userLaunchablePackages.contains(it.packageName) }
            .filter { it.packageName != context.packageName && !launcherPackages.contains(it.packageName) }
            .sortedByDescending { it.totalTimeInForeground }
    }
}
