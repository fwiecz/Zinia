package de.hpled.zinia

import android.app.Application
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hpled.zinia.entities.*
import de.hpled.zinia.newdevice.AddNewDeviceActivity

@Database(entities = arrayOf(Device::class, Mood::class, MoodTask::class), version = 3)
abstract class ApplicationDB : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun moodDao(): MoodDao
    abstract fun moodTaskDao(): MoodTaskDao
}

class ApplicationDbViewModel(app: Application) : AndroidViewModel(app) {
    private val context = getApplication<Application>().applicationContext
    private val database = Room.databaseBuilder(context, ApplicationDB::class.java, "app-database")
        .addMigrations(Migrations.FROM_1_TO_2)
        .fallbackToDestructiveMigration()
        .build()
    val deviceDao = database.deviceDao()
    val devices = deviceDao.findAllLiveData()
    val moodDao = database.moodDao()
    val moodTaskDao = database.moodTaskDao()

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
     * Asynchronously Deletes the given device from the database.
     */
    fun deleteDevice(device: Device) = AsyncTask.execute { deviceDao.deleteAll(device) }

    /**
     * Returns a list of all devices. Cannot run on main thread.
     */
    fun findAllDevices() = deviceDao.findAll()

    companion object {
        private val newDeviceIntentFeatures = listOf(
            AddNewDeviceActivity.INTENT_IP,
            AddNewDeviceActivity.INTENT_NAME,
            AddNewDeviceActivity.INTENT_NUM_LEDS,
            AddNewDeviceActivity.INTENT_TYPE
        )
    }
}