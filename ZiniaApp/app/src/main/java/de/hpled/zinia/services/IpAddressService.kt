package de.hpled.zinia.services

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import java.net.InetAddress
import java.nio.ByteBuffer

class IpAddressService {
    companion object {

        /**
         * Returns the IPv4 address of the android device.
         * This method cannot run on the main thread.
         */
        fun getOwnIp(context: Application) : String {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ip = wifiManager.connectionInfo.ipAddress
            val bytebuffer = ByteBuffer.allocate(4)
            val bytearray = bytebuffer.putInt(ip).array().reversedArray()
            return InetAddress.getByAddress(bytearray).toString().replace("/", "")
        }
    }
}