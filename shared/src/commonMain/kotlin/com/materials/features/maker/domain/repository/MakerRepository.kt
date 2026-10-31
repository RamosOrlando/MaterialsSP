package com.materials.features.maker.domain.repository

import com.materials.core.domain.util.ResultState
import com.materials.features.maker.domain.model.Maker
import kotlinx.coroutines.flow.Flow

interface MakerRepository {
    suspend fun refreshMakers(): ResultState<Unit>
    fun getMakersFlow(): Flow<ResultState<List<Maker>>>
    fun listenToRealtimeChanges(): Flow<Unit>
    suspend fun saveMaker(maker: Maker): ResultState<Unit>
}
