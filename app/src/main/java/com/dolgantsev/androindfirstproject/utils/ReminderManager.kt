package com.dolgantsev.androindfirstproject.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.edit
import com.dolgantsev.androindfirstproject.utils.NotificationReceiver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Reminder(
    val filmId: Int,
    val timestamp: Long
)

object ReminderManager {
    private const val PREFS_NAME = "reminder_prefs"
    private const val REMINDERS_KEY = "reminders_key"
    private val gson = Gson()

    fun saveReminder(context: Context, reminder: Reminder) {
        val reminders = getReminders(context).toMutableList()
        reminders.removeAll { it.filmId == reminder.filmId } // Удаляем старое, если есть
        reminders.add(reminder)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(REMINDERS_KEY, gson.toJson(reminders)) }

        scheduleNotification(context, reminder)
    }

    fun getReminders(context: Context): List<Reminder> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(REMINDERS_KEY, null) ?: "[]"
        val type = object : TypeToken<List<Reminder>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun updateReminder(context: Context, filmId: Int, newTimestamp: Long) {
        val reminder = Reminder(filmId, newTimestamp)
        saveReminder(context, reminder)
    }

    fun deleteReminder(context: Context, filmId: Int) {
        val reminders = getReminders(context).toMutableList()
        reminders.removeAll { it.filmId == filmId }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(REMINDERS_KEY, gson.toJson(reminders)) }

        cancelNotification(context, filmId)
    }

    fun deleteAllReminders(context: Context) {
        getReminders(context).forEach { reminder ->
            cancelNotification(context, reminder.filmId)
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(REMINDERS_KEY, "[]") }
    }

    private fun scheduleNotification(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("filmId", reminder.filmId)
            action = "com.dolgantsev.androindfirstproject.NOTIFY"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.filmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminder.timestamp,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminder.timestamp,
                pendingIntent
            )
        }
    }

    private fun cancelNotification(context: Context, filmId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("filmId", filmId)
            action = "com.dolgantsev.androindfirstproject.NOTIFY"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            filmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun hasReminder(context: Context, filmId: Int): Boolean {
        return getReminders(context).any { it.filmId == filmId }
    }

    fun getReminderTime(context: Context, filmId: Int): Long? {
        return getReminders(context).find { it.filmId == filmId }?.timestamp
    }
}