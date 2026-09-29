package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.FondoRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.VerdeRescat
import com.rescatapp.core.designsystem.visual
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearDisponibilidad
import com.rescatapp.features.inicio.domain.InicioUiState

@Composable
fun InicioScreen(
    onExplorar: (Categoria?) -> Unit,
    onPublicar: () -> Unit = {},
    onVerDetalle: (Int) -> Unit = {},
    viewModel: InicioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    InicioContent(
        estado = estado,
        onExplorar = onExplorar,
        onPublicar = onPublicar,
        onVerDetalle = onVerDetalle
    )
}

@Composable
fun InicioContent(
    estado: InicioUiState,
    onExplorar: (Categoria?) -> Unit,
    onPublicar: () -> Unit = {},
    onVerDetalle: (Int) -> Unit = {}
) {
    Scaffold(
        containerColor = FondoRescat,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onPublicar,
                containerColor = VerdeRescat,
                contentColor = Color.White
            ) {
                Text("Publicar oferta")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Inicio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Explora por categoría",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { onExplorar(null) },
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaRescat),
                modifier = Modifier.fillMaxWidth()
            ) {
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

            Text(
                "Ofertas cerca de ti",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )

            if (estado.ofertasCerca.isEmpty()) {
                Text(
                    "No hay ofertas disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                estado.ofertasCerca.forEach { oferta ->
                    TarjetaOfertaInicio(oferta = oferta, onClick = { onVerDetalle(oferta.id) })
                }
            }
        }
    }
}

@Composable
private fun TarjetaOfertaInicio(oferta: Oferta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                oferta.categoria.visual().simbolo,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    oferta.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    oferta.comercio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    oferta.precioRescate.formatearDinero(),
                    style = MaterialTheme.typography.titleSmall,
                    color = NaranjaRescat,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    oferta.cantidadDisponible.formatearDisponibilidad(),
                    style = MaterialTheme.typography.labelSmall,
                    color = VerdeRescat,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
