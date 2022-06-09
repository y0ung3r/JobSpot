package com.anyjob.ui.animations.radar

import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.VisibilityMode

/**
 * Параметры для Fade анимации
 */
class RadarParameters : AnimationParameters() {
    /**
     * Указывает как нужно запустить Fade: на скрытие View или на его отображение
     */
    var mode: VisibilityMode = VisibilityMode.Show

    /**
     * Указывает нужно ли проигрывать анимацию бесконечно
     */
    var infinity: Boolean = false

    /**
     * Радиус
     */
    var maxRadius: Float = 0.0f

    /**
     *  Делегат, вызываемый с каждый шагом анимации
     */
    var onUpdate: ((radiusFraction: Double) -> Unit)? = null
}