package com.materials.features.auth.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.materials.features.auth.domain.model.UserProfile
import com.materials.features.auth.domain.model.UserRole

@Entity(tableName = "Profile")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val fullName: String?,
    val email: String,
    val role: String // Store as String for Room simplicity
)

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
