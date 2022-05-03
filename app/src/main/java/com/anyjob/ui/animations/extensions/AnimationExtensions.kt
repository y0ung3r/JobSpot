package com.anyjob.ui.animations.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.annotation.FloatRange
import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.SlideDirection
import com.anyjob.ui.animations.fade.FadeOutParameters
import com.anyjob.ui.animations.slide.SlideParameters

/**
 * Конфигурирует Fade анимацию
 * @param from Начальное значение
 * @param to Конечное значение
 * @param parameters Параметры анимации
 */
private fun View.fade(@FloatRange(from = 0.0, to = 1.0) from: Float, @FloatRange(from = 0.0, to = 1.0) to: Float, parameters: AnimationParameters): ViewPropertyAnimator {
    alpha = from
    visibility = View.VISIBLE

    val animator = animate()

    animator.setInterpolator(
        AccelerateDecelerateInterpolator()
    )
    .alpha(to)
    .setDuration(parameters.animationLength)
    .setStartDelay(parameters.delayBeforeStart)

    return animator
}

/**
 * Конфигурирует Fade in анимацию
 * @param parameters Параметры анимации
 */
fun View.fadeIn(parameters: AnimationParameters) {
    fade(0.0f, 1.0f, parameters).start()
}

/**
 * Конфигурирует Fade out анимацию
 * @param parameters Параметры анимации
 */
fun View.fadeOut(parameters: FadeOutParameters) {
    val animator = fade(1.0f, 0.0f, parameters)

    animator.withEndAction {
        visibility = if (parameters.goneAfterAnimation) View.GONE else View.INVISIBLE
    }
    .start()
}

/**
 * Конфигурирует Slide анимацию
 * @param parameters Параметры анимации
 */
fun View.slide(parameters: SlideParameters) {
    visibility = View.VISIBLE

    var fromX: Float = 0.0f
    var fromY: Float = 0.0f
    var toX: Float = 0.0f
    var toY: Float = 0.0f

    when (parameters.direction) {
        SlideDirection.FromTopToCenter -> {
            fromY = -1.0f
            toY = 0.0f
        }

        SlideDirection.FromCenterToTop -> {
            fromY = 0.0f
            toY = -1.0f
        }

        SlideDirection.FromCenterToLeft -> {
            fromX = 0.0f
            toX = -1.0f
        }

        SlideDirection.FromCenterToRight -> {
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
        duration = parameters.animationLength
        startOffset = parameters.delayBeforeStart
    }

    startAnimation(animation)
}