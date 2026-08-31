package com.materials.features.provider.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.provider.domain.model.Provider
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {
    suspend fun refreshProviders(): Resource<Unit>
    fun getProvidersFlow(): Flow<Resource<List<Provider>>>
    fun listenToRealtimeChanges(): Flow<Unit>
    suspend fun saveProvider(provider: Provider): Resource<Unit>
}
