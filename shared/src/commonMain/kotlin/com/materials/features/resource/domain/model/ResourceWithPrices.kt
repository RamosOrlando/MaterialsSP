package com.materials.features.resource.domain.model

import com.materials.features.price_history.domain.model.PriceHistory
import com.materials.features.provider.domain.model.Provider
import com.materials.features.maker.domain.model.Maker
import com.materials.core.common.domain.model.Resource
import kotlinx.serialization.Serializable

@Serializable
data class ResourceWithPrices(
    val resource: Resource,
    val maker: Maker,
    val prices: List<PriceWithProvider>
)

@Serializable
data class PriceWithProvider(
    val priceHistory: PriceHistory,
    val provider: Provider?
)
