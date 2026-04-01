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
import kotlin.math.max
import kotlin.math.min

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

        val stats = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

        if (stats.isNullOrEmpty()) return emptyList()

        val appUsageList = stats.map { (pkg, uStat) ->
            val appName = getAppName(pkg)
            val time = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uStat.totalTimeVisible
            } else {
                uStat.totalTimeInForeground
            }
            AppUsage(pkg, appName, time, uStat.lastTimeUsed)
        }.filter { it.totalTimeInForeground > 0 }

        return filterAndSortUsage(appUsageList)
    }

    fun getWeeklyUsageStats(): List<AppUsage> {
        if (!hasUsageStatsPermission()) return emptyList()

        val calendar = Calendar.getInstance()
        
        // Find the start of the current week (Monday)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // By default Calendar.getInstance() might use Sunday depending on locale.
        // We force Monday as the start of "This Week" for insights.
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        // Use queryUsageStats for weekly history. It's pre-aggregated by the OS 
        // and persists much longer than raw UsageEvents.
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (stats.isNullOrEmpty()) return emptyList()

        // Aggregate stats by package
        val packageTimeMap = mutableMapOf<String, Long>()
        val packageLastUsedMap = mutableMapOf<String, Long>()

        for (uStat in stats) {
            val pkg = uStat.packageName
            val time = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uStat.totalTimeVisible
            } else {
                uStat.totalTimeInForeground
            }
            if (time > 0) {
                packageTimeMap[pkg] = (packageTimeMap[pkg] ?: 0L) + time
                packageLastUsedMap[pkg] = kotlin.math.max(packageLastUsedMap[pkg] ?: 0L, uStat.lastTimeUsed)
            }
        }

        val appUsageList = packageTimeMap.map { (pkg, totalTime) ->
            val appName = getAppName(pkg)
            AppUsage(pkg, appName, totalTime, packageLastUsedMap[pkg] ?: 0L)
        }

        return filterAndSortUsage(appUsageList)
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
