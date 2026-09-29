package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rescatapp.core.designsystem.FondoRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.VerdeRescat
import com.rescatapp.core.designsystem.visual
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearDisponibilidad
import com.rescatapp.features.explorar.domain.ExplorarUiState

@Composable
fun ExplorarScreen(
    estado: ExplorarUiState,
    onBuscar: (String) -> Unit,
    onCategoria: (Categoria?) -> Unit,
    onOrden: (OrdenOfertas) -> Unit,
    onVerDetalle: (Int) -> Unit,
    onInicio: () -> Unit,
    onPublicar: () -> Unit
) {
    var mostrarFiltros by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(FondoRescat)) {
        Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Explorar",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onInicio) { Text("Inicio") }
                TextButton(onClick = onPublicar) { Text("Publicar") }
            }
            OutlinedTextField(
                value = estado.texto,
                onValueChange = onBuscar,
                label = { Text("Buscar ofertas") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    FilterChip(
                        selected = estado.categoria != null,
                        onClick = { mostrarFiltros = true },
                        label = { Text(estado.categoria?.etiqueta ?: "Filtros") }
                    )
                    DropdownMenu(
                        expanded = mostrarFiltros,
                        onDismissRequest = { mostrarFiltros = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = {
                                onCategoria(null)
                                mostrarFiltros = false
                            }
                        )
                        Categoria.entries.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.etiqueta) },
                                onClick = {
                                    onCategoria(categoria)
                                    mostrarFiltros = false
                                }
                            )
                        }
                    }
                }
                OrdenOfertas.entries.forEach { orden ->
                    FilterChip(
                        selected = estado.orden == orden,
                        onClick = { onOrden(orden) },
                        label = { Text(orden.etiqueta) }
                    )
                }
            }
        }

        Text(
            text = "${estado.cantidad} ofertas encontradas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 12.dp)
        )

        if (estado.ofertas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No encontramos ofertas", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.ofertas, key = { it.id }) { oferta ->
                    TarjetaOferta(oferta = oferta, onClick = { onVerDetalle(oferta.id) })
                }
            }
        }
    }
}

@Composable
private fun TarjetaOferta(oferta: Oferta, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(264.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(108.dp)
                .background(oferta.categoria.visual().color),
            contentAlignment = Alignment.Center
        ) {
            Text(oferta.categoria.visual().simbolo, fontSize = 46.sp)
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                color = NaranjaRescat,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "-${oferta.porcentajeDescuento}%",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
        Column(Modifier.fillMaxSize().padding(10.dp)) {
            Text(
                oferta.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                oferta.comercio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    oferta.precioNormal.formatearDinero(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    oferta.precioRescate.formatearDinero(),
                    style = MaterialTheme.typography.titleSmall,
                    color = NaranjaRescat,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                oferta.cantidadDisponible.formatearDisponibilidad(),
                style = MaterialTheme.typography.labelSmall,
                color = colorDisponibilidad(oferta.disponibilidad),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun colorDisponibilidad(disponibilidad: Disponibilidad): Color = when (disponibilidad) {
    Disponibilidad.ALTA -> VerdeRescat
    Disponibilidad.MEDIA -> NaranjaRescat
    Disponibilidad.BAJA, Disponibilidad.AGOTADA -> Color(0xFFBE3535)
}
