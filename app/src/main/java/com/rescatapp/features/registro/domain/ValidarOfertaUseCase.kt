package com.rescatapp.features.registro.domain

import com.rescatapp.core.domain.Respuesta
import com.rescatapp.core.domain.parseadores.Parseador
import com.rescatapp.core.domain.validadores.Validador
import javax.inject.Inject

class ValidarOfertaUseCase @Inject constructor() {
    operator fun invoke(campos: CamposRegistro): ErroresRegistro = validar(campos)

    companion object {


        fun validar(campos: CamposRegistro): ErroresRegistro {
            val errorNombre = if (Validador.validarNoVacio(
                    campos.nombre,
                    MensajeError.NOMBRE_VACIO
                ) is Respuesta.Error
            ) {
                MensajeError.NOMBRE_VACIO
            } else {
                null
            }

            val errorComercio = if (Validador.validarNoVacio(
                    campos.comercio,
                    MensajeError.COMERCIO_VACIO
                ) is Respuesta.Error
            ) {
                MensajeError.COMERCIO_VACIO
            } else {
                null
            }

            val errorCategoria = if (campos.categoria == null) {
                MensajeError.CATEGORIA_VACIA
            } else {
                null
            }

            val errorDescripcion = if (Validador.validarNoVacio(
                    campos.descripcion,
                    MensajeError.DESCRIPCION_VACIA
                ) is Respuesta.Error
            ) {
                MensajeError.DESCRIPCION_VACIA
            } else {
                null
            }

            val pesoValidoNoVacio = Validador.validarNoVacio(campos.pesoKg, MensajeError.PESO_VACIO)
            val errorPeso = if (pesoValidoNoVacio is Respuesta.Error) {
                MensajeError.PESO_VACIO
            } else {
                val pesoDecimal = Parseador.aDecimal(campos.pesoKg)
                if (pesoDecimal == null) {
                    MensajeError.PESO_VACIO
                } else {
                    val respuestaMayorCero = Validador.validarMayorCero(
                        pesoDecimal,
                        MensajeError.PESO_VACIO
                    )
                    if (respuestaMayorCero is Respuesta.Error) {
                        MensajeError.PESO_VACIO
                    } else {
                        null
                    }
                }
            }

            val precioNormalNoVacio = Validador.validarNoVacio(
                campos.precioNormal,
                MensajeError.PRECIO_NORMAL_VACIO
            )
            val precioNormalDecimal = if (precioNormalNoVacio is Respuesta.Exito) {
                Parseador.aDecimal(campos.precioNormal)
            } else {
                null
            }
            val errorPrecioNormal = if (precioNormalDecimal == null) {
                MensajeError.PRECIO_NORMAL_VACIO
            } else {
                val respuestaMayorCero = Validador.validarMayorCero(
                    precioNormalDecimal,
                    MensajeError.PRECIO_NORMAL_VACIO
                )
                if (respuestaMayorCero is Respuesta.Error) {
                    MensajeError.PRECIO_NORMAL_VACIO
                } else {
                    null
                }
            }

            val precioRescateNoVacio = Validador.validarNoVacio(
                campos.precioRescate,
                MensajeError.PRECIO_RESCATE_VACIO
            )
            val precioRescateDecimal = if (precioRescateNoVacio is Respuesta.Exito) {
                Parseador.aDecimal(campos.precioRescate)
            } else {
                null
            }
            val errorPrecioRescate = if (precioRescateDecimal == null) {
                MensajeError.PRECIO_RESCATE_VACIO
            } else {
                val respuestaMayorCero = Validador.validarMayorCero(
                    precioRescateDecimal,
                    MensajeError.PRECIO_RESCATE_VACIO
                )
                if (respuestaMayorCero is Respuesta.Error) {
                    MensajeError.PRECIO_RESCATE_VACIO
                } else if (errorPrecioNormal == null && precioNormalDecimal != null &&
                    precioRescateDecimal >= precioNormalDecimal
                ) {
                    MensajeError.PRECIO_RESCATE_MENOR_NORMAL
                } else {
                    null
                }
            }

            val cantidadNoVacia = Validador.validarNoVacio(
                campos.cantidadDisponible,
                MensajeError.CANTIDAD_INVALIDA
            )
            val cantidadEntero = if (cantidadNoVacia is Respuesta.Exito) {
                Parseador.aEntero(campos.cantidadDisponible)
            } else {
                null
            }
            val errorCantidad = if (cantidadEntero == null) {
                MensajeError.CANTIDAD_INVALIDA
            } else {
                val respuestaMayorCero = Validador.validarMayorCero(
                    cantidadEntero,
                    MensajeError.CANTIDAD_INVALIDA
                )
                if (respuestaMayorCero is Respuesta.Error) {
                    MensajeError.CANTIDAD_INVALIDA
                } else {
                    null
                }
            }

            val respuestaHoraDesde = Validador.validarFormatoHora(campos.horaDesde)
            val errorHoraDesde = if (respuestaHoraDesde is Respuesta.Error) {
                MensajeError.FORMATO_HORA_INVALIDO
            } else {
                null
            }

            val respuestaHoraHasta = Validador.validarFormatoHora(campos.horaHasta)
            val errorHoraHasta = if (respuestaHoraHasta is Respuesta.Error) {
                MensajeError.FORMATO_HORA_INVALIDO
            } else {
                if (errorHoraDesde == null) {
                    val horaInicio = Parseador.aHora(campos.horaDesde)
                    val horaFin = Parseador.aHora(campos.horaHasta)
                    if (horaInicio != null && horaFin != null && !horaFin.isAfter(horaInicio)) {
                        MensajeError.HORA_FINAL_MAYOR
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            return ErroresRegistro(
                nombre = errorNombre,
                comercio = errorComercio,
                categoria = errorCategoria,
                descripcion = errorDescripcion,
                pesoKg = errorPeso,
                precioNormal = errorPrecioNormal,
                precioRescate = errorPrecioRescate,
                cantidadDisponible = errorCantidad,
                horaDesde = errorHoraDesde,
                horaHasta = errorHoraHasta
            )
        }
    }
}
