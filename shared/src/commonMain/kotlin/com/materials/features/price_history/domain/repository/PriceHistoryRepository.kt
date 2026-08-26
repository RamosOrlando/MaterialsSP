package com.materials.features.price_history.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.price_history.domain.model.PriceHistory
import com.materials.features.price_history.domain.model.PriceHistoryDetail
import kotlinx.coroutines.flow.Flow

interface PriceHistoryRepository {
    suspend fun refreshPriceHistory(): Resource<Unit>
    suspend fun upsertPriceHistory(priceHistory: PriceHistory): Resource<Unit>
    fun getPriceHistoryDetailFlow(): Flow<Resource<List<PriceHistoryDetail>>>
    fun listenToRealtimeChanges(): Flow<Unit>
}
