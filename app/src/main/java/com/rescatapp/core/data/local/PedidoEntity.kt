package com.rescatapp.core.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import java.time.LocalDate

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ofertaId: Int,
    val nombreOferta: String,
    val comercio: String,
    val categoria: Categoria,
    val precioPagado: Double,
    val ahorro: Double,
    val pesoKg: Double,
    val horaRetiroDesde: String,
    val horaRetiroHasta: String,
    val estado: EstadoPedido,
    val imagenUri: String? = null,
    val fecha: LocalDate = LocalDate.now(),
    val codigoServidor: String? = null,
    val fechaRetiro: LocalDate = fecha,
    val clienteNombre: String? = null,
    val clienteTelefono: String? = null,
    @ColumnInfo(defaultValue = "'APROBADO'")
    val estadoPagoSimulado: String = EstadoPagoSimulado.segun(estado).name
) {
    fun toDomain(): Pedido = Pedido(
        id = id,
        ofertaId = ofertaId,
        nombreOferta = nombreOferta,
        comercio = comercio,
        categoria = categoria,
        precioPagado = precioPagado,
        ahorro = ahorro,
        pesoKg = pesoKg,
        horaRetiroDesde = horaRetiroDesde,
        horaRetiroHasta = horaRetiroHasta,
        estado = estado,
        imagenUri = imagenUri,
        fecha = fecha,
        codigoServidor = codigoServidor,
        fechaRetiro = fechaRetiro,
        clienteNombre = clienteNombre,
        clienteTelefono = clienteTelefono,
        estadoPagoSimulado = EstadoPagoSimulado.valueOf(estadoPagoSimulado)
    )
}

fun Pedido.toEntity(): PedidoEntity = PedidoEntity(
    id = id,
    ofertaId = ofertaId,
    nombreOferta = nombreOferta,
    comercio = comercio,
    categoria = categoria,
    precioPagado = precioPagado,
    ahorro = ahorro,
    pesoKg = pesoKg,
    horaRetiroDesde = horaRetiroDesde,
    horaRetiroHasta = horaRetiroHasta,
    estado = estado,
    imagenUri = imagenUri,
    fecha = fecha,
    codigoServidor = codigoServidor,
    fechaRetiro = fechaRetiro,
    clienteNombre = clienteNombre,
    clienteTelefono = clienteTelefono,
    estadoPagoSimulado = estadoPagoSimulado.name
)
