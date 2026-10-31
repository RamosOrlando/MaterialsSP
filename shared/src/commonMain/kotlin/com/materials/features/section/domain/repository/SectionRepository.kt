package com.materials.features.section.domain.repository

import com.materials.core.domain.util.ResultState
import com.materials.features.section.domain.model.Section
import kotlinx.coroutines.flow.Flow

interface SectionRepository {
    suspend fun refreshSections(): ResultState<Unit>
    fun getSectionsFlow(): Flow<ResultState<List<Section>>>
    fun listenToRealtimeChanges(): Flow<Unit>
}
