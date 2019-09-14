package de.hpled.zinia.viewmodels

import android.graphics.Color
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.hpled.zinia.dto.DeviceStatusDTO
import de.hpled.zinia.entities.Device
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DeviceViewModel : ViewModel() {
    val handler = Handler();
    private val executor = ScheduledThreadPoolExecutor(5)
    var targetColor = 0
    var targetIP = ""
    var isExecuting = false
        private set
    private var scheduledFuture: ScheduledFuture<*>? = null
    val deviceIsOn = MutableLiveData(false)

    fun startExecutor(ip: String) {
        isExecuting = true
        targetIP = ip
        scheduledFuture = executor.scheduleWithFixedDelay(
            executorRunnable, sendingFrequency, sendingFrequency, TimeUnit.MILLISECONDS)
    }

    fun stopExecutor() {
        scheduledFuture?.cancel(false)
        isExecuting = false
    }

    private fun sendSingleColor(ip: String, color: Int) : Runnable {
        val c = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))
        val url = URL("http://$ip/setSingleColor?r=${c[0]}&g=${c[1]}&b=${c[2]}")
        return HttpRequestService.requestToRunnable<Any>(url, {}, {}, Any::class.java)
    }

    fun executeSingleColor(ip: String, color: Int) {
        executor.execute(sendSingleColor(ip, color))
    }

    private val executorRunnable = Runnable {
        sendSingleColor(targetIP, targetColor).run()
    }

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