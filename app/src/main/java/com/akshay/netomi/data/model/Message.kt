package com.akshay.netomi.data.model

import java.util.UUID

/**
 * Model representing a message in a chat room.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSent: Boolean = false,
    val isSentByUser: Boolean = false,
    val isUnread: Boolean = true,
    val roomName: String = "Netomi"
)
