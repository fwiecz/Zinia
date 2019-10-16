package de.hpled.zinia.newdevice.viewmodels

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.services.HttpRequestService
import de.hpled.zinia.views.StatusIndicatorView.State
import java.net.URL
import java.util.*

class AddDeviceManualViewModel : ViewModel() {
    val buttonIsClickable = MutableLiveData<Boolean>(false)
    val manualIpStatus = MutableLiveData<State>(State.UNKNOWN)
    val ipRegex = Regex("[\\d]+[.][\\d]+[.][\\d]+[.][\\d]+")
    private var timer = Timer()
    private val handler = Handler()

    /**
     * After the entered IP address was changed, [keyTypedCheckDelay] ms needs to pass without any
     * changes before the IP address will be checked.
     */
    fun checkForManualIP(ipAddress: String) {
        if(ipAddress.matches(ipRegex)) {
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {asyncHttpRequest(ipAddress)}
            },
                keyTypedCheckDelay
            )
        }
    }

    private fun asyncHttpRequest(ip: String) {
        val url = URL("http://$ip/")
        handler.post { manualIpStatus.value = State.LOADING }

        HttpRequestService.request<DeviceStatusDTO>(url,
            success = {
                handler.post { manualIpStatus.value = State.SUCCESS }
            },
            error = {
                handler.post { manualIpStatus.value = State.ERROR }
            },
            responseType = DeviceStatusDTO::class.java,
            timeout = connectionTimeOut
        )
    }

    companion object {
        private const val keyTypedCheckDelay = 1200L
        private const val connectionTimeOut = 2000
    }
}