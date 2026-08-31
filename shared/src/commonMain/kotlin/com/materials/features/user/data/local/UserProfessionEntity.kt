package com.materials.features.user.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.materials.features.user.domain.model.UserProfession

@Entity(tableName = "UserProfession")
data class UserProfessionEntity(
    @PrimaryKey val professionId: Int,
    val name: String
)

fun UserProfessionEntity.toDomain() = UserProfession(
    professionId = professionId,
    name = name
)

fun UserProfession.toEntity() = UserProfessionEntity(
    professionId = professionId ?: 0,
    name = name
)
