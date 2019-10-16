package de.hpled.zinia.newdevice.interfaces

import de.hpled.zinia.entities.DeviceType

interface NewDeviceListener {

    /**
     * Gets triggered when a newInstance device should be created.
     */
    fun onNewDevice(ip: String, name: String, numLeds: Int, type: DeviceType)
}