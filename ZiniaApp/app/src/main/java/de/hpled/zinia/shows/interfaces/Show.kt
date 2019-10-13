package de.hpled.zinia.shows.interfaces

import android.graphics.drawable.Drawable

enum class ShowType {
    COLOR_SEQUENCE
}

interface Show {
    fun getShowId() : Long
    fun getShowName() : String
    fun getShowIconRes() : Int
    fun getShowType() : ShowType
    fun getBackgroundGradientValues() : IntArray?
    fun getSendingJob(ip: String) : Runnable
}