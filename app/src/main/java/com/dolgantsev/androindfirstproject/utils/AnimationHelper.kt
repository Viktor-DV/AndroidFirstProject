package com.dolgantsev.androindfirstproject.utils

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import kotlin.math.hypot

object AnimationHelper {

    fun performFragmentCircularRevealAnimation(rootView: View, position: Int) {
        rootView.post {
            val location = IntArray(2)
            rootView.getLocationInWindow(location)

            val x: Int = when (position) {
                1 -> location[0] + rootView.width
                2 -> location[0]
                3 -> location[0] // Исправлено: используем X-координату
                4 -> location[0] + rootView.width
                else -> location[0]
            }
            val y: Int = when (position) {
                1 -> location[1] + rootView.height
                2 -> location[1] + rootView.height
                3 -> location[1]
                4 -> location[1]
                else -> location[1]
            }

            val startRadius = 0f
            val endRadius = hypot(rootView.width.toDouble(), rootView.height.toDouble()).toFloat()

            val anim: Animator = ViewAnimationUtils.createCircularReveal(
                rootView,
                x,
                y,
                startRadius,
                endRadius
            )
            rootView.visibility = View.VISIBLE
            anim.start()
        }
    }
}