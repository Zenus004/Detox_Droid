package com.detox.detox_droid

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class DetoxDroidApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // --- Global Exception Handler ---
        // This catches any untracked crashes to log them securely before the app dies.
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "FATAL CRASH caught by Global Exception Handler on thread: \${thread.name}")
            // [Future production prep]: 
            // FirebaseCrashlytics.getInstance().recordException(throwable)
            
            // Pass the crash back to Android to trigger the system crash dialogue and cleanly terminate
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
