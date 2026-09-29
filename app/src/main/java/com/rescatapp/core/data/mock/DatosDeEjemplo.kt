package com.rescatapp.core.data.mock

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido

val ofertasDeEjemplo: List<Oferta> = listOf(
    Oferta(
        14, "Pack Sorpresa", "Panadería La Central", Categoria.PANADERIA,
        "Surtido de panes y facturas del día",
        1.0, 45.0, 25.0, 5, "18:00", "20:00"
    ),
    Oferta(
        13, "Pack Croissants", "Panadería La Central", Categoria.PANADERIA, "Croissants del día",
        0.6, 30.0, 15.0, 3, "17:00", "19:00"
    ),
    Oferta(
        12, "Pizza Familiar", "Pizzería Don Marco", Categoria.COMIDA, "Pizza grande",
        1.2, 80.0, 45.0, 2, "20:00", "22:00"
    ),
    Oferta(
        11, "2 Pizzas Medianas", "Pizzería Don Marco", Categoria.COMIDA, "Dos pizzas",
        1.6, 110.0, 55.0, 1, "20:00", "22:00"
    ),
    Oferta(
        10, "Pack Café+", "Café Aroma", Categoria.CAFETERIA, "Bebida y postre",
        0.5, 35.0, 18.0, 6, "16:00", "18:00"
    ),
    Oferta(
        9, "Pack Mañanero", "Café Aroma", Categoria.CAFETERIA, "Desayuno",
        0.7, 40.0, 22.0, 4, "10:00", "12:00"
    ),
    Oferta(
        8, "Tortas Mix", "Dulcería Bella", Categoria.POSTRES, "Porciones de torta",
        0.9, 60.0, 30.0, 4, "18:00", "20:00"
    ),
    Oferta(
        7, "Caja Cupcakes", "Dulcería Bella", Categoria.POSTRES, "Cupcakes del día",
        0.6, 45.0, 25.0, 2, "18:00", "20:00"
    ),
    Oferta(
        6, "Bowl Saludable", "Green Kitchen", Categoria.COMIDA, "Bowl con verduras",
        0.8, 55.0, 28.0, 3, "13:00", "15:00"
    ),
    Oferta(
        5, "Pack Lácteos", "Súper Norte", Categoria.COMIDA, "Lácteos del día",
        2.0, 50.0, 25.0, 8, "19:00", "21:00"
    ),
    Oferta(
        4, "Pack Empanadas", "Panadería La Central", Categoria.PANADERIA, "Empanadas surtidas",
        1.0, 40.0, 20.0, 4, "18:00", "20:00"
    ),
    Oferta(
        3, "Burger + Papas", "Pizzería Don Marco", Categoria.COMIDA, "Hamburguesa y papas",
        0.7, 65.0, 35.0, 2, "21:00", "23:00"
    ),
    Oferta(
        2, "Pack Frutas", "Green Kitchen", Categoria.COMIDA, "Frutas de temporada",
        2.5, 45.0, 22.0, 5, "17:00", "19:00"
    ),
    Oferta(
        1, "Sándwich Combo", "Café Aroma", Categoria.CAFETERIA, "Sándwich y café",
        0.6, 50.0, 28.0, 3, "12:00", "14:00"
    )
)

val pedidosDeEjemplo: List<Pedido> = listOf(
    Pedido(
        5, 14, "Pack Sorpresa", "Panadería La Central", Categoria.PANADERIA,
        25.0, 20.0, 1.0, "18:00", "20:00", EstadoPedido.LISTO
    ),
    Pedido(
        4, 12, "Pizza Familiar", "Pizzería Don Marco", Categoria.COMIDA,
        45.0, 35.0, 1.2, "20:00", "22:00", EstadoPedido.PREPARANDO
    ),
    Pedido(
        3, 10, "Pack Café+", "Café Aroma", Categoria.CAFETERIA,
        18.0, 17.0, 0.5, "16:00", "18:00", EstadoPedido.RECOGIDO
    ),
    Pedido(
        2, 8, "Tortas Mix", "Dulcería Bella", Categoria.POSTRES,
        30.0, 30.0, 0.9, "18:00", "20:00", EstadoPedido.RECOGIDO
    ),
    Pedido(
        1, 1, "Sándwich Combo", "Café Aroma", Categoria.CAFETERIA,
        28.0, 22.0, 0.6, "12:00", "14:00", EstadoPedido.CANCELADO
    )
)
