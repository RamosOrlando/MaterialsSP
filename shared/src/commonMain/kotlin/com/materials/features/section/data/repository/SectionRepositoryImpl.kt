package com.materials.features.section.data.repository

import com.materials.core.domain.util.Resource
import com.materials.features.section.data.local.SectionDao
import com.materials.features.section.data.local.toDomain
import com.materials.features.section.data.local.toEntity
import com.materials.features.section.data.remote.SectionRemoteDataSource
import com.materials.features.section.domain.model.Section
import com.materials.features.section.domain.repository.SectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class SectionRepositoryImpl(
    private val sectionDao: SectionDao,
    private val remoteDataSource: SectionRemoteDataSource
) : SectionRepository {

    override suspend fun refreshSections(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteSections = remoteDataSource.getSections()
            sectionDao.insertSections(remoteSections.map { it.toEntity() })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override fun getSectionsFlow(): Flow<Resource<List<Section>>> {
        return sectionDao.getSections()
            .map { entities ->
                val domainSections = entities.map { it.toDomain() }
                Resource.Success(domainSections) as Resource<List<Section>>
            }
            .onStart { emit(Resource.Loading) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override fun listenToRealtimeChanges(): Flow<Unit> = flow {
        remoteDataSource.observeSections().collect {
            refreshSections()
            emit(Unit)
        }
    }
}
