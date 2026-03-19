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
            val time = uStat.totalTimeInForeground
            if (time > 0) {
                packageTimeMap[pkg] = (packageTimeMap[pkg] ?: 0L) + time
                packageLastUsedMap[pkg] = max(packageLastUsedMap[pkg] ?: 0L, uStat.lastTimeUsed)
            }
        }

        val appUsageList = packageTimeMap.map { (pkg, totalTime) ->
            val appName = getAppName(pkg)
            AppUsage(pkg, appName, totalTime, packageLastUsedMap[pkg] ?: 0L)
        }

        return filterAndSortUsage(appUsageList)
    }

    private fun getUsageStatsFromEvents(startTime: Long, endTime: Long): List<AppUsage> {
        // High-precision event-based logic for Daily tracking
        val queryStartTime = startTime - (24 * 3600 * 1000L)
        val events = usageStatsManager.queryEvents(queryStartTime, endTime)

        class TimeRange(var start: Long, var end: Long)
        val packageRanges = mutableMapOf<String, MutableList<TimeRange>>()
        val activeActivities = mutableMapOf<String, Long>()
        val lastUsedTime = mutableMapOf<String, Long>()
        
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName
            val className = event.className ?: "unknown_class"
            
            val eventKey = "$pkg:$className"
            
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    val existingStart = activeActivities[eventKey]
                    if (existingStart != null) {
                        // Force close the previous interval if another resume happens without pause
                        val sessionStart = max(existingStart, startTime)
                        val sessionEnd = min(event.timeStamp, endTime)
                        if (sessionEnd > sessionStart) {
                            packageRanges.getOrPut(pkg) { mutableListOf() }.add(TimeRange(sessionStart, sessionEnd))
                        }
                    }
                    activeActivities[eventKey] = event.timeStamp
                    lastUsedTime[pkg] = max(lastUsedTime[pkg] ?: 0L, event.timeStamp)
                }
                UsageEvents.Event.ACTIVITY_PAUSED, UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val resumedTime = activeActivities.remove(eventKey)
                    if (resumedTime != null) {
                        val sessionStart = max(resumedTime, startTime)
                        val sessionEnd = min(event.timeStamp, endTime)
                        if (sessionEnd > sessionStart) {
                            packageRanges.getOrPut(pkg) { mutableListOf() }.add(TimeRange(sessionStart, sessionEnd))
                        }
                    }
                }
            }
        }

        // Handle apps currently in foreground
        for ((eventKey, resumedTime) in activeActivities) {
            val pkg = eventKey.substringBefore(":")
            val sessionStart = max(resumedTime, startTime)
            if (endTime > sessionStart) {
                packageRanges.getOrPut(pkg) { mutableListOf() }.add(TimeRange(sessionStart, endTime))
            }
        }

        // Merge intervals to prevent double-counting overlapping activities
        val totalTimeMap = mutableMapOf<String, Long>()
        for ((pkg, ranges) in packageRanges) {
            if (ranges.isEmpty()) continue
            
            ranges.sortBy { it.start }
            var totalPackageTime = 0L
            var currentStart = ranges[0].start
            var currentEnd = ranges[0].end

            for (i in 1 until ranges.size) {
                val range = ranges[i]
                if (range.start <= currentEnd) {
                    currentEnd = max(currentEnd, range.end)
                } else {
                    totalPackageTime += (currentEnd - currentStart)
                    currentStart = range.start
                    currentEnd = range.end
                }
            }
            totalPackageTime += (currentEnd - currentStart)
            totalTimeMap[pkg] = totalPackageTime
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
        val launcherPackages = packageManager.queryIntentActivities(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            .map { it.activityInfo.packageName }
            .toSet()

        return usageList
            .filter { it.totalTimeInForeground > 0 }
            .filter { it.packageName != context.packageName && !launcherPackages.contains(it.packageName) }
            .sortedByDescending { it.totalTimeInForeground }
    }
}
