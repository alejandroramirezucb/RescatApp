package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo

internal enum class PestanaOfertas(val titulo: String) {
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas")
}

@Composable
fun OfertasNegocioScreen(viewModel: OfertasNegocioViewModel = hiltViewModel()) {
    val ofertas by viewModel.ofertas.collectAsStateWithLifecycle()
    val ofertasNegocio = remember(ofertas) {
        ofertas.filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
    }
    val ofertasActivas = remember(ofertasNegocio) { ofertasNegocio.filter { !it.estaAgotada } }
    val ofertasFinalizadas = remember(ofertasNegocio) { ofertasNegocio.filter { it.estaAgotada } }

    var pestana by remember { mutableStateOf(PestanaOfertas.ACTIVAS) }
    var ofertaParaEditar by remember { mutableStateOf<Oferta?>(null) }

    ofertaParaEditar?.let { oferta ->
        DialogoEditarOferta(
            oferta = oferta,
            onGuardar = { actualizada ->
                viewModel.actualizarOferta(actualizada)
                ofertaParaEditar = null
            },
            onDescartar = { ofertaParaEditar = null }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoOfertasNegocio()
        SelectorOfertasNegocio(pestana) { pestana = it }
        ListaOfertasNegocio(
            ofertas = if (pestana == PestanaOfertas.ACTIVAS) ofertasActivas else ofertasFinalizadas,
            pestana = pestana,
            onEditar = { ofertaParaEditar = it }
        )
    }
}

@Composable
private fun DialogoEditarOferta(
    oferta: Oferta,
    onGuardar: (Oferta) -> Unit,
    onDescartar: () -> Unit
) {
    var cantidad by remember { mutableStateOf(oferta.cantidadDisponible.toString()) }
    var precioRescate by remember { mutableStateOf(oferta.precioRescate.toString()) }
    var horaDesde by remember { mutableStateOf(oferta.horaRetiroDesde) }
    var horaHasta by remember { mutableStateOf(oferta.horaRetiroHasta) }

    AlertDialog(
        onDismissRequest = onDescartar,
        title = {
            Text(
                text = "Editar oferta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = oferta.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Naranja,
                    fontWeight = FontWeight.Bold
                )

                CampoConError(
                    etiqueta = "Unidades disponibles",
                    valor = cantidad,
                    ejemplo = "10",
                    tipoTeclado = KeyboardType.Number,
                    onValorCambiado = { cantidad = it }
                )

                CampoConError(
                    etiqueta = "Precio de rescate (Bs.)",
                    valor = precioRescate,
                    ejemplo = "25",
                    tipoTeclado = KeyboardType.Decimal,
                    destacarBordeNaranja = true,
                    onValorCambiado = { precioRescate = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoConError(
                        etiqueta = "Hora inicio",
                        valor = horaDesde,
                        ejemplo = "18:00",
                        modifier = Modifier.weight(1f),
                        onValorCambiado = { horaDesde = it }
                    )
                    CampoConError(
                        etiqueta = "Hora fin",
                        valor = horaHasta,
                        ejemplo = "20:00",
                        modifier = Modifier.weight(1f),
                        onValorCambiado = { horaHasta = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nuevaCantidad = cantidad.toIntOrNull() ?: oferta.cantidadDisponible
                    val nuevoPrecio = precioRescate.toDoubleOrNull() ?: oferta.precioRescate
                    val ofertaActualizada = oferta.copy(
                        cantidadDisponible = nuevaCantidad,
                        precioRescate = nuevoPrecio,
                        horaRetiroDesde = horaDesde.trim(),
                        horaRetiroHasta = horaHasta.trim()
                    )
                    onGuardar(ofertaActualizada)
                }
            ) {
                Text("Guardar cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDescartar) {
                Text("Cancelar")
            }
        }
    )
}
