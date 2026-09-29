package com.rescatapp.features.registro.domain

import com.rescatapp.core.model.Categoria

val camposValidos = CamposRegistro(
    nombre = "Pack Salteñas",
    comercio = "Panadería La Central",
    categoria = Categoria.PANADERIA,
    descripcion = "Salteñas del día",
    pesoKg = "1,5",
    precioNormal = "40",
    precioRescate = "20",
    cantidadDisponible = "4",
    horaRetiroDesde = "09:00",
    horaRetiroHasta = "11:00"
)
