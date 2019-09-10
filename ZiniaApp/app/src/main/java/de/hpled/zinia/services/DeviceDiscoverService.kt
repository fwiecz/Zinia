package de.hpled.zinia.services

import android.net.Uri
import android.os.AsyncTask
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class DeviceDiscoverService {

    companion object {

        private val gson = Gson()

        @JvmStatic
        fun request(url: URL,
                    successListener: Runnable,
                    onErrorListener: Runnable,
                    timeout: Int = 2000) {
            AsyncTask.execute {
                val connection = url.openConnection().apply {
                    connectTimeout = timeout
                    readTimeout = timeout
                    doOutput = true
                }
                try {
                    connection.connect()
                    val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                    val response = gson.fromJson<Map<String, String>>(reader.readText(), Map::class.java)
                    successListener.run()
                    // TODO handle response
                } catch (e: Exception) {
                    e.printStackTrace()
                    onErrorListener.run()
                }
            }
        }

        @JvmStatic
        fun request(url: URL,
                    success: () -> Unit,
                    error: () -> Unit,
                    timeout: Int = 2000) {
            request(url, Runnable(success), Runnable(error), timeout)
        }
    }
}