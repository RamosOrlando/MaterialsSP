package com.materials.features.category.data.remote

import com.materials.features.category.domain.model.Category

interface CategoryRemoteDataSource {
    suspend fun getCategories(): List<Category>
}
