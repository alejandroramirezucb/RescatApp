package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.DivisorColorRescat
import com.rescatapp.core.designsystem.FondoGrisRescat
import com.rescatapp.core.designsystem.NaranjaCirculoRescat
import com.rescatapp.core.designsystem.NaranjaClaroRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.TextoGrisRescat
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearPeso
import com.rescatapp.features.perfil.domain.PerfilUiState

@Composable
fun PerfilScreen(onVolver: () -> Unit = {}, viewModel: PerfilViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    PerfilContent(estado = estado, onVolver = onVolver)
}

@Composable
fun PerfilContent(estado: PerfilUiState, onVolver: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoGrisRescat)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderPerfil(
            nombre = estado.nombre,
            correo = estado.correo,
            inicial = estado.inicial,
            onVolver = onVolver
        )
        EstadisticasRow(
            rescates = estado.rescates,
            ahorrado = estado.ahorrado,
            aprovechado = estado.aprovechado
        )
    }
}

// ---------- Header naranja ----------
@Composable
private fun HeaderPerfil(nombre: String, correo: String, inicial: String, onVolver: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NaranjaRescat)
    ) {
        // Círculo decorativo arriba a la derecha
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = 35.dp, y = (-35).dp)
                .clip(CircleShape)
                .background(NaranjaCirculoRescat.copy(alpha = 0.55f))
        )

        Column(modifier = Modifier.padding(16.dp)) {
            TextButton(
                onClick = onVolver,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "← Volver",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(NaranjaClaroRescat)
                        .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = inicial,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = nombre,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = correo,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ---------- Estadísticas ----------
@Composable
private fun EstadisticasRow(rescates: Int, ahorrado: Double, aprovechado: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Estadistica("$rescates", "Rescates", Modifier.weight(1f))
        Box(Modifier.width(1.dp).height(36.dp).background(DivisorColorRescat))
        Estadistica(ahorrado.formatearDinero(), "Ahorrado", Modifier.weight(1f))
        Box(Modifier.width(1.dp).height(36.dp).background(DivisorColorRescat))
        Estadistica(aprovechado.formatearPeso(), "Aprovechado", Modifier.weight(1f))
    }
}

@Composable
private fun Estadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, color = NaranjaRescat, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(etiqueta, color = TextoGrisRescat, fontSize = 10.sp)
    }
}
