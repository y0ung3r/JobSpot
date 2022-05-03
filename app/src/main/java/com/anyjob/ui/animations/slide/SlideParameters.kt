package com.anyjob.ui.animations.slide

import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.SlideDirection

/**
 * Параметря для Slide анимации
 */
class SlideParameters : AnimationParameters() {
    /**
     * Направление движения для анимации
     */
    var direction: SlideDirection = SlideDirection.FromTopToCenter
}