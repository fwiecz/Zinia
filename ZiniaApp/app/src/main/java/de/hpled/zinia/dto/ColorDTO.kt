package de.hpled.zinia.dto

import android.graphics.Color

data class ColorDTO (
    var r: Int?,
    var g: Int?,
    var b: Int?,
    var w: Int?
) {
    fun toHSV() : FloatArray {
        return FloatArray(3, {0f}).also { Color.colorToHSV(toRGB(), it) }
    }

    fun toRGB() : Int {
        return Color.rgb(r ?: 0, g ?: 0, b ?: 0)
    }

    fun toRGBW() : Int {
        return Color.argb(w ?: 0, r ?: 0, g ?: 0, b ?: 0)
    }

    /**
     * Returns true if all values are not null.
     */
    fun isNotNull() : Boolean {
        return r != null && g != null && b != null
    }

    companion object {
        fun from(color: Int) : ColorDTO {
            return ColorDTO(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color))
        }
    }
}