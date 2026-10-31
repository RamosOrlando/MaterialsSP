package com.materials.features.resource.domain.repository

import com.materials.core.domain.util.ResultState
import com.materials.core.common.domain.model.Resource
import com.materials.features.resource.domain.model.ResourceWithPrices
import kotlinx.coroutines.flow.Flow

interface ResourceRepository {
    suspend fun refreshResources(): ResultState<Unit>
    fun getResourcesFlow(query: String = "", sectionId: String? = null): Flow<ResultState<List<ResourceWithPrices>>>
    fun getResourcesOnlyFlow(query: String = "", sectionId: String? = null): Flow<ResultState<List<Resource>>>
    suspend fun updateResource(resource: Resource): ResultState<Unit>
    suspend fun deleteResource(resourceId: String): ResultState<Unit>
    suspend fun getResourceCount(sectionId: String): Int
    fun getResourceCountFlow(sectionId: String): Flow<Int>
    fun listenToRealtimeChanges(): Flow<Unit>
}
