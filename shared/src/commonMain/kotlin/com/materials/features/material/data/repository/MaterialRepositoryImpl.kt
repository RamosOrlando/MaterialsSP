package com.materials.features.material.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.material.data.local.MaterialDao
import com.materials.features.price_history.data.local.PriceHistoryDao
import com.materials.features.material.data.local.toDomain
import com.materials.features.material.data.local.toEntity
import com.materials.features.material.data.remote.MaterialRemoteDataSource
import com.materials.features.price_history.data.remote.PriceHistoryRemoteDataSource
import com.materials.features.price_history.data.local.toDomain
import com.materials.features.price_history.data.local.toEntity
import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.model.PriceWithProvider
import com.materials.features.material.domain.repository.MaterialRepository
import com.materials.features.provider.data.local.ProviderDao
import com.materials.features.provider.data.local.toDomain
import com.materials.features.maker.data.local.MakerDao
import com.materials.features.maker.data.local.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class MaterialRepositoryImpl(
    private val materialDao: MaterialDao,
    private val providerDao: ProviderDao,
    private val priceHistoryDao: PriceHistoryDao,
    private val makerDao: MakerDao,
    private val remoteDataSource: MaterialRemoteDataSource,
    private val priceHistoryRemoteDataSource: PriceHistoryRemoteDataSource
) : MaterialRepository {

    override suspend fun refreshMaterials(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteMaterials = remoteDataSource.getMaterials()
            materialDao.insertMaterials(remoteMaterials.map { it.toEntity() })
            
            val remotePrices = priceHistoryRemoteDataSource.getPriceHistories()
            priceHistoryDao.insertPriceHistories(remotePrices.map { it.toEntity() })
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getMaterialsFlow(): Flow<Resource<List<MaterialWithPrices>>> {
        return combine(
            materialDao.getMaterials(),
            providerDao.getProviders(),
            priceHistoryDao.getAllPriceHistory(),
            makerDao.getMakers()
        ) { materials, providers, prices, makers ->
            materials.map { materialEntity ->
                val materialPrices = prices
                    .filter { it.materialId == materialEntity.materialId }
                    .map { priceEntity ->
                        PriceWithProvider(
                            priceHistory = priceEntity.toDomain(),
                            provider = providers.find { it.providerId == priceEntity.providerId }?.toDomain()
                        )
                    }
                MaterialWithPrices(
                    material = materialEntity.toDomain(),
                    maker = makers.find { it.makerId == materialEntity.makerId }?.toDomain(),
                    prices = materialPrices
                )
            }
        }
            .map { Resource.Success(it) as Resource<List<MaterialWithPrices>> }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = combine(
        remoteDataSource.observeMaterials(),
        priceHistoryRemoteDataSource.observePriceHistories()
    ) { _, _ -> }.map {
        refreshMaterials()
        Unit
    }
}
