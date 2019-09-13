package de.hpled.zinia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Switch
import de.hpled.zinia.entities.Device
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class DeviceActivity : AppCompatActivity() {

    private lateinit var device: Device
    private lateinit var onOffSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        device = intent.getSerializableExtra(INTENT_DEVICE) as Device
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = device.name
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.device_actionbar_menu, menu)
        return menu != null
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        onOffSwitch = menu?.findItem(R.id.deviceOnOffSwitchMenuItem).let {
            it?.actionView?.findViewById(R.id.deviceOnOffSwitch)
                ?: throw IllegalStateException("Cannot find Switch")
        }
        onOffSwitch.setOnClickListener { println("switch") }
        return menu != null
    }

    companion object {
        const val INTENT_DEVICE = "DEVICE"
    }
}
