package com.confecciones.esperanza.models

import com.google.gson.annotations.SerializedName


data class OrdersResponse(
    @SerializedName("pedidos") val pedidos: List<Order>
)

// CORRECCIÓN DEFINITIVA: Modelo sincronizado con el backend C#
data class Order(
    @SerializedName("idPedido") val id: Int,
    @SerializedName("cliente") val cliente: Cliente?,
    @SerializedName("fechaRegistro") val registrationDate: String?,
    @SerializedName("fechaEntrega") val deliveryDate: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("totalPedido") val total: Double?,
    @SerializedName("detallesPedido") val details: List<OrderDetail>?,
    @SerializedName("totalItems") val totalItems: Int?,
    @SerializedName("estaVencido") val estaVencido: Boolean?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String?
)

data class OrderDetail(
    @SerializedName("idProducto") val idProducto: Int?,
    @SerializedName("producto") val productName: String?,
    @SerializedName("cantidad") val cantidad: Int?,
    @SerializedName("precioUnitario") val unitPrice: Double?,
    @SerializedName("subtotal") val subtotal: Double?
)

data class NewOrder(
    val clienteId: Int,
    val fechaEntrega: String,
    val detalles: List<NewOrderDetail>
)

data class NewOrderDetail(
    val productoId: Int,
    val cantidad: Int,
    val precioUnitario: Double
)

data class OrderRequest(
    val clienteId: Int,
    val fechaEntrega: String,
    val detalles: List<NewOrderDetail>
)

data class OrderOperationResponse(
    val exito: Boolean,
    val mensaje: String,
    val idPedido: Int?
)

