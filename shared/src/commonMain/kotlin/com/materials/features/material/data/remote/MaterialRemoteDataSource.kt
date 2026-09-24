package com.materials.features.material.data.remote

import com.materials.core.common.domain.model.Material
import kotlinx.coroutines.flow.Flow

interface MaterialRemoteDataSource {
    suspend fun getMaterials(): List<Material>
    suspend fun updateMaterial(material: Material)
    suspend fun deleteMaterial(materialId: String)
    fun observeMaterials(): Flow<Unit>
}
