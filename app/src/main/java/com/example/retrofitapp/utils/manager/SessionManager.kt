package com.example.retrofitapp.utils.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import javax.inject.Inject

class SessionManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val KEY_USER_ID = "userId"
    }

    fun loginUser(userId: String) {
        Log.i("Log in User", userId)
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun logoutUser() {
        Log.i("Log out ", "Success")
        editor.remove(KEY_USER_ID)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        Log.i("Log in User", "true")
        return sharedPreferences.contains(KEY_USER_ID)
    }

    fun getUserEmail(): String?{

        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}