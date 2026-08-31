package com.materials.features.maker.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.maker.data.local.MakerDao
import com.materials.features.maker.data.local.toDomain
import com.materials.features.maker.data.local.toEntity
import com.materials.features.maker.data.remote.MakerRemoteDataSource
import com.materials.features.maker.domain.model.Maker
import com.materials.features.maker.domain.repository.MakerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class MakerRepositoryImpl(
    private val makerDao: MakerDao,
    private val remoteDataSource: MakerRemoteDataSource
) : MakerRepository {

    override suspend fun refreshMakers(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            println("Starting refreshMakers...")
            val remoteMakers = remoteDataSource.getMakers()
            println("Fetched ${remoteMakers.size} makers")
            makerDao.insertMakers(remoteMakers.map { it.toEntity() })
            println("refreshMakers finished successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            println("Error refreshing makers: ${e.message}")
            e.printStackTrace()
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getMakersFlow(): Flow<Resource<List<Maker>>> {
        return makerDao.getMakers()
            .map { entities ->
                val domainMakers = entities.map { it.toDomain() }
                Resource.Success(domainMakers) as Resource<List<Maker>>
            }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = flow {
        remoteDataSource.observeMakers().collect {
            refreshMakers()
            emit(Unit)
        }
    }

    override suspend fun saveMaker(maker: Maker): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            remoteDataSource.saveMaker(maker)
            makerDao.insertMakers(listOf(maker.toEntity()))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}
