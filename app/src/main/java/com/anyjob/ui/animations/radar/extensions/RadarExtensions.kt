package com.anyjob.ui.animations.radar.extensions

import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import com.anyjob.ui.animations.VisibilityMode
import com.anyjob.ui.animations.radar.RadarParameters
import com.yandex.mapkit.mapview.MapView

fun startRadar(parameters: RadarParameters): ValueAnimator {
    return ValueAnimator().apply {
        duration = parameters.animationLength
        startDelay = parameters.delayBeforeStart
        interpolator = AccelerateDecelerateInterpolator()

        if (parameters.infinite) {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }

        val startValue = 0.0f
        setFloatValues(startValue, parameters.maxRadius)
        setEvaluator(FloatEvaluator())

        val onUpdate = parameters.onUpdate
        val onRepeat = parameters.onRepeat
        val onCancel = parameters.onCancel
        val onEnd = parameters.onEnd

        if (onUpdate != null)
            addUpdateListener {
                val ratio = it.animatedFraction * parameters.maxRadius
                val invertedRatio = (1.0f - it.animatedFraction) * parameters.maxRadius

                onUpdate.invoke(it, ratio, invertedRatio)
            }

        if (onRepeat != null)
            doOnRepeat {
                onRepeat.invoke(this)
            }

        if (onCancel != null)
            doOnCancel {
                onCancel.invoke(this)
            }

        if (onEnd != null)
            doOnEnd {
                onEnd.invoke(this)
            }

        when (parameters.mode) {
            VisibilityMode.Show -> start()
            VisibilityMode.Hide -> reverse()
        }
    }
}