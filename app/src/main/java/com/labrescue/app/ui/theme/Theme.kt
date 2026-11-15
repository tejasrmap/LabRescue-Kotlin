package com.labrescue.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = ObsidianBg,
    primaryContainer = ObsidianCardElevated,
    onPrimaryContainer = CyanNeon,
    secondary = IndigoNeon,
    onSecondary = TextWhite,
    secondaryContainer = ObsidianCard,
    onSecondaryContainer = TextHighEmphasis,
    background = ObsidianBg,
    onBackground = TextHighEmphasis,
    surface = ObsidianSurface,
    onSurface = TextHighEmphasis,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextMediumEmphasis,
    outline = CardBorderColor,
    error = ErrorRose,
    onError = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = BlueCobalt,
    onPrimary = TextWhite,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = BlueCobalt,
    secondary = IndigoNeon,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = IndigoNeon,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFCBD5E1),
    error = ErrorRose,
    onError = TextWhite
)

@Composable
fun LabRescueTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
