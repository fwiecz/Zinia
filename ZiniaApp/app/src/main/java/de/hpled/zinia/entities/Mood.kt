package de.hpled.zinia.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson


/**
 * Contains various [MoodTask]s so multiple devices can be controlled with one 'click'.
 */
@TypeConverters(LongArrayConverter::class)
@Entity(tableName = "moods")
data class Mood (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    var turnOffUnusedDevices: Boolean,
    var taskIds: LongArray
) {
    // When accessing Mood, also access all MoodTasks and store them here
    @Ignore
    var tasks: List<MoodTask>? = null
}

@Dao
interface MoodDao {
    @Query("SELECT * FROM moods")
    fun findAll() : List<Mood>

    @Query("SELECT * FROM moods")
    fun findAllLiveData() : LiveData<List<Mood>>

    @Query("SELECT * FROM moods WHERE id = (:id) LIMIT 1")
    fun findById(id: Long) : Mood

    @Query("SELECT * FROM moods WHERE name = (:name)")
    fun findAllByName(name: String) : List<Mood>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg moods: Mood) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mood: Mood) : Long

    @Delete
    fun deleteAll(vararg mood: Mood)
}