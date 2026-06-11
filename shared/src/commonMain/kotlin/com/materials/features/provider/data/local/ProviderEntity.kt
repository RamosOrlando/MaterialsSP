package com.materials.features.provider.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.provider.domain.model.Provider

@Entity(
    tableName = "providers",
    indices = [
        Index(value = ["name", "address"], unique = true)
    ]
)
data class ProviderEntity(
    @PrimaryKey val providerId: Int,
    val name: String,
    val address: String?,
    val telephone: Long?,
    val city: String?,
    val email: String?,
    val imagePath: String?
)

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
