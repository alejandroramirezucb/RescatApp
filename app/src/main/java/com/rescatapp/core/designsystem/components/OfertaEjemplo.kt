package com.rescatapp.core.designsystem.components

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

val ofertaEjemplo = Oferta(
    id = 1,
    nombre = "Pack Sorpresa",
    comercio = "Panadería La Central",
    categoria = Categoria.PANADERIA,
    descripcion = "Surtido de panes del día",
    pesoKg = 1.0,
    precioNormal = 45.0,
    precioRescate = 25.0,
    cantidadDisponible = 5,
    horaRetiroDesde = "18:00",
    horaRetiroHasta = "20:00"
)
