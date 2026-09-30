package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.util.formatearDinero

private enum class PestanaOfertas(val titulo: String) {
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas")
}

@Composable
fun OfertasNegocioScreen(viewModel: OfertasNegocioViewModel = hiltViewModel()) {
    val todasLasOfertas by viewModel.ofertas.collectAsStateWithLifecycle()
    val ofertasNegocio = remember(todasLasOfertas) {
        todasLasOfertas.filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
    }
    var pestanaActual by remember { mutableStateOf(PestanaOfertas.ACTIVAS) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra superior naranja con nombre del negocio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Naranja)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = UsuarioDemo.NEGOCIO_DEMO,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "Mis ofertas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Control segmentado Activas / Finalizadas
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PestanaOfertas.entries.forEach { pestana ->
                        val seleccionada = pestana == pestanaActual
                        val bgColor = if (seleccionada) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            Color.Transparent
                        }
                        val textColor = if (seleccionada) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Surface(
                            color = bgColor,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            onClick = { pestanaActual = pestana }
                        ) {
                            Text(
                                text = pestana.titulo,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Por ahora todas las del negocio se muestran en "Activas"
            // y "Finalizadas" queda vacía para futura implementación
            val ofertasAMostrar = if (pestanaActual == PestanaOfertas.ACTIVAS) {
                ofertasNegocio
            } else {
                emptyList()
            }

            if (ofertasAMostrar.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay ofertas finalizadas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(ofertasAMostrar) { oferta ->
                    TarjetaOfertaNegocio(oferta)
                }
            }
        }
    }
}

@Composable
private fun TarjetaOfertaNegocio(oferta: Oferta) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(100.dp)) {
            ImagenCategoria(
                categoria = oferta.categoria,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxSize(),
                tamanoIcono = 36.dp
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Nombre + chip estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = oferta.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ChipEstadoOferta(texto = "Activa", color = Color(0xFF4CAF50))
                }

                // Vendidos / Quedan
                val vendidos = (oferta.cantidadDisponible * 2) // dato simulado
                Text(
                    text = "Vendidos: $vendidos · Quedan: ${oferta.cantidadDisponible}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Precio + botón Editar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = oferta.precioRescate.formatearDinero(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Naranja
                    )
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        ),
                        onClick = {}
                    ) {
                        Text(
                            text = "Editar",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipEstadoOferta(texto: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
