package com.materials.features.price_history.data.remote

import com.materials.features.price_history.domain.model.PriceHistory
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

class SupabasePriceHistoryDataSource(
    private val supabaseClient: io.github.jan.supabase.SupabaseClient
) : PriceHistoryRemoteDataSource {

    override suspend fun getPriceHistories(): List<PriceHistory> = withContext(Dispatchers.IO) {
        supabaseClient.postgrest["PriceHistory"]
            .select()
            .decodeList<PriceHistory>()
    }

    override fun observePriceHistories(): Flow<Unit> = callbackFlow {
        val channel = supabaseClient.realtime.channel("price_history_changes") {}
        val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "PriceHistory"
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
