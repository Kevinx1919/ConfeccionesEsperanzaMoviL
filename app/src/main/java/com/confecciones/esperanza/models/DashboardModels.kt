package com.confecciones.esperanza.models

// Respuesta de Métricas
data class MetricasResponse(
    val proximoPedido: ProximoPedido,
    val progresoProximoPedido: ProgresoProximoPedido,
    val pedidosPendientes: List<PedidoPendiente>
)

data class ProximoPedido(
    val idPedido: Int,
    val clienteNombre: String,
    val fechaEntrega: String,
    val estado: String,
    val porcentajeCompletado: Double,
    val totalTareas: Int,
    val tareasCompletadas: Int,
    val totalPedido: Double,
    val imagenProducto: String
)

data class ProgresoProximoPedido(
    val idPedido: Int,
    val totalTareas: Int,
    val tareasCompletadas: Int,
    val tareasPendientes: Int,
    val tareasEnProceso: Int,
    val porcentajeCompletado: Double,
    val fechaEntrega: String,
    val tiempoRestante: String,
    val diasRestantes: Int,
    val horasRestantes: Int,
    val minutosRestantes: Int,
    val estaVencido: Boolean
)

data class PedidoPendiente(
    val idPedido: Int,
    val clienteNombre: String,
    val fechaRegistro: String,
    val fechaEntrega: String,
    val estado: String,
    val estado_descripcion: String,
    val clienteIdCliente: Int,
    val clienteEmail: String,
    val detallesPedido: List<DetallePedido>,
    val totalPedido: Double,
    val totalItems: Int,
    val estaVencido: Boolean,
    val fechaCreacion: String,
    val fechaActualizacion: String
)

data class DetallePedido(
    val id: Int,
    val producto_IdProducto: Int,
    val productoNombre: String,
    val productoDescripcion: String,
    val colorDescripcion: String,
    val tallaDescripcion: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val fechaCreacion: String
)

data class DashboardAlerta(
    val id: Int? = null,
    val tipo: String? = null,
    val titulo: String? = null,
    val descripcion: String? = null,
    val prioridad: String? = null,
    val fecha: String? = null
)

data class ProductividadUsuario(
    val userId: String? = null,
    val userName: String? = null,
    val tareasCompletadas: Int? = null,
    val tareasEnProceso: Int? = null,
    val eficiencia: Double? = null
)
