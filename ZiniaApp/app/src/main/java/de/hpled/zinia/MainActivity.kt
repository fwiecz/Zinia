package de.hpled.zinia

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {

    private val appProperties : AppPropertiesViewModel by lazy {
        ViewModelProviders.of(this).get(AppPropertiesViewModel::class.java)
    }

    private val appDatabaseViewModel : ApplicationDbViewModel by lazy {
        ViewModelProviders.of(this).get(ApplicationDbViewModel::class.java)
    }

    private val database : ApplicationDB by lazy { appDatabaseViewModel.database }

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
            }
        }
        return true
    }
}
