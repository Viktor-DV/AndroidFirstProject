package com.dolgantsev.androindfirstproject.core.dto

import android.content.SharedPreferences
import io.reactivex.rxjava3.core.Observable

fun SharedPreferences.asObservable(key: String): Observable<String> {
    return Observable.create { emitter ->
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
            if (changedKey == key) {
                val value = prefs.getString(changedKey, "popular") ?: "popular"
                emitter.onNext(value)
            }
        }
        registerOnSharedPreferenceChangeListener(listener)
        emitter.setCancellable {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
        // Отправляем начальное значение
        val initialValue = getString(key, "popular") ?: "popular"
        emitter.onNext(initialValue)
    }
}