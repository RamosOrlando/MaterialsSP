package com.materials.features.category.data.remote

import com.materials.features.category.domain.model.Category
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SupabaseCategoryDataSource(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient
) : CategoryRemoteDataSource {

    override suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        supabaseClient.postgrest["Category"]
            .select()
            .decodeList<Category>()
    }
}
