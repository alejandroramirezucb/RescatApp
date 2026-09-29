package com.rescatapp.core.data

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido

private const val LA_CENTRAL = "Panadería La Central"
private const val DON_MARCO = "Pizzería Don Marco"
private const val CAFE_AROMA = "Café Aroma"
private const val DULCERIA_BELLA = "Dulcería Bella"
private const val GREEN_KITCHEN = "Green Kitchen"

val ofertasDeEjemplo: List<Oferta> = listOf(
    Oferta(
        14, "Pack Sorpresa", LA_CENTRAL, Categoria.PANADERIA,
        "Surtido de panes y facturas del día", 1.0, 45.0, 25.0, 5, "18:00", "20:00"
    ),
    Oferta(
        13, "Pack Croissants", LA_CENTRAL, Categoria.PANADERIA,
        "Croissants de mantequilla del día", 0.6, 30.0, 15.0, 3, "17:00", "19:00"
    ),
    Oferta(
        12, "Pizza Familiar", DON_MARCO, Categoria.COMIDA,
        "Pizza familiar de la casa", 1.2, 80.0, 45.0, 2, "20:00", "22:00"
    ),
    Oferta(
        11, "2 Pizzas Medianas", DON_MARCO, Categoria.COMIDA,
        "Dos pizzas medianas surtidas", 1.6, 110.0, 55.0, 1, "20:00", "22:00"
    ),
    Oferta(
        10, "Pack Café+", CAFE_AROMA, Categoria.CAFETERIA,
        "Café para llevar y porción de torta", 0.5, 35.0, 18.0, 6, "16:00", "18:00"
    ),
    Oferta(
        9, "Pack Mañanero", CAFE_AROMA, Categoria.CAFETERIA,
        "Desayuno con café y medialunas", 0.7, 40.0, 22.0, 4, "10:00", "12:00"
    ),
    Oferta(
        8, "Tortas Mix", DULCERIA_BELLA, Categoria.POSTRES,
        "Porciones de torta surtidas", 0.9, 60.0, 30.0, 4, "18:00", "20:00"
    ),
    Oferta(
        7, "Caja Cupcakes", DULCERIA_BELLA, Categoria.POSTRES,
        "Cupcakes decorados del día", 0.6, 45.0, 25.0, 2, "18:00", "20:00"
    ),
    Oferta(
        6, "Bowl Saludable", GREEN_KITCHEN, Categoria.COMIDA,
        "Bowl de verduras y granos", 0.8, 55.0, 28.0, 3, "13:00", "15:00"
    ),
    Oferta(
        5, "Pack Lácteos", "Súper Norte", Categoria.COMIDA,
        "Lácteos próximos a vencer", 2.0, 50.0, 25.0, 8, "19:00", "21:00"
    ),
    Oferta(
        4, "Pack Empanadas", LA_CENTRAL, Categoria.PANADERIA,
        "Empanadas surtidas", 1.0, 40.0, 20.0, 4, "18:00", "20:00"
    ),
    Oferta(
        3, "Burger + Papas", DON_MARCO, Categoria.COMIDA,
        "Hamburguesa con papas fritas", 0.7, 65.0, 35.0, 2, "21:00", "23:00"
    ),
    Oferta(
        2, "Pack Frutas", GREEN_KITCHEN, Categoria.COMIDA,
        "Frutas de temporada", 2.5, 45.0, 22.0, 5, "17:00", "19:00"
    ),
    Oferta(
        1, "Sándwich Combo", CAFE_AROMA, Categoria.CAFETERIA,
        "Sándwich con bebida", 0.6, 50.0, 28.0, 3, "12:00", "14:00"
    )
)

val pedidosDeEjemplo: List<Pedido> = listOf(
    pedidoDeEjemplo(idPedido = 5, idOferta = 14, estado = EstadoPedido.LISTO),
    pedidoDeEjemplo(idPedido = 4, idOferta = 12, estado = EstadoPedido.PREPARANDO),
    pedidoDeEjemplo(idPedido = 3, idOferta = 10, estado = EstadoPedido.RECOGIDO),
    pedidoDeEjemplo(idPedido = 2, idOferta = 8, estado = EstadoPedido.RECOGIDO),
    pedidoDeEjemplo(idPedido = 1, idOferta = 1, estado = EstadoPedido.CANCELADO)
)

private fun pedidoDeEjemplo(idPedido: Int, idOferta: Int, estado: EstadoPedido): Pedido =
    ofertasDeEjemplo.first { it.id == idOferta }.crearPedido(idPedido, estado)
