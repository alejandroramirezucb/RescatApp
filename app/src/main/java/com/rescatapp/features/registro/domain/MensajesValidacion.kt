package com.rescatapp.features.registro.domain

object MensajesValidacion {
    const val NOMBRE_VACIO = "Ingresa el nombre de la oferta"
    const val COMERCIO_VACIO = "Ingresa el nombre del comercio"
    const val CATEGORIA_SIN_ELEGIR = "Selecciona una categoría"
    const val DESCRIPCION_VACIA = "Describe el contenido de la oferta"
    const val PESO_INVALIDO = "Ingresa un peso válido en kg"
    const val PRECIO_NORMAL_INVALIDO = "Ingresa un precio normal válido"
    const val PRECIO_RESCATE_INVALIDO = "Ingresa un precio de rescate válido"
    const val PRECIO_RESCATE_NO_MENOR = "Debe ser menor al precio normal"
    const val CANTIDAD_INVALIDA = "Ingresa una cantidad válida"
    const val FORMATO_HORA_INVALIDO = "Usa el formato HH:mm"
    const val HORA_FINAL_NO_POSTERIOR = "Debe ser posterior a la hora de inicio"
}
