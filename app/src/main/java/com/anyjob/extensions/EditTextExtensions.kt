package com.anyjob.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

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