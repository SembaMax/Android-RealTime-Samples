package com.semba.androidrealtimesamples.SocketIO

import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.IO
import java.net.URISyntaxException

class SocketIOClient {

    lateinit var socket: Socket

    companion object {
        const val URL = ""
        const val SOCKET_TAG = "SOCKET_IO"
        const val EVENT = "event"
    }

    init {
        try {
            socket = IO.socket(URL)
        }
        catch (e: URISyntaxException)
        {
            Log.e(SOCKET_TAG, "Failed to connect")
        }
    }

    fun connect()
    {
        socket.connect()
    }

    fun disconnect()
    {
        socket.disconnect()
    }

    fun isConnected(): Boolean
    {
        return socket.connected()
    }

    fun send(msg: String)
    {
        socket.emit(EVENT, msg)
    }

    fun subscribe(onNewMessageCallback: Emitter.Listener)
    {
        socket.on(EVENT, onNewMessageCallback)
    }
}