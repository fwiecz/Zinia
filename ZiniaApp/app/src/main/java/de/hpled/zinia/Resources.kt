package de.hpled.zinia

import de.hpled.zinia.entities.DeviceType

class Resources {
    companion object {
        fun getDeviceIconId(type: DeviceType) = when(type) {
            DeviceType.LED_CHAIN -> R.drawable.led_strip_icon
            DeviceType.SINGLE_LED -> R.drawable.single_led_icon
            else -> null
        }
    }
}