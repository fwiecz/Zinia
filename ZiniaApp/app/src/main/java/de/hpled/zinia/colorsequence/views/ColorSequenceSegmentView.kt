package de.hpled.zinia.colorsequence.views

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape
import android.renderscript.Sampler
import android.view.View
import kotlin.math.min

class ColorSequenceSegmentView(c: Context, private val innerRadius: Float) : View(c) {

    private val shape = ShapeDrawable()
    private val rippleColor = Color.argb(50, 0, 0, 0)
    private val ripple = RippleDrawable(ColorStateList.valueOf(rippleColor), shape, shape)
    private val rippleRect = RectF()
    private val outerRect = RectF()
    private val innerRect = RectF()
    private var color = Color.WHITE
    private var lastangle = 0f
    private var targetStart: Float = 0f

    init {
        isClickable = false
        shape.paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth =
                STROKE
            color = Color.TRANSPARENT
        }
    }

    fun setPressed(x: Float, y: Float) {
        ripple.setHotspot(x , y - STROKE *2)
        ripple.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        postDelayed({setUntouched()}, 10)
    }

    fun setUntouched() {
        ripple.state = intArrayOf()
    }

    fun setColor(col: Int) {
        color = col
        ValueAnimator.ofArgb(shape.paint.color, col).apply {
            duration = 300
            addUpdateListener { shape.paint.color = it.animatedValue as Int; invalidate() }
            start()
        }
    }

    fun getColor() = color

    fun setAngle(start: Float, degrees: Float) {
        setAngle(targetStart, start, degrees)
    }

    fun setAngle(fromStart: Float, start: Float, degrees: Float) {
        ValueAnimator.ofFloat(fromStart, start).apply {
            duration = 500L
            addUpdateListener { targetStart = it.animatedValue as Float }
            start()
        }
        ValueAnimator.ofFloat(lastangle, degrees).apply {
            duration = 500L
            addUpdateListener {
                lastangle = it.animatedValue as Float
                setShape(targetStart, it.animatedValue as Float)
                invalidate()
            }
            start()
        }
    }

    private fun setShape(start: Float, degrees: Float) {
        background = ripple
        outerRect.set(
            STROKE,
            STROKE, width - STROKE, height - STROKE
        )
        innerRect.set(
            width / 2f - innerRadius, height / 2f - innerRadius,
            width / 2f + innerRadius, height / 2f + innerRadius
        )

        ripple.setBounds(0, 0, width, height)
        shape.setBounds(0, 0, width, height)

        val path = Path()

        val degOffInn =
            degreeOffset(
                innerRect
            )
        if (degOffInn * 2 < degrees) {
            path.addArc(
                innerRect,
                start + degrees - degOffInn,
                min(-degrees + degreeOffset(
                    innerRect
                ) * 2, -0.1f)
            )
        } else {
            path.addArc(
                innerRect,
                start + degrees / 2f,
                -0.1f
            )
        }

        path.arcTo(
            outerRect,
            start + degreeOffset(
                outerRect
            ),
            degrees - degreeOffset(
                outerRect
            ) * 2
        )

        path.close()

        shape.shape = PathShape(path, width.toFloat(), height.toFloat())
        path.computeBounds(rippleRect, false)
        ripple.setHotspotBounds(
            rippleRect.left.toInt(),
            rippleRect.top.toInt(),
            rippleRect.right.toInt(),
            rippleRect.bottom.toInt()
        )
    }

    companion object {
        private const val STROKE = 40f
        private fun degreeOffset(bounds: RectF): Float {
            return 3000 / bounds.width()
        }
    }
}