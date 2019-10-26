package de.hpled.zinia.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import de.hpled.zinia.xcolor.Xcolor
import de.hpled.zinia.xcolor.XcolorList

/**
 * TypeConverter for [Xcolor]
 */
class XcolorConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(json: String) : Xcolor {
        return gson.fromJson<Xcolor>(json, Xcolor::class.java)
    }

    @TypeConverter
    fun toString(xcolor: Xcolor) : String {
        return gson.toJson(xcolor)
    }
}

/**
 * TypeConverter for [XcolorList]
 */
class XcolorListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(json: String) : XcolorList {
        return gson.fromJson<XcolorList>(json, XcolorList::class.java)
    }

    @TypeConverter
    fun toString(xcolorlist: XcolorList) : String {
        return gson.toJson(xcolorlist)
    }
}

