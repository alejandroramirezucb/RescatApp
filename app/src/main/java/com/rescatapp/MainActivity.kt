package com.rescatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rescatapp.core.data.repository.RepositorioOfertas
import com.rescatapp.core.data.repository.RepositorioPedidos
import com.rescatapp.core.designsystem.TemaRescat
import com.rescatapp.navigation.RescatNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var ofertas: RepositorioOfertas

    @Inject lateinit var pedidos: RepositorioPedidos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TemaRescat {
                RescatNavHost(ofertas, pedidos)
            }
        }
    }
}
