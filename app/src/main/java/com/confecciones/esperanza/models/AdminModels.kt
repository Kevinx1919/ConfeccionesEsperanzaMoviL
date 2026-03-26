package com.confecciones.esperanza.models

data class Rol(
    val id: String,
    val nombre: String,
    val descripcion: String? = null
)

data class Producto(
    val idProducto: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val activo: Boolean = true
)
