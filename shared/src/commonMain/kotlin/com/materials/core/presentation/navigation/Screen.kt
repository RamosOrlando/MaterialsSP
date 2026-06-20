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
    data class Section(val categoryId: String? = null) : Screen
    
    @Serializable
    data class Material(val sectionId: String? = null) : Screen

    @Serializable
    data class MaterialsSelected(val materialIds: List<String>) : Screen

    @Serializable
    data class PrintPreview(
        val materialIds: List<String>,
        val quantities: Map<String, Double> = emptyMap()
    ) : Screen
    
    @Serializable
    data object PriceHistory : Screen
}
