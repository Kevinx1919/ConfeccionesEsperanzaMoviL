package com.confecciones.esperanza.network

import com.confecciones.esperanza.models.*
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("Auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("Auth/profile")
    suspend fun getProfile(): Response<ProfileData>

    @PUT("Auth/profile")
    suspend fun updateProfile(
        @Body request: ProfileUpdateRequest
    ): Response<ProfileResponse>

    @POST("Auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("Dashboard/metricas")
    suspend fun getMetricas(): Response<MetricasResponse>

    @GET("Dashboard/alertas")
    suspend fun getAlertas(): Response<JsonObject>

    @GET("Dashboard/productividad-usuarios")
    suspend fun getProductividadUsuarios(): Response<JsonObject>

    // Endpoints de Clientes
    @GET("Customer")
    suspend fun getClientes(): Response<ClientesResponse>

    @GET("Customer/{id}")
    suspend fun getCliente(
        @Path("id") id: Int
    ): Response<Cliente>

    @GET("Customer/by-email/{email}")
    suspend fun getClienteByEmail(
        @Path("email") email: String
    ): Response<Cliente>

    @GET("Customer/by-document/{numeroDoc}")
    suspend fun getClienteByDocument(
        @Path("numeroDoc") numeroDoc: String
    ): Response<Cliente>

    @POST("Customer")
    suspend fun createCliente(
        @Body request: ClienteRequest
    ): Response<ClienteOperationResponse>

    @PUT("Customer/{id}")
    suspend fun updateCliente(
        @Path("id") id: Int,
        @Body request: ClienteRequest
    ): Response<ClienteOperationResponse>

    @DELETE("Customer/{id}")
    suspend fun deleteCliente(
        @Path("id") id: Int
    ): Response<ClienteOperationResponse>

    // Endpoints de Materiales
    @GET("Material")
    suspend fun getMateriales(): Response<MaterialesResponse>

    @GET("Material/{id}")
    suspend fun getMaterial(
        @Path("id") id: Int
    ): Response<Material>

    @POST("Material")
    suspend fun createMaterial(
        @Body request: CreateMaterialRequest
    ): Response<MaterialOperationResponse>

    @PUT("Material/{id}")
    suspend fun updateMaterial(
        @Path("id") id: Int,
        @Body request: CreateMaterialRequest
    ): Response<MaterialOperationResponse>

    @DELETE("Material/{id}")
    suspend fun deleteMaterial(
        @Path("id") id: Int
    ): Response<MaterialOperationResponse>

    @GET("Material/tipos")
    suspend fun getTiposMaterial(): Response<List<TipoMaterial>>

    @GET("Color")
    suspend fun getColores(): Response<List<Color>>

    // Endpoints de Tareas
    @GET("Task")
    suspend fun getTareas(): Response<TareasResponse>

    @GET("Task/asignaciones")
    suspend fun getTareaAsignaciones(): Response<List<TareaAsignacion>>

    // Endpoints de Empleados
    @GET("User")
    suspend fun getUsers(): Response<EmployeesResponse>

    @GET("Roles")
    suspend fun getRoles(): Response<List<Rol>>

    // Endpoints de Productos
    @GET("Product")
    suspend fun getProductos(): Response<List<Producto>>

    // Endpoints de Pedidos
    @GET("Order")
    suspend fun getOrders(): Response<OrdersResponse>

    @GET("Order/{id}")
    suspend fun getOrder(
        @Path("id") id: Int
    ): Response<Order>

    @POST("Order")
    suspend fun createOrder(
        @Body order: OrderRequest
    ): Response<OrderOperationResponse>
}
