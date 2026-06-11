package com.materials.features.section.data.remote

import com.materials.features.section.domain.model.Section
import kotlinx.coroutines.flow.Flow

interface SectionRemoteDataSource {
    suspend fun getSections(): List<Section>
    fun observeSections(): Flow<Unit>
}
