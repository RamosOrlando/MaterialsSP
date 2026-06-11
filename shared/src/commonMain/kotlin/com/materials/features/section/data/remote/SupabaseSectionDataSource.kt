package com.materials.features.section.data.remote

import com.materials.features.section.domain.model.Section
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupabaseSectionDataSource(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient
) : SectionRemoteDataSource {

    override suspend fun getSections(): List<Section> = withContext(Dispatchers.IO) {
        supabaseClient.postgrest["Section"]
            .select()
            .decodeList<Section>()
    }

    override fun observeSections(): Flow<Unit> = callbackFlow {
        val channel = supabaseClient.realtime.channel("section_changes") {}
        val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "Section"
        }
        
        val job = flow.onEach {
            trySend(Unit)
        }.launchIn(this)

        channel.subscribe()
        
        awaitClose {
            launch {
                channel.unsubscribe()
            }
            job.cancel()
        }
    }
}
