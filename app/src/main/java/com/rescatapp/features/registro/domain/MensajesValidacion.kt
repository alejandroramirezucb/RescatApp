package com.rescatapp.features.registro.domain

import com.rescatapp.core.domain.ValidadorCamposOferta

object MensajesValidacion {
    const val NOMBRE_VACIO = "Ingresa el nombre de la oferta"
    const val COMERCIO_VACIO = "Ingresa el nombre del comercio"
    const val CATEGORIA_SIN_ELEGIR = "Selecciona una categoría"
    const val DESCRIPCION_VACIA = "Describe el contenido de la oferta"
    const val PESO_INVALIDO = "Ingresa un peso válido en kg"
    const val PRECIO_NORMAL_INVALIDO = "Ingresa un precio normal válido"
    const val PRECIO_RESCATE_INVALIDO = ValidadorCamposOferta.PRECIO_RESCATE_INVALIDO
    const val PRECIO_RESCATE_NO_MENOR = ValidadorCamposOferta.PRECIO_RESCATE_NO_MENOR
    const val CANTIDAD_INVALIDA = ValidadorCamposOferta.CANTIDAD_INVALIDA
    const val FORMATO_HORA_INVALIDO = ValidadorCamposOferta.FORMATO_HORA_INVALIDO
    const val HORA_FINAL_NO_POSTERIOR = ValidadorCamposOferta.HORA_FINAL_NO_POSTERIOR
}
