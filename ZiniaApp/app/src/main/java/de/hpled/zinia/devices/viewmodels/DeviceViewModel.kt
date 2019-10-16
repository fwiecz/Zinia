package de.hpled.zinia.devices.viewmodels

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.dto.BrightnessDTO
import de.hpled.zinia.dto.ColorDTO
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.Device
import de.hpled.zinia.services.BrightnessSendingService
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledThreadPoolExecutor

class DeviceViewModel : ViewModel() {
    private val handler = Handler()
    val colorSendingService = ColorSendingService(sendingFrequency)
    val brightnessSendingService = BrightnessSendingService(sendingFrequency)
    val deviceIsOn = MutableLiveData(false)
    val deviceColor = MutableLiveData<ColorDTO>()
    val deviceBrightness = MutableLiveData<Int>()
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

    fun getDeviceBrightness(device: Device) {
        val url = URL("http://${device.ipAddress}/getBrightness")
        val req = HttpRequestService.requestToRunnable<BrightnessDTO>(url,
            success = {handler.post { deviceBrightness.value = it.br }},
            error = {}, responseType = BrightnessDTO::class.java)
        executor.execute(req)
    }

    companion object {
        private const val sendingFrequency = 500L
    }
}