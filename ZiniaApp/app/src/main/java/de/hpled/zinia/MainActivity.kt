package de.hpled.zinia

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.hpled.zinia.newdevice.AddNewDeviceActivity

class MainActivity : AppCompatActivity() {

    private val appProperties : AppPropertiesViewModel by lazy {
        ViewModelProviders.of(this).get(AppPropertiesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appProperties.switchNightmode(appProperties.getNightmode())
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
            NEW_DEVICE_REQUEST_CODE -> {

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val NEW_DEVICE_REQUEST_CODE = 0
    }
}
