package com.materials.features.resource.domain.use_case

import com.materials.core.domain.util.ResultState
import com.materials.core.common.domain.model.Resource
import com.materials.features.resource.domain.model.ResourceWithPrices
import com.materials.features.resource.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetResourcesUseCase(
    private val repository: ResourceRepository
) {
    suspend fun refresh(): ResultState<Unit> = repository.refreshResources()
    
    fun executeFlow(query: String = "", sectionId: String? = null): Flow<ResultState<List<Resource>>> {
        return repository.getResourcesOnlyFlow(query, sectionId)
    }

    fun executeFlowWithPrices(query: String = "", sectionId: String? = null): Flow<ResultState<List<ResourceWithPrices>>> {
        return repository.getResourcesFlow(query, sectionId)
    }

    suspend fun updateResource(resource: Resource): ResultState<Unit> {
        return repository.updateResource(resource)
    }

    suspend fun deleteResource(resourceId: String): ResultState<Unit> {
        return repository.deleteResource(resourceId)
    }

    suspend fun getNextIndex(sectionId: String): Int {
        return repository.getResourceCount(sectionId) + 1
    }

    fun getNextIndexFlow(sectionId: String): Flow<Int> {
        return repository.getResourceCountFlow(sectionId).map { it + 1 }
    }
}
