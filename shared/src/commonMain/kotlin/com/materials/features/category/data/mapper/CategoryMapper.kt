package com.materials.features.category.data.mapper

import com.materials.features.category.data.local.CategoryEntity
import com.materials.features.category.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        categoryId = categoryId,
        name = name,
        description = description,
        imagePath = imagePath
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = categoryId ?: "",
        name = name,
        description = description,
        imagePath = imagePath
    )
}
