package de.hpled.zinia.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import de.hpled.zinia.R

class BrightnessWarmthView(c: Context, attr: AttributeSet?) : LinearLayout(c, attr) {

    init {
        View.inflate(context, R.layout.view_brightness_warmth_slider, this)
    }
}