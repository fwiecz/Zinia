package de.hpled.zinia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.entities.Device
import de.hpled.zinia.fragments.ColorPickerFragment
import de.hpled.zinia.fragments.DeleteDialogFragment
import de.hpled.zinia.viewmodels.DeviceViewModel
import java.lang.IllegalStateException

class DeviceActivity : AppCompatActivity(), ColorPickerFragment.OnColorChangedListener {

    private lateinit var device: Device
    private lateinit var onOffSwitch: Switch
    private val database by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }
    private val colorPicker by lazy {
        supportFragmentManager.findFragmentById(R.id.colorPickFragment) as ColorPickerFragment
    }
    private val viewmodel by lazy {
        ViewModelProviders.of(this).get(DeviceViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        device = intent.getSerializableExtra(INTENT_DEVICE) as Device
        supportActionBar?.title = device.name
        colorPicker.onColorChangedListener += this
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
        onOffSwitch.setOnClickListener {  }
        return menu != null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.deviceDeleteOption -> onDeleteDevice()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onDeleteDevice() {
        DeleteDialogFragment(device, {
            database.deleteDevice(it)
            finish()
        }).show(supportFragmentManager, null)
    }

    override fun onColorChanged(color: Int, final: Boolean) {
        viewmodel.targetColor = color
        if (final) {
            viewmodel.stopExecutor()
            viewmodel.executeSingleColor(device.ipAddress, color)
        } else if (!viewmodel.isExecuting) {
            viewmodel.startExecutor(device.ipAddress)
        }
    }

    companion object {
        const val INTENT_DEVICE = "DEVICE"
    }
}
