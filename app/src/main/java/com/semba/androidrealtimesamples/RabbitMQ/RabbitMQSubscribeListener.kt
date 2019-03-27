package com.semba.androidrealtimesamples.RabbitMQ

import java.lang.Exception

interface RabbitMQSubscribeListener {
    fun onMessageDelivery(msg: String)
    fun onError(e: Exception)
}