package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.data.services.UsageStatsHelper
import com.detox.detox_droid.domain.models.AppUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchWeeklyUsageUseCase @Inject constructor(
    private val usageStatsHelper: UsageStatsHelper
) {
    suspend operator fun invoke(): List<AppUsage> = withContext(Dispatchers.IO) {
        // We reuse daily stats fetching structure but for expanding it we can add INTERVAL_WEEKLY in UsageStatsHelper.
        // For now, this invokes the daily usage stats as placeholder.
        usageStatsHelper.getDailyUsageStats()
    }
}
