package net.ginapps.testapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO: use different tress for debug and release
        Timber.plant(Timber.DebugTree())
    }
}
