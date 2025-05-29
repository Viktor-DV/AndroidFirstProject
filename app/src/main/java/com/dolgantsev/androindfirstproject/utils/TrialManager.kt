package com.dolgantsev.androindfirstproject.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dolgantsev.androindfirstproject.BuildConfig
import java.util.concurrent.TimeUnit

object TrialManager {
    private const val PREFS_NAME = "TrialPrefs"
    private const val KEY_TRIAL_ACTIVATED = "trial_activated"
    private const val KEY_TRIAL_START_TIMESTAMP = "trial_start_timestamp"
    private const val KEY_FIRST_LAUNCH_PROMPTED = "first_launch_prompted"
    private const val TRIAL_PERIOD_DAYS = 7L
    private const val KEY_PROMO_SHOWN_TIMESTAMP = "promo_shown_timestamp"
    private const val PROMO_COOLDOWN_HOURS = 12L

    // Проверяет, активирован ли пробный период
    fun isTrialActivated(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TRIAL_ACTIVATED, false)
    }

    // Проверяет, был ли показан диалог активации на первом запуске
    fun wasFirstLaunchPrompted(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_LAUNCH_PROMPTED, false)
    }

    // Отмечает, что диалог активации на первом запуске был показан
    fun markFirstLaunchPrompted(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean(KEY_FIRST_LAUNCH_PROMPTED, true)
        }
    }

    // Активирует пробный период
    fun activateTrial(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putBoolean(KEY_TRIAL_ACTIVATED, true)
            putLong(KEY_TRIAL_START_TIMESTAMP, System.currentTimeMillis())
        }
    }

    // Проверяет, активен ли пробный период
    private fun isTrialActive(context: Context): Boolean {
        if (!isTrialActivated(context)) return false

        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val startTimestamp = prefs.getLong(KEY_TRIAL_START_TIMESTAMP, 0L)
        if (startTimestamp == 0L) return false

        val daysSinceStart = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startTimestamp)
        return daysSinceStart < TRIAL_PERIOD_DAYS
    }

    // Проверяет, можно ли получить доступ к премиум-функциям
    fun canAccessPremiumFeatures(context: Context): Boolean {
        return BuildConfig.IS_PAID_VERSION || isTrialActive(context)
    }

    // Проверяет, можно ли показать промо-диалог (раз в 12 часов)
    fun canShowPromoDialog(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastShownTimestamp = prefs.getLong(KEY_PROMO_SHOWN_TIMESTAMP, 0L)
        if (lastShownTimestamp == 0L) return true

        val hoursSinceLastShown = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - lastShownTimestamp)
        return hoursSinceLastShown >= PROMO_COOLDOWN_HOURS
    }

    // Отмечает, что промо-диалог был показан
    fun markPromoDialogShown(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putLong(KEY_PROMO_SHOWN_TIMESTAMP, System.currentTimeMillis())
        }
    }
}