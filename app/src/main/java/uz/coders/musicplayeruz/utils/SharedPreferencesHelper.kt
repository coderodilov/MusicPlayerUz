package uz.coders.musicplayeruz.utils

import android.content.Context

/* 
* Created by Coder Odilov on 19/06/2023
*/

class SharedPreferencesHelper(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("AppConfig", Context.MODE_PRIVATE)

    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

}