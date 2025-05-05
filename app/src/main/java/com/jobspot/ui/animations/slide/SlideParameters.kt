package com.jobspot.ui.animations.slide

import com.jobspot.ui.animations.AnimationParameters
import com.jobspot.ui.animations.VisibilityMode

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

    /**
     * Запускает действие после выполнения Slide
     */
    var launchAfter: (() -> Unit)? = null
}