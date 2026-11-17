package com.rescatapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.rescatapp.core.data.supabase.SupabaseConexion
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.navigation.RescatAppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var supabase: SupabaseConexion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        recibirEnlace(intent)
        setContent {
            RescatAppTheme {
                RescatAppNavHost()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        supabase.activarSincronizacion(true)
    }

    override fun onStop() {
        supabase.activarSincronizacion(false)
        super.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recibirEnlace(intent)
    }

    private fun recibirEnlace(intent: Intent?) {
        val uri = intent?.data ?: return
        // Consumirlo evita canjear por segunda vez un codigo tras una rotacion.
        intent.data = null
        lifecycleScope.launch {
            supabase.inicializar()
            supabase.recibirCallback(uri)
        }
    }
}
