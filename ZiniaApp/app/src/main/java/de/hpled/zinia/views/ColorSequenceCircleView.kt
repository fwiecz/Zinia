package de.hpled.zinia.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.shape.ShapePath
import de.hpled.zinia.R
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import kotlin.math.acos
import kotlin.math.min

interface OnSegmentClickListener {
    fun onSegmentClick(index: Int, color: Int)
}

class ColorSequenceCircleView(c: Context, attr: AttributeSet? = null) : FrameLayout(c, attr),
    View.OnTouchListener {
    private var dia = 10
    private var viewRect = RectF()
    private var innerRadius: Float = 0f
    private val angleReference = Vector2D(1.0, 0.0)
    private var pressedSegment: ColorSequenceSegmentView? = null
    val onSegmentClickListener = mutableSetOf<OnSegmentClickListener>()

    init {
        setWillNotDraw(false)
        if (attr != null) {
            val ta = context.theme.obtainStyledAttributes(
                attr,
                R.styleable.ColorSequenceCircleView,
                0,
                0
            )
            innerRadius = ta.getDimension(R.styleable.ColorSequenceCircleView_innerRadius, 0f)
        }
        setOnTouchListener(this)
    }

    fun setColorList(colors: List<Int>) {
        updateMetrics()
        while(colors.size < childCount) {
            removeViewAt(0)
        }

        val params = LayoutParams(dia, dia).apply { gravity = Gravity.CENTER }
        val degreesPerSegment = 360f / colors.size

        colors.forEachIndexed { index, i ->
            if(index < childCount) {
                val seg = getChildAt(index) as ColorSequenceSegmentView
                seg.setAngle(degreesPerSegment * index, degreesPerSegment)
                seg.setColor(i)
            }
            else {
                val seg = ColorSequenceSegmentView(context, innerRadius)
                addView(seg, params)
                post {
                    seg.setAngle(degreesPerSegment * index, degreesPerSegment)
                    seg.setColor(i)
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null && childCount > 0) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val center = Vector2D(width / 2.0, height / 2.0)
                val pos = Vector2D(event.x.toDouble(), event.y.toDouble())
                val diff = pos.subtract(center)
                if (diff.norm.let { it < dia / 2 && it > innerRadius }) {
                    val angleRaw =
                        acos(diff.dotProduct(angleReference) / (diff.norm * angleReference.norm))
                    val angle = ((Math.toDegrees(angleRaw) * Math.signum(diff.y)) + 360) % 360
                    val index = (angle / (360f / childCount)).toInt()
                    pressedSegment = getChildAt(index) as ColorSequenceSegmentView
                    pressedSegment?.setPressed(event.x, event.y)
                }
            }
        }
        return false
    }

    fun updateMetrics() {
        dia = min(width, height)
        val offsetX = (width - dia) / 2f
        val offsetY = (height - dia) / 2f
        viewRect = RectF(offsetX, offsetY, dia.toFloat() + offsetX, dia.toFloat() + offsetY)
    }

    companion object {
        private const val SWEEP_MARGIN = 12.0f
    }
}