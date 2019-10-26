package de.hpled.zinia.colorpick.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class ColorWheelView(c: Context, attr: AttributeSet?) : View(c, attr) {

    private val paintColor = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintWhite = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        updateMetrics()
    }

    fun updateMetrics() {
        post {
            paintColor.shader = getColorGradient()
            paintWhite.shader = getWhiteGradient()
            println("W: $width, H: $height")
            invalidate()
        }
    }

    private fun getColorGradient() : SweepGradient {
        return SweepGradient(width / 2f, height / 2f, sweepColors, null)
    }

    private fun getWhiteGradient() : RadialGradient {
        return RadialGradient(width / 2f, height / 2f, max(radius(), 100f),
            radialColors, null, Shader.TileMode.CLAMP)
    }

    private fun radius () : Float {
        return min(width / 2f, height / 2f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(width / 2f, height / 2f, radius(), paintColor)
        canvas?.drawCircle(width / 2f, height / 2f, radius(), paintWhite)
        canvas?.drawCircle(width / 2f, height / 2f, radius(), paintWhite)
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