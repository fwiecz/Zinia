package de.hpled.zinia.viewmodels

import android.graphics.Color
import androidx.lifecycle.ViewModel
import de.hpled.zinia.services.HttpRequestService
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class DeviceViewModel : ViewModel() {
    private val executor = ScheduledThreadPoolExecutor(5)
    var targetColor = 0
    var targetIP = ""
    var isExecuting = false
        private set
    private var scheduledFuture: ScheduledFuture<*>? = null

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

    fun sendSingleColor(ip: String, color: Int) : Runnable {
        val c = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))
        val url = URL("http://$ip/setSingleColor?r=${c[0]}%g=${c[1]}%b=${c[2]}")
        return HttpRequestService.requestToRunnable<Any>(url, {}, {})
    }

    private val executorRunnable = Runnable {
        sendSingleColor(targetIP, targetColor).run()
    }

    companion object {
        private const val sendingFrequency = 500L
    }
}