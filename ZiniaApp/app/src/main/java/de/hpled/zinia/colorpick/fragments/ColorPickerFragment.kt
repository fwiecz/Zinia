package de.hpled.zinia.colorpick.fragments


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import de.hpled.zinia.R
import de.hpled.zinia.colorpick.views.BrightnessWhiteView
import de.hpled.zinia.colorpick.views.ColorPickerView
import de.hpled.zinia.colorpick.views.OnBrightnessWhiteChangedListener
import de.hpled.zinia.colorpick.views.OnColorChangedListener
import de.hpled.zinia.dto.ColorDTO
import de.hpled.zinia.xcolor.Xcolor

interface OnXcolorChangeListener {
    enum class Type {
        RGB_CHANGED,
        W_CHANGED,
        BRIGHTNESS_CHANGED
    }

    fun onXcolorChange(xcolor: Xcolor, type: Type, final: Boolean)
}

/**
 * The User can intuitively pick brightness targetColor.
 */
class ColorPickerFragment : Fragment(), OnColorChangedListener,
    OnBrightnessWhiteChangedListener {
    private val handler = Handler()
    private lateinit var root: LinearLayout
    private val colorPicker by lazy {
        root.findViewById<ColorPickerView>(R.id.colorPickerView)
    }
    private val slider by lazy {
        root.findViewById<BrightnessWhiteView>(R.id.brightnessWarmthView)
    }
    private val doneButton by lazy {
        root.findViewById<ImageButton>(R.id.colorPickerFragmentDone)
    }
    val onColorChangedListener = mutableSetOf<OnColorChangedListener>()
    val onBrightnessWarmthChangedListener = mutableSetOf<OnBrightnessWhiteChangedListener>()
    val onXcolorChangeListener = mutableSetOf<OnXcolorChangeListener>()

    var warmthIsEnabled = false
        set(value) {
            field = value
            slider.whiteIsEnabled = value
            colorPicker.updateMetrics()
        }

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
        colorPicker.setThumbToColor(ColorDTO.from(color))
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
        onXcolorChangeListener.forEach {
            val xcol = Xcolor.fromColor(color).apply {
                w = slider.getWhite()
                brightness = slider.getBrightness()
            }
            it.onXcolorChange(xcol, OnXcolorChangeListener.Type.RGB_CHANGED, final)
        }
    }

    override fun onBrightnessChanged(value: Int, final: Boolean) {
        onBrightnessWarmthChangedListener.forEach { it.onBrightnessChanged(value, final) }
        onXcolorChangeListener.forEach {
            val xcol = Xcolor.fromColor(colorPicker.getColor()).apply {
                w = slider.getWhite()
                brightness = value
            }
            it.onXcolorChange(xcol, OnXcolorChangeListener.Type.BRIGHTNESS_CHANGED, final)
        }
    }

    override fun onWhiteChanged(value: Int, final: Boolean) {
        onBrightnessWarmthChangedListener.forEach { it.onWhiteChanged(value, final) }
        onXcolorChangeListener.forEach {
            val xcol = Xcolor.fromColor(colorPicker.getColor()).apply {
                w = value
                brightness = slider.getBrightness()
            }
            it.onXcolorChange(xcol, OnXcolorChangeListener.Type.W_CHANGED, final)
        }
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
