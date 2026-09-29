package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraSuperior
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.EfectoMensajeTemporal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.components.InsigniaDescuento
import com.rescatapp.core.designsystem.components.PreciosOferta
import com.rescatapp.core.designsystem.components.TextoDisponibilidad
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearHorario
import com.rescatapp.core.util.formatearPeso

@Composable
fun DetalleScreen(onVolver: () -> Unit, viewModel: DetalleViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }
    EfectoMensajeTemporal(estado.mensaje, estadoSnackbar, viewModel::limpiarMensaje)

    Scaffold(
        topBar = { BarraSuperior(titulo = "Detalle de la oferta", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(estadoSnackbar) }
    ) { espacioInterno ->
        val oferta = estado.oferta
        if (oferta == null) {
            Column(modifier = Modifier.padding(espacioInterno).padding(20.dp)) {
                EstadoVacio("Oferta no encontrada")
                BotonPrincipal(texto = "Volver", onClick = onVolver)
            }
        } else {
            Column(modifier = Modifier.padding(espacioInterno)) {
                InformacionOferta(oferta = oferta, onReservar = viewModel::reservar)
            }
        }
    }
}

@Composable
private fun InformacionOferta(oferta: Oferta, onReservar: () -> Unit) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ImagenCategoria(
            categoria = oferta.categoria,
            modifier = Modifier.fillMaxWidth().height(220.dp),
            tamanoIcono = 88.dp
        ) {
            InsigniaDescuento(
                porcentajeDescuento = oferta.porcentajeDescuento,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            )
        }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = oferta.nombre, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "${oferta.comercio} · ${oferta.categoria.etiqueta}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = oferta.descripcion, style = MaterialTheme.typography.bodyLarge)
            PreciosOferta(oferta, estiloPrecioRescate = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Ahorras ${oferta.ahorroPorUnidad.formatearDinero()}",
                style = MaterialTheme.typography.titleSmall,
                color = Verde
            )
            HorizontalDivider()
            DatoOferta("Peso", oferta.pesoKg.formatearPeso())
            DatoOferta("Retiro", formatearHorario(oferta.horaRetiroDesde, oferta.horaRetiroHasta))
            TextoDisponibilidad(oferta)
            BotonPrincipal(
                texto = if (oferta.estaAgotada) "Agotado" else "Reservar",
                onClick = onReservar,
                habilitado = !oferta.estaAgotada
            )
        }
    }
}

@Composable
private fun DatoOferta(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(text = valor, style = MaterialTheme.typography.titleSmall)
    }
}
