package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.DivisorColorRescat
import com.rescatapp.core.designsystem.FondoGrisRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.RojoFondoRescat
import com.rescatapp.core.designsystem.RojoTextoRescat
import com.rescatapp.core.designsystem.TextoGrisRescat
import com.rescatapp.core.designsystem.TextoOscuroRescat
import com.rescatapp.core.designsystem.VerdeFondoRescat
import com.rescatapp.core.designsystem.VerdeRescat
import com.rescatapp.core.designsystem.visual
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.features.pedidos.domain.ConstruirProgresoPedidoUseCase
import com.rescatapp.features.pedidos.domain.PasoProgreso

@Composable
fun PedidosScreen(
    onVolver: () -> Unit = {},
    onInicio: () -> Unit = {},
    onExplorar: () -> Unit = {},
    onPublicar: () -> Unit = {},
    onPerfil: () -> Unit = {},
    viewModel: PedidosViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        containerColor = FondoGrisRescat,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            PedidosContent(
                estado = estado,
                onCambiarPestana = viewModel::cambiarPestana,
                onAvanzarPedido = viewModel::avanzarPedido,
                onCancelarPedido = viewModel::cancelarPedido,
                onVolver = onVolver,
                onInicio = onInicio,
                onExplorar = onExplorar,
                onPublicar = onPublicar,
                onPerfil = onPerfil
            )
        }
    }
}

@Composable
fun PedidosContent(
    estado: PedidosUiState,
    onCambiarPestana: (PestanaPedidos) -> Unit = {},
    onAvanzarPedido: (Int) -> Unit = {},
    onCancelarPedido: (Int) -> Unit = {},
    onVolver: () -> Unit = {},
    onInicio: () -> Unit = {},
    onExplorar: () -> Unit = {},
    onPublicar: () -> Unit = {},
    onPerfil: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGrisRescat)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onVolver,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "← Volver",
                        color = NaranjaRescat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Mis pedidos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextoOscuroRescat
                )
            }

            TabsPedidos(
                pestanaSeleccionada = estado.pestana,
                onSeleccion = onCambiarPestana
            )

            Spacer(Modifier.height(14.dp))

            val lista = if (estado.pestana ==
                PestanaPedidos.ACTIVOS
            ) {
                estado.activos
            } else {
                estado.historial
            }

            if (lista.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val textoVacio = if (estado.pestana == PestanaPedidos.ACTIVOS) {
                        "Aún no tienes pedidos activos"
                    } else {
                        "Aún no tienes pedidos en tu historial"
                    }
                    Text(
                        text = textoVacio,
                        color = TextoGrisRescat,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(lista, key = { it.id }) { pedido ->
                        TarjetaPedido(
                            pedido = pedido,
                            onAvanzarPedido = onAvanzarPedido,
                            onCancelarPedido = onCancelarPedido
                        )
                    }
                }
            }
        }
    }
}

// ---------- Tabs ----------
@Composable
private fun TabsPedidos(
    pestanaSeleccionada: PestanaPedidos,
    onSeleccion: (PestanaPedidos) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DivisorColorRescat)
            .padding(5.dp)
    ) {
        PestanaPedidos.entries.forEach { pestana ->
            val activo = pestana == pestanaSeleccionada
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .then(
                        if (activo) {
                            Modifier
                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                .background(Color.White, RoundedCornerShape(10.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSeleccion(pestana) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pestana.titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activo) NaranjaRescat else TextoGrisRescat
                )
            }
        }
    }
}

// ---------- Tarjeta de pedido ----------
@Composable
private fun TarjetaPedido(
    pedido: Pedido,
    onAvanzarPedido: (Int) -> Unit,
    onCancelarPedido: (Int) -> Unit
) {
    val construirProgresoUseCase = remember { ConstruirProgresoPedidoUseCase() }
    val pasos = construirProgresoUseCase(pedido)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
    ) {
        // Parte superior: imagen/icono + datos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(82.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            listOf(NaranjaRescat.copy(alpha = 0.85f), NaranjaRescat)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(pedido.categoria.visual().simbolo, fontSize = 32.sp)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pedido.nombreOferta,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextoOscuroRescat,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (!pedido.estaActivo) {
                            ChipEstado(estado = pedido.estado)
                        }
                        Text(
                            text = pedido.precioPagado.formatearDinero(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NaranjaRescat
                        )
                    }
                }
                Text(
                    text = pedido.comercio,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextoGrisRescat
                )
                Text(
                    text = "Hoy ${pedido.horaRetiroDesde}–${pedido.horaRetiroHasta}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextoGrisRescat
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DivisorColorRescat)
        )

        // Stepper de estados
        StepperPedido(
            pasos = pasos,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
        )

        // Botón de acción dinámico + Cancelar
        if (pedido.estaActivo) {
            val textoBoton = when (pedido.estado) {
                EstadoPedido.RESERVADO -> "Marcar como Preparando"
                EstadoPedido.PREPARANDO -> "Marcar como Listo"
                EstadoPedido.LISTO -> "Confirmar retiro"
                else -> null
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pedido.estado == EstadoPedido.RESERVADO) {
                    TextButton(
                        onClick = { onCancelarPedido(pedido.id) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            color = RojoTextoRescat,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                if (textoBoton != null) {
                    Button(
                        onClick = { onAvanzarPedido(pedido.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = NaranjaRescat),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = textoBoton,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ---------- Stepper de estados ----------
@Composable
private fun StepperPedido(pasos: List<PasoProgreso>, modifier: Modifier = Modifier) {
    val diametro = 22.dp
    val grosorLinea = 2.dp

    Box(modifier = modifier.fillMaxWidth()) {
        // Líneas conectoras (detrás de los círculos)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = diametro / 2 - grosorLinea / 2)
        ) {
            Spacer(Modifier.weight(0.5f))
            for (i in 0 until pasos.size - 1) {
                val lineaCompletada = pasos[i].completado && pasos[i + 1].completado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(grosorLinea)
                        .background(if (lineaCompletada) NaranjaRescat else DivisorColorRescat)
                )
            }
            Spacer(Modifier.weight(0.5f))
        }

        // Círculos y etiquetas
        Row(modifier = Modifier.fillMaxWidth()) {
            pasos.forEach { paso ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CirculoPaso(paso = paso, diametro = diametro)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = paso.estado,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (paso.completado) NaranjaRescat else TextoGrisRescat,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun CirculoPaso(paso: PasoProgreso, diametro: Dp) {
    val colorFondo = if (paso.completado) NaranjaRescat else DivisorColorRescat
    Box(
        modifier = Modifier
            .size(diametro)
            .clip(CircleShape)
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        when {
            paso.actual -> {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            paso.completado -> {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ChipEstado(estado: EstadoPedido) {
    val (texto, colorTexto, colorFondo) = when (estado) {
        EstadoPedido.RECOGIDO -> Triple("Completado", VerdeRescat, VerdeFondoRescat)
        EstadoPedido.CANCELADO -> Triple("Cancelado", RojoTextoRescat, RojoFondoRescat)
        else -> Triple(estado.etiqueta, TextoGrisRescat, DivisorColorRescat)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = colorTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
