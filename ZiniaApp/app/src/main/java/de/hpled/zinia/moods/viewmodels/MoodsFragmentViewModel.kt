package de.hpled.zinia.moods.viewmodels

import android.graphics.Color
import androidx.lifecycle.ViewModel
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.services.BrightnessSendingService
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledThreadPoolExecutor

class MoodsFragmentViewModel : ViewModel() {

    private val executor = ScheduledThreadPoolExecutor(10)

    fun playMoodTasks(moodTasks: List<MoodTask>) {
        moodTasks.forEach {
            val afterOn = Runnable {
                if (it.color != null) {
                    val job = ColorSendingService.sendSingleColor(it.device!!.ipAddress, it.color!!)
                    executor.execute(job)
                    if(it.device!!.isRGBW) {
                        val jobW = BrightnessSendingService.sendSingleWarmth(
                            it.device!!.ipAddress,
                            Color.alpha(it.color!!))
                        executor.execute(jobW)
                    }
                }
                val br = BrightnessSendingService.sendSingleBrightness(
                    it.device!!.ipAddress,
                    it.brightness
                )
                executor.execute(br)
            }

            turnDeviceOn(it.device!!, afterOn)
        }
    }

    fun turnDeviceOn(device: Device, afterThat: Runnable) {
        val url = URL("http://${device.ipAddress}/setOn")
        val job = HttpRequestService.requestToRunnable<Any>(url, {afterThat.run()}, {}, Any::class.java)
        executor.execute(job)
    }

    fun turnDeviceOff(device: Device) {
        val url = URL("http://${device.ipAddress}/setOff")
        val job = HttpRequestService.requestToRunnable<Any>(url, {}, {}, Any::class.java)
        executor.execute(job)
    }
}