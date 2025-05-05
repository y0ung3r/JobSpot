package com.jobspot.ui.animations

/**
 * Представляет параметры для анимации
 */
open class AnimationParameters {
    /**
     * Время с момента начала выполнения анимации до ее окончания в миллисекундах
     */
    var animationLength: Long = 1000

    /**
     * Задержка перед началом выполнения анимации в миллисекундах
     */
    var delayBeforeStart: Long = 0
}
