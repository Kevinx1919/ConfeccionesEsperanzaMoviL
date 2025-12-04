package com.confecciones.esperanza.models

// Respuesta de lista de clientes
data class ClientesResponse(
    val clientes: List<Cliente>
)

// Modelo de Cliente
data class Cliente(
    val idCliente: Int,
    val nombreCliente: String,
    val apellidoCliente: String,
    val nombreCompleto: String,
    val emailCliente: String,
    val telefonoCliente: String,
    val numeroDocCliente: String,
    val direccionCliente: String,
    val codigoPostalCliente: String,
    val fechaCreacion: String,
    val fechaActualizacion: String?,
    val totalPedidos: Int
)

// Request para crear/actualizar cliente
data class ClienteRequest(
    val nombreCliente: String,
    val apellidoCliente: String,
    val emailCliente: String,
    val telefonoCliente: String,
    val numeroDocCliente: String,
    val direccionCliente: String,
    val codigoPostalCliente: String
)

// Respuesta genérica para operaciones
data class ClienteOperationResponse(
    val isSuccess: Boolean,
    val message: String,
    val cliente: Cliente?
)