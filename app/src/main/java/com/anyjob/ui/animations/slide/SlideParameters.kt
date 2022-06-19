package com.anyjob.ui.animations.slide

import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.VisibilityMode

/**
 * Параметря для Slide анимации
 */
class SlideParameters : AnimationParameters() {
    /**
     * Сторона Layout, к которой будет прикреплен анимируемый View
     */
    var from: SlideFrom = SlideFrom.Top

    /**
     * Указывает как нужно запустить Slide: на скрытие View или на его отображение
     */
    var mode: VisibilityMode = VisibilityMode.Show
}