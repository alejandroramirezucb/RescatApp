package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.EncabezadoSeccion
import com.rescatapp.core.designsystem.components.TarjetaComercio
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.agruparComercios

@Composable
internal fun ComerciosInicio(
    ofertas: List<Oferta>,
    onExplorarNegocios: () -> Unit,
    onVerComercio: (String) -> Unit
) {
    val comercios = ofertas.agruparComercios()
    if (comercios.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EncabezadoSeccion(
            titulo = "Comercios con ofertas",
            subtitulo = "Con packs disponibles ahora",
            textoAccion = "Ver negocios",
            onAccion = onExplorarNegocios,
            modifier = Modifier.padding(rellenoLateral)
        )
        LazyRow(
            contentPadding = rellenoLateral,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comercios, key = { it.nombre }) { comercio ->
                TarjetaComercio(
                    comercio = comercio,
                    onClick = { onVerComercio(comercio.nombre) },
                    modifier = Modifier.width(168.dp)
                )
            }
        }
    }
}
