package com.materials.features.provider.domain.repository

import com.materials.core.domain.util.ResultState
import com.materials.features.provider.domain.model.Provider
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {
    suspend fun refreshProviders(): ResultState<Unit>
    fun getProvidersFlow(): Flow<ResultState<List<Provider>>>
    fun listenToRealtimeChanges(): Flow<Unit>
    suspend fun saveProvider(provider: Provider): ResultState<Unit>
}
