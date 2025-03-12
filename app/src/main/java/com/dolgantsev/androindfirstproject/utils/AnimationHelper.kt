package com.dolgantsev.androindfirstproject.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateDecelerateInterpolator
import kotlin.math.hypot
import kotlin.math.roundToInt

object AnimationHelper {
    // Это переменная для того, чтобы круг проявления расходился именно от иконки меню навигации
    private const val menuItems = 4

    // В метод у нас приходит 3 параметра:
    // 1 - наше rootView, которое одновременно является и контейнером
    // и объектом анимации
    // 2 - активити для того, чтобы вернуть выполнение нового треда в UI поток
    // 3 - позиция в меню навигации, чтобы круг проявления расходился именно от иконки меню навигации
    fun performFragmentCircularRevealAnimation(rootView: View, activity: Activity, position: Int) {
        // Создаем Handler для выполнения задач в главном потоке
        val handler = Handler(Looper.getMainLooper())

        // Выполняем задачу в главном потоке
        handler.post {
            // Суперсложная математика вычисления старта анимации
            val itemCenter = rootView.width / (menuItems * 2)
            val step = (itemCenter * 2) * (position - 1) + itemCenter

            val x: Int = step
            val y: Int = rootView.y.roundToInt() + rootView.height

            val startRadius = 0
            val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble())

            // Создаем саму анимацию
            ViewAnimationUtils.createCircularReveal(rootView, x, y, startRadius.toFloat(), endRadius.toFloat()).apply {
                // Устанавливаем время анимации
                duration = 500
                // Интерполятор для более естественной анимации
                interpolator = AccelerateDecelerateInterpolator()
                // Запускаем
                start()
            }
            // Выставляем видимость нашего элемента
            rootView.visibility = View.VISIBLE
        }
    }
}
