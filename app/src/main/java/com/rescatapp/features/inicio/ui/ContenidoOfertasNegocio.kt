package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta

@Composable
internal fun ListaOfertasNegocio(
    estado: OfertasNegocioUiState,
    onSeleccion: (PestanaOfertas) -> Unit,
    onReintentar: () -> Unit,
    onPublicar: (Oferta) -> Unit,
    onEditar: (Oferta) -> Unit,
    onEliminar: (Oferta) -> Unit,
    onReintentarOperacion: () -> Unit,
    onLimpiarMensaje: () -> Unit
) {
    val esRemota = estado.pestana == PestanaOfertas.EN_LINEA
    val ofertas = when (estado.pestana) {
        PestanaOfertas.ACTIVAS -> estado.activas
        PestanaOfertas.FINALIZADAS -> estado.finalizadas
        PestanaOfertas.EN_LINEA -> (estado.estadoRemoto as? EstadoRemoto.Exito)?.datos.orEmpty()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { EncabezadoOfertasNegocio() }
        item {
            SelectorOfertasNegocio(pestana = estado.pestana, onSeleccion = onSeleccion)
        }
        item {
            Text(
                text = if (esRemota) {
                    "Ofertas publicadas en línea"
                } else {
                    "Ofertas locales · En este dispositivo"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        estado.mensaje?.let { mensaje ->
            item { MensajeOfertas(mensaje, "Entendido", onLimpiarMensaje) }
        }
        estado.errorLocal?.let { mensaje ->
            item { MensajeOfertas(mensaje, "Entendido", onLimpiarMensaje, error = true) }
        }
        if (estado.guardandoLocal) {
            item { ProgresoOfertas("Guardando cambios locales…") }
        }
        if (estado.eliminandoLocal) {
            item { ProgresoOfertas("Eliminando oferta local.") }
        }
        estado.errorOperacion?.let { mensaje ->
            item {
                MensajeOfertas(
                    mensaje,
                    "Reintentar operación",
                    onReintentarOperacion,
                    error = true,
                    habilitado = !estado.ocupado
                )
            }
        }
        estado.operacionEnCurso?.let { operacion ->
            item {
                val accion = when (operacion.tipo) {
                    TipoOperacionOferta.PUBLICAR -> "Publicando"
                    TipoOperacionOferta.EDITAR -> "Guardando cambios de"
                    TipoOperacionOferta.ELIMINAR -> "Eliminando"
                }
                ProgresoOfertas("$accion «${operacion.oferta.nombre}»…")
            }
        }
        if (esRemota && estado.estadoRemoto is EstadoRemoto.Cargando) {
            item { ProgresoOfertas("Cargando ofertas en línea…") }
        } else if (esRemota && estado.estadoRemoto is EstadoRemoto.Error) {
            item {
                MensajeOfertas(
                    estado.estadoRemoto.mensaje,
                    "Reintentar",
                    onReintentar,
                    error = true
                )
            }
        } else if (ofertas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay ofertas ${estado.pestana.titulo.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(ofertas, key = { it.id }) { oferta ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    TarjetaOfertaNegocio(
                        oferta = oferta,
                        esRemota = esRemota,
                        habilitada = !estado.ocupado,
                        procesando = estado.operacionEnCurso?.let {
                            it.oferta.id == oferta.id &&
                                esRemota == (it.tipo != TipoOperacionOferta.PUBLICAR)
                        } == true,
                        onEditar = { onEditar(oferta) },
                        onPublicar = if (estado.pestana == PestanaOfertas.ACTIVAS) {
                            { onPublicar(oferta) }
                        } else {
                            null
                        },
                        onEliminar = { onEliminar(oferta) }
                    )
                }
            }
        }
    }
}
