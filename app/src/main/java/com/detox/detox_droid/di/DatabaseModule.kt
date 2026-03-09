package com.detox.detox_droid.di

import android.content.Context
import androidx.room.Room
import com.detox.detox_droid.data.local.room.AppDatabase
import com.detox.detox_droid.data.local.room.dao.BlockedAppDao
import com.detox.detox_droid.data.local.room.dao.DetoxScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideBlockedAppDao(database: AppDatabase): BlockedAppDao {
        return database.blockedAppDao
    }

    @Provides
    @Singleton
    fun provideDetoxScheduleDao(database: AppDatabase): DetoxScheduleDao {
        return database.detoxScheduleDao
    }
}
