package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Cliente
import com.confecciones.esperanza.models.ClienteRequest
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CustomerUiState {
    object Idle : CustomerUiState()
    object Loading : CustomerUiState()
    data class Success<T>(val data: T) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
    data class OperationSuccess(val message: String) : CustomerUiState()
}

class CustomerViewModel : ViewModel() {

    private val _clientesState = MutableStateFlow<CustomerUiState>(CustomerUiState.Idle)
    val clientesState: StateFlow<CustomerUiState> = _clientesState

    private val _clienteState = MutableStateFlow<CustomerUiState>(CustomerUiState.Idle)
    val clienteState: StateFlow<CustomerUiState> = _clienteState

    private val _operationState = MutableStateFlow<CustomerUiState>(CustomerUiState.Idle)
    val operationState: StateFlow<CustomerUiState> = _operationState

    private val _clientes = MutableStateFlow<List<Cliente>>(emptyList())
    val clientes: StateFlow<List<Cliente>> = _clientes

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun loadClientes(token: String) {
        viewModelScope.launch {
            _clientesState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.getClientes("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    val clientesList = response.body()!!.clientes
                    _clientes.value = clientesList
                    _clientesState.value = CustomerUiState.Success(clientesList)
                } else {
                    _clientesState.value = CustomerUiState.Error("Error al cargar clientes: ${response.code()}")
                }
            } catch (e: Exception) {
                _clientesState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun loadCliente(token: String, id: Int) {
        viewModelScope.launch {
            _clienteState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.getCliente("Bearer $token", id)

                if (response.isSuccessful && response.body() != null) {
                    _clienteState.value = CustomerUiState.Success(response.body()!!)
                } else {
                    _clienteState.value = CustomerUiState.Error("Error al cargar cliente: ${response.code()}")
                }
            } catch (e: Exception) {
                _clienteState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun searchClienteByEmail(token: String, email: String) {
        viewModelScope.launch {
            _clienteState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.getClienteByEmail("Bearer $token", email)

                if (response.isSuccessful && response.body() != null) {
                    _clienteState.value = CustomerUiState.Success(response.body()!!)
                } else {
                    _clienteState.value = CustomerUiState.Error("Cliente no encontrado")
                }
            } catch (e: Exception) {
                _clienteState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun searchClienteByDocument(token: String, documento: String) {
        viewModelScope.launch {
            _clienteState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.getClienteByDocument("Bearer $token", documento)

                if (response.isSuccessful && response.body() != null) {
                    _clienteState.value = CustomerUiState.Success(response.body()!!)
                } else {
                    _clienteState.value = CustomerUiState.Error("Cliente no encontrado")
                }
            } catch (e: Exception) {
                _clienteState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun createCliente(token: String, request: ClienteRequest) {
        viewModelScope.launch {
            _operationState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.createCliente("Bearer $token", request)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.isSuccess) {
                        _operationState.value = CustomerUiState.OperationSuccess(result.message)
                        loadClientes(token) // Recargar lista
                    } else {
                        _operationState.value = CustomerUiState.Error(result.message)
                    }
                } else {
                    _operationState.value = CustomerUiState.Error("Error al crear cliente: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun updateCliente(token: String, id: Int, request: ClienteRequest) {
        viewModelScope.launch {
            _operationState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.updateCliente("Bearer $token", id, request)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.isSuccess) {
                        _operationState.value = CustomerUiState.OperationSuccess(result.message)
                        loadClientes(token) // Recargar lista
                    } else {
                        _operationState.value = CustomerUiState.Error(result.message)
                    }
                } else {
                    _operationState.value = CustomerUiState.Error("Error al actualizar cliente: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun deleteCliente(token: String, id: Int) {
        viewModelScope.launch {
            _operationState.value = CustomerUiState.Loading

            try {
                val response = RetrofitClient.apiService.deleteCliente("Bearer $token", id)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.isSuccess) {
                        _operationState.value = CustomerUiState.OperationSuccess(result.message)
                        loadClientes(token) // Recargar lista
                    } else {
                        _operationState.value = CustomerUiState.Error(result.message)
                    }
                } else {
                    _operationState.value = CustomerUiState.Error("Error al eliminar cliente: ${response.code()}")
                }
            } catch (e: Exception) {
                _operationState.value = CustomerUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredClientes(): List<Cliente> {
        val query = _searchQuery.value.lowercase()
        return if (query.isBlank()) {
            _clientes.value
        } else {
            _clientes.value.filter {
                it.nombreCompleto.lowercase().contains(query) ||
                        it.emailCliente.lowercase().contains(query) ||
                        it.numeroDocCliente.contains(query) ||
                        it.telefonoCliente.contains(query)
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = CustomerUiState.Idle
    }

    fun resetClienteState() {
        _clienteState.value = CustomerUiState.Idle
    }
}