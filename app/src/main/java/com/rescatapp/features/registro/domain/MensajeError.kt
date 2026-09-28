package com.rescatapp.features.registro.domain

object MensajeError {
    const val NombreVacio = "Ingresa el nombre de la oferta"
    const val ComercioVacio = "Ingresa el nombre del comercio"
    const val CategoriaVacio = "Selecciona una categoría"
    const val DescripcionVacia = "Describe el contenido de la oferta"
    const val PesoVacio = "Ingresa un peso válido en kg"
    const val PrecioNormalVacio = "Ingresa un precio normal válido"
    const val PrecioRescateVacio = "Ingresa un precio de rescate válido"
    const val PrecioRescateMenorNormal = "Debe ser menor al precio normal"
    const val CantidadInvalida = "Ingresa una cantidad válida"
    const val FormatoHoraInvalido = "Usa el formato HH:mm"
    const val HoraFinalMayor = "Debe ser posterior a la hora de inicio"
}