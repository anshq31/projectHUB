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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.projecthub.viewModel.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

private val DarkColorScheme = darkColorScheme(
    primary = StandardGold,
    onPrimary = PureBlack,
    primaryContainer = RichGold,
    onPrimaryContainer = OffWhite,
    secondary = MediumGold,
    onSecondary = DarkBlack,
    secondaryContainer = DeepGold.copy(alpha = 0.3f),
    onSecondaryContainer = OffWhite,
    tertiary = AccentGold,
    onTertiary = Color.Black,
    tertiaryContainer = DeepGold.copy(alpha = 0.2f),
    onTertiaryContainer = OffWhite,
    background = DarkestBlack,
    onBackground = OffWhite,
    surface = DarkBlack,
    onSurface = OffWhite,
    surfaceVariant = SoftBlack.copy(alpha = 0.4f),
    onSurfaceVariant = LightGray,
    outline = SoftGray,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF8B1D2C),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF9C7C38),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF8E1),
    onPrimaryContainer = Color(0xFF5D4200),
    secondary = Color(0xFF795548),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFF4E342E),
    tertiary = Color(0xFF7E57C2),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEDE7F6),
    onTertiaryContainer = Color(0xFF4527A0),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF212121),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFF9E9E9E),
    error = Color(0xFFB00020),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    scrim = Color(0x52000000),
    inverseSurface = Color(0xFF121212),
    inverseOnSurface = Color(0xFFFFFFFF),
    surfaceTint = Color(0xFF9C7C38).copy(alpha = 0.1f)
)

@Composable
fun ProjectHUBTheme(
    themeViewModel: ThemeViewModel,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkMode.collectAsState()

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
