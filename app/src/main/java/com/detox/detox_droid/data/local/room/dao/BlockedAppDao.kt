package com.detox.detox_droid.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.detox.detox_droid.data.local.room.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Query("SELECT * FROM blocked_apps")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>

    /** Returns only apps that are actively blocked (not whitelisted). Used by IsAppBlockedUseCase. */
    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1 AND isWhitelisted = 0")
    fun getActivelyBlockedApps(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName")
    suspend fun getBlockedAppByPackage(packageName: String): BlockedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApps(apps: List<BlockedAppEntity>)

    @Update
    suspend fun updateBlockedApp(app: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteBlockedApp(packageName: String)
}

