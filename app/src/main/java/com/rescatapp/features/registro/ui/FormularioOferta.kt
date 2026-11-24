package com.rescatapp.features.registro.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro
import kotlin.math.roundToInt

@Composable
fun FormularioOferta(
    campos: CamposRegistro,
    errores: ErroresRegistro,
    onCampoCambiado: (CamposRegistro) -> Unit
) {
    val normal = campos.precioNormal.replace(',', '.').toDoubleOrNull()
    val oferta = campos.precioRescate.replace(',', '.').toDoubleOrNull()
    val porcentajeAhorro = if (
        normal != null && oferta != null && normal > 0 && oferta > 0 && oferta < normal
    ) {
        ((1 - oferta / normal) * 100).roundToInt()
    } else {
        0
    }

    SeccionFormularioOferta(
        titulo = "Información del pack",
        descripcion = "Cuenta qué ofreces y elige la categoría que mejor lo representa."
    ) {
        CampoConError(
            etiqueta = "Nombre del pack",
            valor = campos.nombre,
            ejemplo = "Ej. Pack Sorpresa",
            onValorCambiado = { onCampoCambiado(campos.copy(nombre = it)) },
            error = errores.nombre
        )

        CampoConError(
            etiqueta = "Descripción",
            valor = campos.descripcion,
            ejemplo = "¿Qué incluye?",
            onValorCambiado = { onCampoCambiado(campos.copy(descripcion = it)) },
            error = errores.descripcion
        )

        SelectorCategoriaOferta(
            seleccionada = campos.categoria,
            error = errores.categoria,
            onSeleccion = { onCampoCambiado(campos.copy(categoria = it)) }
        )

        CampoConError(
            etiqueta = "Peso estimado (kg)",
            valor = campos.pesoKg,
            ejemplo = "1.5",
            tipoTeclado = KeyboardType.Decimal,
            onValorCambiado = { onCampoCambiado(campos.copy(pesoKg = it)) },
            error = errores.pesoKg
        )
    }

    SeccionFormularioOferta(
        titulo = "Precio y ahorro",
        descripcion = "Indica el precio habitual y el precio de rescate en bolivianos."
    ) {
        FilaCamposOferta(
            primero = { espacio ->
                CampoConError(
                    etiqueta = "Precio original",
                    valor = campos.precioNormal,
                    ejemplo = "45",
                    tipoTeclado = KeyboardType.Decimal,
                    onValorCambiado = { onCampoCambiado(campos.copy(precioNormal = it)) },
                    error = errores.precioNormal,
                    modifier = espacio
                )
            },
            segundo = { espacio ->
                CampoConError(
                    etiqueta = "Precio oferta",
                    valor = campos.precioRescate,
                    ejemplo = "25",
                    tipoTeclado = KeyboardType.Decimal,
                    destacarBordeNaranja = true,
                    onValorCambiado = { onCampoCambiado(campos.copy(precioRescate = it)) },
                    error = errores.precioRescate,
                    modifier = espacio
                )
            }
        )
        TarjetaAhorroCliente(porcentaje = porcentajeAhorro)
    }

    SeccionFormularioOferta(
        titulo = "Disponibilidad y retiro",
        descripcion = "Define cuántos packs hay y en qué horario se pueden retirar."
    ) {
        CampoConError(
            etiqueta = "Cantidad",
            valor = campos.cantidadDisponible,
            ejemplo = "10",
            tipoTeclado = KeyboardType.Number,
            onValorCambiado = { onCampoCambiado(campos.copy(cantidadDisponible = it)) },
            error = errores.cantidadDisponible
        )
        CampoConError(
            etiqueta = "Fecha de retiro",
            valor = campos.fechaRetiro,
            ejemplo = "2026-11-18",
            onValorCambiado = {
                onCampoCambiado(campos.copy(fechaRetiro = it))
            },
            error = errores.fechaRetiro
        )
        FilaCamposOferta(
            primero = { espacio ->
                CampoConError(
                    etiqueta = "Hora inicio",
                    valor = campos.horaRetiroDesde,
                    ejemplo = "18:00",
                    onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroDesde = it)) },
                    error = errores.horaRetiroDesde,
                    modifier = espacio
                )
            },
            segundo = { espacio ->
                CampoConError(
                    etiqueta = "Hora fin",
                    valor = campos.horaRetiroHasta,
                    ejemplo = "20:00",
                    onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroHasta = it)) },
                    error = errores.horaRetiroHasta,
                    modifier = espacio
                )
            }
        )
    }

    SeccionFormularioOferta(
        titulo = "Fotografía del pack",
        descripcion = "Opcional. Añade una imagen que ayude a reconocer tu oferta."
    ) {
        SeccionFotoOferta(
            imagenUri = campos.imagenUri,
            onFotoSeleccionada = { onCampoCambiado(campos.copy(imagenUri = it)) }
        )
    }
}
