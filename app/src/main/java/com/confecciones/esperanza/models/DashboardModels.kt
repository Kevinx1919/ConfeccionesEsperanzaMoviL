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

// Respuesta de Resumen de Ventas
data class ResumenVentasResponse(
    val ventasHoy: Double,
    val ventasSemana: Double,
    val ventasMes: Double,
    val tendencia: List<TendenciaVenta>
)

data class TendenciaVenta(
    val fecha: String,
    val monto: Double,
    val pedidos: Int
)