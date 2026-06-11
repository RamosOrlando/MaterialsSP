package com.materials.core.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Login : Screen

    @Serializable
    data object Category : Screen
    
    @Serializable
    data object Maker : Screen

    @Serializable
    data object Provider : Screen
    
    @Serializable
    data class Section(val categoryId: Int? = null) : Screen
    
    @Serializable
    data class Material(val sectionId: Int? = null) : Screen
    
    @Serializable
    data object PriceHistory : Screen
}
