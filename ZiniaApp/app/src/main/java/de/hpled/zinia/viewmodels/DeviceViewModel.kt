package de.hpled.zinia.viewmodels

import android.graphics.Color
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.Device
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DeviceViewModel : ViewModel() {
    private val handler = Handler()
    val colorSendingService = ColorSendingService(sendingFrequency)
    val deviceIsOn = MutableLiveData(false)

    fun turnDeviceOn(device: Device) {
        val url = URL("http://${device.ipAddress}/setOn")
        HttpRequestService.request<Any>(url,
            success = {handler.post { deviceIsOn.value = true }},
            error = {handler.post { deviceIsOn.value = false }},
            responseType = Any::class.java)
    }

    fun turnDeviceOff(device: Device) {
        val url = URL("http://${device.ipAddress}/setOff")
        HttpRequestService.request<Any>(url,
            success = {handler.post { deviceIsOn.value = false }},
            error = {handler.post { deviceIsOn.value = true }},
            responseType = Any::class.java)
    }

    fun getDeviceStatus(device: Device) {
        val url = URL("http://${device.ipAddress}/")
        HttpRequestService.request<DeviceStatusDTO>(url,
            success = {handler.post { deviceIsOn.value = it.isOn == 1 }},
            error = {}, responseType = DeviceStatusDTO::class.java)
    }

    companion object {
        private const val sendingFrequency = 500L
    }
}