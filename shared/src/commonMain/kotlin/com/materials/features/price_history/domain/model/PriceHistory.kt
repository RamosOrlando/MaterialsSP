package com.materials.features.price_history.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceHistory(
    @SerialName("historyId") val historyId: Long,
    @SerialName("materialId") val materialId: String,
    @SerialName("providerId") val providerId: Int,
    @SerialName("price") val price: Double,
    @SerialName("quoteDate") val quoteDate: String
)
