package de.hpled.zinia.services

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.URL
import kotlin.reflect.typeOf

class HttpRequestService {

    interface OnResponseListener<T> {
        fun onSuccess(response: T)
        fun onError()
    }

    companion object {

        private val gson = Gson()
        private const val TAG = "HttpRequestService"

        /**
         * Creates a simple http request.
         * @return The request as Runnable
         */
        @JvmStatic
        fun <T>requestToRunnable(
            url: URL,
            responseListener: OnResponseListener<T>,
            responseType: Type = Any::class.java,
            timeout: Int = 2000
        ) : Runnable {
            return Runnable {
                val connection = url.openConnection().apply {
                    connectTimeout = timeout
                    readTimeout = timeout
                    doOutput = true
                }
                try {
                    Log.i(TAG, "Connecting to $url")
                    connection.connect()
                    val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                    val response = gson.fromJson<T>(reader.readText(), responseType)
                    responseListener.onSuccess(response)
                } catch (e: Exception) {
                    responseListener.onError()
                    Log.w(TAG, "Cannot connect to $url.")
                }
            }
        }

        /**
         * Creates a simple request and runs it on a background Thread via [AsyncTask.execute].
         */
        @JvmStatic
        fun <T> request(
            url: URL,
            responseListener: OnResponseListener<T>,
            responseType: Type = Any::class.java,
            timeout: Int = 2000
        ) {
            AsyncTask.execute {
                requestToRunnable(url, responseListener, responseType, timeout).run()
            }
        }

        /**
         * Creates a simple http request.
         * @return The request as Runnable
         */
        @JvmStatic
        fun <T> requestToRunnable(
            url: URL,
            success: (T) -> Unit,
            error: () -> Unit,
            responseType: Type = Any::class.java,
            timeout: Int = 2000
        ) : Runnable {
            return requestToRunnable(url, object : OnResponseListener<T> {
                override fun onSuccess(response: T) {
                    success(response)
                }
                override fun onError() {
                    error()
                }
            }, responseType, timeout)
        }

        /**
         * Creates a simple request and runs it on a background Thread via [AsyncTask.execute].
         */
        @JvmStatic
        fun <T> request(
            url: URL,
            success: (T) -> Unit,
            error: () -> Unit,
            responseType: Type = Any::class.java,
            timeout: Int = 2000
        ) {
            request(url, object : OnResponseListener<T> {
                override fun onSuccess(response: T) {
                    success(response)
                }
                override fun onError() {
                    error()
                }
            }, responseType, timeout)
        }
    }
}