package com.semba.androidrealtimesamples

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.gson.JsonSyntaxException
import com.semba.androidrealtimesamples.Shared.Constants
import com.semba.androidrealtimesamples.Shared.NotificationManager
import com.semba.androidrealtimesamples.SignalR.SignalR
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnectionListener
import com.smartarmenia.dotnetcoresignalrclientjava.HubEventListener
import com.smartarmenia.dotnetcoresignalrclientjava.HubMessage
import org.koin.java.KoinJavaComponent.inject
import java.lang.Exception


class RealTimeService : Service() {

    private val notificationManager: NotificationManager by inject(NotificationManager::class.java)

    companion object {
        const val SIGNALR_SERVICE_NOTIFICATION_ID = 1
        const val SERVICE_TAG = "FOREGROUND SERVICE"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(SERVICE_TAG, "onStartCommand")
        val notification = notificationManager.makeStatusNotification("Awesome Notification", "", applicationContext, false)
        startForeground(SIGNALR_SERVICE_NOTIFICATION_ID, notification)
        connect()
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()

        disconnect()
        sendConnectedMessage(false)
        Log.d(SERVICE_TAG,"onDestroy")
    }


    private fun connect() {

    }

    private fun reconnect() {

    }

    private fun disconnect() {

    }

    private fun sendMessage()
    {

    }

    private fun subscribe(){

    }

    private fun unsubscribe(){

    }

    private fun sendConnectedMessage(isConnected: Boolean) {
        //Broadcast it or use EventBus
    }

}