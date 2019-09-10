package de.hpled.zinia.newdevice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.hpled.zinia.R
import de.hpled.zinia.entities.DeviceType

class AddNewDeviceActivity : AppCompatActivity(), NewDeviceListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sectionsPagerAdapter = NewDeviceMethodsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        sectionsPagerAdapter.addDeviceManualFragment.newDeviceListener += this
    }

    override fun onNewDevice(ip: String, name: String, numLeds: Int, type: DeviceType) {
        val intent = Intent().apply {
            putExtra(INTENT_IP, ip)
            putExtra(INTENT_NAME, name)
            putExtra(INTENT_NUM_LEDS, numLeds)
            putExtra(INTENT_TYPE, type)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        const val INTENT_IP = "INTENT_IP"
        const val INTENT_NAME = "INTENT_NAME"
        const val INTENT_NUM_LEDS = "INTENT_NUM_LEDS"
        const val INTENT_TYPE = "INTENT_TYPE"
    }
}