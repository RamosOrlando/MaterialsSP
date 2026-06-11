package com.materials.features.category.data.remote

import com.materials.features.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRemoteDataSource {
    suspend fun getCategories(): List<Category>
    fun observeCategories(): Flow<Unit>
}
