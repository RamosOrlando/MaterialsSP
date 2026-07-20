package com.materials.features.material.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.material.domain.model.Material
import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMaterialsUseCase(
    private val repository: MaterialRepository
) {
    suspend fun refresh(): Resource<Unit> = repository.refreshMaterials()
    
    fun executeFlow(query: String = "", sectionId: String? = null): Flow<Resource<List<Material>>> {
        return repository.getMaterialsOnlyFlow(query, sectionId)
    }

    fun executeFlowWithPrices(query: String = "", sectionId: String? = null): Flow<Resource<List<MaterialWithPrices>>> {
        return repository.getMaterialsFlow(query, sectionId)
    }
}
