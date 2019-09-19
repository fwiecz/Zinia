package de.hpled.zinia.viewmodels

import androidx.lifecycle.ViewModel
import de.hpled.zinia.entities.Device
import de.hpled.zinia.entities.MoodTask
import de.hpled.zinia.services.ColorSendingService
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledThreadPoolExecutor

class MoodsFragmentViewModel : ViewModel() {

    private val executor = ScheduledThreadPoolExecutor(10)

    fun playMoodTasks(moodTasks: List<MoodTask>) {
        moodTasks.forEach {
            val afterOn = Runnable {
                if(it.color != null) {
                    val job = ColorSendingService.sendSingleColor(it.device!!.ipAddress, it.color!!)
                    executor.execute(job)
                }
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