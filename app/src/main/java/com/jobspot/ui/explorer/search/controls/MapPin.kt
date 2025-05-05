package com.jobspot.ui.explorer.search.controls

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.jobspot.R
import com.jobspot.ui.animations.drag.DragTo
import com.jobspot.ui.animations.extensions.drag

class MapPin
@JvmOverloads
constructor(
    context: Context,
    attributes: AttributeSet? = null
) : RelativeLayout(context, attributes) {
    private val _anchor = ImageView(context)
    private val _pinView = ImageView(context)
    private val _shadowView = ImageView(context)

    var isTouchEventsDisabled = false

    override fun onFinishInflate() {
        super.onFinishInflate()

        _anchor.id = View.generateViewId()
        _pinView.id = View.generateViewId()
        _pinView.setBackgroundResource(R.drawable.map_pin)
        _shadowView.setBackgroundResource(R.drawable.map_pin_shadow)

        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val anchorParameters = LayoutParams(width, height)
        anchorParameters.addRule(CENTER_IN_PARENT, TRUE)

        val pinParameters = LayoutParams(anchorParameters)
        val shadowParameters = LayoutParams(anchorParameters)
        pinParameters.addRule(ABOVE, _anchor.id)

        val position = -1 // to add last
        addView(_anchor, position, anchorParameters)
        addView(_pinView, position, pinParameters)
        addView(_shadowView, position, shadowParameters)
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        if (motionEvent != null && !isTouchEventsDisabled) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> _pinView.drag(DragTo.Up, _shadowView)
                MotionEvent.ACTION_UP -> _pinView.drag(DragTo.Down, _shadowView)
            }
        }

        return super.dispatchTouchEvent(motionEvent)
    }
}