package com.anyjob.ui.controls

import android.content.Context
import android.content.res.Resources
import android.graphics.Color.red
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.updateMargins
import com.anyjob.R
import com.anyjob.ui.animations.drag.DragTo
import com.anyjob.ui.animations.extensions.drag

class MapPin
@JvmOverloads
constructor(
    context: Context,
    attributes: AttributeSet? = null
) : RelativeLayout(context, attributes) {
    private val _pinView = ImageView(context)
    private val _shadowView = ImageView(context)

    override fun onFinishInflate() {
        super.onFinishInflate()

        _pinView.id = View.generateViewId()
        _pinView.setImageResource(R.drawable.map_pin)
        _shadowView.setBackgroundResource(R.drawable.map_pin_shadow)

        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val pinParameters = LayoutParams(width, height)

        pinParameters.addRule(CENTER_IN_PARENT, TRUE)

        val shadowParameters = LayoutParams(pinParameters)
        shadowParameters.addRule(BELOW, _pinView.id)

        shadowParameters.updateMargins(
            top = -_shadowView.background.intrinsicHeight.div(2)
        )

        val position = -1 // to add last
        addView(_pinView, position, pinParameters)
        addView(_shadowView, position, shadowParameters)
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        if (motionEvent != null) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> _pinView.drag(DragTo.Up, _shadowView)
                MotionEvent.ACTION_UP -> _pinView.drag(DragTo.Down, _shadowView)
            }
        }

        return super.dispatchTouchEvent(motionEvent)
    }
}