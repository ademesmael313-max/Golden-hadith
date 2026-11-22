package com.goldenhadith.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    background = IvoryBackground,
    onBackground = CharcoalOnSurface,
    surface = IvorySurface,
    onSurface = CharcoalOnSurface,
    surfaceVariant = IvorySurfaceVariant,
    onSurfaceVariant = MutedOnSurfaceVariant,
    outline = SubtleOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGoldPrimary,
    onPrimary = Color(0xFF281E00),
    primaryContainer = DarkGoldContainer,
    onPrimaryContainer = DarkOnGoldContainer,
    secondary = DarkEmeraldSecondary,
    onSecondary = Color(0xFF00201C),
    secondaryContainer = DarkEmeraldContainer,
    onSecondaryContainer = DarkOnEmeraldContainer,
    background = ObsidianBackground,
    onBackground = LightOnSurface,
    surface = ObsidianSurface,
    onSurface = LightOnSurface,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = DarkOutline
)

@Composable
fun GoldenHadithTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GoldenHadithTypography,
        content = content
    )
}
