package com.confecciones.esperanza.network

import com.confecciones.esperanza.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("Auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("Auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileData>

    @PUT("Auth/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest
    ): Response<ProfileResponse>

    @GET("Dashboard/metricas")
    suspend fun getMetricas(@Header("Authorization") token: String): Response<MetricasResponse>

    @GET("Dashboard/resumen-ventas")
    suspend fun getResumenVentas(@Header("Authorization") token: String): Response<ResumenVentasResponse>

    @GET("Dashboard/pedido/{pedidoId}/progreso")
    suspend fun getProgresoPedido(
        @Header("Authorization") token: String,
        @Path("pedidoId") pedidoId: Int
    ): Response<ProgresoProximoPedido>

    // Endpoints de Clientes
    @GET("Customer")
    suspend fun getClientes(@Header("Authorization") token: String): Response<ClientesResponse>

    @GET("Customer/{id}")
    suspend fun getCliente(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Cliente>

    @GET("Customer/by-email/{email}")
    suspend fun getClienteByEmail(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): Response<Cliente>

    @GET("Customer/by-document/{numeroDoc}")
    suspend fun getClienteByDocument(
        @Header("Authorization") token: String,
        @Path("numeroDoc") numeroDoc: String
    ): Response<Cliente>

    @POST("Customer")
    suspend fun createCliente(
        @Header("Authorization") token: String,
        @Body request: ClienteRequest
    ): Response<ClienteOperationResponse>

    @PUT("Customer/{id}")
    suspend fun updateCliente(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: ClienteRequest
    ): Response<ClienteOperationResponse>

    @DELETE("Customer/{id}")
    suspend fun deleteCliente(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ClienteOperationResponse>

    // Endpoints de Materiales
    @GET("Material")
    suspend fun getMateriales(@Header("Authorization") token: String): Response<MaterialesResponse>

    @GET("Material/{id}")
    suspend fun getMaterial(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Material>

    @POST("Material")
    suspend fun createMaterial(
        @Header("Authorization") token: String,
        @Body request: CreateMaterialRequest
    ): Response<MaterialOperationResponse>

    @PUT("Material/{id}")
    suspend fun updateMaterial(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: CreateMaterialRequest
    ): Response<MaterialOperationResponse>

    @DELETE("Material/{id}")
    suspend fun deleteMaterial(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<MaterialOperationResponse>

    @GET("Material/tipos")
    suspend fun getTiposMaterial(@Header("Authorization") token: String): Response<List<TipoMaterial>>

    @GET("Color")
    suspend fun getColores(@Header("Authorization") token: String): Response<List<Color>>

    // Endpoints de Tareas
    @GET("Task")
    suspend fun getTareas(@Header("Authorization") token: String): Response<TareasResponse>

    // Endpoints de Empleados
    @GET("User")
    suspend fun getUsers(@Header("Authorization") token: String): Response<EmployeesResponse>

    // Endpoints de Pedidos
    @GET("Order")
    suspend fun getOrders(@Header("Authorization") token: String): Response<OrdersResponse>

    @GET("Order/{id}")
    suspend fun getOrder(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Order>

    @POST("Order")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body order: OrderRequest
    ): Response<OrderOperationResponse>
}
