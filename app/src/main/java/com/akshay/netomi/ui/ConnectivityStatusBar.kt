package com.akshay.netomi.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akshay.netomi.model.StatusInfo

@Composable
fun ConnectivityStatusBar(
    isConnected: Boolean,
    showMessage: Boolean
) {
    AnimatedVisibility(
        visible = showMessage,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        val statusInfo = if (isConnected) {
            StatusInfo(Color(0xFF4CAF50), "Back Online")
        } else {
            StatusInfo(Color(0xFFF44336), "No Internet Connection")
        }

        NetworkStatusBanner(statusInfo)
    }
}

@Composable
fun StatusInfo(x0: Color, x1: String) {
    TODO("Not yet implemented")
}

@Composable
private fun NetworkStatusBanner(statusInfo: StatusInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(statusInfo.backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusInfo.message,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}