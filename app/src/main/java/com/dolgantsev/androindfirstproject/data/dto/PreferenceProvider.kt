package com.dolgantsev.androindfirstproject.data.dto

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.reactivex.rxjava3.core.Observable

class PreferenceProvider(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun saveCategory(category: String) {
        sharedPreferences.edit { putString(CATEGORY_KEY, category) }
    }

    fun getCategory(): String {
        return sharedPreferences.getString(CATEGORY_KEY, "popular") ?: "popular"
    }

    fun asObservable(): Observable<String> {
        return sharedPreferences.asObservable(CATEGORY_KEY)
    }

    companion object {
        private const val PREFERENCES_NAME = "film_preferences"
        const val CATEGORY_KEY = "category_key"
    }
}