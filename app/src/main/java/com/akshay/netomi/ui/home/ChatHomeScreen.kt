package com.akshay.netomi.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akshay.netomi.data.model.ChatRoom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatHomeScreen(
    viewModel: ChatHomeViewModel, onChatClick: (ChatRoom) -> Unit
) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surface, colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    )
            ) {
                if (chatRooms.isEmpty()) {
                    EmptyChatList()
                } else {
                    ChatRoomsList(chatRooms = chatRooms, onChatClick = onChatClick)
                }

                FloatingActionButton(
                    onClick = { /* Create new chat */ },
                    shape = CircleShape,
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp, pressedElevation = 8.dp
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New Chat",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = colorScheme.surfaceColorAtElevation(3.dp),
        tonalElevation = 3.dp,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Chats", style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ), color = colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Search */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = colorScheme.onSurface
                    )
                }

                IconButton(onClick = { /* More options */ }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatRoomsList(
    chatRooms: List<ChatRoom>, onChatClick: (ChatRoom) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(chatRooms.size, key = { chatRooms[it].name }) { index ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                modifier = Modifier.animateItemPlacement()
            ) {
                ChatRoomItem(
                    chatRoom = chatRooms[index], onChatClick = onChatClick
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoom, onChatClick: (ChatRoom) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onChatClick(chatRoom) }) {
        Row(
            modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            ChatAvatar(name = chatRoom.name)

            Spacer(modifier = Modifier.width(12.dp))

            // Chat info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                // Chat name and time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chatRoom.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (chatRoom.unreadCount > 0) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Text(
                        text = formatTimestampV2(chatRoom.messages.lastOrNull()?.timestamp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (chatRoom.unreadCount > 0) colorScheme.primary
                        else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Preview message and unread count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (chatRoom.lastMessage.isNotBlank()) {
                        // Message preview
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (chatRoom.unreadCount > 0) {
                                // Unread indicator dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(colorScheme.primary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Text(
                                text = chatRoom.lastMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (chatRoom.unreadCount > 0) colorScheme.onSurface
                                else colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Unread count badge
                        if (chatRoom.unreadCount > 0) {
                            UnreadBadge(count = chatRoom.unreadCount)
                        }
                    } else {
                        Text(
                            text = "No messages yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colorScheme.primary, colorScheme.tertiary
                    )
                ), shape = CircleShape
            ), contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.first().toString().uppercase(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = colorScheme.onPrimary
        )
    }
}

@Composable
fun UnreadBadge(count: Int) {
    Box(
        modifier = Modifier
            .background(colorScheme.primary, CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = colorScheme.onPrimary
        )
    }
}

@Composable
fun EmptyChatList() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp),
                tint = colorScheme.primary.copy(alpha = 0.6f)
            )

            Text(
                text = "No conversations yet",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.onSurface
            )

            Text(
                text = "Start a new chat with the + button",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )


            OutlinedButton(
                onClick = { /* Handle new chat */ },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.primary
                ),
                border = BorderStroke(1.dp, colorScheme.primary),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Chat")
            }
        }
    }
}

fun formatTimestampV2(timestamp: Long?): String {
    if (timestamp == null) return ""

    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 24 * 60 * 60 * 1000 -> {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        }

        diff < 48 * 60 * 60 * 1000 -> {
            "Yesterday"
        }

        diff < 7 * 24 * 60 * 60 * 1000 -> {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
        }

        else -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
        }
    }
}