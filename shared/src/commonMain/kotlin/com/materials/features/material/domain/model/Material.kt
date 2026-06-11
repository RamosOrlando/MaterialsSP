package com.materials.features.material.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Material(
    @SerialName("materialId") val materialId: String,
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("unit") val unit: String? = null,
    @SerialName("makerId") val makerId: String? = null,
    @SerialName("sectionId") val sectionId: Int
)
