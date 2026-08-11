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
        return repository.getMaterialsFlow(query, null).map { resource ->
            if (resource is Resource.Success<List<MaterialWithPrices>>) {
                // Ensure we only show materials that actually have prices
                val filtered = resource.data.filter { it.prices.isNotEmpty() }

                // Sort by latest quote date (descending), then by material name (ascending)
                val sorted = filtered.sortedWith(
                    compareByDescending<MaterialWithPrices> { 
                        it.prices.maxByOrNull { p -> p.priceHistory.quoteDate }?.priceHistory?.quoteDate ?: ""
                    }.thenBy { it.material.name }
                )

                Resource.Success(sorted)
            } else {
                resource
            }
        }
    }
}
