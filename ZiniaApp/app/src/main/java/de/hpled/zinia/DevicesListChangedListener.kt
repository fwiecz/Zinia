package de.hpled.zinia

import de.hpled.zinia.entities.Device

interface DevicesListChangedListener {
    fun onDevicesChanged(devices: List<Device>)
}