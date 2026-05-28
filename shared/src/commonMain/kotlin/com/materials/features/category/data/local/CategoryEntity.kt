package com.materials.features.category.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val categoryId: Int,
    val name: String,
    val description: String,
    val imagePath: String
)
