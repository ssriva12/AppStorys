package com.example.carousal.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class StorageService @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "AppStorysPrefs",
        Context.MODE_PRIVATE
    )

    fun setItem(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getItem(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeItem(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
} 