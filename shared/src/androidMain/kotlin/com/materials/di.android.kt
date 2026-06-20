package com.materials

import androidx.room.Room
import androidx.room.RoomDatabase
import com.materials.core.data.local.AppDatabase
import com.materials.core.util.pdf.AndroidPdfGenerator
import com.materials.core.util.pdf.PdfGenerator
import com.materials.core.util.share.AndroidShareManager
import com.materials.core.util.share.ShareManager
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
