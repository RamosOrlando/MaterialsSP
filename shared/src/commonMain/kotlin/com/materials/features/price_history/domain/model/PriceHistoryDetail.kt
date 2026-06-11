package com.materials.features.price_history.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PriceHistoryDetail(
    val historyId: Long,
    val materialName: String,
    val materialCode: String,
    val providerName: String,
    val price: Double,
    val quoteDate: String,
    val unit: String?
)
