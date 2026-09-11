package com.materials.features.maker.data.mapper

import com.materials.core.database.maker.MakerEntity
import com.materials.features.maker.domain.model.Maker

fun MakerEntity.toDomain() = Maker(
    makerId = makerId,
    name = name,
    imagePath = imagePath
)

fun Maker.toEntity() = MakerEntity(
    makerId = makerId,
    name = name,
    imagePath = imagePath
)
