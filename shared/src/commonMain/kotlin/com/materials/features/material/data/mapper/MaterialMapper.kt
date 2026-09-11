package com.materials.features.material.data.mapper

import com.materials.core.database.material.MaterialEntity
import com.materials.core.common.domain.model.Material

fun MaterialEntity.toDomain() = Material(
    materialId = materialId,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId,
    specId = specId,
    historyId = historyId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate
)

fun Material.toEntity() = MaterialEntity(
    materialId = materialId,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId,
    specId = specId,
    historyId = historyId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate
)
