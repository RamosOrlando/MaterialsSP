package com.materials.features.provider.domain.use_case

import com.materials.core.domain.util.Resource
import com.materials.features.provider.domain.model.Provider
import com.materials.features.provider.domain.repository.ProviderRepository

class SaveProviderUseCase(
    private val repository: ProviderRepository
) {
    suspend fun execute(provider: Provider): Resource<Unit> {
        return repository.saveProvider(provider)
    }
}
