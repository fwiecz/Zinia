package de.hpled.zinia.xcolor

import de.hpled.zinia.services.BrightnessSendingService
import de.hpled.zinia.services.ColorSendingService

class XcolorSendingService {
    companion object {
        fun sendSingleXcolor(ip: String, color: Xcolor): Runnable {
            return Runnable {
                val job = ColorSendingService.sendSingleColor(ip, color.toRgb())
                val jobW = BrightnessSendingService.sendSingleWarmth(ip, color.w)
                val br = BrightnessSendingService.sendSingleBrightness(ip, color.brightness)
                job.run()
                jobW.run()
                br.run()
            }
        }
    }
}