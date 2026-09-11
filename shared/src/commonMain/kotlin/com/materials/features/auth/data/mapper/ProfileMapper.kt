package com.materials.features.auth.data.mapper

import com.materials.core.database.auth.ProfileEntity
import com.materials.features.auth.domain.model.UserProfile
import com.materials.features.auth.domain.model.UserRole

fun ProfileEntity.toDomain() = UserProfile(
    id = id,
    fullName = fullName,
    email = email,
    role = UserRole.valueOf(role)
)

fun UserProfile.toEntity() = ProfileEntity(
    id = id,
    fullName = fullName,
    email = email,
    role = role.name
)
