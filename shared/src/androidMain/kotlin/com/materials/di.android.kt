package com.materials

import androidx.room.Room
import androidx.room.RoomDatabase
import com.materials.core.database.AppDatabase
import com.materials.core.common.util.pdf.AndroidPdfGenerator
import com.materials.core.common.util.pdf.PdfGenerator
import com.materials.core.common.util.share.AndroidShareManager
import com.materials.core.common.util.share.ShareManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = androidContext()
        val dbFile = context.getDatabasePath("materials.db")
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
    }
    
    single<PdfGenerator> { AndroidPdfGenerator(androidContext()) }
    single<ShareManager> { AndroidShareManager(androidContext()) }
}
