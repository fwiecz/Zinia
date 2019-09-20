package de.hpled.zinia.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import de.hpled.zinia.R

interface OnBrightnessWarmthChangedListener {
    fun onBrightnessChanged(value: Int, final: Boolean)
    fun onWarmthChanged(value: Int, final: Boolean)
}

class BrightnessWarmthView(c: Context, attr: AttributeSet?) : LinearLayout(c, attr) {

    private val warmth by lazy { findViewById<LinearLayout>(R.id.warmthLayout) }
    private val brSlider by lazy { findViewById<SeekBar>(R.id.brightnessSlider) }
    private val warmSlider by lazy { findViewById<SeekBar>(R.id.warmthSlider) }
    val listener : MutableSet<OnBrightnessWarmthChangedListener> = mutableSetOf()

    var warmthIsEnabled = false
        set(value) {
            field = value
            warmth.visibility = if(value)View.VISIBLE else View.GONE
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
                listener.forEach { it.onWarmthChanged(progress, false) }
            }
        }
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            listener.forEach { it.onWarmthChanged(warmSlider.progress, true) }
        }
    }

    fun setBrightness(value: Int) {
        brSlider.progress = value
    }

    fun setWarmth(value: Int) {
        warmSlider.progress = value
    }

    init {
        View.inflate(context, R.layout.view_brightness_warmth_slider, this)
        brSlider.apply {
            max = BRIGHTNESS_MAX
            progress = BRIGHTNESS_MAX
            setOnSeekBarChangeListener(brightnesSeekBarListener)
        }
        warmSlider.apply {
            max = WARMTH_MAX
            progress = 0
            setOnSeekBarChangeListener(warmthSeekBarListener)
        }
    }

    companion object {
        private const val BRIGHTNESS_MAX = 255
        private const val WARMTH_MAX = 255
    }
}