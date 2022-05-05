package com.anyjob.ui.animations

import android.view.View

/**
 * Определяет режим выполнения для Slide
 */
enum class VisibilityMode {
    /**
     * Показать анимируемый View
     */
    Show,

    /**
     * Скрыть анимируемый View
     */
    Hide,
}

/**
 * Преобразует VisibilityMode в visibility
 */
fun VisibilityMode.asVisibility(): Int = when (this) {
    VisibilityMode.Show -> View.VISIBLE
    VisibilityMode.Hide -> View.GONE
}