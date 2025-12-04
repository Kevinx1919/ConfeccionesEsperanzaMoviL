package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.MetricasResponse
import com.confecciones.esperanza.models.ResumenVentasResponse
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success<T>(val data: T) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {

    private val _metricasState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val metricasState: StateFlow<DashboardUiState> = _metricasState

    private val _ventasState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val ventasState: StateFlow<DashboardUiState> = _ventasState

    fun loadMetricas(token: String) {
        viewModelScope.launch {
            _metricasState.value = DashboardUiState.Loading

            try {
                val response = RetrofitClient.apiService.getMetricas("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    _metricasState.value = DashboardUiState.Success(response.body()!!)
                } else {
                    _metricasState.value = DashboardUiState.Error("Error al cargar métricas: ${response.code()}")
                }
            } catch (e: Exception) {
                _metricasState.value = DashboardUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun loadResumenVentas(token: String) {
        viewModelScope.launch {
            _ventasState.value = DashboardUiState.Loading

            try {
                val response = RetrofitClient.apiService.getResumenVentas("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    _ventasState.value = DashboardUiState.Success(response.body()!!)
                } else {
                    _ventasState.value = DashboardUiState.Error("Error al cargar ventas: ${response.code()}")
                }
            } catch (e: Exception) {
                _ventasState.value = DashboardUiState.Error("Error: ${e.message ?: "Desconocido"}")
            }
        }
    }

    fun refreshData(token: String) {
        loadMetricas(token)
        loadResumenVentas(token)
    }
}