package de.hpled.zinia.newdevice

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.hpled.zinia.ApplicationDbViewModel
import de.hpled.zinia.R
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.DeviceType
import de.hpled.zinia.newdevice.adapter.NewDeviceMethodsPagerAdapter
import de.hpled.zinia.newdevice.interfaces.NewDeviceListener

class AddNewDeviceActivity : AppCompatActivity(),
    NewDeviceListener {

    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sectionsPagerAdapter = NewDeviceMethodsPagerAdapter(
            this,
            supportFragmentManager
        )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        sectionsPagerAdapter.addDeviceManualFragment.newDeviceListener += this
        sectionsPagerAdapter.searchDevicesFragment.newDeviceListener += this
    }

    override fun onNewDevice(ip: String, name: String, numLeds: Int, type: DeviceType, isRGBW: Boolean) {
        AsyncTask.execute {
            val device = Device.newInstance(ip, name, numLeds, type, isRGBW)
            database.deviceDao.insert(device)
            finish()
        }
    }
}