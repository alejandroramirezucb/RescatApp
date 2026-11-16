package com.rescatapp.features.auth.data

import java.security.MessageDigest

object SeguridadAuth {

    private const val SAL = "RescatApp_Salt_Segura_2026"

    fun hashearContrasena(contrasena: String): String = MessageDigest.getInstance("SHA-256")
        .digest("$SAL:$contrasena".toByteArray())
        .joinToString("") { "%02x".format(it) }

    fun verificarContrasena(contrasena: String, hashGuardado: String): Boolean =
        hashearContrasena(contrasena) == hashGuardado
}
