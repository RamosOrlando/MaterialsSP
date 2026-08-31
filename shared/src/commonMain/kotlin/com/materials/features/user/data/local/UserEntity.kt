package com.materials.features.user.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.materials.features.user.domain.model.User

@Entity(
    tableName = "User",
    foreignKeys = [
        ForeignKey(
            entity = UserRoleEntity::class,
            parentColumns = ["roleId"],
            childColumns = ["roleId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserProfessionEntity::class,
            parentColumns = ["professionId"],
            childColumns = ["professionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("roleId"),
        Index("professionId")
    ]
)
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val lastName: String,
    val email: String,
    val roleId: Int,
    val professionId: Int,
    val createdAt: String,
    val cellphone: Int?
)

fun UserEntity.toDomain() = User(
    userId = userId,
    name = name,
    lastName = lastName,
    email = email,
    roleId = roleId,
    professionId = professionId,
    createdAt = createdAt,
    cellphone = cellphone
)

fun User.toEntity() = UserEntity(
    userId = userId,
    name = name,
    lastName = lastName,
    email = email,
    roleId = roleId,
    professionId = professionId,
    createdAt = createdAt,
    cellphone = cellphone
)
