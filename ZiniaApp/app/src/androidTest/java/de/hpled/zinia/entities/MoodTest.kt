package de.hpled.zinia.entities

import android.graphics.Color
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.hpled.zinia.ApplicationDB
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoodTest {

    private lateinit var db: ApplicationDB
    private lateinit var dao: MoodDao
    private lateinit var taskDao: MoodTaskDao
    private lateinit var deviceDao: DeviceDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, ApplicationDB::class.java).build()
        dao = db.moodDao()
        taskDao = db.moodTaskDao()
        deviceDao = db.deviceDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createRemoveMood() {
        val device = Device.newInstance("192.168.188.51", "TestDevice", 20, DeviceType.LED_CHAIN)
        Log.d("TEST", device.toString())
        val devID = deviceDao.insert(device)
        val moodTask = MoodTask(0, devID, Color.YELLOW)
        Log.d("TEST", moodTask.toString())
        val taskId = taskDao.insert(moodTask)

        assertTrue( dao.findAll().isEmpty() )
        val mood = Mood(0, "testMood", true, longArrayOf(taskId))
        Log.d("TEST", mood.toString())
        val id = dao.insert(mood)
        assertTrue(dao.findAll().isNotEmpty())

        val getMood = dao.findById(id)
        Log.d("TEST", getMood.toString())
        dao.deleteAll(getMood)
        assertTrue(dao.findAll().isEmpty())
    }
}