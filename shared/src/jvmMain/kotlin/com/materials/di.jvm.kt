package com.materials

import androidx.room.Room
import androidx.room.RoomDatabase
import com.materials.core.data.local.AppDatabase
import java.io.File
import org.koin.dsl.module

actual val platformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val dbFile = File(System.getProperty("user.home"), "materials.db")
        Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath
        )
    }
}
