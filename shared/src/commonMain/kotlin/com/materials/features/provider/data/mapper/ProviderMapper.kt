package com.materials.features.provider.data.mapper

import com.materials.core.database.provider.ProviderEntity
import com.materials.features.provider.domain.model.Provider

fun ProviderEntity.toDomain() = Provider(
    providerId = providerId,
    name = name,
    address = address,
    telephone = telephone,
    city = city,
    email = email,
    imagePath = imagePath
)

fun Provider.toEntity() = ProviderEntity(
    providerId = providerId,
    name = name,
    address = address,
    telephone = telephone,
    city = city,
    email = email,
    imagePath = imagePath
)
