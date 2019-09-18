package de.hpled.zinia.services

import android.graphics.Color
import de.hpled.zinia.fragments.ColorPickerFragment
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class ColorSendingService(private val sendingFrequency: Long) :
    ColorPickerFragment.OnColorChangedListener {

    private val executor = ScheduledThreadPoolExecutor(5)
    private var targetColor = 0
    var targetIP = ""
    var isExecuting = false
        private set
    private var scheduledFuture: ScheduledFuture<*>? = null

    fun startExecutor(ip: String) {
        isExecuting = true
        targetIP = ip
        scheduledFuture = executor.scheduleWithFixedDelay(
            executorRunnable,
            sendingFrequency,
            sendingFrequency, TimeUnit.MILLISECONDS
        )
    }

    fun stopExecutor() {
        scheduledFuture?.cancel(false)
        isExecuting = false
    }

    private fun sendSingleColor(ip: String, color: Int): Runnable {
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

    override fun onColorChanged(color: Int, final: Boolean) {
        targetColor = color
        if (final) {
            stopExecutor()
            executeSingleColor(targetIP, color)
        } else if (!isExecuting) {
            startExecutor(targetIP)
        }
    }
}