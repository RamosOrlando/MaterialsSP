package com.materials.features.material.data.remote

import com.materials.features.material.domain.model.Material
import kotlinx.coroutines.flow.Flow

interface MaterialRemoteDataSource {
    suspend fun getMaterials(): List<Material>
    fun observeMaterials(): Flow<Unit>
}
