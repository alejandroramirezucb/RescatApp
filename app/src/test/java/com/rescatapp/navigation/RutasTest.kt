package com.rescatapp.navigation

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.RolUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RutasTest {
    @Test
    fun explorarSinCategoriaRetornaLaRutaSimple() {
        assertEquals("explorar", Rutas.explorar(null))
    }

    @Test
    fun explorarConCategoriaAgregaElParametro() {
        assertEquals("explorar?categoria=POSTRES", Rutas.explorar(Categoria.POSTRES))
        assertEquals("explorar?categoria=PANADERIA", Rutas.explorar(Categoria.PANADERIA))
    }

    @Test
    fun detalleIncluyeElIdDeLaOferta() {
        assertEquals("detalle/14", Rutas.detalle(14))
    }

    @Test
    fun cadaPestanaSeReconocePorSuRutaRegistrada() {
        val pestanaExplorar = DestinoPrincipal.desdeRuta(Rutas.EXPLORAR_POR_CATEGORIA)

        assertEquals(DestinoPrincipal.EXPLORAR, pestanaExplorar)
        assertNull(DestinoPrincipal.desdeRuta(Rutas.DETALLE))
    }

    @Test
    fun cadaRolTieneCuatroPestanasAlcanzables() {
        assertEquals(
            listOf(
                DestinoPrincipal.INICIO,
                DestinoPrincipal.EXPLORAR,
                DestinoPrincipal.PEDIDOS,
                DestinoPrincipal.PERFIL
            ),
            DestinoPrincipal.paraRol(RolUsuario.CLIENTE)
        )
        assertEquals(
            listOf(
                DestinoPrincipal.INICIO,
                DestinoPrincipal.OFERTAS,
                DestinoPrincipal.PEDIDOS,
                DestinoPrincipal.PERFIL
            ),
            DestinoPrincipal.paraRol(RolUsuario.NEGOCIO)
        )
    }

    @Test
    fun publicarSoloSeMuestraEnElInicioDelNegocio() {
        assertEquals(true, DestinoPrincipal.INICIO.muestraBotonPublicar(RolUsuario.NEGOCIO))
        assertEquals(false, DestinoPrincipal.EXPLORAR.muestraBotonPublicar(RolUsuario.CLIENTE))
    }
}
