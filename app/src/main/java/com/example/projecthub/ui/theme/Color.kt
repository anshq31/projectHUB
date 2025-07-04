package com.example.projecthub.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
object LightColors {
    val Primary = Color(0xFFFFC107)           // Yellow primary
    val OnPrimary = Color(0xFF000000)         // Black text on yellow
    val Background = Color(0xFFFFFFFF)        // Pure white background
    val OnBackground = Color(0xFF212121)      // Dark gray text
    val Surface = Color(0xFFF5F5F5)           // Light gray surface
    val OnSurface = Color(0xFF212121)         // Dark gray text on surface
    val Secondary = Color(0xFF757575)         // Medium gray secondary
    val OnSecondary = Color(0xFFFFFFFF)       // White text on secondary
    val Error = Color(0xFFD32F2F)             // Red error
    val OnError = Color(0xFFFFFFFF)           // White text on error
    val Success = Color(0xFF388E3C)           // Green success
    val OnSuccess = Color(0xFFFFFFFF)         // White text on success
}

// Dark Theme Colors
object DarkColors {
    val Primary = Color(0xFFFFC107)           // Same yellow primary
    val OnPrimary = Color(0xFF000000)         // Black text on yellow
    val Background = Color(0xFF000000)        // Pure black background
    val OnBackground = Color(0xFFE0E0E0)      // Light gray text
    val Surface = Color(0xFF121212)           // Dark gray surface
    val OnSurface = Color(0xFFE0E0E0)         // Light gray text on surface
    val Secondary = Color(0xFF757575)         // Medium gray secondary
    val OnSecondary = Color(0xFF000000)       // Black text on secondary
    val Error = Color(0xFFEF5350)             // Lighter red error for dark mode
    val OnError = Color(0xFF000000)           // Black text on error
    val Success = Color(0xFF66BB6A)           // Lighter green success for dark mode
    val OnSuccess = Color(0xFF000000)         // Black text on success
}