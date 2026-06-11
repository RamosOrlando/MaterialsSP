package com.materials.features.section.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Section(
    @SerialName("sectionId") val sectionId: Int,
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("categoryId") val categoryId: Int? = null,
    @SerialName("imagePath") val imagePath: String? = null
)
