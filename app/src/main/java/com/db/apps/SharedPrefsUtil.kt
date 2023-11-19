package com.db.apps

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPrefsUtil(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val key = "city"
    fun saveString( value: String) {
        try {
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
            Log.d("saveString", "String saved: $value")
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun getString( defaultValue: String = ""): String {
        Log.d("getString", "String : ${sharedPreferences.getString(key, defaultValue)}")
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    companion object {
        const val PREFS_NAME = "city_name"
    }
}