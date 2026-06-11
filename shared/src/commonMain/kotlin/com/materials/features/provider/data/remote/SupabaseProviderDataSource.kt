package com.materials.features.provider.data.remote

import com.materials.features.provider.domain.model.Provider
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

class SupabaseProviderDataSource(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient
) : ProviderRemoteDataSource {

    override suspend fun getProviders(): List<Provider> = withContext(Dispatchers.IO) {
        supabaseClient.postgrest["Provider"]
            .select()
            .decodeList<Provider>()
    }

    override fun observeProviders(): Flow<Unit> = callbackFlow {
        val channel = supabaseClient.realtime.channel("provider_changes") {}
        val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "Provider"
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
