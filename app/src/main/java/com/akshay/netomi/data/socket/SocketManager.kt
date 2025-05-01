package com.akshay.netomi.data.socket

import com.akshay.netomi.data.model.Message
import com.google.gson.Gson
import com.piesocket.channels.Channel
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() {

    private var pieSocket: PieSocket? = null
    private var channel: Channel? = null
    private val chatRoom: String = "Netomi"

    private val _messageFlow = MutableSharedFlow<Message>(replay = 0, extraBufferCapacity = 1)
    val messageFlow = _messageFlow.asSharedFlow()

    fun connect() {
        val options = PieSocketOptions()
        options.clusterId = "s14571.blr1"
        options.apiKey = "0ijCV4zQfRf9uQlGxuV6VfZL4cpMyKX01pORmYvC"

        pieSocket = PieSocket(options)
        channel = pieSocket?.join(chatRoom)

        channel?.let { listen(it, "system:connected") }
        channel?.let { listen(it, "new-message") }


    }

    private fun listen(channel: Channel, eventName: String) {
        channel.listen(eventName, object : PieSocketEventListener() {
            override fun handleEvent(event: PieSocketEvent?) {
                event?.data?.let { message ->
                    if (event.event == "new-message") {
                        val metaData: Message? = Gson().fromJson(event.meta, Message::class.java)
                        metaData?.let {
                            val messageId = metaData.id
                            val dateTime = metaData.timestamp
                            val userId = "1"
                            val userName = "User1"
                            emitMessage(message, dateTime, userId, userName, messageId)
                        } ?: run {
                            emitMessage(
                                content = message,
                                dateTime = System.currentTimeMillis(),
                                userId = "0",
                                userName = "Server",
                                messageId = ""
                            )
                        }
                    }
                }
            }
        })
    }

    fun send(eventName: String, message: Message) {
        val event = PieSocketEvent(eventName)
        event.data = message.text
        event.meta = Gson().toJson(message)
        channel?.publish(event)
    }

    private fun emitMessage(
        content: String,
        dateTime: Long,
        userId: String,
        userName: String,
        messageId: String
    ) {
        val message = if (userId == "1" && userName == "User1") {
            Message(
                id = messageId,
                text = content,
                timestamp = dateTime,
                isSent = true,
                isSentByUser = true,
                isUnread = false
            )

        } else {
            Message(
                text = content,
                timestamp = dateTime,
                isSent = true,
                isSentByUser = false,
                isUnread = true
            )
        }
        _messageFlow.tryEmit(message)
    }

    fun disconnect() {
        channel?.disconnect()
    }
}