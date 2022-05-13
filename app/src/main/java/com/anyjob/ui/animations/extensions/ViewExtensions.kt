package com.anyjob.ui.animations.extensions

import android.view.View
import android.view.ViewGroup
import androidx.transition.*
import com.anyjob.ui.animations.VisibilityMode
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