package com.materials.features.material.domain.model

import com.materials.features.price_history.domain.model.PriceHistory
import com.materials.features.provider.domain.model.Provider
import com.materials.features.maker.domain.model.Maker
import kotlinx.serialization.Serializable

@Serializable
data class MaterialWithPrices(
    val material: Material,
    val maker: Maker? = null,
    val prices: List<PriceWithProvider>
)

@Serializable
data class PriceWithProvider(
    val priceHistory: PriceHistory,
    val provider: Provider?
)
