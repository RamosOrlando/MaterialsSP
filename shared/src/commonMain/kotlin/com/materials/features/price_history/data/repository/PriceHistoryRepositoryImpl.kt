package com.materials.features.price_history.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.material.data.local.MaterialDao
import com.materials.features.price_history.data.local.PriceHistoryDao
import com.materials.features.price_history.data.local.toDomain
import com.materials.features.price_history.data.local.toEntity
import com.materials.features.price_history.data.remote.PriceHistoryRemoteDataSource
import com.materials.features.price_history.domain.model.PriceHistoryDetail
import com.materials.features.price_history.domain.repository.PriceHistoryRepository
import com.materials.features.provider.data.local.ProviderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class PriceHistoryRepositoryImpl(
    private val materialDao: MaterialDao,
    private val providerDao: ProviderDao,
    private val priceHistoryDao: PriceHistoryDao,
    private val remoteDataSource: PriceHistoryRemoteDataSource
) : PriceHistoryRepository {

    override suspend fun refreshPriceHistory(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val remotePrices = remoteDataSource.getPriceHistories()
            priceHistoryDao.insertPriceHistories(remotePrices.map { it.toEntity() })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getPriceHistoryDetailFlow(): Flow<Resource<List<PriceHistoryDetail>>> {
        return combine(
            materialDao.getMaterials(),
            providerDao.getProviders(),
            priceHistoryDao.getAllPriceHistory()
        ) { materials, providers, prices ->
            prices.map { priceEntity ->
                val material = materials.find { it.materialId == priceEntity.materialId }
                val provider = providers.find { it.providerId == priceEntity.providerId }
                PriceHistoryDetail(
                    historyId = priceEntity.historyId,
                    materialName = material?.name ?: "Desconocido",
                    materialCode = material?.code ?: "N/A",
                    providerName = provider?.name ?: "Desconocido",
                    price = priceEntity.price,
                    quoteDate = priceEntity.quoteDate,
                    unit = material?.unit
                )
            }.sortedByDescending { it.quoteDate }
        }
            .map { Resource.Success(it) as Resource<List<PriceHistoryDetail>> }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = remoteDataSource.observePriceHistories().map {
        refreshPriceHistory()
        Unit
    }
}
