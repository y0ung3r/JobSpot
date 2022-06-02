package com.anyjob.ui.animations.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.util.Range
import android.view.View
import android.view.ViewGroup
import androidx.transition.*
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.drag.DragTo
import com.anyjob.ui.animations.fade.FadeParameters
import com.anyjob.ui.animations.slide.SlideParameters
import com.anyjob.ui.animations.slide.extensions.asGravity

/**
 * Запускает анимацию
 */
private fun View.beginAnimation(transition: Transition, trigger: () -> Unit) {
    val sceneView = parent as ViewGroup
    TransitionManager.beginDelayedTransition(sceneView, transition)

    trigger()
}

/**
 * Устанавливает подходящее visibility для View, чтобы анимация запускалась правильно
 */
private fun View.ensureVisibilityValid(mode: VisibilityMode) = when (mode) {
    VisibilityMode.Show -> visibility = View.GONE
    VisibilityMode.Hide -> visibility = View.VISIBLE
}

/**
 * Конфигурирует и запускает Fade анимацию
 * @param parameters Параметры анимации
 */
fun View.fade(parameters: FadeParameters) {
    val fadeTransition = AutoTransition().apply {
        duration = parameters.animationLength
        startDelay = parameters.delayBeforeStart
    }

    ensureVisibilityValid(parameters.mode)
    beginAnimation(fadeTransition) {
        visibility = parameters.mode.asVisibility()
    }
}

/**
 * Конфигурирует и запускает Slide анимацию
 * @param parameters Параметры анимации
 */
fun View.slide(parameters: SlideParameters) {
    val edge = parameters.from.asGravity()

    val slideTransition: Transition = Slide(edge).apply {
        addTarget(this@slide)
    }

    val transitionSet = TransitionSet().apply {
        ordering = TransitionSet.ORDERING_TOGETHER
        addTransition(slideTransition)
        addTransition(
            ChangeBounds()
        )
        duration = parameters.animationLength
        startDelay = parameters.delayBeforeStart
    }

    ensureVisibilityValid(parameters.mode)
    beginAnimation(transitionSet) {
        visibility = parameters.mode.asVisibility()
    }
}

/**
 * Запускает анимацию Slide с параметром animationLength = 300
 * @param visibilityMode Указывает как отобразить анимацию: на скрытие или на появление
 */
fun View.slide(visibilityMode: VisibilityMode) {
    val parameters = SlideParameters().apply {
        mode = visibilityMode
        animationLength = 300
    }

    slide(parameters)
}

fun View.drag(direction: DragTo, shadow: View? = null) {
    val animatorSet = AnimatorSet()

    var translation = 0.0f
    var shadowOpacityFrom = 0.0f
    var shadowOpacityTo = 0.0f
    var shadowScale = 0.0f

    when (direction) {
        DragTo.Up ->  {
            translation = -height / 10.0f
            shadowOpacityFrom = 1.0f
            shadowOpacityTo = 0.4f
            shadowScale = 1.5f
        }

        DragTo.Down ->  {
            translation = height / 25.0f
            shadowOpacityFrom = 0.4f
            shadowOpacityTo = 1.0f
            shadowScale = 1.0f
        }
    }

    val translateAnimation = ObjectAnimator.ofFloat(
        this,
        "translationY",
        translation
    )

    if (shadow != null) {
        val opacityAnimation = ObjectAnimator.ofFloat(
            shadow,
            "alpha",
            shadowOpacityFrom,
            shadowOpacityTo
        )

        val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(
            shadow,
            PropertyValuesHolder.ofFloat("scaleX", shadowScale),
            PropertyValuesHolder.ofFloat("scaleY", shadowScale)
        )

        animatorSet.playTogether(
            translateAnimation,
            opacityAnimation,
            scaleAnimation
        )
    }
    else {
        animatorSet.playTogether(translateAnimation)
    }

    animatorSet.start()
}