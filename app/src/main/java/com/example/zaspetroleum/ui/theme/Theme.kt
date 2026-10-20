package com.example.zaspetroleum.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = ZasOrange,
    onPrimary = Color.White,
    primaryContainer = ZasOrangeTint,
    onPrimaryContainer = ZasOrangeDark,
    secondary = ZasNavy,
    onSecondary = Color.White,
    tertiary = ZasNavyLight,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = ZasDanger,
    errorContainer = ZasDangerBg,
    onErrorContainer = ZasDanger
)

private val DarkColors = darkColorScheme(
    primary = ZasOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3A2A1A),
    onPrimaryContainer = ZasOrange,
    secondary = Color(0xFF9DB7E6),
    onSecondary = Color(0xFF0B1220),
    tertiary = Color(0xFF9DB7E6),
    background = Color(0xFF0B1220),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF141C2E),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1C2740),
    onSurfaceVariant = Color(0xFFA7B3C7),
    outline = Color(0xFF3A4766),
    outlineVariant = Color(0xFF2A3550),
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF3D1717),
    onErrorContainer = Color(0xFFF87171)
)

/** Same name and first parameter as the generated theme, so existing calls keep working. */
@Composable
fun ZasPetroleumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
