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
    private var targetWarmth = 0
    private var isExecuting = false
    private var scheduledFuture: ScheduledFuture<*>? = null
    var targetIP: String = ""

    fun startExecutor(runnable: Runnable) {
        isExecuting = true
        scheduledFuture = executor.scheduleWithFixedDelay(
            runnable,
            sendingFrequency,
            sendingFrequency,
            TimeUnit.MILLISECONDS
        )
    }

    fun stopExecutor() {
        scheduledFuture?.cancel(false)
        isExecuting = false
    }

    private val executorRunnableBrightness = Runnable {
        sendSingleBrightness(targetIP, targetBrightness).run()
    }

    private val executorRunnableWarmth = Runnable {
        sendSingleWarmth(targetIP, targetWarmth).run()
    }

    override fun onBrightnessChanged(value: Int, final: Boolean) {
        targetBrightness = value
        if(final) {
            stopExecutor()
            executor.execute(sendSingleBrightness(targetIP, value))
        } else if(!isExecuting) {
            startExecutor(executorRunnableBrightness)
        }
    }

    override fun onWarmthChanged(value: Int, final: Boolean) {
        targetWarmth = value
        if(final) {
            stopExecutor()
            executor.execute(sendSingleWarmth(targetIP, value))
        } else if(!isExecuting) {
            startExecutor(executorRunnableWarmth)
        }
    }

    companion object {
        fun sendSingleBrightness(ip: String, value: Int) : Runnable{
            val url = URL("http://$ip/setBrightness?br=$value")
            return HttpRequestService.requestToRunnable<Any>(url, {}, {}, Any::class.java)
        }

        fun sendSingleWarmth(ip: String, value: Int) : Runnable{
            val url = URL("http://$ip/setWhite?w=$value")
            return HttpRequestService.requestToRunnable<Any>(url, {}, {}, Any::class.java)
        }
    }
}