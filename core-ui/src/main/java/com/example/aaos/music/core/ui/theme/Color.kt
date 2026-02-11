package com.example.aaos.music.core.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Theme Colors
val GradientStart = Color(0xFF2F374C)
val GradientEnd = Color(0xFF000000)
val DarkBackground = GradientStart // Fallback to black
val DarkSurface = Color(0x80000000) // Semi-transparent black for surface
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkSurfaceVariant = Color(0xFF2F374C) // Matches GradientStart
val DarkOnSurfaceVariant = Color(0xFFC4C7D0) // Light grey for text on variant
val DarkPrimary = Color(0xFFFFFFFF) // White as per design
val DarkOnPrimary = Color(0xFF000000) // Black icon on white primary
val DarkSecondary = Color(0xFFB0BEC5) // Valid secondary for dark mode
val DarkOutline = Color(0xFF68696E) // For borders/strokes

// Light Theme Colors

val LightGradientStart = Color(0xFFFFFFFF)
val LightGradientEnd = Color(0xFF2F374C)
val LightBackground = Color(0xFFF5F5F7) // Keep a neutral light background
val LightSurface = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1A1A1A)
val LightSurfaceVariant = Color(0xFFE1E2E4)
val LightOnSurfaceVariant = Color(0xFF44474E)
val LightPrimary = Color(0xFF2F374C) // Using the SVG color as Primary
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF53607A) // A complementary secondary
val LightOnSecondary = Color(0xFFFFFFFF)
val LightOutline = Color(0xFF74777F)
