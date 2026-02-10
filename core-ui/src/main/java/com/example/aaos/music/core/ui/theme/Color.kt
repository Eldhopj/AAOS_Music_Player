package com.example.aaos.music.core.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Theme Colors
val GradientStart = Color(0xFF2F374C)
val GradientEnd = Color(0xFF000000)
val DarkBackground = GradientEnd // Fallback to black
val DarkSurface = Color(0x80000000) // Semi-transparent black for surface
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkSurfaceVariant = Color(0xFF2F374C) // Matches GradientStart
val DarkOnSurfaceVariant = Color(0xFFC4C7D0) // Light grey for text on variant
val DarkPrimary = Color(0xFFFFFFFF) // White as per design
val DarkOnPrimary = Color(0xFF000000) // Black icon on white primary
val DarkSecondary = Color(0xFFB0BEC5) // Valid secondary for dark mode

// Light Theme Colors
val LightBackground = Color(0xFFF5F5F7)
val LightSurface = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1A1A1A)
val LightPrimary = Color(0xFF00BCD4) // Slightly darker Cyan for light mode visibility
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF757575)

// Common
val BrandCyan = Color(0xFF00E5FF)
