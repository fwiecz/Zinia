package de.hpled.zinia.newdevice

import android.os.AsyncTask
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import de.hpled.zinia.views.StatusIndicatorView.State
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.util.*

class NewDeviceViewModel : ViewModel() {

    val buttonIsClickable = MutableLiveData<Boolean>(false)
    val manualIpStatus = MutableLiveData<State>(State.UNKNOWN)
    val ipRegex = Regex("[\\d]+[.][\\d]+[.][\\d]+[.][\\d]+")
    private var timer = Timer()
    private val gson = Gson()
    private val handler = Handler()

    fun checkForManualIP(ipAddress: String) {
        if(ipAddress.matches(ipRegex)) {
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {asyncHttpRequest(ipAddress)}
            }, keyTypedCheckDelay)
        }
    }

    private fun asyncHttpRequest(ip: String) {
        val url = URL("http://$ip/prefs")
        handler.post { manualIpStatus.value = State.LOADING }
        AsyncTask.execute {
            val connection = url.openConnection().apply {
                connectTimeout = connectionTimeOute
                readTimeout = connectionTimeOute
                doOutput = true
            }
            try {
                connection.connect()
                val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                val response = gson.fromJson<Map<String, String>>(reader.readText(), Map::class.java)
                handler.post { manualIpStatus.value = State.SUCCESS }
                // TODO handle response
            } catch (e: Exception) {
                e.printStackTrace()
                handler.post { manualIpStatus.value = State.ERROR }
            }
        }
    }

    companion object {
        private const val keyTypedCheckDelay = 1200L
        private const val connectionTimeOute = 2000
    }

}