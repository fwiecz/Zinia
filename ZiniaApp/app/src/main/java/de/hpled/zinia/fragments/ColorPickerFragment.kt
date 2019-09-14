package de.hpled.zinia.fragments


import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.SweepGradient
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout

import de.hpled.zinia.R
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

/**
 * The User can intuitively pick a color.
 */
class ColorPickerFragment : Fragment() {

    private lateinit var root: ConstraintLayout
    private val frame by lazy { root.findViewById<FrameLayout>(R.id.colorWheelFrame) }
    private val thumb by lazy { root.findViewById<View>(R.id.colorWheelThumb) }
    private val handler = Handler()
    private lateinit var center : Vector2D
    private var radius: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_color_picker, container, false) as ConstraintLayout
        return root
    }

    override fun onStart() {
        super.onStart()
        frame.setOnTouchListener(onTouchListener)
        thumb.visibility = View.INVISIBLE
        handler.post {
            center = Vector2D(frame.width / 2.0, frame.height / 2.0)
            radius = frame.width / 2.0
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

    private val onTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    thumb.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_MOVE -> {
                    val v = checkThumbsStaysInBounds(event.x.toDouble(), event.y.toDouble())
                    thumb.x = v.x.toFloat() - (thumb.width / 2f)
                    thumb.y = v.y.toFloat() - (thumb.height / 2f)
                }
            }
            v?.performClick()
            return true
        }
    }
}
