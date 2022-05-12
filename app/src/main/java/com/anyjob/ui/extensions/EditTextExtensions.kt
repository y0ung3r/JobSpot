package com.anyjob.ui.extensions

import android.text.Editable
import android.text.TextWatcher
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
        val text = editText.text.toString()

        if (reactTo == actionId) {
            action(text)
        }

        false
    }
}