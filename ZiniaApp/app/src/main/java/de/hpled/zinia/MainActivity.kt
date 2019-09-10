package de.hpled.zinia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.DeviceType
import de.hpled.zinia.newdevice.AddNewDeviceActivity

class MainActivity : AppCompatActivity() {

    private val appProperties : AppPropertiesViewModel by lazy {
        ViewModelProviders.of(this).get(AppPropertiesViewModel::class.java)
    }

    private val appDatabaseViewModel : ApplicationDbViewModel by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }

    private val listDevicesFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.listDevicesFragment) as ListDevicesFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appProperties.switchNightmode(appProperties.getNightmode())
        appDatabaseViewModel.devices.observe(this, Observer { listDevicesFragment.updateDevices(it)})
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_option_menu, menu)
        return menu != null
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.main_options_menu_nightmode)?.isChecked = appProperties.getNightmode()
        return menu != null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null) {
            when(item.itemId) {
                R.id.main_options_menu_nightmode -> {
                    item.isChecked = !item.isChecked
                    appProperties.setNightmode(item.isChecked)
                }
                R.id.main_options_menu_new_device -> {
                    val intent = Intent(applicationContext, AddNewDeviceActivity::class.java)
                    startActivityForResult(intent, NEW_DEVICE_REQUEST_CODE)
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            NEW_DEVICE_REQUEST_CODE -> { data?.run { appDatabaseViewModel.saveNewDevice(this) } }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val NEW_DEVICE_REQUEST_CODE = 0
    }
}
