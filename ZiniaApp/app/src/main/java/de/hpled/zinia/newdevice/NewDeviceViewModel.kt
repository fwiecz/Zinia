package de.hpled.zinia.newdevice

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import de.hpled.zinia.services.DeviceDiscoverService
import de.hpled.zinia.views.StatusIndicatorView.State
import java.net.URL
import java.util.*

class NewDeviceViewModel : ViewModel() {

    val buttonIsClickable = MutableLiveData<Boolean>(false)
    val manualIpStatus = MutableLiveData<State>(State.UNKNOWN)
    val ipRegex = Regex("[\\d]+[.][\\d]+[.][\\d]+[.][\\d]+")
    private var timer = Timer()
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

        DeviceDiscoverService.request(url,
            success = {
                handler.post { manualIpStatus.value = State.SUCCESS }
            },
            error = {
                handler.post { manualIpStatus.value = State.ERROR }
            })
    }

    companion object {
        private const val keyTypedCheckDelay = 1200L
        private const val connectionTimeOute = 2000
    }

}