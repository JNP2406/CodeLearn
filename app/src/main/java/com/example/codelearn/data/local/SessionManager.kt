package com.example.codelearn.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "codelearn_session",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_NAME = "name"
        private const val KEY_CAMPUS = "campus"
        private const val KEY_IS_DEMO = "is_demo"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveLoginSession(
        token: String,
        userId: Int,
        email: String,
        role: String,
        name: String,
        campus: String?,
        isDemo: Boolean
    ) {
        preferences.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role)
            .putString(KEY_NAME, name)
            .putString(KEY_CAMPUS, campus)
            .putBoolean(KEY_IS_DEMO, isDemo)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }

    fun getUserId(): Int {
        return preferences.getInt(KEY_USER_ID, -1)
    }

    fun getEmail(): String? {
        return preferences.getString(KEY_EMAIL, null)
    }

    fun getRole(): String? {
        return preferences.getString(KEY_ROLE, null)
    }

    fun getName(): String? {
        return preferences.getString(KEY_NAME, null)
    }

    fun getCampus(): String? {
        return preferences.getString(KEY_CAMPUS, null)
    }

    fun isDemo(): Boolean {
        return preferences.getBoolean(KEY_IS_DEMO, false)
    }

    fun logout() {
        preferences.edit()
            .clear()
            .apply()
    }
}