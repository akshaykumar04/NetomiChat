package com.akshay.netomi.data.model

/**
 * Updated Chat Room model with message history.
 */
data class ChatRoom(
    val name: String,
    var lastMessage: String = "",
    var unreadCount: Int = 0,
    val messages: List<Message> = mutableListOf()
)
