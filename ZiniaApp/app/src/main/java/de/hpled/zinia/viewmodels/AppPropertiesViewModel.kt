package de.hpled.zinia.viewmodels

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel

/**
 * This Properties class contains App properties such as nightmode. Properties are persisted via
 * SharedPreferences.
 */
class AppPropertiesViewModel(app: Application) : AndroidViewModel(app) {
    private val context = getApplication<Application>().applicationContext
    private val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, 0)
    private val edit = sharedPrefs.edit()

    /**
     * Returns whether the app should be displayed in nightmode or not.
     */
    fun getNightmode() = sharedPrefs.getBoolean(NIGHTMODE_KEY, false)

    /**
     * Sets whether the app should be displayed in nightmode or not.
     */
    fun setNightmode(nightmodeEnabled: Boolean) {
        edit.putBoolean(NIGHTMODE_KEY, nightmodeEnabled)
        edit.commit()
        switchNightmode(nightmodeEnabled)
    }

    /**
     * Changes the actual UI appearance to nightmode or default mode.
     */
    fun switchNightmode(enabled: Boolean) {
        (context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).apply {
            nightMode = if(enabled)UiModeManager.MODE_NIGHT_YES else UiModeManager.MODE_NIGHT_NO
        }
    }

    companion object {
        private const val SHARED_PREFS_NAME = "APP_PROPERTIES"
        private const val NIGHTMODE_KEY = "NIGHTMODE"
    }
}