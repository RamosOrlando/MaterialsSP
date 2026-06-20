package com.materials.features.provider.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.provider.data.local.ProviderDao
import com.materials.features.provider.data.local.toDomain
import com.materials.features.provider.data.local.toEntity
import com.materials.features.provider.data.remote.ProviderRemoteDataSource
import com.materials.features.provider.domain.model.Provider
import com.materials.features.provider.domain.repository.ProviderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ProviderRepositoryImpl(
    private val providerDao: ProviderDao,
    private val remoteDataSource: ProviderRemoteDataSource
) : ProviderRepository {

    override suspend fun refreshProviders(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            println("Starting refreshProviders...")
            val remoteProviders = remoteDataSource.getProviders()
            println("Fetched ${remoteProviders.size} providers")
            providerDao.insertProviders(remoteProviders.map { it.toEntity() })
            println("refreshProviders finished successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            println("Error refreshing providers: ${e.message}")
            e.printStackTrace()
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getProvidersFlow(): Flow<Resource<List<Provider>>> {
        return providerDao.getProviders()
            .map { entities ->
                val domainProviders = entities.map { it.toDomain() }
                Resource.Success(domainProviders) as Resource<List<Provider>>
            }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = flow {
        remoteDataSource.observeProviders().collect {
            refreshProviders()
            emit(Unit)
        }
    }
}
