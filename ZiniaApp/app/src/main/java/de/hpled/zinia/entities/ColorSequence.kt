package de.hpled.zinia.entities

import androidx.lifecycle.LiveData
import androidx.room.*

@TypeConverters(IntArrayConverter::class)
@Entity(tableName = "color_sequence")
data class ColorSequence (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var transitionSpeed: Float,
    var keepingTimeMillis: Int,
    var colors: IntArray
)

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