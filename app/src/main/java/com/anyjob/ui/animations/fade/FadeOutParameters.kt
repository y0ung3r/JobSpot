package com.anyjob.ui.animations.fade

import com.anyjob.ui.animations.AnimationParameters

/**
 * Параметры для Fade out анимации
 */
class FadeOutParameters : AnimationParameters() {

    /**
     * Указывает нужно ли присваивать элементу значение View.GONE для visibility после выполнения анимации.
     * Если указан False, элементу будет присвоено значение View.INVISIBLE для visibility
     */
    var goneAfterAnimation: Boolean = false
}