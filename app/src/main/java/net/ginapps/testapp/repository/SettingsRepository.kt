package net.ginapps.testapp.repository

import android.content.SharedPreferences

interface SettingsRepository {
    var signInCompleted: Boolean
}

class SharedPrefSettingsRepository(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {
    override var signInCompleted: Boolean
        get() = sharedPreferences.getBoolean(SIGN_IN_COMPLETED, false)
        set(value) {
            sharedPreferences.edit().putBoolean(SIGN_IN_COMPLETED, value).apply()
        }

    private companion object {
        const val SIGN_IN_COMPLETED = "sign_in_completed"
    }
}
