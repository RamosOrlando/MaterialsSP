package com.materials.core.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    // Pre-populated with your project ID from the provided URL
    const val SUPABASE_URL = "https://ztgjdqjfrffjlvmbknlw.supabase.co"
    
    // IMPORTANT: Replace this with your actual Supabase Anon / Public Key
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp0Z2pkcWpmcmZmamx2bWJrbmx3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc1Njg3MDksImV4cCI6MjA5MzE0NDcwOX0.BRYXBBfEchNXyQT1oMUrZQDGyG_uONqu2XeWWB5mjN0"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Realtime)
        install(Auth)
    }
}
