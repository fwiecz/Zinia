package de.hpled.zinia.services

import de.hpled.zinia.colorpick.views.OnBrightnessWarmthChangedListener
import java.net.URL
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class BrightnessSendingService(private val sendingFrequency: Long) :
    OnBrightnessWarmthChangedListener {

    private val executor = ScheduledThreadPoolExecutor(1)
    private var targetBrightness = 255
    private var isExecuting = false
    private var scheduledFuture: ScheduledFuture<*>? = null
    var targetIP: String = ""

    fun startExecutor() {
        isExecuting = true
        scheduledFuture = executor.scheduleWithFixedDelay(
            executorRunnable,
            sendingFrequency,
            sendingFrequency,
            TimeUnit.MILLISECONDS
        )
    }

    fun stopExecutor() {
        scheduledFuture?.cancel(false)
        isExecuting = false
    }

    private val executorRunnable = Runnable {
        sendSingleBrightness(targetIP, targetBrightness).run()
    }

    override fun onBrightnessChanged(value: Int, final: Boolean) {
        targetBrightness = value
        if(final) {
            stopExecutor()
            executor.execute(sendSingleBrightness(targetIP, value))
        } else if(!isExecuting) {
            startExecutor()
        }
    }

    companion object {
        fun sendSingleBrightness(ip: String, value: Int) : Runnable{
            val url = URL("http://$ip/setBrightness?br=$value")
            return HttpRequestService.requestToRunnable<Any>(url, {}, {}, Any::class.java)
        }
    }

    override fun onWarmthChanged(value: Int, final: Boolean) {
        // Warmth should be send via [ColorSendingService]
    }
}