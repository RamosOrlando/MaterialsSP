package com.materials.features.price_history.data.mapper

import com.materials.core.database.price_history.PriceHistoryEntity
import com.materials.features.price_history.domain.model.PriceHistory

fun PriceHistoryEntity.toDomain() = PriceHistory(
    historyId = historyId,
    materialId = materialId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate,
    username = username
)

fun PriceHistory.toEntity() = PriceHistoryEntity(
    historyId = historyId,
    materialId = materialId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate,
    username = username
)
