package com.jobspot.ui.extensions

import kotlin.math.ln

fun getZoomLevel(radius: Float): Float {
    val scale = (radius + radius / 2.0f) / 500.0f
    val zoomLevel = 16.0 - ln(scale) / ln(2.0)
    return zoomLevel.toFloat()
}