// data/SupabaseManager.kt
package com.autocall.app.data

import android.content.Context
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object SupabaseManager {

    private var client: io.github.jan.supabase.SupabaseClient? = null

    fun init(supabaseUrl: String, supabaseAnonKey: String) {
        client = createSupabaseClient(supabaseUrl, supabaseAnonKey) {
            install(Realtime)
        }
    }

    fun isInitialized() = client != null

    // Returns a flow of incoming phone numbers for this token
    suspend fun listenForCalls(token: String): Flow<String> {
        val c = client ?: throw IllegalStateException("Supabase not initialized")
        val channel = c.channel("calls:$token")

        val flow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "calls"
            filter = "token=eq.$token"
        }

        channel.subscribe()

        return flow.map { insert ->
            // Extract 'number' field from the new record
            insert.record["number"]?.jsonPrimitive?.content ?: ""
        }
    }

    suspend fun disconnect() {
        client?.realtime?.disconnect()
    }
}
