package com.g80bits.smsgateway

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.CompletableFuture

interface MessageListener {
    fun onMessage(message: SmsModel)
}

class WebSocketManager(private val listener: MessageListener) {
    private var client: OkHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var connectionStatus: CompletableFuture<Boolean>? = null

    fun start(url: String, id: String): Boolean {
        return try {
            connectionStatus = CompletableFuture<Boolean>()
            val request = Request.Builder().url("ws://$url?id=$id").build()
            webSocket = client.newWebSocket(request, MyWebSocketListener(listener))
            return connectionStatus!!.get()
        } catch (e: Exception) {
            false
        }
    }

    fun stop(): Boolean {
        if (webSocket != null) {
            return try {
                webSocket?.close(1000, "Goodbye !")
                client.dispatcher.executorService.shutdown()
                connectionStatus!!.get()
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    fun send(message: String) {
        if (webSocket != null) {
            webSocket?.send(message)
        }
    }

    private inner class MyWebSocketListener(private val callback: MessageListener) :
        WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("WebSocket opened: ${response.message}")
            connectionStatus!!.complete(true)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                println("Message received: $text")
                val gson = Gson()
                val message = gson.fromJson(text, SmsModel::class.java)
                println("Message received: $message")
                callback.onMessage(message)
            } catch (e: Exception) {
                println("Message received: $text")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("Message received: ${bytes.hex()}")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, reason)
            println("WebSocket closing: $code / $reason")
            connectionStatus!!.complete(false)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            connectionStatus!!.complete(false)
        }
    }
}
