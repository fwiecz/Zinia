package de.hpled.zinia

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.DeviceDao

@Database(entities = arrayOf(Device::class), version = 1)
abstract class ApplicationDB : RoomDatabase() {
    abstract fun deviceDao() : DeviceDao
}

class ApplicationDbViewModel(app: Application) : AndroidViewModel(app) {
    val context = getApplication<Application>().applicationContext
    val database = Room.databaseBuilder(context, ApplicationDB::class.java, "app-database").build()
}