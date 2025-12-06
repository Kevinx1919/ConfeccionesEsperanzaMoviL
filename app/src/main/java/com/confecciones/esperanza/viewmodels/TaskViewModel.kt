package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Tarea
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> = _tareas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getTareas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getTareas("Bearer $token")
                if (response.isSuccessful) {
                    _tareas.value = response.body()?.tareas ?: emptyList()
                } else {
                    _error.value = "Error al obtener las tareas"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
