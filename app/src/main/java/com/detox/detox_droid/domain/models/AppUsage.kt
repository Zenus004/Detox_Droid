package com.detox.detox_droid.domain.models

data class AppUsage(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long,
    val lastTimeUsed: Long
)
