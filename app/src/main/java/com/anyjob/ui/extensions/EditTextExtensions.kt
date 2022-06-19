package com.anyjob.ui.extensions

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.redmadrobot.inputmask.MaskedTextChangedListener

/**
 * Метод-расширения для упрощения настройки действия при afterTextChanged в EditText
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(
                editable.toString()
            )
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Ignore...
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // Ignore...
        }
    })
}

/**
 * Метод-расширения для закрепления MaskedTextChangedListener к EditText'у
 */
fun EditText.attachMaskedTextChangedListener(listener: MaskedTextChangedListener) {
    addTextChangedListener(listener)
    onFocusChangeListener = listener
}

/**
 * Метод-расширения, позволяющий выполнить действие при отправке действия с клавиатуры
 */
fun EditText.onEditorActionReceived(reactTo: Int, action: (text: String) -> Unit) {
    setOnEditorActionListener { editText, actionId, _ ->
        val isReceived = reactTo == actionId
        val text = editText.text.toString()

        if (isReceived) {
            action(text)
        }

        return@setOnEditorActionListener isReceived
    }
}

/**
 * Метод-расширения, добавляющий поддержку масок на EditText
 */
fun EditText.setupMask(mask: String, onChanged: (maskFilled: Boolean, extractedValue: String, formattedValue: String) -> Unit) {
    val editText = this
    val maskedTextChangedListener = MaskedTextChangedListener(mask, editText)
    maskedTextChangedListener.onTextChanged(onChanged)
    editText.attachMaskedTextChangedListener(maskedTextChangedListener)
}

/**
 * Метод-расширения, возвращающий фильтр указанного типа
 */
inline fun <reified TInputFilter> EditText.getFilter(): TInputFilter? {
    val filter = filters.firstOrNull {
        filter -> filter is TInputFilter
    }

    return filter as TInputFilter?
}

/**
 * Метод-расширения, возвращающий максимальное количество символов, которое можно вписать в текстовое поле
 */
fun EditText.getMaxLength(): Int {
    val lengthFilter = getFilter<InputFilter.LengthFilter>() ?: return Int.MAX_VALUE;
    return lengthFilter.max
}