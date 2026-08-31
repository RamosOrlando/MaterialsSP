package com.materials.features.user.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.materials.features.user.domain.model.UserRole

@Entity(tableName = "UserRole")
data class UserRoleEntity(
    @PrimaryKey val roleId: Int,
    val name: String
)

fun UserRoleEntity.toDomain() = UserRole(
    roleId = roleId,
    name = name
)

fun UserRole.toEntity() = UserRoleEntity(
    roleId = roleId ?: 0,
    name = name
)
