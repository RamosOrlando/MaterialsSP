package com.materials.features.category.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.category.domain.model.Category
import com.materials.features.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategoriesUseCase(
    private val repository: CategoryRepository
) {
    suspend fun refresh(): Resource<Unit> = repository.refreshCategories()
    
    fun executeFlow(query: String = ""): Flow<Resource<List<Category>>> {
        return repository.getCategoriesFlow().map { resource ->
            if (resource is Resource.Success<List<Category>> && query.isNotBlank()) {
                val filtered = resource.data.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
                Resource.Success(filtered)
            } else {
                resource
            }
        }
    }
}
