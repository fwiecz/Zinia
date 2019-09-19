package de.hpled.zinia.views

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import de.hpled.zinia.R
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import kotlin.math.acos
import kotlin.math.min


interface OnColorChangedListener {
    /**
     * The selected targetColor has changed.
     * @param final whether the user has stopped giving input
     */
    fun onColorChanged(color: Int, final: Boolean)
}

class ColorPickerView(c: Context, attr: AttributeSet) : RelativeLayout(c, attr) {

    private val frame by lazy { findViewById<FrameLayout>(R.id.colorWheelFrame) }
    private val thumb by lazy { findViewById<View>(R.id.colorWheelThumb) }
    private val mHandler = Handler()
    private lateinit var center : Vector2D
    private val angleReference = Vector2D(0.0, 1.0)
    private var radius: Double = 0.0
    val onColorChangedListener = mutableListOf<OnColorChangedListener>()

    private val onTouchListener = OnTouchListener { view, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    thumb.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_MOVE -> {
                    val v = checkThumbsStaysInBounds(event.x.toDouble(), event.y.toDouble())
                    thumb.x = v.x.toFloat() - (thumb.width / 2f)
                    thumb.y = v.y.toFloat() - (thumb.height / 2f)
                    informListener(v.x, v.y, false)
                }
                MotionEvent.ACTION_UP -> {
                    val v = checkThumbsStaysInBounds(event.x.toDouble(), event.y.toDouble())
                    informListener(v.x, v.y, true)
                }
            }
            view?.performClick()
            true
    }

    init {
        View.inflate(context, R.layout.view_color_picker, this)
        frame.setOnTouchListener(onTouchListener)
        thumb.visibility = View.INVISIBLE
        mHandler.post {
            center = Vector2D(frame.width / 2.0, frame.height / 2.0)
            radius = min(frame.width / 2.0, frame.height / 2.0)
        }
    }

    private fun checkThumbsStaysInBounds(x: Double, y: Double) : Vector2D {
        val v = Vector2D(x, y).subtract(center)
        if(v.norm > radius) {
            return v.normalize().scalarMultiply(radius).add(center)
        } else {
            return Vector2D(x, y)
        }
    }

    private fun informListener(x: Double, y: Double, final: Boolean) {
        val v = Vector2D(x, y).subtract(center)
        val angleRaw = acos(v.dotProduct(angleReference) / (v.norm * angleReference.norm))
        val angle = (Math.toDegrees(angleRaw) * -Math.signum(v.x)) + 180
        val length = v.norm / radius
        val color = Color.HSVToColor(floatArrayOf(angle.toFloat(), length.toFloat(), 1f))
        onColorChangedListener.forEach {
            it.onColorChanged(color, final)
        }
    }
}