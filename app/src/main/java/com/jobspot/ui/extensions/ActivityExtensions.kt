package com.jobspot.ui.extensions

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(applicationContext, message, duration).show()
}