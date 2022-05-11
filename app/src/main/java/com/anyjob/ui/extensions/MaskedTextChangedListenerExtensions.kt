package com.anyjob.ui.extensions

import com.redmadrobot.inputmask.MaskedTextChangedListener

/**
 * Метод-расширения для упрощения настройки ValueListener в MaskedTextChangedListener
 */
fun MaskedTextChangedListener.onTextChanged(onTextChanged: (maskFilled: Boolean, extractedValue: String, formattedValue: String) -> Unit) {
    valueListener = object : MaskedTextChangedListener.ValueListener {
        override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {
            onTextChanged(maskFilled, extractedValue, formattedValue)
        }
    }
}