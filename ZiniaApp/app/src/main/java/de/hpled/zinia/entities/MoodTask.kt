package de.hpled.zinia.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import java.io.Serializable

/**
 * Contains what the given devices should perform during its respective [Mood].
 */
@Entity
data class MoodTask (
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var deviceId: Long,
    var color: Int?,
    var brightness: Int
) : Serializable {
    // When accessing MoodTask, also access the device and store it here
    @Ignore
    var device: Device? = null
}

@Dao
interface MoodTaskDao {
    @Query("SELECT * FROM moodtask")
    fun findAll() : List<MoodTask>

    @Query("SELECT * FROM moodtask")
    fun findAllLiveData() : LiveData<List<MoodTask>>

    @Query("SELECT * FROM moodtask WHERE id = (:id) LIMIT 1")
    fun findById(id: Long) : MoodTask

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg moodTasks: MoodTask) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(moodTask: MoodTask) : Long

    @Delete
    fun deleteAll(vararg moodTasks: MoodTask)
}