package de.hpled.zinia.entities

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.*
import de.hpled.zinia.R
import de.hpled.zinia.dto.ColorSequenceDTO
import de.hpled.zinia.services.HttpRequestService
import de.hpled.zinia.shows.interfaces.Show
import de.hpled.zinia.shows.interfaces.ShowType
import de.hpled.zinia.xcolor.XcolorList
import java.net.URL


@TypeConverters(IntArrayConverter::class, XcolorListConverter::class)
@Entity(tableName = "color_sequence")
data class ColorSequence(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    var transitionSpeed: Int,
    var keepingTimeMillis: Int,
    var colors: XcolorList
) : Show {
    fun toDTO() : ColorSequenceDTO {
        return ColorSequenceDTO(colors.size, keepingTimeMillis, colors.map { it.toIntArray() })
    }

    override fun getSendingJob(ip: String) : Runnable {
        val url = URL("http://$ip/setColorSequence?speed=$transitionSpeed")
        return HttpRequestService.jsonRequestRunnable<Any>(url, toDTO())
    }

    override fun getShowId() = id
    override fun getShowName() = name
    override fun getShowIconRes() = R.drawable.material_palette
    override fun getShowType() = ShowType.COLOR_SEQUENCE
    override fun getBackgroundGradientValues() = colors.map { it.toRgb() }.toIntArray()
}

@Dao
interface ColorSequenceDao {
    @Query("SELECT * FROM color_sequence")
    fun findAll() : List<ColorSequence>

    @Query("SELECT * FROM color_sequence")
    fun findAllLiveData() : LiveData<List<ColorSequence>>

    @Query("SELECT * FROM color_sequence WHERE id = (:id) LIMIT 1")
    fun findById(id: Long) : ColorSequence

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg show: ColorSequence) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(show: ColorSequence) : Long

    @Delete
    fun deleteAll(vararg show: ColorSequence)
}