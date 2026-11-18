package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.model.RolUsuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SesionDaoTest {

    private lateinit var database: RescatAppDatabase
    private lateinit var sesionDao: SesionDao

    @Before
    fun crearBaseDatos() {
        val contexto: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(contexto, RescatAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sesionDao = database.sesionDao()
    }

    @After
    fun cerrarBaseDatos() {
        database.close()
    }

    @Test
    fun sesionInicialmenteEsNull() = runTest {
        val sesion = sesionDao.obtenerSesion()
        assertNull(sesion)
    }

    @Test
    fun guardarSesionPersisteYPermiteObservar() = runTest {
        val sesion = SesionEntity(
            usuarioId = "usr-42",
            correo = "test@rescatapp.com",
            nombre = "Tester",
            rol = RolUsuario.CLIENTE,
            token = "token",
            refreshToken = "renovacion",
            expira = 0
        )

        sesionDao.guardarSesion(sesion)

        val obtenida = sesionDao.obtenerSesion()
        assertNotNull(obtenida)
        assertEquals("usr-42", obtenida?.usuarioId)
        assertEquals("test@rescatapp.com", obtenida?.correo)

        val observada = sesionDao.observarSesion().first()
        assertEquals("Tester", observada?.nombre)
    }

    @Test
    fun guardarNuevaSesionReemplazaLaAnterior() = runTest {
        sesionDao.guardarSesion(
            SesionEntity(
                usuarioId = "usr-1",
                correo = "usr1@test.com",
                nombre = "Usuario Uno",
                rol = RolUsuario.CLIENTE,
                token = "token",
                refreshToken = "renovacion",
                expira = 0
            )
        )

        sesionDao.guardarSesion(
            SesionEntity(
                usuarioId = "usr-2",
                correo = "usr2@test.com",
                nombre = "Usuario Dos",
                rol = RolUsuario.NEGOCIO,
                nombreNegocio = "Comercio Dos",
                token = "token",
                refreshToken = "renovacion",
                expira = 0
            )
        )

        val actual = sesionDao.obtenerSesion()
        assertNotNull(actual)
        assertEquals("usr-2", actual?.usuarioId)
        assertEquals(RolUsuario.NEGOCIO, actual?.rol)
        assertEquals("Comercio Dos", actual?.nombreNegocio)
    }

    @Test
    fun limpiarSesionEliminaLaSesionActiva() = runTest {
        sesionDao.guardarSesion(
            SesionEntity(
                usuarioId = "usr-1",
                correo = "usr1@test.com",
                nombre = "Usuario Uno",
                rol = RolUsuario.CLIENTE,
                token = "token",
                refreshToken = "renovacion",
                expira = 0
            )
        )

        val eliminadas = sesionDao.limpiarSesion()
        assertEquals(1, eliminadas)
        assertNull(sesionDao.obtenerSesion())
    }
}
