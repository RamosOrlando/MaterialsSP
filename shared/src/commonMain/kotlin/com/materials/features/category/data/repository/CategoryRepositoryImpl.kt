package com.materials.features.category.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.category.data.local.CategoryDao
import com.materials.features.category.data.mapper.toDomain
import com.materials.features.category.data.mapper.toEntity
import com.materials.features.category.data.remote.CategoryRemoteDataSource
import com.materials.features.category.domain.model.Category
import com.materials.features.category.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val remoteDataSource: CategoryRemoteDataSource
) : CategoryRepository {

    override suspend fun refreshCategories(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteCategories = remoteDataSource.getCategories()
            categoryDao.insertCategories(remoteCategories.map { it.toEntity() })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getCategoriesFlow(): Flow<Resource<List<Category>>> {
        return categoryDao.getCategories()
            .map { entities ->
                val domainCategories = entities.map { it.toDomain() }
                Resource.Success(domainCategories) as Resource<List<Category>>
            }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = flow {
        remoteDataSource.observeCategories().collect {
            refreshCategories()
            emit(Unit)
        }
    }
}
