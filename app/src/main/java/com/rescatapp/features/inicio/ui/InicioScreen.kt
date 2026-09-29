package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.CremaRescat
import com.rescatapp.core.designsystem.DivisorColorRescat
import com.rescatapp.core.designsystem.FondoRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.RojoTextoRescat
import com.rescatapp.core.designsystem.TextoGrisRescat
import com.rescatapp.core.designsystem.TextoOscuroRescat
import com.rescatapp.core.designsystem.VerdeRescat
import com.rescatapp.core.designsystem.visual
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearDisponibilidad
import com.rescatapp.core.util.formatearPeso
import com.rescatapp.features.inicio.domain.InicioUiState

@Composable
fun InicioScreen(
    onExplorar: (String?) -> Unit,
    onPublicar: () -> Unit = {},
    onVerDetalle: (Int) -> Unit = {},
    onPedidos: () -> Unit = {},
    onPerfil: () -> Unit = {},
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
    onExplorar: (String?) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Encabezado: Ubicacion, Saludo, Subtitulo y Barra de busqueda
            EncabezadoInicio(
                usuario = estado.usuario,
                onBuscar = { onExplorar(null) }
            )

            // 2. Tu impacto esta semana
            BloqueImpactoSemanal(impacto = estado.impacto)

            // 3. Explora por categoria
            SeccionExploraPorCategoria(
                onSeleccionarCategoria = onExplorar
            )

            // 4. Ofertas cerca de ti
            SeccionOfertasCerca(
                ofertas = estado.ofertasDisponibles,
                onVerTodo = { onExplorar(null) },
                onVerDetalle = onVerDetalle
            )

            // 5. Se estan agotando
            SeccionSeEstanAgotando(
                ofertas = estado.seEstanAgotando,
                onVerDetalle = onVerDetalle
            )
        }
    }
}

@Composable
private fun EncabezadoInicio(usuario: String, onBuscar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("📍", fontSize = 14.sp)
            Text(
                text = "Santa Cruz de la Sierra",
                style = MaterialTheme.typography.bodySmall,
                color = TextoGrisRescat,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "Hola, $usuario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextoOscuroRescat
        )

        Text(
            text = "¿Qué vamos a aprovechar hoy?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoGrisRescat
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Barra de busqueda que no abre teclado y navega a Explorar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = "Buscar ofertas", onClick = onBuscar),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🔍", fontSize = 16.sp)
                Text(
                    text = "Buscar ofertas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoGrisRescat
                )
            }
        }
    }
}

@Composable
private fun SeccionExploraPorCategoria(onSeleccionarCategoria: (String?) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Explora por categoría",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextoOscuroRescat
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = false,
                onClick = { onSeleccionarCategoria(null) },
                label = { Text("Todos") }
            )
            Categoria.entries.forEach { categoria ->
                FilterChip(
                    selected = false,
                    onClick = { onSeleccionarCategoria(categoria.name) },
                    label = { Text(categoria.etiqueta) }
                )
            }
        }
    }
}

@Composable
private fun SeccionOfertasCerca(
    ofertas: List<Oferta>,
    onVerTodo: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Ofertas cerca de ti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuroRescat
                )
                Text(
                    "Lo que está disponible ahora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onVerTodo) {
                Text(
                    text = "Ver todo",
                    color = NaranjaRescat,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (ofertas.isEmpty()) {
            Text(
                text = "No hay ofertas disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ofertas.size) { index ->
                    val oferta = ofertas[index]
                    TarjetaOfertaHorizontal(
                        oferta = oferta,
                        onClick = { onVerDetalle(oferta.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionSeEstanAgotando(ofertas: List<Oferta>, onVerDetalle: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CremaRescat),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Se están agotando",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoOscuroRescat
            )
            Text(
                text = "No dejes que se acaben",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (ofertas.isEmpty()) {
                Text(
                    text = "No hay ofertas por agotarse",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ofertas.size) { index ->
                        val oferta = ofertas[index]
                        TarjetaOfertaHorizontal(
                            oferta = oferta,
                            onClick = { onVerDetalle(oferta.id) },
                            subetiqueta = "Hasta ${oferta.horaRetiroHasta}"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaOfertaHorizontal(
    oferta: Oferta,
    onClick: () -> Unit,
    subetiqueta: String? = null
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(oferta.categoria.visual().simbolo, fontSize = 28.sp)
                Text(
                    oferta.precioRescate.formatearDinero(),
                    style = MaterialTheme.typography.titleSmall,
                    color = NaranjaRescat,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                oferta.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                oferta.comercio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (subetiqueta != null) {
                Text(
                    text = subetiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = RojoTextoRescat,
                    fontWeight = FontWeight.Bold
                )
            } else {
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

@Composable
private fun BloqueImpactoSemanal(impacto: Impacto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tu impacto esta semana",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoOscuroRescat
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemImpacto(
                    valor = "${impacto.rescates}",
                    etiqueta = "rescates",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(DivisorColorRescat)
                )
                ItemImpacto(
                    valor = impacto.ahorrado.formatearDinero(),
                    etiqueta = "ahorrado",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(DivisorColorRescat)
                )
                ItemImpacto(
                    valor = impacto.kgAprovechados.formatearPeso(),
                    etiqueta = "aprovechado",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ItemImpacto(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "$valor $etiqueta"
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = VerdeRescat
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = TextoGrisRescat
        )
    }
}
