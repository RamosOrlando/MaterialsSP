package com.materials.features.category.domain.repository

import com.materials.core.domain.util.ResultState
import com.materials.features.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun refreshCategories(): ResultState<Unit>
    fun getCategoriesFlow(): Flow<ResultState<List<Category>>>
    fun listenToRealtimeChanges(): Flow<Unit>
}
