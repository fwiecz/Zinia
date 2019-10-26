package de.hpled.zinia.xcolor

import android.graphics.Color
import java.io.Serializable
import java.util.*

/**
 * The Extended Color class provides editable fields, additional channels and useful functionality.
 * This color class is specialized for representation on [Device]s so there is no alpha channel.
 * To achieve dark colors you should turn the brightness down instead of lowering the rgb values
 * directly.
 */
data class Xcolor (
    var r: Int = 0,
    var g: Int = 0,
    var b: Int = 0,
    var w: Int = 0,
    var brightness: Int = 255
) : Serializable {

    fun toRgb() : Int {
        return Color.rgb(r, g, b)
    }

    fun toArgb() : Int {
        return Color.argb(brightness, r, g, b)
    }

    fun toHsv() : FloatArray {
        return floatArrayOf(0f, 0f, 0f).also {
            Color.colorToHSV(toRgb(), it)
        }
    }

    fun toIntArray() : IntArray {
        return intArrayOf(r, g, b, w, brightness)
    }

    fun setRgb(red: Int, green: Int, blue: Int) {
        r = red; g = green; b = blue
    }

    fun setRgb(color: Int) {
        r = Color.red(color)
        g = Color.green(color)
        b = Color.blue(color)
    }

    companion object {
        private val random = Random()

        fun fromXcolor(xcolor: Xcolor) : Xcolor {
            return xcolor.let { Xcolor(it.r, it.g, it.b, it.w, it.brightness) }
        }

        fun fromHSV(hue: Float, sat: Float = 1f, value: Float = 1f) : Xcolor {
            return Color.HSVToColor(floatArrayOf(hue, sat, value)).let {
                Xcolor(Color.red(it), Color.green(it), Color.blue(it))
            }
        }

        /**
         * Converts a conventional color to [Xcolor]. Notice that the alpha value will be ignored.
         * Furthermore
         */
        fun fromColor(color: Int) : Xcolor {
            return color.let {
                Xcolor(
                    Color.red(it),
                    Color.green(it),
                    Color.blue(it)
                )
            }
        }

        fun fromIntArray(array: IntArray) : Xcolor {
            return array.let {
                Xcolor(
                    it.getOrNull(0) ?: 0,
                    it.getOrNull(1) ?: 0,
                    it.getOrNull(2) ?: 0,
                    it.getOrNull(3) ?: 0,
                    it.getOrNull(4) ?: 0
                )
            }
        }

        fun random() : Xcolor {
            return fromHSV(random.nextFloat() * 360)
        }
    }
}