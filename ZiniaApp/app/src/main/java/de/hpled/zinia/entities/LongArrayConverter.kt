package de.hpled.zinia.entities

import androidx.room.TypeConverter
import com.google.gson.Gson

/**
 * TypeConverter for [LongArray], used for one-to-many relations in room database.
 */
class LongArrayConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(json: String) = gson.fromJson<LongArray>(json, LongArray::class.java)

    @TypeConverter
    fun toString(array: LongArray) = gson.toJson(array)
}