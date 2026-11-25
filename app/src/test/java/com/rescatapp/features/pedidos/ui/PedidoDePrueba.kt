package com.rescatapp.features.pedidos.ui

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

internal val pedidoDePrueba = Pedido(
    id = 77,
    ofertaId = 1,
    nombreOferta = "Pack Sorpresa",
    comercio = "Panadería La Central",
    categoria = Categoria.PANADERIA,
    precioPagado = 25.0,
    ahorro = 20.0,
    pesoKg = 1.0,
    horaRetiroDesde = "18:00",
    horaRetiroHasta = "20:00",
    estado = EstadoPedido.RESERVADO,
    codigoServidor = "#APV-77",
    clienteNombre = "Ana Pérez",
    clienteTelefono = "71234567"
)
