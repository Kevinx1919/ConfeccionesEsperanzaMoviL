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
                        (it.cliente?.let { c -> "${c.nombreCliente} ${c.apellidoCliente}" }
                            ?.contains(query, ignoreCase = true) ?: false) ||
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

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    private val _clients = MutableStateFlow<List<Cliente>>(emptyList())
    val clients: StateFlow<List<Cliente>> = _clients

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.getOrders()
                if (response.isSuccessful) {
                    _orders.value = response.body()?.pedidos ?: emptyList()
                } else {
                    _error.value = "Error al cargar los pedidos: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Excepcion al cargar pedidos: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun loadOrderById(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedOrder.value = null
            _error.value = null
            try {
                val response = RetrofitClient.apiService.getOrder(orderId)
                if (response.isSuccessful) {
                    _selectedOrder.value = response.body()
                } else {
                    _error.value = "Error al cargar el detalle del pedido"
                }
            } catch (e: Exception) {
                _error.value = "Excepcion al cargar detalle: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getClientes()
                if (response.isSuccessful) {
                    _clients.value = response.body()?.clientes ?: emptyList()
                }
            } catch (e: Exception) {
                // Silencioso para no bloquear el formulario
            }
        }
    }

    fun createOrder(newOrder: OrderRequest, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.createOrder(newOrder)
                if (response.isSuccessful && response.body()?.exito == true) {
                    onResult(true, response.body()?.mensaje ?: "Pedido creado con exito")
                    loadOrders()
                } else {
                    onResult(false, response.body()?.mensaje ?: "Error al crear el pedido: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Excepcion al crear el pedido: ${e.message}")
            }
        }
    }
}
