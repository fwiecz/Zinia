package de.hpled.zinia.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ColorWheelView(c: Context, attr: AttributeSet?) : View(c, attr) {

    private val paintColor = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintWhite = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colorGradient by lazy {
        SweepGradient(width / 2f, height / 2f, sweepColors, null)
    }
    private val whiteGradient by lazy {
        RadialGradient(width / 2f, height / 2f, radius, radialColors, null, Shader.TileMode.CLAMP)
    }
    private val radius by lazy {
        min(width / 2f, height / 2f)
    }

    init {
        post {
            paintColor.shader = colorGradient
            paintWhite.shader = whiteGradient
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(width / 2f, height / 2f, radius, paintColor)
        canvas?.drawCircle(width / 2f, height / 2f, radius, paintWhite)
        canvas?.drawCircle(width / 2f, height / 2f, radius, paintWhite)
    }

    companion object {
        private val sweepColors = intArrayOf(
            Color.RED,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA,
            Color.RED
        )

        private val radialColors = intArrayOf(
            Color.WHITE,
            Color.TRANSPARENT
        )
    }
}