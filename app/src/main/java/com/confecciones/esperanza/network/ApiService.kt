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
}