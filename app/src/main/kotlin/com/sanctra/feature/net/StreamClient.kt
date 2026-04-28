package com.sanctra.feature.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class StreamClient(
    private val client: OkHttpClient,
    private val orchestratorWsUrl: String
) {
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    fun connect(sessionId: String, personId: String) {
        val url = "$orchestratorWsUrl/turn/stream".toHttpUrl().newBuilder()
            .addQueryParameter("session_id", sessionId)
            .addQueryParameter("person_id", personId)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Connection opened
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch { _messages.emit(text) }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Handle error
            }
        })
    }

    fun sendAudio(data: ByteArray) {
        webSocket?.send(ByteString.of(*data))
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        scope.cancel()
    }
}
