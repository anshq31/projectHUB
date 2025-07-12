package com.example.projecthub.ui.theme

import AppTypography
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.projecthub.viewModel.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

// Simplified Dark Color Scheme - using clean dark colors with yellow primary
private val DarkColorScheme = darkColorScheme(
    primary = DarkColors.Primary,
    onPrimary = DarkColors.OnPrimary,
    background = DarkColors.Background,
    onBackground = DarkColors.OnBackground,
    surface = DarkColors.Surface,
    onSurface = DarkColors.OnSurface,
    secondary = DarkColors.Secondary,
    onSecondary = DarkColors.OnSecondary,
    error = DarkColors.Error,
    onError = DarkColors.OnError,
    // Additional Material3 colors for consistency
    primaryContainer = DarkColors.Primary.copy(alpha = 0.2f),
    onPrimaryContainer = DarkColors.OnBackground,
    secondaryContainer = DarkColors.Secondary.copy(alpha = 0.2f),
    onSecondaryContainer = DarkColors.OnBackground,
    errorContainer = DarkColors.Error.copy(alpha = 0.2f),
    onErrorContainer = DarkColors.OnBackground,
    outline = DarkColors.Secondary,
    surfaceVariant = DarkColors.Surface.copy(alpha = 0.8f),
    onSurfaceVariant = DarkColors.OnSurface.copy(alpha = 0.8f)
)

// Simplified Light Color Scheme - using clean light colors with yellow primary
private val LightColorScheme = lightColorScheme(
    primary = LightColors.Primary,
    onPrimary = LightColors.OnPrimary,
    background = LightColors.Background,
    onBackground = LightColors.OnBackground,
    surface = LightColors.Surface,
    onSurface = LightColors.OnSurface,
    secondary = LightColors.Secondary,
    onSecondary = LightColors.OnSecondary,
    error = LightColors.Error,
    onError = LightColors.OnError,
    // Additional Material3 colors for consistency
    primaryContainer = LightColors.Primary.copy(alpha = 0.1f),
    onPrimaryContainer = LightColors.OnBackground,
    secondaryContainer = LightColors.Secondary.copy(alpha = 0.1f),
    onSecondaryContainer = LightColors.OnBackground,
    errorContainer = LightColors.Error.copy(alpha = 0.1f),
    onErrorContainer = LightColors.OnBackground,
    outline = LightColors.Secondary,
    surfaceVariant = LightColors.Surface.copy(alpha = 0.8f),
    onSurfaceVariant = LightColors.OnSurface.copy(alpha = 0.8f)
)

@Composable
fun ProjectHUBTheme(
    themeViewModel: ThemeViewModel,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkMode.collectAsState()

    // Clean theme selection - minimal and consistent
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}