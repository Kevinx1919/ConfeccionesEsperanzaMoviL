package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Employee
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredEmployees = MutableStateFlow<List<Employee>>(emptyList())
    val filteredEmployees: StateFlow<List<Employee>> = _filteredEmployees.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterEmployees()
    }

    private fun filterEmployees() {
        val query = _searchQuery.value.lowercase()
        _filteredEmployees.value = if (query.isBlank()) {
            _employees.value
        } else {
            _employees.value.filter {
                it.userName.lowercase().contains(query) ||
                it.email.lowercase().contains(query) ||
                it.phoneNumber?.contains(query) ?: false
            }
        }
    }

    fun getEmployees(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getUsers("Bearer $token")
                if (response.isSuccessful) {
                    _employees.value = response.body()?.employees ?: emptyList()
                    filterEmployees() // Initial filter
                } else {
                    _error.value = "Error al obtener los empleados"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
