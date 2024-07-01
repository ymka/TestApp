package net.ginapps.testapp

import android.app.Application
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO: use different tress for debug and release
        Timber.plant(Timber.DebugTree())
    }
}
