package com.materials.features.material.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.material.domain.model.Material
import com.materials.features.material.domain.model.MaterialWithPrices
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {
    suspend fun refreshMaterials(): Resource<Unit>
    fun getMaterialsFlow(query: String = "", sectionId: String? = null): Flow<Resource<List<MaterialWithPrices>>>
    fun getMaterialsOnlyFlow(query: String = "", sectionId: String? = null): Flow<Resource<List<Material>>>
    fun listenToRealtimeChanges(): Flow<Unit>
}
