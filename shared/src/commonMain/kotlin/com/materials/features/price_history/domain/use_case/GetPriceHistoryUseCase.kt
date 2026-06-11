package com.materials.features.price_history.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.price_history.domain.model.PriceHistoryDetail
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.repository.MaterialRepository

class GetPriceHistoryUseCase(
    private val repository: MaterialRepository
) {
    fun executeFlow(query: String = ""): Flow<Resource<List<MaterialWithPrices>>> {
        return repository.getMaterialsFlow().map { resource ->
            if (resource is Resource.Success<List<MaterialWithPrices>>) {
                val filtered = if (query.isNotBlank()) {
                    resource.data.filter {
                        it.material.name.contains(query, ignoreCase = true) ||
                                it.material.code.contains(query, ignoreCase = true) ||
                                it.maker?.name?.contains(query, ignoreCase = true) == true ||
                                it.prices.any { p -> p.provider?.name?.contains(query, ignoreCase = true) == true }
                    }
                } else {
                    resource.data
                }

                // Sort by:
                // 1. Availability of prices (materials with prices first)
                // 2. Latest quote date (descending)
                // 3. Material name (ascending)
                val sorted = filtered.sortedWith(
                    compareByDescending<MaterialWithPrices> { it.prices.isNotEmpty() }
                        .thenByDescending { it.prices.maxByOrNull { p -> p.priceHistory.quoteDate }?.priceHistory?.quoteDate }
                        .thenBy { it.material.name }
                )

                Resource.Success(sorted)
            } else {
                resource
            }
        }
    }
}
