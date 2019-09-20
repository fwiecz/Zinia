package de.hpled.zinia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.Device
import de.hpled.zinia.fragments.ColorPickerFragment
import de.hpled.zinia.fragments.DeleteDialogFragment
import de.hpled.zinia.services.HttpRequestService
import de.hpled.zinia.viewmodels.DeviceViewModel
import de.hpled.zinia.views.BrightnessWarmthView
import de.hpled.zinia.views.ColorPickerView
import de.hpled.zinia.views.OnBrightnessWarmthChangedListener
import java.lang.IllegalStateException
import java.net.URL

class DeviceActivity : AppCompatActivity(), OnBrightnessWarmthChangedListener{
    private lateinit var device: Device
    private lateinit var onOffSwitch: Switch
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(DeviceViewModel::class.java)
    }
    private val colorPicker by lazy {
        findViewById<ColorPickerView>(R.id.deviceColorPickerView)
    }
    private val brWarmSlider by lazy {
        findViewById<BrightnessWarmthView>(R.id.deviceBrightnessWarmth)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        device = intent.getSerializableExtra(INTENT_DEVICE) as Device
        supportActionBar?.title = device.name
        viewmodel.colorSendingService.targetIP = device.ipAddress
        viewmodel.brightnessSendingService.targetIP = device.ipAddress
    }

    override fun onStart() {
        super.onStart()
        colorPicker.onColorChangedListener += viewmodel.colorSendingService
        viewmodel.deviceColor.observe(this, Observer { colorPicker.setThumbToColor(it) })
        viewmodel.getDeviceColor(device)
        brWarmSlider.listener += this
        brWarmSlider.listener += viewmodel.brightnessSendingService
        viewmodel.deviceBrightness.observe(this, Observer { brWarmSlider.setBrightness(it) })
        viewmodel.getDeviceBrightness(device)
    }

    override fun onWarmthChanged(value: Int, final: Boolean) {

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
        onOffSwitch.setOnClickListener {
            if(onOffSwitch.isChecked) {
                viewmodel.turnDeviceOn(device)
            } else {
                viewmodel.turnDeviceOff(device)
            }
        }
        viewmodel.deviceIsOn.observe(this, Observer {
            onOffSwitch.isChecked = it
        })
        viewmodel.getDeviceStatus(device)
        return menu != null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.deviceDeleteOption -> onDeleteDevice()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onDeleteDevice() {
        DeleteDialogFragment(device) {
            database.deleteDevice(it)
            finish()
        }.show(supportFragmentManager, null)
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }

    companion object {
        const val INTENT_DEVICE = "DEVICE"
    }

    override fun onBrightnessChanged(value: Int, final: Boolean) {
        // Handled by BrightnessSendingService
    }
}
