package com.dolgantsev.androindfirstproject.utils

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun SharedPreferences.asFlow(key: String): Flow<String?> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
        if (changedKey == key) {
            trySend(prefs.getString(key, null))
        }
    }
    registerOnSharedPreferenceChangeListener(listener)
    trySend(getString(key, null))
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}