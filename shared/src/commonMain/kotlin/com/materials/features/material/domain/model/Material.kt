package com.materials.features.material.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Material(
    @SerialName("materialId") val materialId: String,
    @SerialName("name") val name: String,
    @SerialName("unit") val unit: String,
    @SerialName("makerId") val makerId: String,
    @SerialName("sectionId") val sectionId: String,
    @SerialName("specId") val specId: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("quoteDate") val quoteDate: String? = null
)
