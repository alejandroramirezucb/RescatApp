package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.SelectorPestanas
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo

@Composable
internal fun EncabezadoOfertasNegocio() {
    Text(
        text = UsuarioDemo.NEGOCIO_DEMO,
        style = MaterialTheme.typography.labelMedium,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .background(Naranja)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
internal fun SelectorOfertasNegocio(
    pestana: PestanaOfertas,
    onSeleccion: (PestanaOfertas) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            text = "Mis ofertas",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))
        SelectorPestanas(
            titulos = PestanaOfertas.entries.map { it.titulo },
            indiceSeleccionado = pestana.ordinal,
            onSeleccion = { onSeleccion(PestanaOfertas.entries[it]) }
        )
    }
}

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
        contentPadding = PaddingValues(bottom = 48.dp),
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

@Composable
private fun ProgresoOfertas(mensaje: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(color = Naranja)
        Text(mensaje, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MensajeOfertas(
    mensaje: String,
    accion: String,
    onAccion: () -> Unit,
    error: Boolean = false,
    habilitado: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (error) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(mensaje, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onAccion, enabled = habilitado) { Text(accion) }
        }
    }
}
