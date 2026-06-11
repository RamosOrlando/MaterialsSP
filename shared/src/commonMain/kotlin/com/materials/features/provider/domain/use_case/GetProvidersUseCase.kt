package com.materials.features.provider.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.provider.domain.model.Provider
import com.materials.features.provider.domain.repository.ProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetProvidersUseCase(
    private val repository: ProviderRepository
) {
    suspend fun refresh(): Resource<Unit> = repository.refreshProviders()
    
    fun executeFlow(query: String = ""): Flow<Resource<List<Provider>>> {
        return repository.getProvidersFlow().map { resource ->
            if (resource is Resource.Success<List<Provider>> && query.isNotBlank()) {
                val filtered = resource.data.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            (it.city?.contains(query, ignoreCase = true) == true)
                }
                Resource.Success(filtered)
            } else {
                resource
            }
        }
    }
}
