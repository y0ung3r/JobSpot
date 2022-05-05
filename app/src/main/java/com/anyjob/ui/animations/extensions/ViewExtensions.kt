package com.anyjob.ui.animations.extensions

import android.view.View
import android.view.ViewGroup
import androidx.transition.*
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.asVisibility
import com.anyjob.ui.animations.fade.FadeParameters
import com.anyjob.ui.animations.slide.SlideParameters
import com.anyjob.ui.animations.slide.asGravity

/**
 * Устанавливает подходящее visibility для View, чтобы анимация запускалась правильно
 */
private fun View.ensureVisibilityValid(mode: VisibilityMode) = when (mode) {
    VisibilityMode.Show -> visibility = View.GONE
    VisibilityMode.Hide -> visibility = View.VISIBLE
}

/**
 * Конфигурирует Fade анимацию
 * @param parameters Параметры анимации
 */
fun View.fade(parameters: FadeParameters) {
    val fadeTransition = AutoTransition().apply {
        duration = parameters.animationLength
        startDelay = parameters.delayBeforeStart
    }

    ensureVisibilityValid(parameters.mode)

    val sceneView = parent as ViewGroup
    TransitionManager.beginDelayedTransition(sceneView, fadeTransition)

    visibility = parameters.mode.asVisibility()
}

/**
 * Конфигурирует Slide анимацию
 * @param parameters Параметры анимации
 */
fun View.slide(parameters: SlideParameters) {
    val edge = parameters.from.asGravity()

    val slideTransition: Transition = Slide(edge).apply {
        addTarget(this@slide)
    }

    var transitionSet = TransitionSet().apply {
        ordering = TransitionSet.ORDERING_TOGETHER
        addTransition(slideTransition)
        addTransition(
            ChangeBounds()
        )
        duration = parameters.animationLength
        startDelay = parameters.delayBeforeStart
    }

    ensureVisibilityValid(parameters.mode)

    val sceneView = parent as ViewGroup
    TransitionManager.beginDelayedTransition(sceneView, transitionSet)

    visibility = parameters.mode.asVisibility()
}