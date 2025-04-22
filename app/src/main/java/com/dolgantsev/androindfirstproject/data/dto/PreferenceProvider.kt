package com.dolgantsev.androindfirstproject.data.dto

import android.content.Context
import android.content.SharedPreferences

class PreferenceProvider(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveCategory(category: String) {
        sharedPreferences.edit().putString(CATEGORY_KEY, category).apply()
    }

    fun getCategory(): String {
        return sharedPreferences.getString(CATEGORY_KEY, "popular") ?: "popular"
    }

    // Добавляем методы для регистрации и снятия слушателя
    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        private const val PREFERENCES_NAME = "film_preferences"
        const val CATEGORY_KEY = "category_key"
    }
}