package com.akshay.netomi.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Teal80 = Color(0xFF80CBC4)
val SeafoamGreen80 = Color(0xFFB2DFDB)
val SoftBlue80 = Color(0xFFB3E5FC)

val Teal40 = Color(0xFF00897B)
val SeafoamGreen40 = Color(0xFF26A69A)
val SoftBlue40 = Color(0xFF039BE5)

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    secondary = SeafoamGreen80,
    tertiary = SoftBlue80
)

private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    secondary = SeafoamGreen40,
    tertiary = SoftBlue40
)

@Composable
fun NetomiChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}