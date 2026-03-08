package com.detox.detox_droid.data.repository_impl

import com.detox.detox_droid.data.local.room.dao.BlockedAppDao
import com.detox.detox_droid.data.local.room.entity.BlockedAppEntity
import com.detox.detox_droid.domain.repository_interfaces.BlockedAppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlockedAppRepositoryImpl @Inject constructor(
    private val blockedAppDao: BlockedAppDao
) : BlockedAppRepository {

    override fun getAllBlockedApps(): Flow<List<BlockedAppEntity>> {
        return blockedAppDao.getAllBlockedApps()
    }

    override fun getActivelyBlockedApps(): Flow<List<BlockedAppEntity>> {
        return blockedAppDao.getActivelyBlockedApps()
    }

    override suspend fun getBlockedAppByPackage(packageName: String): BlockedAppEntity? {
        return blockedAppDao.getBlockedAppByPackage(packageName)
    }

    override suspend fun insertBlockedApp(app: BlockedAppEntity) {
        blockedAppDao.insertBlockedApp(app)
    }

    override suspend fun insertBlockedApps(apps: List<BlockedAppEntity>) {
        blockedAppDao.insertBlockedApps(apps)
    }

    override suspend fun updateBlockedApp(app: BlockedAppEntity) {
        blockedAppDao.updateBlockedApp(app)
    }

    override suspend fun deleteBlockedApp(packageName: String) {
        blockedAppDao.deleteBlockedApp(packageName)
    }
}

