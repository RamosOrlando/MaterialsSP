package com.materials.features.maker.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.maker.domain.model.Maker
import com.materials.features.maker.domain.repository.MakerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMakersUseCase(
    private val repository: MakerRepository
) {
    suspend fun refresh(): Resource<Unit> = repository.refreshMakers()
    
    fun executeFlow(query: String = ""): Flow<Resource<List<Maker>>> {
        return repository.getMakersFlow().map { resource ->
            if (resource is Resource.Success<List<Maker>> && query.isNotBlank()) {
                val filtered = resource.data.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                Resource.Success(filtered)
            } else {
                resource
            }
        }
    }
}
