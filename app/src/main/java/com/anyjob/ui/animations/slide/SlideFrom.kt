package com.anyjob.ui.animations.slide

import android.view.Gravity

/**
 * Определяет к какой стороне Layout'а необходимо прикрепить View, которая будет анимироваться
 */
enum class SlideFrom {
    /**
     * Сверху
     */
    Top,

    /**
     * Снизу
     */
    Bottom,

    /**
     * Слева
     */
    Left,

    /**
     * Справа
     */
    Right
}

/**
 * Преобразует SlideFrom в Gravity
 */
fun SlideFrom.asGravity(): Int = when (this) {
    SlideFrom.Top -> Gravity.TOP
    SlideFrom.Bottom -> Gravity.BOTTOM
    SlideFrom.Left -> Gravity.START
    SlideFrom.Right -> Gravity.END
}