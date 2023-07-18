package com.zerotier.sockets.okhttpclient

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zerotier.sockets.ZeroTierNative
import com.zerotier.sockets.ZeroTierNode
import com.zerotier.sockets.ZeroTierSocketsSocketFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder.build
import okhttp3.OkHttpClient.Builder.socketFactory
import okhttp3.Request
import okhttp3.Request.Builder.build
import okhttp3.Request.Builder.post
import okhttp3.Request.Builder.url
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ctxt = this.applicationContext
        val storagePath = ctxt.filesDir.absolutePath
        val nwid = "0123456789abcdef".toLong(16)
        val remoteAddr = "10.147.19.67"
        val port = 8080

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
        val plain: MediaType = parse.parse("text/plain; charset=utf-8")
        val client: OkHttpClient = Builder()
            .socketFactory(ZeroTierSocketsSocketFactory(remoteAddr, port))
            .build()
        val postBody = "Hello from OkHttp!"
        val request: Request = Builder()
            .url("http://www.example.com")
            .post(RequestBody.create(plain, postBody))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "FAILURE!!")
                Log.d(TAG, e.message, e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "RESPONSE!!")
                Log.d(TAG, response.toString())
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}