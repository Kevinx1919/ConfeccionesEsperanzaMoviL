package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.DashboardAlerta
import com.confecciones.esperanza.models.MetricasResponse
import com.confecciones.esperanza.models.ProductividadUsuario
import com.confecciones.esperanza.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success<T>(val data: T) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {
    private val gson = Gson()

    private val _metricasState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val metricasState: StateFlow<DashboardUiState> = _metricasState

    private val _alertasState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val alertasState: StateFlow<DashboardUiState> = _alertasState

    private val _productividadState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val productividadState: StateFlow<DashboardUiState> = _productividadState

    fun loadMetricas() {
        viewModelScope.launch {
            _metricasState.value = DashboardUiState.Loading

            try {
                val response = RetrofitClient.apiService.getMetricas()

                if (response.isSuccessful && response.body() != null) {
                    _metricasState.value = DashboardUiState.Success(response.body()!!)
                } else {
                    _metricasState.value = DashboardUiState.Error("Error al cargar metricas: ${response.code()}")
                }
            } catch (e: Exception) {
                _metricasState.value = DashboardUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun loadAlertas() {
        viewModelScope.launch {
            _alertasState.value = DashboardUiState.Loading

            try {
                val response = RetrofitClient.apiService.getAlertas()

                if (response.isSuccessful && response.body() != null) {
                    _alertasState.value = DashboardUiState.Success(parseAlertas(response.body()!!))
                } else {
                    _alertasState.value = DashboardUiState.Error("Error al cargar alertas: ${response.code()}")
                }
            } catch (e: Exception) {
                _alertasState.value = DashboardUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun loadProductividad() {
        viewModelScope.launch {
            _productividadState.value = DashboardUiState.Loading

            try {
                val response = RetrofitClient.apiService.getProductividadUsuarios()

                if (response.isSuccessful && response.body() != null) {
                    _productividadState.value =
                        DashboardUiState.Success(parseProductividad(response.body()!!))
                } else {
                    _productividadState.value =
                        DashboardUiState.Error("Error al cargar productividad: ${response.code()}")
                }
            } catch (e: Exception) {
                _productividadState.value = DashboardUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun refreshData() {
        loadMetricas()
        loadAlertas()
        loadProductividad()
    }

    private fun parseAlertas(jsonObject: JsonObject): List<DashboardAlerta> {
        val array = findFirstArray(
            jsonObject = jsonObject,
            preferredKeys = listOf("alertas", "data", "items", "result")
        ) ?: return emptyList()

        return array.mapNotNull { element ->
            runCatching { gson.fromJson(element, DashboardAlerta::class.java) }.getOrNull()
        }
    }

    private fun parseProductividad(jsonObject: JsonObject): List<ProductividadUsuario> {
        val array = findFirstArray(
            jsonObject = jsonObject,
            preferredKeys = listOf("productividadUsuarios", "usuarios", "data", "items", "result")
        ) ?: return emptyList()

        return array.mapNotNull { element ->
            runCatching { gson.fromJson(element, ProductividadUsuario::class.java) }.getOrNull()
        }
    }

    private fun findFirstArray(
        jsonObject: JsonObject,
        preferredKeys: List<String>
    ): JsonArray? {
        preferredKeys.forEach { key ->
            val candidate = jsonObject.get(key)
            if (candidate != null) {
                extractArray(candidate)?.let { return it }
            }
        }

        jsonObject.entrySet().forEach { entry ->
            extractArray(entry.value)?.let { return it }
        }

        return null
    }

    private fun extractArray(element: JsonElement): JsonArray? {
        return when {
            element.isJsonArray -> element.asJsonArray
            element.isJsonObject -> {
                val nestedObject = element.asJsonObject
                nestedObject.entrySet().firstNotNullOfOrNull { extractArray(it.value) }
            }
            else -> null
        }
    }
}
