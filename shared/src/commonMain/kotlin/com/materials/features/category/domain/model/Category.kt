package com.materials.features.category.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    @SerialName("categoryId") val categoryId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("imagePath") val imagePath: String
)
