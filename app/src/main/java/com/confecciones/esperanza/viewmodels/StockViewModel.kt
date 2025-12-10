package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Material
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StockViewModel : ViewModel() {

    private val _materiales = MutableStateFlow<List<Material>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredMateriales = MutableStateFlow<List<Material>>(emptyList())
    val filteredMateriales: StateFlow<List<Material>> = _filteredMateriales.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterMateriales()
    }

    private fun filterMateriales() {
        val query = _searchQuery.value.lowercase()
        _filteredMateriales.value = if (query.isBlank()) {
            _materiales.value
        } else {
            _materiales.value.filter {
                it.nombre.lowercase().contains(query) ||
                it.proveedor.lowercase().contains(query)
            }
        }
    }

    fun getMateriales(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getMateriales("Bearer $token")
                if (response.isSuccessful) {
                    _materiales.value = response.body()?.materiales ?: emptyList()
                    filterMateriales() // Initial filter
                } else {
                    _error.value = "Error al obtener los materiales"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
