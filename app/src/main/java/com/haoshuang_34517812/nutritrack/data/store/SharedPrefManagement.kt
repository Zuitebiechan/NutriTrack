package com.haoshuang_34517812.nutritrack.data.store

import android.content.Context
import android.content.SharedPreferences
import com.haoshuang_34517812.nutritrack.NutriTrackApp
import com.haoshuang_34517812.nutritrack.util.Constants

/**
 * Management of application preferences.
 */
class PreferencesManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = Constants.SHARED_PREFS_NAME
        private const val KEY_ADMIN_PASSWORD = Constants.KEY_ADMIN_PASSWORD

        private const val DEFAULT_ADMIN_PASSWORD = Constants.DEFAULT_ADMIN_PASSWORD

        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(NutriTrackApp.appContext).also { instance = it }
            }
        }
    }

    /**
     * Get the current admin password.
     * If not set, set the default password and return it.
     */
    private fun getAdminPassword(): String {
        val storedPassword = sharedPreferences.getString(KEY_ADMIN_PASSWORD, null)

        if (storedPassword == null) {
            setAdminPassword(DEFAULT_ADMIN_PASSWORD)
            return DEFAULT_ADMIN_PASSWORD
        }

        return storedPassword
    }

    /**
     * Set a new admin password.
     */
    private fun setAdminPassword(newPassword: String) {
        sharedPreferences.edit().putString(KEY_ADMIN_PASSWORD, newPassword).apply()
    }

    /**
     * Check if the provided password matches the stored admin password.
     */
    fun verifyAdminPassword(inputPassword: String): Boolean {
        return inputPassword == getAdminPassword()
    }
}