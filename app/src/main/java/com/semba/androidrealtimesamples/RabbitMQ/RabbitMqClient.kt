package com.semba.androidrealtimesamples.RabbitMQ

import android.util.Log
import com.rabbitmq.client.*
import java.lang.Exception
import java.util.concurrent.LinkedBlockingDeque

enum class DequeEnds{
    FIRST,
    LAST
}

class RabbitMqClient {

    private val factory: ConnectionFactory = ConnectionFactory()
    private val messageQueue: LinkedBlockingDeque<String> = LinkedBlockingDeque()
    private var publishThread: Thread? = null
    private var subscribeThread: Thread? = null

    internal class ConnectionBox
    {
        var connection: Connection? = null
        var channel: Channel? = null

        constructor(connection: Connection?, channel: Channel?) {
            this.connection = connection
            this.channel = channel
        }
    }

    companion object {
        const val URL = "HOST"
        const val USERNAME = "Semba"
        const val PASSWORD = "xxxxxx"
        const val RabbitMQ_TAG = "RabbitMQ Log"
        const val EXCHANGE_NAME = "topic"
        const val ROUTING_KEY = "route"
        const val TIMEOUT: Long = 30000
        const val QOS = 0 //The Maximum number of messages that the server will deliver, 0 if Unlimited
    }

    private fun connect(): ConnectionBox?
    {
        // Called internally
        try {
            factory.host = URL
            factory.username = USERNAME
            factory.password = PASSWORD
            factory.isAutomaticRecoveryEnabled = false
            /** you can use "factory.setUri()" as a convenience method for setting the fields**/

            //Set up timeout parameters
            factory.connectionTimeout
            factory.handshakeTimeout
            factory.channelRpcTimeout
            factory.workPoolTimeout
            factory.shutdownTimeout

            val connection = factory.newConnection()
            val channel = connection?.createChannel()
            channel?.basicQos(QOS)
            channel?.confirmSelect()

            return ConnectionBox(connection,channel)
        }
        catch (e: Exception)
        {
            Log.e(RabbitMQ_TAG, "Failed to connect", e)
        }

        return null
    }

    fun publish()
    {
        val connectionBox = connect()
        val channel = connectionBox?.channel
        publishThread = Thread {
            while (true) {
                val msg = messageQueue.takeFirst()
                try {
                    channel?.basicPublish(EXCHANGE_NAME, ROUTING_KEY, AMQP.BasicProperties(), msg.toByteArray())
                    channel?.waitForConfirmsOrDie(TIMEOUT)
                } catch (e: InterruptedException) {
                    // We've been interrupted.
                    break
                } catch (e: Exception) {
                    messageQueue.putFirst(msg)
                    Thread.sleep(5000)
                    Log.e(RabbitMQ_TAG, "Publish is failed", e)
                }
            }
        }

        publishThread?.start()
    }

    fun stopPublishing()
    {
        publishThread?.interrupt()
    }

    fun subscribe(callback: RabbitMQSubscribeListener?)
    {
        val connectionBox = connect()
        val channel = connectionBox?.channel
        subscribeThread = Thread {
            val queue = channel?.queueDeclare()
            channel?.queueBind(queue?.queue, EXCHANGE_NAME, ROUTING_KEY)
            val consumer = QueueingConsumer(channel)
            channel?.basicConsume(queue?.queue, true, consumer)

            while (true)
            {
                try {
                    val delivery = consumer.nextDelivery(TIMEOUT)
                    val msg = String(delivery.body)
                    callback?.onMessageDelivery(msg)

                } catch (e: InterruptedException) {
                    // We've been interrupted.
                    break
                } catch (e: Exception) {
                    Log.e(RabbitMQ_TAG, "Connection is broken", e)
                    callback?.onError(e)
                }
            }
        }
    }

    fun unsubscribe()
    {
        subscribeThread?.interrupt()
    }

    fun addMessage(whichEnd: DequeEnds, msg: String)
    {
        when(whichEnd)
        {
            DequeEnds.FIRST ->
            {
                messageQueue.putFirst(msg)
            }

            DequeEnds.LAST ->
            {
                messageQueue.putLast(msg)
            }
        }
    }

    fun removeMessageAt(whichEnd: DequeEnds)
    {
        when(whichEnd)
        {
            DequeEnds.FIRST ->
            {
                messageQueue.removeFirst()
            }

            DequeEnds.LAST ->
            {
                messageQueue.removeLast()
            }
        }
    }

    fun removeMessage(msg: String)
    {
        messageQueue.remove(msg)
    }

    fun clearMessages()
    {
        messageQueue.clear()
    }

}