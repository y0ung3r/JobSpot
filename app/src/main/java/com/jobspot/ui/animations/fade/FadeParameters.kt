package com.jobspot.ui.animations.fade

import com.jobspot.ui.animations.AnimationParameters
import com.jobspot.ui.animations.VisibilityMode

/**
 * Параметры для Fade анимации
 */
class FadeParameters : AnimationParameters() {
    /**
     * Указывает как нужно запустить Fade: на скрытие View или на его отображение
     */
    var mode: VisibilityMode = VisibilityMode.Show
}