package com.akshay.netomi.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.navigation.compose.rememberNavController
import com.akshay.netomi.model.ConnectionState
import com.akshay.netomi.ui.nav.AppNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            ConnectivityStatusBar(
                isConnected = connectionState.isConnected,
                showMessage = connectionState.shouldShowMessage
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}