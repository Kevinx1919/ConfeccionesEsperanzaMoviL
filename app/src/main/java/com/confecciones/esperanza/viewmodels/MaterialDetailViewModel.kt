package com.confecciones.esperanza.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.confecciones.esperanza.models.Material
import com.confecciones.esperanza.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MaterialDetailUiState {
    object Loading : MaterialDetailUiState()
    data class Success(val material: Material) : MaterialDetailUiState()
    data class Error(val message: String) : MaterialDetailUiState()
    object Deleted : MaterialDetailUiState()
}

class MaterialDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<MaterialDetailUiState>(MaterialDetailUiState.Loading)
    val uiState: StateFlow<MaterialDetailUiState> = _uiState

    fun getMaterial(token: String, materialId: Int) {
        viewModelScope.launch {
            _uiState.value = MaterialDetailUiState.Loading
            try {
                val response = RetrofitClient.apiService.getMaterial("Bearer $token", materialId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = MaterialDetailUiState.Success(it)
                    } ?: run {
                        _uiState.value = MaterialDetailUiState.Error("Material no encontrado")
                    }
                } else {
                    _uiState.value = MaterialDetailUiState.Error("Error al obtener el material")
                }
            } catch (e: Exception) {
                _uiState.value = MaterialDetailUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deleteMaterial(token: String, materialId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteMaterial("Bearer $token", materialId)
                if (response.isSuccessful) {
                    _uiState.value = MaterialDetailUiState.Deleted
                } else {
                    _uiState.value = MaterialDetailUiState.Error("Error al eliminar el material")
                }
            } catch (e: Exception) {
                _uiState.value = MaterialDetailUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
