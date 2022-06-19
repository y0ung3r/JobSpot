package com.anyjob.ui.animations.fade

import com.anyjob.ui.animations.AnimationParameters
import com.anyjob.ui.animations.VisibilityMode

/**
 * Параметры для Fade анимации
 */
class FadeParameters : AnimationParameters() {
    /**
     * Указывает как нужно запустить Fade: на скрытие View или на его отображение
     */
    var mode: VisibilityMode = VisibilityMode.Show
}