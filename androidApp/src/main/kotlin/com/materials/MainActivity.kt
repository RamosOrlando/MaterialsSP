package com.materials

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.materials.core.network.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        SupabaseClient.client.handleDeeplinks(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        SupabaseClient.client.handleDeeplinks(intent)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}