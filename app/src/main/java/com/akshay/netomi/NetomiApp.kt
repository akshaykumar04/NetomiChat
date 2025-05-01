package com.akshay.netomi

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.akshay.netomi.data.socket.SocketManager
import com.akshay.netomi.utils.NetworkObserver
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class NetomiApp : Application(), LifecycleObserver {

    @Inject
    lateinit var socketManager: SocketManager

    @Inject
    lateinit var networkObserver: NetworkObserver

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        CoroutineScope(Dispatchers.IO).launch {
            networkObserver.networkStatus.collect { isConnected ->
                if (isConnected) {
                    socketManager.connect()
                } else {
                    Log.d("Socket", "Not connecting: No Internet available at startup.")
                }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        socketManager.disconnect()
    }
}