package com.materials.features.section.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.section.domain.model.Section
import com.materials.features.section.domain.repository.SectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSectionsUseCase(
    private val repository: SectionRepository
) {
    suspend fun refresh(): Resource<Unit> = repository.refreshSections()
    
    fun executeFlow(query: String = "", categoryId: Int? = null): Flow<Resource<List<Section>>> {
        return repository.getSectionsFlow().map { resource ->
            if (resource is Resource.Success<List<Section>>) {
                var filtered = resource.data
                
                if (categoryId != null) {
                    filtered = filtered.filter { it.categoryId == categoryId }
                }
                
                if (query.isNotBlank()) {
                    filtered = filtered.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.code.contains(query, ignoreCase = true)
                    }
                }
                Resource.Success(filtered)
            } else {
                resource
            }
        }
    }
}
