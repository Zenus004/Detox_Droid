package com.detox.detox_droid.di

import com.detox.detox_droid.data.repository_impl.SettingsRepositoryImpl
import com.detox.detox_droid.domain.repository_interfaces.SettingsRepository
import com.detox.detox_droid.data.repository_impl.BlockedAppRepositoryImpl
import com.detox.detox_droid.data.repository_impl.DetoxScheduleRepositoryImpl
import com.detox.detox_droid.domain.repository_interfaces.BlockedAppRepository
import com.detox.detox_droid.domain.repository_interfaces.DetoxScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindBlockedAppRepository(
        blockedAppRepositoryImpl: BlockedAppRepositoryImpl
    ): BlockedAppRepository

    @Binds
    @Singleton
    abstract fun bindDetoxScheduleRepository(
        detoxScheduleRepositoryImpl: DetoxScheduleRepositoryImpl
    ): DetoxScheduleRepository
}
