package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Categoria

@Composable
fun InicioScreen(onExplorar: (Categoria?) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Inicio", style = MaterialTheme.typography.headlineMedium)
        Text("Explora por categoría", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { onExplorar(null) }, modifier = Modifier.fillMaxWidth()) {
            Text("Todas las ofertas")
        }
        Categoria.entries.forEach { categoria ->
            OutlinedButton(
                onClick = { onExplorar(categoria) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(categoria.etiqueta)
            }
        }
    }
}
