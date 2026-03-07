package com.detox.detox_droid.domain.repository_interfaces

import com.detox.detox_droid.data.local.room.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

interface BlockedAppRepository {
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>
    /** Emits only apps with isBlocked=true AND isWhitelisted=false. Used by the AccessibilityService. */
    fun getActivelyBlockedApps(): Flow<List<BlockedAppEntity>>
    suspend fun getBlockedAppByPackage(packageName: String): BlockedAppEntity?
    suspend fun insertBlockedApp(app: BlockedAppEntity)
    suspend fun insertBlockedApps(apps: List<BlockedAppEntity>)
    suspend fun updateBlockedApp(app: BlockedAppEntity)
    suspend fun deleteBlockedApp(packageName: String)
}
