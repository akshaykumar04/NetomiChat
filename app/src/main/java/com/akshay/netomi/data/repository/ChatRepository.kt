package com.akshay.netomi.data.repository

import android.util.Log
import com.akshay.netomi.data.model.ChatRoom
import com.akshay.netomi.data.model.Message
import com.akshay.netomi.utils.NetworkObserver
import com.akshay.netomi.data.socket.SocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Repository to manage ChatRoom data, messages, and operations.
 */
class ChatRepository @Inject constructor(
    private val socketManager: SocketManager,
    private val networkObserver: NetworkObserver
) {

    private val _chatRoomsFlow = MutableStateFlow(
        listOf(
            ChatRoom(name = "Netomi")
        )
    )
    val chatRoomsFlow: StateFlow<List<ChatRoom>> = _chatRoomsFlow.asStateFlow()

    private val mutex = Mutex()
    private val queuedMessages = mutableListOf<Message>()

    init {
        observeNetworkChanges()
        observeIncomingMessages()
    }

    private fun observeNetworkChanges() {
        CoroutineScope(Dispatchers.IO).launch {
            networkObserver.networkStatus.collect { isConnected ->
                if (isConnected) {
                    delay(5000) // Ensuring socket is connected
                    retryQueuedMessages()
                }
            }
        }
    }

    private fun observeIncomingMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            socketManager.messageFlow.collect { message ->
                updateChatRoom(message)
            }
        }
    }

    private suspend fun updateChatRoom(message: Message) {
        _chatRoomsFlow.value = _chatRoomsFlow.value.map { chatRoom ->
            if (chatRoom.name == message.roomName) chatRoom.addOrUpdateMessage(message) else chatRoom
        }

        removeMessageFromQueueIfExists(message)
    }

    fun markChatAsRead(roomName: String) {
        _chatRoomsFlow.value = _chatRoomsFlow.value.map { chatRoom ->
            if (chatRoom.name == roomName) {
                chatRoom.copy(unreadCount = 0)
            } else chatRoom
        }
    }

    suspend fun sendMessage(message: Message) {
        mutex.withLock {
            queueMessage(message)
        }
        socketManager.send("new-message", message)
        updateChatRoom(message)
    }

    private fun queueMessage(message: Message) {
        queuedMessages.add(message)
    }

    private suspend fun retryQueuedMessages() {
        val queuedCopy: List<Message>

        mutex.withLock {
            queuedCopy = queuedMessages.toList()
        }

        val sentMessageIds = mutableListOf<String>()

        for (message in queuedCopy) {
            try {
                socketManager.send("new-message", message)
                sentMessageIds.add(message.id)
            } catch (e: Exception) {
                Log.e("Socket", "Failed to resend message: ${message.id}", e)
            }
        }

        mutex.withLock {
            queuedMessages.removeAll { it.id in sentMessageIds }
        }
    }


    private suspend fun removeMessageFromQueueIfExists(message: Message) {
        mutex.withLock {
            val index = queuedMessages.indexOfFirst { it.id == message.id }
            if (index != -1 && message.isSent) {
                queuedMessages.removeAt(index)
            }
        }
    }

    private fun ChatRoom.addOrUpdateMessage(message: Message): ChatRoom {
        val updatedMessages = messages.toMutableList()
        val existingIndex = updatedMessages.indexOfFirst { it.id == message.id }

        if (existingIndex != -1) {
            // Update existing message
            updatedMessages[existingIndex] = message
        } else {
            // Add new message
            updatedMessages.add(message)
        }

        return this.copy(
            messages = updatedMessages,
            lastMessage = message.text,
            unreadCount = if (!message.isSentByUser) this.unreadCount + 1 else this.unreadCount
        )
    }
}
