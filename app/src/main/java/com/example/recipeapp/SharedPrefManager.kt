package com.example.recipeapp.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor(private val context: Context) {

    companion object {
        private const val PREF_NAME = "UserSession"
        private var instance: SharedPrefManager? = null

        fun getInstance(context: Context): SharedPrefManager {
            if (instance == null) {
                instance = SharedPrefManager(context.applicationContext)
            }
            return instance!!
        }
    }

    //menggunakan sessionId untuk menyimpan session
    fun saveSessionId(sessionId: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("session_id", sessionId).apply()
    }

    fun getSessionId(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("session_id", null)
    }

//    fun saveToken(token: String) {
//        val sharedPreferences: SharedPreferences =
//            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        sharedPreferences.edit().putString("token", token).apply()
//    }
//
//    fun getToken(): String? {
//        val sharedPreferences: SharedPreferences =
//            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//        return sharedPreferences.getString("token", null)
//    }

    fun saveUsername(username: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun getUsername(): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", null)
    }

    //Memeriksa apakah user sudah login berdasarkan session_id
    val isLoggedIn: Boolean
        get() {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.contains("session_id")
        }

    //Menghapus session
    fun clear() {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }


}
