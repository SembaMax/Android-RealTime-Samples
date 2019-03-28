package com.semba.androidrealtimesamples.MQTT

import android.content.Context
import android.util.Log
import com.semba.androidrealtimesamples.Shared.Config
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTClient(mContext: Context) {

    private var mqttClient: MqttAndroidClient? = null
    private var callback: MqttCallbackExtended = getDefaultCallback()
    set(value) {mqttClient?.setCallback(value)}

    companion object {
        const val MQTT_TAG = "MQTT Log"
        const val CLIENT_ID = "MQTT_CLIENT_ID"
        const val QOS = 2
        const val MQTT_TOPIC = "Topic"
        const val MQTT_DISCONNECT_TIMEOUT: Long = 30000
    }

    init {
        mqttClient = MqttAndroidClient(mContext, Config.MQTT_BROKER_URL, CLIENT_ID)
    }

    private fun getDefaultCallback() = object : MqttCallbackExtended {
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            if (reconnect) {
                Log.e(MQTT_TAG, "reconnect to ${Config.MQTT_BROKER_URL}")

            } else {
                Log.e(MQTT_TAG, "connected to ${Config.MQTT_BROKER_URL}")
            }
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            if (message?.payload != null) {
                val msg = String(message.payload!!)
                Log.e(MQTT_TAG, "Incoming message: $message")
            } else {
                Log.e(MQTT_TAG, "Incoming message: ${message?.payload}")
            }
        }

        override fun connectionLost(cause: Throwable?) {
            if (cause != null) {
                Log.e(MQTT_TAG, "connectionLost", cause as MqttException)

            } else {
                Log.e(MQTT_TAG, "connectionLost")
            }
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Log.e(MQTT_TAG, "deliveryComplete ${token.toString()}")
        }
    }

    fun connect() {

        if (isConnected()) {
            return
        }

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1  //It's 'MQTT_VERSION_3_1_1' By Default
        mqttConnectOptions.isAutomaticReconnect = false
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = "USERNAME"
        mqttConnectOptions.password = "PASSWORD".toCharArray()

        try {
            mqttClient?.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.e(MQTT_TAG, "connected ${Config.MQTT_BROKER_URL}")
                    mqttClient?.setBufferOpts(getDisconnectedBufferOptions())
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(MQTT_TAG, "failed to connect ${Config.MQTT_BROKER_URL}")
                    exception?.printStackTrace()
                    callback.connectionLost(null)
                }
            })

        } catch (exception: MqttException) {
            Log.e(MQTT_TAG, "connect", exception)
        }
    }

    fun publish(topic: String, msg: String) {

        if (!isConnected()) {
            return
        }

        try {
            val mqttMassage = MqttMessage()
            mqttMassage.payload = msg.toByteArray()
            mqttMassage.isRetained = true
            mqttMassage.qos = QOS

            mqttClient?.publish(topic, mqttMassage)
            Log.e(MQTT_TAG, "${mqttClient?.bufferedMessageCount} massages in buffer")

        } catch (e: MqttException) {
            System.err.println("Error Publishing: " + e.message)
            e.printStackTrace()

        } catch (e: Exception) {
            System.err.println("Error Publishing: " + e.message)
            e.printStackTrace()
        }
    }

    fun disconnect() {

        if (!isConnected()) {
            return
        }

        mqttClient?.disconnect(MQTT_DISCONNECT_TIMEOUT,null, object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.e(MQTT_TAG, "MQTT is Disconnected")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(MQTT_TAG, "MQTT is failed to disconnect")
            }
        })
    }

    private fun isConnected(): Boolean {

        try {
            if (mqttClient?.isConnected == true) {
                return true
            }
        } catch (e: IllegalArgumentException) {
            Log.e(MQTT_TAG, "isConnected", e)

        } catch (e: Exception) {
            Log.e(MQTT_TAG, "isConnected", e)
        }

        return false
    }

    private fun getDisconnectedBufferOptions(): DisconnectedBufferOptions{
        val disconnectedBufferOptions = DisconnectedBufferOptions()
        disconnectedBufferOptions.isBufferEnabled = true
        disconnectedBufferOptions.bufferSize = 100
        disconnectedBufferOptions.isPersistBuffer = true
        disconnectedBufferOptions.isDeleteOldestMessages = false
        return disconnectedBufferOptions
    }

    fun subscribeToTopic(topic: String) {
        try {
            mqttClient?.subscribe(topic, QOS, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.e(MQTT_TAG, "Subscribed Successfully to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e(MQTT_TAG, "Failed to subscribe $topic")
                }
            })
        } catch (exception: MqttException) {
            Log.e(MQTT_TAG, "Add Topic", exception)
        }
    }

    private fun unsubscribeToTopics(topic: String) {
        mqttClient?.unsubscribe(topic, null, object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.e(MQTT_TAG, "Unsubscribed Successfully to $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(MQTT_TAG, "Failed to unsubscribe $topic")
            }
        })
    }

}