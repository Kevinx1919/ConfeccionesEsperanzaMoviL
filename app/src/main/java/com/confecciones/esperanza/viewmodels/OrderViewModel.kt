package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.*
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    // --- Para la lista de pedidos y búsqueda ---
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredOrders: StateFlow<List<Order>> =
        combine(_orders, _searchQuery) { orders, query ->
            if (query.isBlank()) {
                orders
            } else {
                orders.filter {
                    it.id.toString().contains(query, ignoreCase = true) ||
                            (it.cliente?.let { c -> "${c.nombreCliente} ${c.apellidoCliente}" }?.contains(query, ignoreCase = true) ?: false) ||
                            it.estado?.contains(query, ignoreCase = true) == true
                }
            }
        }.let { sourceFlow ->
            val state = MutableStateFlow<List<Order>>(emptyList())
            viewModelScope.launch {
                sourceFlow.collect { state.value = it }
            }
            state.asStateFlow()
        }

    // --- Para la pantalla de detalle ---
    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    // --- Para el formulario de creación ---
    private val _clients = MutableStateFlow<List<Cliente>>(emptyList())
    val clients: StateFlow<List<Cliente>> = _clients

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadOrders(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.getOrders("Bearer $token")
                if (response.isSuccessful) {
                    _orders.value = response.body()?.pedidos ?: emptyList()
                } else {
                    _error.value = "Error al cargar los pedidos: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepción al cargar pedidos: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun loadOrderById(token: String, orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedOrder.value = null // Limpiar el pedido anterior
            _error.value = null
            try {
                val response = RetrofitClient.apiService.getOrder("Bearer $token", orderId)
                if (response.isSuccessful) {
                    _selectedOrder.value = response.body()
                } else {
                    _error.value = "Error al cargar el detalle del pedido"
                }
            } catch (e: Exception) {
                _error.value = "Excepción al cargar detalle: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun loadClients(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getClientes("Bearer $token")
                if (response.isSuccessful) {
                    _clients.value = response.body()?.clientes ?: emptyList()
                }
            } catch (e: Exception) {
                // Manejar error silenciosamente o mostrarlo si es necesario
            }
        }
    }

    fun createOrder(token: String, newOrder: OrderRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.createOrder("Bearer $token", newOrder)
                if (response.isSuccessful && response.body()?.exito == true) {
                    onResult(true, response.body()?.mensaje ?: "Pedido creado con éxito")
                    loadOrders(token) // Recargar la lista de pedidos
                } else {
                    onResult(false, response.body()?.mensaje ?: "Error al crear el pedido: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Excepción al crear el pedido: ${e.message}")
            }
        }
    }
}
