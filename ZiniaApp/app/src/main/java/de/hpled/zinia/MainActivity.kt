package de.hpled.zinia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hpled.zinia.fragments.ListDevicesFragment
import de.hpled.zinia.newdevice.AddNewDeviceActivity
import de.hpled.zinia.viewmodels.AppPropertiesViewModel

class MainActivity : AppCompatActivity() {

    private val appProperties: AppPropertiesViewModel by lazy {
        ViewModelProviders.of(this).get(AppPropertiesViewModel::class.java)
    }

    private val navigationFragment by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    private val bottomNavigation by lazy {
        findViewById<BottomNavigationView>(R.id.homeBottomNavigation)
    }

    private var quitApplication: Boolean = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appProperties.switchNightmode(appProperties.getNightmode())
        val appBarConfiguration = AppBarConfiguration(appBarConfSet)
        setupActionBarWithNavController(navigationFragment, appBarConfiguration)
        bottomNavigation.setupWithNavController(navigationFragment)
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
        when (item?.itemId) {
            R.id.main_options_menu_nightmode -> {
                item.isChecked = !item.isChecked
                appProperties.setNightmode(item.isChecked)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (quitApplication) {
            finish()
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.on_press_back_close_app),
                Toast.LENGTH_LONG
            ).show()
            quitApplication = true
            handler.postDelayed({quitApplication = false}, PRESS_BACK_TIMEOUT)
        }
    }

    companion object {
        private val appBarConfSet = setOf(
            R.id.navigation_devices, R.id.navigation_dashboard
        )
        private const val PRESS_BACK_TIMEOUT = 2000L
    }
}
