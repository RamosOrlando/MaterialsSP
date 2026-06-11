package com.materials.features.section.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.section.domain.model.Section
import kotlinx.coroutines.flow.Flow

interface SectionRepository {
    suspend fun refreshSections(): Resource<Unit>
    fun getSectionsFlow(): Flow<Resource<List<Section>>>
    fun listenToRealtimeChanges(): Flow<Unit>
}
