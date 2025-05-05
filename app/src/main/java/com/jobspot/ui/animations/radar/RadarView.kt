package com.jobspot.ui.animations.radar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import com.jobspot.R
import com.jobspot.ui.extensions.getZoomLevel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.mapview.MapView
import kotlin.math.pow

data class SearchRadar(
    val positionOnMap: Point,
    var screenPositionX: Float,
    var screenPositionY: Float,
    var radius: Float,
    val paint: Paint,
    var cameraListener: CameraListener? = null
)

data class RadarAnimation(
    val mapView: MapView,
    val searchRadar: SearchRadar,
    val animator: ValueAnimator
)

class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val fillColor = resources.getString(R.color.light_purple).toColorInt()
    private val pendingAnimations: MutableList<RadarAnimation> = mutableListOf()

    fun startNewPulse(mapView: MapView, positionOnMap: Point, radarParameters: RadarParameters) {
        val screenCoordinates = mapView.mapWindow.worldToScreen(positionOnMap)
            ?: return

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = fillColor
        }

        val searchRadar = SearchRadar(positionOnMap, screenCoordinates.x, screenCoordinates.y, 0.0f, paint)

        val cameraListener = CameraListener { _, _, _, _ ->
            val position = mapView.mapWindow.worldToScreen(positionOnMap)
                ?: return@CameraListener

            searchRadar.screenPositionX = position.x
            searchRadar.screenPositionY = position.y

            invalidate()
        }

        searchRadar.cameraListener = cameraListener
        mapView.mapWindow.map.addCameraListener(cameraListener)

        ValueAnimator
            .ofFloat(0.0f, radarParameters.maxRadius)
            .apply {
                duration = radarParameters.animationLength
                startDelay = radarParameters.delayBeforeStart
                interpolator = AccelerateDecelerateInterpolator()

                if (radarParameters.infinite) {
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE
                }

                addUpdateListener {
                    searchRadar.radius = it.animatedFraction * radarParameters.maxRadius

                    if (radarParameters.infinite) {
                        val alphaChannel = ((1.0f - it.animatedFraction) * Color.alpha(fillColor)).toInt()
                        searchRadar.paint.color = ColorUtils.setAlphaComponent(fillColor, alphaChannel)
                    }

                    invalidate()
                }

                pendingAnimations.add(RadarAnimation(mapView, searchRadar, this))

                start()
            }
    }

    fun stopLastPulse(radarParameters: RadarParameters) {
        pendingAnimations.lastOrNull()?.let { animation ->
            animation.animator.apply {
                cancel()
                removeAllUpdateListeners()
                removeAllListeners()

                duration = radarParameters.animationLength
                startDelay = radarParameters.delayBeforeStart
                interpolator = AccelerateDecelerateInterpolator()

                val maxRadius = animation.searchRadar.radius
                setFloatValues(0.0f, maxRadius)

                repeatMode = ValueAnimator.RESTART
                repeatCount = 0

                addUpdateListener {
                    animation.searchRadar.radius = it.animatedFraction * maxRadius
                    invalidate()
                }

                doOnEnd {
                    animation.searchRadar.cameraListener?.let { animation.mapView.mapWindow.map.removeCameraListener(it) }
                    animation.animator.removeAllUpdateListeners()
                    animation.animator.removeAllListeners()
                    pendingAnimations.remove(animation)
                }

                reverse()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        pendingAnimations.forEach {
            val shouldDraw =
                it.searchRadar.screenPositionX + it.searchRadar.radius >= 0 &&
                        it.searchRadar.screenPositionY + it.searchRadar.radius >= 0 &&
                        it.searchRadar.screenPositionX - it.searchRadar.radius <= width &&
                        it.searchRadar.screenPositionY - it.searchRadar.radius <= height

            if (shouldDraw) {
                val zoomLevel = it.mapView.mapWindow.map.cameraPosition.zoom
                val adaptedRadius = it.searchRadar.radius / (1f / (2.0f.pow(zoomLevel - 16.0f)))

                canvas.drawCircle(it.searchRadar.screenPositionX, it.searchRadar.screenPositionY, adaptedRadius, it.searchRadar.paint)
            }
        }
    }
}