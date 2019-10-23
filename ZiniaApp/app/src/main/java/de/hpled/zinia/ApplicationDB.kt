package de.hpled.zinia

import android.app.Application
import android.content.Intent
import android.os.AsyncTask
import androidx.lifecycle.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hpled.zinia.entities.*
import de.hpled.zinia.newdevice.AddNewDeviceActivity
import de.hpled.zinia.shows.interfaces.Show
import de.hpled.zinia.shows.interfaces.ShowType

@Database(entities = arrayOf(Device::class, Mood::class, MoodTask::class, ColorSequence::class),
    version = 8)
abstract class ApplicationDB : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun moodDao(): MoodDao
    abstract fun moodTaskDao(): MoodTaskDao
    abstract fun colorSequenceDao(): ColorSequenceDao
}

class ApplicationDbViewModel(app: Application) : AndroidViewModel(app) {
    private val context = getApplication<Application>().applicationContext
    private val database = Room.databaseBuilder(context, ApplicationDB::class.java, "app-database")
        .addMigrations(Migrations.FROM_1_TO_2)
        .addMigrations(Migrations.FROM_5_TO_6)
        .addMigrations(Migrations.FROM_6_TO_7)
        .addMigrations(Migrations.FROM_7_TO_8)
        .build()
    val deviceDao = database.deviceDao()
    val devices = deviceDao.findAllLiveData()
    val moodDao = database.moodDao()
    val moodTaskDao = database.moodTaskDao()
    val colorSequenceDao = database.colorSequenceDao()

    /**
     * Stores the device in the database given by the intent.
     */
    fun saveNewDevice(intent: Intent) {
        if (newDeviceIntentFeatures.all { intent.hasExtra(it) }) {
            val ip = intent.getStringExtra(AddNewDeviceActivity.INTENT_IP)
            val name = intent.getStringExtra(AddNewDeviceActivity.INTENT_NAME)
            val numLeds = intent.getIntExtra(AddNewDeviceActivity.INTENT_NUM_LEDS, 0)
            val type = intent.getSerializableExtra(AddNewDeviceActivity.INTENT_TYPE) as DeviceType
            val device = Device.newInstance(ip, name, numLeds, type)
            AsyncTask.execute {
                deviceDao.insert(device)
            }
        }
    }

    /**
     * Asynchronously Deletes the given device from the database. It checks for every Mood, whether
     * the device is part of it and removes it if necessary.
     */
    fun deleteDevice(device: Device) {
        AsyncTask.execute {
            val moods = moodDao.findAll().map { getMoodWithTasks(it.id) }
            moods.forEach { mood ->
                val tasks = mood.tasks
                var tasksToDelete = listOf<MoodTask>()
                if(tasks != null) {
                    tasksToDelete = tasks.filter { it.deviceId == device.id }
                    tasksToDelete.forEach {
                        deleteMoodTask(it.id)
                    }
                }
            }
            deviceDao.deleteAll(device)
        }
    }

    /**
     * Returns a list of all devices. Cannot run on main thread.
     */
    fun findAllDevices() = deviceDao.findAll()

    /**
     * Returns the [MoodTask] with the [device] field already initialized.
     */
    fun getMoodTaskWithDevice(id: Long) : MoodTask {
        val task = moodTaskDao.findById(id)
        task.device = deviceDao.findById(task.deviceId)
        return task
    }

    /**
     * Returns the [Mood] but the [tasks] field has already been filled with objects.
     */
    fun getMoodWithTasks(id: Long) : Mood {
        val mood = moodDao.findById(id)
        mood.tasks = mood.taskIds.map { getMoodTaskWithDevice(it) }
        return mood
    }

    /**
     * Deletes a single MoodTask and removes its references on the respective Mood.
     */
    fun deleteMoodTask(id: Long) {
        val mood = moodDao.findAll().find { it.taskIds.contains(id) }
        val newids = mood?.taskIds?.toMutableList()?.apply { remove(id) }
        if(newids != null) {
            mood.taskIds = newids.toLongArray()
            moodDao.insert(mood)
        }
        val task = getMoodTaskWithDevice(id)
        moodTaskDao.deleteAll(task)
    }

    /**
     * Deletes the Mood and all its MoodTasks
     */
    fun deleteMoodAndMoodTasks(id: Long) {
        AsyncTask.execute {
            val mood = getMoodWithTasks(id)
            val tasks = mood.tasks?.toTypedArray()
            if(tasks != null) {
                moodTaskDao.deleteAll(*tasks)
            }
            moodDao.deleteAll(mood)
        }
    }

    /**
     * Deletes a [Show] based on [Show.getShowType]
     */
    fun deleteShow(show: Show) {
        AsyncTask.execute {
            when(show.getShowType()) {
                ShowType.COLOR_SEQUENCE -> colorSequenceDao.deleteAll(show as ColorSequence)
            }
        }
    }

    /**
     * A [MutableLiveData] object containing all [Show]s
     */
    fun getShowsLiveData(owner: LifecycleOwner) : MutableLiveData<List<Show>> {
        val livedata = MutableLiveData<List<Show>>()
        val observer = Observer<List<Show>> {
            livedata.value = (livedata.value ?: listOf()) + it
        }
        colorSequenceDao.findAllLiveData().observe(owner, observer)
        return livedata
    }

    /**
     * Closes the databse reference.
     */
    fun close() {
        database.close()
    }

    companion object {
        private val newDeviceIntentFeatures = listOf(
            AddNewDeviceActivity.INTENT_IP,
            AddNewDeviceActivity.INTENT_NAME,
            AddNewDeviceActivity.INTENT_NUM_LEDS,
            AddNewDeviceActivity.INTENT_TYPE
        )
    }
}