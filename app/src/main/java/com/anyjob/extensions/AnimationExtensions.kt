package com.anyjob.extensions

import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.*
import androidx.annotation.FloatRange
import androidx.transition.Slide.GravityFlag

/**
 * Конфигурирует Fade анимацию
 */
private fun View.fade(@FloatRange(from = 0.0, to = 1.0) from: Float, @FloatRange(from = 0.0, to = 1.0) to: Float, duration: Long): ViewPropertyAnimator {
    alpha = from
    visibility = View.VISIBLE

    val animator = animate().setInterpolator(
        AccelerateDecelerateInterpolator()
    )
    .alpha(to)
    .setDuration(duration)

    return animator
}

/**
 * Конфигурирует Fade in анимацию
 */
fun View.fadeIn(duration: Long) {
    fade(0.0f, 1.0f, duration).start()
}

/**
 * Конфигурирует Fade out анимацию
 */
fun View.fadeOut(duration: Long, goneAfterAnimation: Boolean = false) {
    val animator = fade(1.0f, 0.0f, duration)

    animator.withEndAction {
        visibility = if (goneAfterAnimation) View.GONE else View.INVISIBLE
    }
    .start()
}

/**
 * Конфигурирует Slide анимацию
 */
private fun View.slideTo(@GravityFlag direction: Int, duration: Long) {
    visibility = View.VISIBLE

    var fromX: Float = 0.0f
    var fromY: Float = 0.0f
    var toX: Float = 0.0f
    var toY: Float = 0.0f

    when (direction) {
        Gravity.BOTTOM -> {
            fromY = -1.0f
            toY = 0.0f
        }

        Gravity.TOP -> {
            fromY = 0.0f
            toY = -1.0f
        }

        Gravity.LEFT -> {
            fromX = 0.0f
            toX = -1.0f
        }

        Gravity.RIGHT -> {
            fromX = 0.0f
            toX = 1.0f
        }
    }

    val animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT,
        fromX,
        Animation.RELATIVE_TO_PARENT,
        toX,
        Animation.RELATIVE_TO_PARENT,
        fromY,
        Animation.RELATIVE_TO_PARENT,
        toY
    )

    animation.apply {
        fillAfter = true
        setDuration(duration)
    }

    startAnimation(animation)
}

/**
 * Конфигурирует Slide down анимацию
 */
fun View.slideDown(duration: Long) = slideTo(Gravity.BOTTOM, duration)