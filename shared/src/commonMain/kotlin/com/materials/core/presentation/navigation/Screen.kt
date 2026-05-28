package com.materials.core.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Category : Screen
    
    @Serializable
    data object Orders : Screen
    
    @Serializable
    data object Profile : Screen
}
