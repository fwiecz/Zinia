package de.hpled.zinia.dto

data class DeviceStatusDTO (
    val numLeds: Int? = null,
    val isOn: Int? = null,
    var isRGBW: Int? = null,
    var ip: String? = null
)