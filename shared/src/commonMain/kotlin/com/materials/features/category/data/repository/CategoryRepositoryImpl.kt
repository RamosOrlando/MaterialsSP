package com.materials.features.category.data.repository

import com.materials.core.domain.util.Resource
import com.materials.core.database.category.CategoryDao
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
            println("DEBUG: Starting refreshCategories from Supabase...")
            val remoteCategories = remoteDataSource.getCategories()
            println("DEBUG: Categories fetched from Supabase: ${remoteCategories.size}")
            
            remoteCategories.forEach { cat ->
                println("DEBUG: Category: id=${cat.categoryId}, name=${cat.name}, desc=${cat.description}")
            }

            categoryDao.insertCategories(remoteCategories.map { it.toEntity() })
            println("DEBUG: Categories successfully inserted into Room")
            Resource.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG: ERROR in refreshCategories: ${e.message}")
            e.printStackTrace()
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getCategoriesFlow(): Flow<Resource<List<Category>>> {
        return categoryDao.getCategories()
            .map { entities ->
                val domainCategories = entities.map { it.toDomain() }
                Resource.Success(domainCategories) as Resource<List<Category>>
            }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = flow {
        remoteDataSource.observeCategories().collect {
            refreshCategories()
            emit(Unit)
        }
    }
}
