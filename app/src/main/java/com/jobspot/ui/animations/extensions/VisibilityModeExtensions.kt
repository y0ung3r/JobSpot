package com.jobspot.ui.animations.extensions

import android.view.View
import androidx.transition.Fade
import com.jobspot.ui.animations.VisibilityMode

/**
 * Преобразует VisibilityMode в visibility
 */
fun VisibilityMode.asVisibility(): Int = when (this) {
    VisibilityMode.Show -> View.VISIBLE
    VisibilityMode.Hide -> View.GONE
}

/**
 * Преобразует VisibilityMode в FadingMode
 */
fun VisibilityMode.asFadingMode(): Int = when (this) {
    VisibilityMode.Show -> Fade.IN
    VisibilityMode.Hide -> Fade.OUT
}