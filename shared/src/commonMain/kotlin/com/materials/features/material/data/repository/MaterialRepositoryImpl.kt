package com.materials.features.material.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.material.data.local.MaterialDao
import com.materials.features.price_history.data.local.PriceHistoryDao
import com.materials.features.material.data.local.toDomain
import com.materials.features.material.data.local.toEntity
import com.materials.features.material.data.remote.MaterialRemoteDataSource
import com.materials.features.price_history.data.remote.PriceHistoryRemoteDataSource
import com.materials.features.maker.data.remote.MakerRemoteDataSource
import com.materials.features.provider.data.remote.ProviderRemoteDataSource
import com.materials.features.price_history.data.local.toDomain
import com.materials.features.price_history.data.local.toEntity
import com.materials.features.material.domain.model.MaterialWithPrices
import com.materials.features.material.domain.model.PriceWithProvider
import com.materials.features.material.domain.repository.MaterialRepository
import com.materials.features.provider.data.local.ProviderDao
import com.materials.features.provider.data.local.toDomain
import com.materials.features.maker.data.local.MakerDao
import com.materials.features.maker.data.local.toDomain
import com.materials.features.maker.data.local.toEntity
import com.materials.features.provider.data.local.toDomain
import com.materials.features.provider.data.local.toEntity
import com.materials.features.material.domain.model.Material
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class MaterialRepositoryImpl(
    private val materialDao: MaterialDao,
    private val providerDao: ProviderDao,
    private val priceHistoryDao: PriceHistoryDao,
    private val makerDao: MakerDao,
    private val remoteDataSource: MaterialRemoteDataSource,
    private val priceHistoryRemoteDataSource: PriceHistoryRemoteDataSource,
    private val makerRemoteDataSource: MakerRemoteDataSource,
    private val providerRemoteDataSource: ProviderRemoteDataSource
) : MaterialRepository {

    override suspend fun refreshMaterials(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            println("Starting refreshMaterials...")
            // Fetch everything in order to satisfy Foreign Key constraints
            val remoteMakers = makerRemoteDataSource.getMakers()
            println("Fetched ${remoteMakers.size} makers")
            makerDao.insertMakers(remoteMakers.map { it.toEntity() })

            val remoteProviders = providerRemoteDataSource.getProviders()
            println("Fetched ${remoteProviders.size} providers")
            providerDao.insertProviders(remoteProviders.map { it.toEntity() })
            
            val remoteMaterials = remoteDataSource.getMaterials()
            println("Fetched ${remoteMaterials.size} materials")
            materialDao.insertMaterials(remoteMaterials.map { it.toEntity() })
            
            val remotePrices = priceHistoryRemoteDataSource.getPriceHistories()
            println("Fetched ${remotePrices.size} prices")
            priceHistoryDao.insertPriceHistories(remotePrices.map { it.toEntity() })
            
            println("refreshMaterials finished successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            println("Error refreshing materials: ${e.message}")
            e.printStackTrace()
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getMaterialsFlow(query: String, sectionId: String?): Flow<Resource<List<MaterialWithPrices>>> {
        return combine(
            materialDao.getMaterialsFiltered(query, sectionId).onStart { emit(emptyList()) },
            providerDao.getProviders().onStart { emit(emptyList()) },
            priceHistoryDao.getAllPriceHistory().onStart { emit(emptyList()) },
            makerDao.getMakers().onStart { emit(emptyList()) }
        ) { materials, providers, prices, makers ->
            val pricesByMaterialId = prices.groupBy { it.materialId }
            val providersById = providers.associateBy { it.providerId }
            val makersById = makers.associateBy { it.makerId }

            materials.map { materialEntity ->
                val materialPrices = pricesByMaterialId[materialEntity.materialId]?.map { priceEntity ->
                    PriceWithProvider(
                        priceHistory = priceEntity.toDomain(),
                        provider = providersById[priceEntity.providerId]?.toDomain()
                    )
                } ?: emptyList()

                MaterialWithPrices(
                    material = materialEntity.toDomain(),
                    maker = makersById[materialEntity.makerId]?.toDomain(),
                    prices = materialPrices
                )
            }
        }
            .map { Resource.Success(it) as Resource<List<MaterialWithPrices>> }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
            .flowOn(Dispatchers.Default)
    }

    override fun getMaterialsOnlyFlow(query: String, sectionId: String?): Flow<Resource<List<Material>>> {
        return materialDao.getMaterialsFiltered(query, sectionId)
            .map { entities ->
                val materials = entities.map { it.toDomain() }
                Resource.Success(materials) as Resource<List<Material>>
            }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
            .flowOn(Dispatchers.IO)
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = combine(
        remoteDataSource.observeMaterials(),
        priceHistoryRemoteDataSource.observePriceHistories()
    ) { _, _ -> }.map {
        refreshMaterials()
        Unit
    }
}
