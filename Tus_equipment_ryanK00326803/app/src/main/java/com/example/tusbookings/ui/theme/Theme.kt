package com.example.tusbookings.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightColors = lightColorScheme(
    primary = TusGreen,
    onPrimary = Color.White,
    primaryContainer = TusGreenLight,
    secondary = TusAmber,
    secondaryContainer = TusAmberLight,
    background = TusBackground
)

private val darkColors = darkColorScheme(
    primary = TusGreenLight,
    onPrimary = TusGreen,
    primaryContainer = TusGreen,
    secondary = TusAmber,
    background = TusDarkBackground
)

@Composable
fun TusBookingsTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        typography = Typography,
        content = content
    )
}
