package com.materials.features.category.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class CategoryEntity(
    @PrimaryKey val categoryId: String,
    val name: String,
    val description: String,
    val imagePath: String
)
