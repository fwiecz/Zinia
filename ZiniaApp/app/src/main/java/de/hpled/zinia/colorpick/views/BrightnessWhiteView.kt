package de.hpled.zinia.colorpick.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import de.hpled.zinia.R

interface OnBrightnessWhiteChangedListener {
    fun onBrightnessChanged(value: Int, final: Boolean)
    fun onWhiteChanged(value: Int, final: Boolean)
}

class BrightnessWhiteView(c: Context, attr: AttributeSet?) : LinearLayout(c, attr) {

    private val whiteLayout by lazy { findViewById<LinearLayout>(R.id.warmthLayout) }
    private val brSlider by lazy { findViewById<SeekBar>(R.id.brightnessSlider) }
    private val whiteSlider by lazy { findViewById<SeekBar>(R.id.warmthSlider) }
    val listener : MutableSet<OnBrightnessWhiteChangedListener> = mutableSetOf()

    var whiteIsEnabled = false
        set(value) {
            field = value
            whiteLayout.visibility = if(value)View.VISIBLE else View.GONE
        }

    private val brightnesSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(fromUser) {
                listener.forEach { it.onBrightnessChanged(progress, false) }
            }
        }
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            listener.forEach { it.onBrightnessChanged(brSlider.progress, true) }
        }
    }

    private val warmthSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(fromUser) {
                listener.forEach { it.onWhiteChanged(progress, false) }
            }
        }
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            listener.forEach { it.onWhiteChanged(whiteSlider.progress, true) }
        }
    }

    fun getWhite() = whiteSlider.progress

    fun getBrightness() = brSlider.progress

    fun setBrightness(value: Int) {
        brSlider.progress = value
    }

    fun setWarmth(value: Int) {
        whiteSlider.progress = value
    }

    init {
        View.inflate(context, R.layout.view_brightness_warmth_slider, this)
        brSlider.apply {
            max = resources.getInteger(R.integer.maxBrightness)
            progress = max
            setOnSeekBarChangeListener(brightnesSeekBarListener)
        }
        whiteSlider.apply {
            max = resources.getInteger(R.integer.maxWarmth)
            progress = 0
            setOnSeekBarChangeListener(warmthSeekBarListener)
        }
    }
}