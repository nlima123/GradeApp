package com.cs436.gradeapp.storage

import android.content.Context

object UserManager {
    private const val PROFILES_NAME = "user_profiles"
    private const val KEY_NAME = "name"
    private const val KEY_LOGGED_IN = "is_logged_in"

    fun saveUser(context: Context, userProfile: UserProfile) {
        val profiles = context.getSharedPreferences(PROFILES_NAME, Context.MODE_PRIVATE)
        profiles.edit().apply {
            putString(KEY_NAME, userProfile.name)
            putBoolean(KEY_LOGGED_IN, userProfile.isLoggedIn)
            apply()
        }
    }

    fun getUser(context: Context): UserProfile? {
        val profiles = context.getSharedPreferences(PROFILES_NAME, Context.MODE_PRIVATE)
        val name = profiles.getString(KEY_NAME, null)
        val isLoggedIn = profiles.getBoolean(KEY_LOGGED_IN, false)
        return if (name != null) UserProfile(name, isLoggedIn) else null
    }

    fun clearUser(context: Context) {
        val profiles = context.getSharedPreferences(PROFILES_NAME, Context.MODE_PRIVATE)
        profiles.edit().clear().apply()
    }
}
