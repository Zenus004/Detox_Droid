package com.detox.detox_droid.data.services

import android.app.AppOpsManager
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
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getDailyUsageStats(): List<AppUsage> {
        if (!hasUsageStatsPermission()) return emptyList()

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        val statsMap = usageStatsManager.queryAndAggregateUsageStats(
            startTime,
            endTime
        )

        val appUsageList = mutableListOf<AppUsage>()

        statsMap?.values?.filter { it.totalTimeInForeground > 0 }?.forEach { usageStat ->
            val appName = try {
                val appInfo = packageManager.getApplicationInfo(usageStat.packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                usageStat.packageName
            }
            appUsageList.add(
                AppUsage(
                    packageName = usageStat.packageName,
                    appName = appName,
                    totalTimeInForeground = usageStat.totalTimeInForeground,
                    lastTimeUsed = usageStat.lastTimeUsed
                )
            )
        }

        val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_HOME)
        }
        val launcherPackages = packageManager.queryIntentActivities(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            .map { it.activityInfo.packageName }
            .toSet()

        return appUsageList
            .filter { it.packageName != context.packageName && !launcherPackages.contains(it.packageName) } // exclude DetoxDroid and launchers
            .sortedByDescending { it.totalTimeInForeground }
    }
}
