package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.FondoRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.VerdeRescat
import com.rescatapp.core.designsystem.visual
import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearDisponibilidad
import com.rescatapp.core.util.formatearHorario
import com.rescatapp.core.util.formatearPeso

@Composable
fun DetalleScreen(onVolver: () -> Unit, viewModel: DetalleViewModel = hiltViewModel()) {
    val estado = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        containerColor = FondoRescat,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(color = Color.White, shadowElevation = 1.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onVolver) { Text("← Volver") }
                    Text(
                        "Detalle",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        if (estado.noEncontrada) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Oferta no encontrada", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            estado.oferta?.let { oferta ->
                ContenidoDetalle(
                    oferta = oferta,
                    onReservar = viewModel::reservar,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ContenidoDetalle(
    oferta: Oferta,
    onReservar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = oferta.categoria.visual()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(216.dp).background(visual.color),
            contentAlignment = Alignment.Center
        ) {
            Text(visual.simbolo, fontSize = 82.sp)
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                color = NaranjaRescat,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "-${oferta.porcentajeDescuento}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    oferta.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    oferta.comercio,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(color = visual.color, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        oferta.categoria.etiqueta,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Text(oferta.descripcion, style = MaterialTheme.typography.bodyLarge)
                FilaInformacion("Peso", oferta.pesoKg.formatearPeso())
                FilaInformacion(
                    "Disponibilidad",
                    oferta.cantidadDisponible.formatearDisponibilidad(),
                    colorDisponibilidad(oferta.disponibilidad)
                )
                FilaInformacion(
                    "Retiro",
                    formatearHorario(oferta.horaRetiroDesde, oferta.horaRetiroHasta)
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        oferta.precioNormal.formatearDinero(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        oferta.precioRescate.formatearDinero(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = NaranjaRescat,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    "Ahorras ${oferta.ahorroPorUnidad.formatearDinero()}",
                    color = VerdeRescat,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onReservar,
                    enabled = !oferta.estaAgotada,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaRescat)
                ) {
                    Text(if (oferta.estaAgotada) "Agotado" else "Reservar")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FilaInformacion(etiqueta: String, valor: String, color: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        Text(valor, color = color, fontWeight = FontWeight.SemiBold)
    }
}

private fun colorDisponibilidad(disponibilidad: Disponibilidad): Color = when (disponibilidad) {
    Disponibilidad.ALTA -> VerdeRescat
    Disponibilidad.MEDIA -> NaranjaRescat
    Disponibilidad.BAJA, Disponibilidad.AGOTADA -> Color(0xFFBE3535)
}
