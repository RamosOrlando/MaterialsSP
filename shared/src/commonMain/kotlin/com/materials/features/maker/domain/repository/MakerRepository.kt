package com.materials.features.maker.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.maker.domain.model.Maker
import kotlinx.coroutines.flow.Flow

interface MakerRepository {
    suspend fun refreshMakers(): Resource<Unit>
    fun getMakersFlow(): Flow<Resource<List<Maker>>>
    fun listenToRealtimeChanges(): Flow<Unit>
    suspend fun saveMaker(maker: Maker): Resource<Unit>
}
