package com.materials.features.material.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMaterialsUseCase(
    private val repository: MaterialRepository
) {
    suspend fun refresh(): Resource<Unit> = repository.refreshMaterials()
    
    fun executeFlow(query: String = "", sectionId: Int? = null): Flow<Resource<List<MaterialWithPrices>>> {
        return repository.getMaterialsFlow().map { resource ->
            if (resource is Resource.Success<List<MaterialWithPrices>>) {
                var filtered = resource.data
                
                if (sectionId != null) {
                    filtered = filtered.filter { it.material.sectionId == sectionId }
                }
                
                if (query.isNotBlank()) {
                    filtered = filtered.filter {
                        it.material.name.contains(query, ignoreCase = true) ||
                                it.material.code.contains(query, ignoreCase = true)
                    }
                }
                Resource.Success(filtered)
            } else {
                resource
            }
        }
    }
}
