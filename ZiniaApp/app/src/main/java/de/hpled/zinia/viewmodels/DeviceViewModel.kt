package de.hpled.zinia.viewmodels

import android.graphics.Color
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.dto.ColorDTO
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.Device
import de.hpled.zinia.services.BrightnessSendingService
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DeviceViewModel : ViewModel() {
    private val handler = Handler()
    val colorSendingService = ColorSendingService(sendingFrequency)
    val brightnessSendingService = BrightnessSendingService(sendingFrequency)
    val deviceIsOn = MutableLiveData(false)
    val deviceColor = MutableLiveData<ColorDTO>()
    val executor = ScheduledThreadPoolExecutor(3)

    fun turnDeviceOn(device: Device) {
        val url = URL("http://${device.ipAddress}/setOn")
        val req = HttpRequestService.requestToRunnable<Any>(url,
            success = {handler.post { deviceIsOn.value = true }},
            error = {handler.post { deviceIsOn.value = false }},
            responseType = Any::class.java)
        executor.execute(req)
    }

    fun turnDeviceOff(device: Device) {
        val url = URL("http://${device.ipAddress}/setOff")
        val req = HttpRequestService.requestToRunnable<Any>(url,
            success = {handler.post { deviceIsOn.value = false }},
            error = {handler.post { deviceIsOn.value = true }},
            responseType = Any::class.java)
        executor.execute(req)
    }

    fun getDeviceStatus(device: Device) {
        val url = URL("http://${device.ipAddress}/")
        val req = HttpRequestService.requestToRunnable<DeviceStatusDTO>(url,
            success = {handler.post { deviceIsOn.value = it.isOn == 1 }},
            error = {}, responseType = DeviceStatusDTO::class.java)
        executor.execute(req)
    }

    fun getDeviceColor(device: Device) {
        val url = URL("http://${device.ipAddress}/getColor")
        val req = HttpRequestService.requestToRunnable<ColorDTO>(url,
            success = {handler.post {
                if(it.isNotNull()) {
                    deviceColor.value = it
                }
            }},
            error = {}, responseType = ColorDTO::class.java)
        executor.execute(req)
    }

    companion object {
        private const val sendingFrequency = 500L
    }
}