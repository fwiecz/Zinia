package de.hpled.zinia.fragments


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout

import de.hpled.zinia.R
import de.hpled.zinia.dto.ColorDTO
import de.hpled.zinia.views.BrightnessWarmthView
import de.hpled.zinia.views.ColorPickerView
import de.hpled.zinia.views.OnBrightnessWarmthChangedListener
import de.hpled.zinia.views.OnColorChangedListener
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

/**
 * The User can intuitively pick a targetColor.
 */
class ColorPickerFragment : Fragment(), OnColorChangedListener, OnBrightnessWarmthChangedListener {
    private val handler = Handler()
    private lateinit var root: LinearLayout
    private val colorPicker by lazy {
        root.findViewById<ColorPickerView>(R.id.colorPickerView)
    }
    private val slider by lazy {
        root.findViewById<BrightnessWarmthView>(R.id.brightnessWarmthView)
    }
    private val doneButton by lazy {
        root.findViewById<ImageButton>(R.id.colorPickerFragmentDone)
    }
    val onColorChangedListener = mutableSetOf<OnColorChangedListener>()
    val onBrightnessWarmthChangedListener = mutableSetOf<OnBrightnessWarmthChangedListener>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_color_picker, container, false) as LinearLayout
        return root
    }

    fun setThumbToColor(color: ColorDTO) {
        colorPicker.setThumbToColor(color)
    }

    fun setThumbToColor(color: Int) {
        colorPicker.setThumbToColor(
            ColorDTO(Color.red(color), Color.green(color), Color.blue(color))
        )
    }

    fun setBrightness(value: Int) {
        slider.setBrightness(value)
    }

    fun setWarmth(value: Int) {
        slider.setWarmth(value)
    }

    fun setOnDoneListener(listener: View.OnClickListener?) {
        doneButton.visibility = if(listener != null)View.VISIBLE else View.GONE
        doneButton.setOnClickListener(listener)
    }

    fun setOnDoneListener( listener: (() -> Unit)? ) {
        doneButton.visibility = if(listener != null)View.VISIBLE else View.GONE
        doneButton.setOnClickListener{ listener?.run { listener() } }
    }

    override fun onColorChanged(color: Int, final: Boolean) {
        onColorChangedListener.forEach { it.onColorChanged(color, final) }
    }

    override fun onBrightnessChanged(value: Int, final: Boolean) {
        onBrightnessWarmthChangedListener.forEach { it.onBrightnessChanged(value, final) }
    }

    override fun onWarmthChanged(value: Int, final: Boolean) {
        onBrightnessWarmthChangedListener.forEach { it.onWarmthChanged(value, final) }
    }

    override fun onStart() {
        super.onStart()
        handler.post { colorPicker.invalidate() }
        colorPicker.onColorChangedListener.apply {
            clear()
            add(this@ColorPickerFragment)
        }
        slider.listener.apply {
            clear()
            add(this@ColorPickerFragment)
        }
    }
}
