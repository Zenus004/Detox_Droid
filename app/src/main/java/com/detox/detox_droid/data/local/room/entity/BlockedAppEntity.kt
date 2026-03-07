package com.detox.detox_droid.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val isWhitelisted: Boolean = false
)
