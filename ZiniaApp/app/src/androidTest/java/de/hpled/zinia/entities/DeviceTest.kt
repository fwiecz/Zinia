package de.hpled.zinia.entities

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
class DeviceTest {

    private lateinit var db: ApplicationDB
    private lateinit var dao: DeviceDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, ApplicationDB::class.java).build()
        dao = db.deviceDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createRemoveDevice() {
        assertTrue( dao.findAll().isEmpty() )
        val device = Device.newInstance("192.168.188.51", "TestDevice", 20, DeviceType.LED_CHAIN)
        Log.d("TEST", device.toString())
        val id = dao.insert(device)
        assertTrue(dao.findAll().isNotEmpty())

        val getDevice = dao.findById(id)
        Log.d("TEST", getDevice.toString())
        dao.deleteAll(getDevice)
        assertTrue(dao.findAll().isEmpty())
    }
}