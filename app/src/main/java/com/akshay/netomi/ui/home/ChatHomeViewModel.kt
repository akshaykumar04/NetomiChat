package com.akshay.netomi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshay.netomi.data.model.ChatRoom
import com.akshay.netomi.data.model.Message
import com.akshay.netomi.data.repository.ChatRepository
import com.akshay.netomi.data.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatHomeViewModel @Inject constructor(
    private val socketManager: SocketManager,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()

    // Track current active room (null if none)
    private val _activeRoomName = MutableStateFlow<String?>(null)
    val activeRoomName = _activeRoomName.asStateFlow()

    val messages: StateFlow<List<Message>> = chatRooms
        .combine(activeRoomName) { rooms, activeRoom ->
            rooms.find { it.name == activeRoom }?.messages.orEmpty()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        observeIncomingMessages()
        observeActiveRoomChanges()
    }

    private fun observeIncomingMessages() {
        // Collect messages from the ChatRepository and update the chat rooms
        viewModelScope.launch {
            chatRepository.chatRoomsFlow.collect { message ->
                _chatRooms.value = message
            }
        }
    }

    private fun observeActiveRoomChanges() {
        viewModelScope.launch {
            activeRoomName
                .filterNotNull()
                .collect { roomName ->
                    chatRepository.markChatAsRead(roomName)
                }
        }
    }

    fun sendMessage(roomName: String, message: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                Message(
                    text = message,
                    timestamp = System.currentTimeMillis(),
                    isSent = false,
                    isSentByUser = true,
                    isUnread = false,
                    roomName = roomName
                )
            )
        }
    }

    // When user enters a chat
    fun setActiveRoom(roomName: String?) {
        _activeRoomName.value = roomName
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }
}
