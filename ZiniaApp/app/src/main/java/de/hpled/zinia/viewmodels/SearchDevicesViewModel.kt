package de.hpled.zinia.viewmodels

import android.app.Application
import android.os.AsyncTask
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.services.HttpRequestService
import de.hpled.zinia.services.IpAddressService
import java.net.URL

class SearchDevicesViewModel(app: Application) : AndroidViewModel(app) {
    private val handler = Handler()
    private val ipSearchRange = Pair(2, 99) // Which IPs will be searched for (XXX.XXX.XXX.??)
    private val ipSearchesTotal = (ipSearchRange.second - ipSearchRange.first) + 1
    private var finishedSearchingCounter = 0
    private lateinit var ownIpAddress: String
    private lateinit var ipPrefix: String
    val isSearching = MutableLiveData<Boolean>(false)
    val onDeviceDiscoveredListener = mutableListOf<OnDeviceDiscoveredListener>()

    init {
        AsyncTask.execute {
            ownIpAddress = IpAddressService.getOwnIp(getApplication())
            ipPrefix = ownIpAddress.dropLastWhile { it.isDigit() }
        }
    }

    private fun countSearch() {
        finishedSearchingCounter ++
        if(finishedSearchingCounter >= ipSearchesTotal) {
            handler.post { isSearching.value = false }
        }
    }

    private fun asyncSearchForOneDevice(ipEnding: Int) {
        val ip = ipPrefix + ipEnding
        val url = URL("http://$ip/")
        HttpRequestService.request<DeviceStatusDTO>(url,
            success = { response ->
                countSearch()
                handler.post {
                    onDeviceDiscoveredListener.forEach { it.onDeviceDiscovered(response, ip) }
                }
            },
            error = {
                countSearch()
            },
            responseType = DeviceStatusDTO::class.java
        )
    }

    /**
     * Searches for devices in the same local network. If a device was found,
     * [OnDeviceDiscoveredListener] will be triggered.
     */
    fun searchForDevices() {
        if(isSearching.value == false) {
            isSearching.value = true
            finishedSearchingCounter = 0
            (ipSearchRange.first .. ipSearchRange.second).forEachIndexed { index, ipEnd ->
                asyncSearchForOneDevice(ipEnd)
            }
        }
    }

    interface OnDeviceDiscoveredListener {
        fun onDeviceDiscovered(dto: DeviceStatusDTO, ip: String)
    }
}