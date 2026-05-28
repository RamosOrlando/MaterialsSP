package com.materials.core.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.materials.features.category.data.local.CategoryDao
import com.materials.features.category.data.local.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}

// Expect object for the database builder that will be implemented in each platform
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : androidx.room.RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
