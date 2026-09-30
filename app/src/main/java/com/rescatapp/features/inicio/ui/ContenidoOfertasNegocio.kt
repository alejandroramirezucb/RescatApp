package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.SelectorPestanas
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo

@Composable
internal fun EncabezadoOfertasNegocio() {
    Text(
        text = UsuarioDemo.NEGOCIO_DEMO,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.fillMaxWidth().background(
            Naranja
        ).padding(horizontal = 20.dp, vertical = 8.dp)
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
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
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
internal fun ListaOfertasNegocio(ofertas: List<Oferta>, pestana: PestanaOfertas) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (ofertas.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay ofertas ${pestana.titulo.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(ofertas) { oferta -> TarjetaOfertaNegocio(oferta) }
        }
    }
}
