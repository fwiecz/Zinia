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
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.min

class SearchDevicesViewModel(app: Application) : AndroidViewModel(app) {
    private val handler = Handler()
    private val ipSearchRange = Pair(2, 99) // Which IPs will be searched for (XXX.XXX.XXX.??)
    private val ipSearchesTotal = (ipSearchRange.second - ipSearchRange.first) + 1
    private var finishedSearchingCounter = 0
    private lateinit var ownIpAddress: String
    private val executor by lazy { ScheduledThreadPoolExecutor(ipSearchesTotal) }
    val ipPrefix = MutableLiveData<String>()
    val isSearching = MutableLiveData<Boolean>(false)
    val onDeviceDiscoveredListener = mutableListOf<OnDeviceDiscoveredListener>()
    val discoveredDevices = MutableLiveData<List<DeviceStatusDTO>>(listOf())
    val noDevicesFound = MutableLiveData(false)

    init {
        AsyncTask.execute {
            ownIpAddress = IpAddressService.getOwnIp(getApplication())
            handler.post {
                ipPrefix.value = ownIpAddress.dropLastWhile { it.isDigit() }
            }
        }
    }

    private fun countSearch() {
        finishedSearchingCounter++
        if (finishedSearchingCounter >= ipSearchesTotal) {
            handler.post {
                isSearching.value = false
                if(discoveredDevices.value?.isEmpty() == true) {
                    noDevicesFound.value = true
                }
            }
        }
    }

    private fun runnableSearchForOneDevice(ipEnding: Int): Runnable {
        val ip = ipPrefix.value + ipEnding
        val url = URL("http://$ip/")
        return HttpRequestService.requestToRunnable<DeviceStatusDTO>(
            url,
            success = { response ->
                countSearch()
                handler.post {
                    onDeviceDiscoveredListener.forEach { it.onDeviceDiscovered(response, ip) }
                }
            },
            error = {
                countSearch()
            },
            responseType = DeviceStatusDTO::class.java,
            timeout = connectionTimeout
        )
    }

    /**
     * Searches for devices in the same local network. If a device was found,
     * [OnDeviceDiscoveredListener] will be triggered.
     */
    fun searchForDevices() {
        if (isSearching.value == false) {
            isSearching.value = true
            finishedSearchingCounter = 0
            discoveredDevices.value = listOf()
            noDevicesFound.value = false
            (ipSearchRange.first..ipSearchRange.second).forEachIndexed { index, ipEnd ->
                executor.schedule(
                    runnableSearchForOneDevice(ipEnd),
                    index * 20L,
                    TimeUnit.MILLISECONDS
                )
            }
        }
    }

    companion object {
        private const val connectionTimeout = 2000
    }

    interface OnDeviceDiscoveredListener {
        fun onDeviceDiscovered(dto: DeviceStatusDTO, ip: String)
    }
}