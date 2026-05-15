package com.electricbiro.runningmetronome.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = OnAccent,
    primaryContainer = AccentSoft,
    onPrimaryContainer = Accent,
    secondary = Accent,
    onSecondary = OnAccent,
    secondaryContainer = BgElev,
    onSecondaryContainer = TextPrimary,
    background = BgBase,
    onBackground = TextPrimary,
    surface = BgBase,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextMute,
    outline = LineStrong,
    outlineVariant = LineBorder,
)

@Composable
fun RunningMetronomeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content,
    )
}
