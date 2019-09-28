package de.hpled.zinia.entities

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.*
import de.hpled.zinia.dto.ColorSequenceDTO
import de.hpled.zinia.services.HttpRequestService
import java.net.HttpURLConnection
import java.net.URL


@TypeConverters(IntArrayConverter::class)
@Entity(tableName = "color_sequence")
data class ColorSequence (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var transitionSpeed: Float,
    var keepingTimeMillis: Int,
    var colors: IntArray
) {
    fun toDTO() : ColorSequenceDTO {
        return ColorSequenceDTO(colors.size, keepingTimeMillis, colors.map {
            intArrayOf(Color.red(it), Color.green(it), Color.blue(it))
        })
    }

    fun getSendingJob(ip: String) : Runnable{
        val url = URL("http://$ip/setColorSequence?speed=$transitionSpeed")
        return HttpRequestService.jsonRequestRunnable<Any>(url, toDTO())
    }
}

@Dao
interface ColorSequenceDao {
    @Query("SELECT * FROM color_sequence")
    fun findAll() : List<ColorSequence>

    @Query("SELECT * FROM color_sequence")
    fun findAllLiveData() : LiveData<List<ColorSequence>>

    @Query("SELECT * FROM color_sequence WHERE id = (:id) LIMIT 1")
    fun findById(id: Long) : ColorSequence

    @Insert
    fun insertAll(vararg colorSequence: ColorSequence) : List<Long>

    @Insert
    fun insert(colorSequence: ColorSequence) : Long

    @Delete
    fun deleteAll(vararg colorSequence: ColorSequence)
}