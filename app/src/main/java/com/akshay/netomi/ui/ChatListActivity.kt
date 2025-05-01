package com.akshay.netomi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.akshay.netomi.model.ConnectionState
import com.akshay.netomi.ui.theme.NetomiChatTheme
import com.akshay.netomi.utils.NetworkObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatListActivity : ComponentActivity() {

    @Inject
    lateinit var networkObserver: NetworkObserver

    private val _connectionState = mutableStateOf(ConnectionState(isConnected = true, shouldShowMessage = false))
    private val connectionState: State<ConnectionState> = _connectionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeNetworkChanges()
        setContent {
            NetomiChatTheme {
                MainAppContent(
                    connectionState = connectionState.value,
                )
            }
        }
    }

    private fun observeNetworkChanges() {
        lifecycleScope.launch {
            networkObserver.networkStatus.collect { connected ->
                _connectionState.value = ConnectionState(
                    isConnected = connected,
                    shouldShowMessage = true
                )

                if (connected) {
                    delay(2000)
                    _connectionState.value = _connectionState.value.copy(shouldShowMessage = false)
                }
            }
        }
    }
}