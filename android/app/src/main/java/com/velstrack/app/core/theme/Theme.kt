package com.velstrack.app.core.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite, // Primary actions are white on black
    secondary = MetallicSilver,
    background = AbsoluteBlack,
    surface = SurfaceGray,
    error = RoseDanger,
    onPrimary = AbsoluteBlack, // Text on primary white buttons should be black
    onSecondary = AbsoluteBlack,
    onBackground = PureWhite,
    onSurface = PureWhite,
    surfaceVariant = ElevatedGray,
    onSurfaceVariant = DarkSilver
)

@Composable
fun VelstrackTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AbsoluteBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = ExecutiveTypography,
        content = content
    )
}
