package com.anyjob.ui.extensions

import kotlin.math.ln

fun getZoomLevel(radius: Double): Float {
    val radius = radius + radius / 2.0f
    val scale = radius / 500.0f
    val zoomLevel = 16.0 - ln(scale) / ln(2.0)
    return zoomLevel.toFloat()
}