package com.materials.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

// Re-export from :core:design-system module for backward compatibility and clean architecture
val IndustrialOrange = com.materials.core.designsystem.theme.IndustrialOrange
val IndustrialCharcoalDark = com.materials.core.designsystem.theme.IndustrialCharcoalDark
val IndustrialCharcoalMedium = com.materials.core.designsystem.theme.IndustrialCharcoalMedium
val IndustrialSteelBlue = com.materials.core.designsystem.theme.IndustrialSteelBlue
val IndustrialBackground = com.materials.core.designsystem.theme.IndustrialBackground
val IndustrialSurface = com.materials.core.designsystem.theme.IndustrialSurface
val IndustrialOutline = com.materials.core.designsystem.theme.IndustrialOutline
val IndustrialShapes = com.materials.core.designsystem.theme.IndustrialShapes
val IndustrialTypography = com.materials.core.designsystem.theme.IndustrialTypography

@Composable
fun IndustrialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    com.materials.core.designsystem.theme.IndustrialTheme(
        darkTheme = darkTheme,
        content = content
    )
}
