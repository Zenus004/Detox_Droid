package com.detox.detox_droid.domain.usecases

import com.detox.detox_droid.data.services.UsageStatsHelper
import com.detox.detox_droid.domain.models.AppUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchDailyUsageUseCase @Inject constructor(
    private val usageStatsHelper: UsageStatsHelper
) {
    suspend operator fun invoke(): List<AppUsage> = withContext(Dispatchers.IO) {
        usageStatsHelper.getDailyUsageStats()
    }
}
