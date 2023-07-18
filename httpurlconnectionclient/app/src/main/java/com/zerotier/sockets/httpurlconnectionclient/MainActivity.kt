package com.zerotier.sockets.httpurlconnectionclient

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zerotier.sockets.ZeroTierNative
import com.zerotier.sockets.ZeroTierNode
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ctxt = this.applicationContext
        val storagePath = ctxt.filesDir.absolutePath
        val nwid = "0123456789abcdef".toLong(16)

        val remoteUrl = "http://10.147.19.69:8080/"


        // ZeroTier setup
        val node = ZeroTierNode()
        node.initFromStorage(storagePath)
        node.start()
        //
        // NOTE: This is demo code and waiting on main thread is not recommended
        //
        Log.d(TAG, "Waiting for node to come online...")
        while (!node.isOnline) {
            ZeroTierNative.zts_util_delay(50)
        }
        Log.d(TAG, "Node ID: " + String.format("%010x", node.id))
        Log.d(TAG, "Joining network...")
        node.join(nwid)
        Log.d(TAG, "Waiting for network...")
        while (!node.isNetworkTransportReady(nwid)) {
            ZeroTierNative.zts_util_delay(50)
        }
        Log.d(TAG, "Joined")

        
        // Socket logic

        val url = URL(remoteUrl)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection


        // prevent NetworkOnMainThreadException in demo code
        StrictMode.setThreadPolicy(ThreadPolicy.LAX)


        try{
            urlConnection.requestMethod = "GET"
            val responseCode: Int = urlConnection.responseCode

            Log.d(TAG, "response: $responseCode")
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inn = BufferedReader(InputStreamReader(urlConnection.inputStream))
                var inputLine: String?
                val response = StringBuffer()
                while (inn.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                inn.close()

                // print result
                println(response.toString())
            }
        }
        finally {
            urlConnection.disconnect()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}