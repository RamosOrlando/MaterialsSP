package com.materials.features.resource.data.mapper

import com.materials.core.database.resource.ResourceEntity
import com.materials.core.common.domain.model.Resource

fun ResourceEntity.toDomain() = Resource(
    resourceId = resourceId,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId,
    specId = specId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate
)

fun Resource.toEntity() = ResourceEntity(
    resourceId = resourceId,
    name = name,
    unit = unit,
    makerId = makerId,
    sectionId = sectionId,
    specId = specId,
    providerId = providerId,
    price = price,
    quoteDate = quoteDate
)
