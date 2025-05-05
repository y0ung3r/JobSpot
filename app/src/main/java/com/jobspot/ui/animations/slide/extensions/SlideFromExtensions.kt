package com.jobspot.ui.animations.slide.extensions

import android.view.Gravity
import com.jobspot.ui.animations.slide.SlideFrom

/**
 * Преобразует SlideFrom в Gravity
 */
fun SlideFrom.asGravity(): Int = when (this) {
    SlideFrom.Top -> Gravity.TOP
    SlideFrom.Bottom -> Gravity.BOTTOM
    SlideFrom.Left -> Gravity.START
    SlideFrom.Right -> Gravity.END
}