package com.materials.features.provider.data.remote

import com.materials.features.provider.domain.model.Provider
import kotlinx.coroutines.flow.Flow

interface ProviderRemoteDataSource {
    suspend fun getProviders(): List<Provider>
    fun observeProviders(): Flow<Unit>
}
