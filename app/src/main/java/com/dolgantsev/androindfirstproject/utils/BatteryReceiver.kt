package com.dolgantsev.androindfirstproject.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.provider.Settings
import android.widget.Toast

class BatteryReceiver : BroadcastReceiver() {

    private var isLowBrightness = false // флаг для отслеживания состояния яркости
    private val lowBatteryThreshold = 15      // порог низкого заряда батареи
    private val restoreBatteryThreshold = 20  // порог восстановления яркости

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> {
                // Уведомление о низком заряде
                Toast.makeText(context, "Низкий уровень заряда батареи!", Toast.LENGTH_LONG).show()
                lowerBrightness(context)
            }
            Intent.ACTION_POWER_CONNECTED -> {
                // Уведомление о подключении зарядки
                Toast.makeText(context, "Зарядка подключена", Toast.LENGTH_LONG).show()
                restoreBrightness(context)
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                // Получаем текущий уровень заряда
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = if (scale > 0) (level * 100 / scale) else -1

                if (batteryPct == -1) return

                // Изменяем яркость при необходимости
                if (batteryPct <= lowBatteryThreshold && !isLowBrightness) {
                    Toast.makeText(context, "Батарея низкая: $batteryPct%", Toast.LENGTH_LONG).show()
                    lowerBrightness(context)
                } else if (batteryPct >= restoreBatteryThreshold && isLowBrightness) {
                    Toast.makeText(context, "Батарея восстановлена: $batteryPct%", Toast.LENGTH_LONG).show()
                    restoreBrightness(context)
                }
            }
        }
    }

    // Снижает яркость до минимума
    private fun lowerBrightness(context: Context) {
        if (!hasWriteSettingsPermission(context)) {
            Toast.makeText(context, "Разрешите изменение настроек для управления яркостью", Toast.LENGTH_LONG).show()
            return
        }

        try {
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 25)
            isLowBrightness = true
            Toast.makeText(context, "Яркость снижена для экономии заряда", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка при снижении яркости", Toast.LENGTH_SHORT).show()
        }
    }

    // Восстанавливает автоматическую яркость
    private fun restoreBrightness(context: Context) {
        if (!hasWriteSettingsPermission(context)) {
            Toast.makeText(context, "Разрешите изменение настроек для управления яркостью", Toast.LENGTH_LONG).show()
            return
        }

        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            )
            isLowBrightness = false
            Toast.makeText(context, "Яркость восстановлена", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка при восстановлении яркости", Toast.LENGTH_SHORT).show()
        }
    }

    // Проверка наличия разрешения на изменение настроек
    private fun hasWriteSettingsPermission(context: Context): Boolean {
        return Settings.System.canWrite(context)
    }
}