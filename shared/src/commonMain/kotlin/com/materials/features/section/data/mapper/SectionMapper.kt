package com.materials.features.section.data.mapper

import com.materials.core.database.section.SectionEntity
import com.materials.features.section.domain.model.Section

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
