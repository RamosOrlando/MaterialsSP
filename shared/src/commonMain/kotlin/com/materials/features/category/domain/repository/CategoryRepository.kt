package com.materials.features.category.domain.repository

import com.materials.core.domain.util.Resource
import com.materials.features.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun refreshCategories(): Resource<Unit>
    fun getCategoriesFlow(): Flow<Resource<List<Category>>>
}
