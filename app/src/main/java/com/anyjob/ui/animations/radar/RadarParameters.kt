package com.anyjob.ui.animations.radar

import android.animation.ValueAnimator
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
    var infinite: Boolean = false

    /**
     * Радиус
     */
    var maxRadius: Float = 0.0f

    /**
     *  Делегат, вызываемый после отмены операции
     */
    var onCancel: ((animator: ValueAnimator) -> Unit)? = null

    /**
     *  Делегат, вызываемый после полного окончания анимации
     */
    var onEnd: ((animator: ValueAnimator) -> Unit)? = null

    /**
     *  Делегат, вызываемый перед повторением анимации
     */
    var onRepeat: ((animator: ValueAnimator) -> Unit)? = null

    /**
     *  Делегат, вызываемый с каждый шагом анимации
     */
    var onUpdate: ((animator: ValueAnimator, radiusFraction: Float, invertedRadiusFraction: Float) -> Unit)? = null
}