package com.materials.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Color Palette Tokens from DESIGN.md
val IndustrialOrange = Color(0xFFA04100) // Primary Construction Orange
val IndustrialCharcoalDark = Color(0xFF191C1E) // On Surface / High Density Text
val IndustrialCharcoalMedium = Color(0xFF5F5E5E) // Secondary Outline / Subtitle
val IndustrialSteelBlue = Color(0xFF505F76) // Tertiary
val IndustrialBackground = Color(0xFFF7F9FB) // Clean cool gray
val IndustrialSurface = Color(0xFFFFFFFF) // Surface Card Background
val IndustrialOutline = Color(0xFFE2BFB0) // Construction light borders

private val LightColorScheme = lightColorScheme(
    primary = IndustrialOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCC),
    onPrimaryContainer = Color(0xFF351000),
    secondary = IndustrialCharcoalMedium,
    onSecondary = Color.White,
    background = IndustrialBackground,
    onBackground = IndustrialCharcoalDark,
    surface = IndustrialSurface,
    onSurface = IndustrialCharcoalDark,
    outline = IndustrialOutline,
    tertiary = IndustrialSteelBlue
)

// Standard shapes matching the "Soft (0.25rem / 4dp)" brand style guidelines in DESIGN.md
val IndustrialShapes = Shapes(
    small = RoundedCornerShape(4.dp),       // Standard buttons/inputs (0.25rem)
    medium = RoundedCornerShape(8.dp),      // Cards and large containers (0.5rem)
    large = RoundedCornerShape(12.dp)       // Badges & Status Indicators
)

// Typography hierarchy matching Inter scale guidelines in DESIGN.md
val IndustrialTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif, // Inter is used as system sans-serif fallback
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.01).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.05.sp
    )
)

@Composable
fun IndustrialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Keep it light or structural as requested, respecting DESIGN.md
    val colorScheme = if (darkTheme) {
        // Fallback or adaptive dark scheme with a professional charcoal backdrop
        darkColorScheme(
            primary = Color(0xFFFFB693),
            onPrimary = Color(0xFF572000),
            background = Color(0xFF191C1E),
            onBackground = Color(0xFFECEEF0),
            surface = Color(0xFF2D3133),
            onSurface = Color(0xFFEFF1F3)
        )
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = IndustrialTypography,
        shapes = IndustrialShapes,
        content = content
    )
}
