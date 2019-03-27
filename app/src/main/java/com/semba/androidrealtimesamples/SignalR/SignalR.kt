package com.semba.androidrealtimesamples.SignalR

import android.util.Log
import com.semba.androidrealtimesamples.Shared.Config
import com.semba.androidrealtimesamples.Shared.Constants
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnectionListener
import com.smartarmenia.dotnetcoresignalrclientjava.HubEventListener
import com.smartarmenia.dotnetcoresignalrclientjava.WebSocketHubConnectionP2

class SignalR {
    private var connection: HubConnection? = null
    private var token: String? = null

    companion object {
        const val SIGNALR_TAG = "Signal R Log"
    }

    fun getInstance(): HubConnection? {
        if (connection != null)
            return connection
        else {
            Log.println(Log.ASSERT, SIGNALR_TAG, "New SignalR Instance Is Created $token")
            return getInstance(token ?: "")
        }
    }

    fun getInstance(token: String): HubConnection? {
        val isNewToken = token != this.token
        if (connection == null || isNewToken) {
            this.token = token
            val authHeader = "AUTH $token"
            connection = WebSocketHubConnectionP2(Config.SIGNALR_HUB_URL, authHeader)
        }
        return connection
    }

    fun getNewInstance(token: String): HubConnection? {
        this.token = token
        val authHeader = "AUTH $token"
        connection = WebSocketHubConnectionP2(Config.SIGNALR_HUB_URL, authHeader)

        return connection
    }

    fun isConnected(): Boolean {
        try {
            return connection?.isConnected ?: false
        } catch (e: Exception) {
            return false
        }
    }

    fun connect() {
        if (!isConnected()) {
            connection?.connect()
            Log.println(Log.ASSERT, SIGNALR_TAG, "Connect SignalR")
        }
    }

    fun disconnect() {
        if (isConnected()) {
            connection?.disconnect()
            Log.println(Log.ASSERT, SIGNALR_TAG, "Disconnect SignalR")
        }
    }

    fun subscribeEvent(eventName: String, eventListener: HubEventListener) {
        this.connection?.subscribeToEvent(eventName, eventListener)
    }

    fun unsubscribeEvent(eventName: String, eventListener: HubEventListener) {
        this.connection?.unSubscribeFromEvent(eventName, eventListener)
    }

    fun addConnectionListener(connectionListener: HubConnectionListener) {
        this.connection?.addListener(connectionListener)
    }

    fun removeConnectionListener(connectionListener: HubConnectionListener) {
        this.connection?.removeListener(connectionListener)
    }
}