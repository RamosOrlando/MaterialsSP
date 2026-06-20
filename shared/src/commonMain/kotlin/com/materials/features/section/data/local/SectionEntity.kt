package com.materials.features.section.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.category.data.local.CategoryEntity
import com.materials.features.section.domain.model.Section

@Entity(
    tableName = "sections",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class SectionEntity(
    @PrimaryKey val sectionId: String,
    val name: String,
    val categoryId: String?,
    val imagePath: String?
)

fun SectionEntity.toDomain() = Section(
    sectionId = sectionId,
    name = name,
    categoryId = categoryId,
    imagePath = imagePath
)

fun Section.toEntity() = SectionEntity(
    sectionId = sectionId,
    name = name,
    categoryId = categoryId,
    imagePath = imagePath
)
