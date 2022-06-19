package com.anyjob.ui.animations.radar.extensions

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.radar.RadarParameters

fun startRadar(parameters: RadarParameters) {
    ValueAnimator().apply {
        duration = parameters.animationLength
        startDelay = parameters.delayBeforeStart
        interpolator = AccelerateDecelerateInterpolator()

        if (parameters.infinity) {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }

        val startValue = 0.0f
        setFloatValues(startValue, parameters.maxRadius)
        setEvaluator(
            FloatEvaluator()
        )

        addUpdateListener {
            val ratio = it.animatedFraction * parameters.maxRadius
            val invertedRatio = (1.0f - it.animatedFraction) * parameters.maxRadius

            parameters.onUpdate?.invoke(
                ratio.toDouble(),
                invertedRatio.toDouble()
            )
        }

        when (parameters.mode) {
            VisibilityMode.Show -> start()
            VisibilityMode.Hide -> reverse()
        }
    }
}