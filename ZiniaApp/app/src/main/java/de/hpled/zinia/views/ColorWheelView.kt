package de.hpled.zinia.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class ColorWheelView(c: Context, attr: AttributeSet?) : View(c, attr) {

    private val paintColor = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintWhite = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colorGradient by lazy {
        SweepGradient(width / 2f, height / 2f, sweepColors, null)
    }
    private val whiteGradient by lazy {
        RadialGradient(width / 2f, height / 2f, width / 2f, radialColors, null, Shader.TileMode.CLAMP)
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
        canvas?.drawCircle(width / 2f, height / 2f, width / 2f, paintColor)
        canvas?.drawCircle(width / 2f, height / 2f, width / 2f, paintWhite)
        canvas?.drawCircle(width / 2f, height / 2f, width / 2f, paintWhite)
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